package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.model.Professor;
import br.com.empowerlearn.empowerlearn_api.repository.ProfessorRepository;
import br.com.empowerlearn.empowerlearn_api.service.CepService;

// NOVOS IMPORTS NECESSÁRIOS PARA MANUSEIO DE ARQUIVOS E RESPOSTAS
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
// FIM DOS NOVOS IMPORTS

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/professores")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CepService cepService;

    // ========================================================
    // 1. CADASTRO (POST)
    // ========================================================
    @PostMapping
    public ResponseEntity<?> cadastrarProfessor(@RequestBody Professor professor) {
        if (professorRepository.existsByEmail(professor.getEmail())) {
            return new ResponseEntity<>("Erro: Este e-mail já está cadastrado.", HttpStatus.BAD_REQUEST);
        }

        try {
            JsonNode endereco = cepService.buscarEnderecoPorCep(professor.getCep());

            if (endereco == null) {
                return new ResponseEntity<>("Erro: CEP inválido ou não encontrado.", HttpStatus.BAD_REQUEST);
            }

            professor.setCidade(endereco.get("localidade").asText());
            professor.setEstado(endereco.get("uf").asText());
            professor.setPais("BRASIL");

        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno ao processar CEP.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Professor novoProfessor = professorRepository.save(professor);
        return new ResponseEntity<>(novoProfessor, HttpStatus.CREATED);
    }

    // ========================================================
    // 2. ENDPOINT: UPLOAD DE FOTO (POST /api/professores/{id}/upload-foto)
    // ========================================================
    @PostMapping("/{id}/upload-foto")
    public ResponseEntity<String> uploadFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("Nenhum arquivo enviado.", HttpStatus.BAD_REQUEST);
        }

        // MUITO IMPORTANTE: Define o diretório de uploads
        // Certifique-se de que este caminho C:\\temp\\uploads realmente existe na sua máquina!
        String uploadDir = "C:\\temp\\empowerlearn\\uploads";

        try {
            // Garante que o diretório exista (se for o primeiro upload)
            Path directoryPath = Paths.get(uploadDir);
            Files.createDirectories(directoryPath);

            // Define o nome do arquivo (ID do professor + nome original)
            String fileName = id + "_" + file.getOriginalFilename();
            Path copyLocation = Paths.get(uploadDir + File.separator + fileName);

            // Salva o arquivo no disco
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

            // Atualiza o objeto Professor no Banco de Dados
            Professor professor = professorRepository.findById(id).orElseThrow(() -> new RuntimeException("Professor não encontrado."));

            String fotoUrl = "/uploads/" + fileName;
            professor.setFotoUrl(fotoUrl);
            professorRepository.save(professor);

            // Retorna o URL para o Front-End
            return new ResponseEntity<>(fotoUrl, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Erro durante o upload: " + e.getMessage());
            return new ResponseEntity<>("Erro ao salvar foto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ========================================================
    // 3. ENDPOINT: BUSCAR POR ID (GET)
    // ========================================================
    @GetMapping("/{id}")
    public ResponseEntity<Professor> buscarPorId(@PathVariable Long id) {
        return professorRepository.findById(id)
                .map(professor -> new ResponseEntity<>(professor, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }



    // Lógica de LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Professor credenciais) {
        Professor professor = professorRepository.findByEmailAndSenha(credenciais.getEmail(), credenciais.getSenha());
        if (professor != null) {
            return new ResponseEntity<>(professor, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Credenciais inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }

    // Lógica de BUSCA
    @GetMapping("/search")
    public ResponseEntity<List<Professor>> buscarProfessores(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String especialidade
    ) {
        List<Professor> professores = professorRepository.buscarProfessoresPorFiltro(nome, especialidade);
        if (professores.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(professores, HttpStatus.OK);
    }

    // Lógica de DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (professorRepository.existsById(id)) {
            professorRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Lógica de Listar Todos
    @GetMapping
    public List<Professor> listarTodos() {
        return professorRepository.findAll();
    }
}