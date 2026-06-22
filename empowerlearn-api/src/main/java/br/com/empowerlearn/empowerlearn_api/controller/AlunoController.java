package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.model.Aluno;
import br.com.empowerlearn.empowerlearn_api.repository.AlunoRepository;
import br.com.empowerlearn.empowerlearn_api.service.CepService;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CepService cepService;

    // ========================================================
    // 1. CADASTRO (POST)
    // ========================================================
    @PostMapping
    public ResponseEntity<?> cadastrarAluno(@RequestBody Aluno aluno) {
        if (alunoRepository.existsByEmail(aluno.getEmail())) {
            return new ResponseEntity<>("Erro: Este e-mail já está cadastrado.", HttpStatus.BAD_REQUEST);
        }

        try {
            JsonNode endereco = cepService.buscarEnderecoPorCep(aluno.getCep());
            if (endereco == null) {
                return new ResponseEntity<>("Erro: CEP inválido ou não encontrado.", HttpStatus.BAD_REQUEST);
            }
            aluno.setCidade(endereco.get("localidade").asText());
            aluno.setEstado(endereco.get("uf").asText());
            aluno.setPais("BRASIL");
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno ao processar CEP.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Aluno novoAluno = alunoRepository.save(aluno);
        return new ResponseEntity<>(novoAluno, HttpStatus.CREATED);
    }

    // ========================================================
    // 2. ENDPOINT: UPLOAD DE FOTO (POST /api/alunos/{id}/upload-foto)
    // ========================================================
    @PostMapping("/{id}/upload-foto")
    public ResponseEntity<String> uploadFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("Nenhum arquivo enviado.", HttpStatus.BAD_REQUEST);
        }

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

        try {
            Path directoryPath = Paths.get(uploadDir);
            Files.createDirectories(directoryPath);

            String fileName = id + "_" + file.getOriginalFilename();
            Path copyLocation = Paths.get(uploadDir + File.separator + fileName);

            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

            Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

            String fotoUrl = "/uploads/" + fileName;
            aluno.setFotoUrl(fotoUrl);
            alunoRepository.save(aluno);

            return new ResponseEntity<>(fotoUrl, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Erro durante o upload: " + e.getMessage());
            return new ResponseEntity<>("Erro ao salvar foto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ========================================================
    // 3. BUSCAR POR ID (GET) - Para ver-perfil.html
    // ========================================================
    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        return alunoRepository.findById(id)
                .map(aluno -> new ResponseEntity<>(aluno, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ========================================================
    // 4. LOGIN (POST)
    // ========================================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Aluno credenciais) {
        Aluno aluno = alunoRepository.findByEmailAndSenha(credenciais.getEmail(), credenciais.getSenha());
        if (aluno != null) {
            return new ResponseEntity<>(aluno, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Credenciais inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }

    // ========================================================
    // 5. EXCLUSÃO DE PERFIL (DELETE) - Rota: /api/alunos/{id}
    // ========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (alunoRepository.existsById(id)) {
            alunoRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}