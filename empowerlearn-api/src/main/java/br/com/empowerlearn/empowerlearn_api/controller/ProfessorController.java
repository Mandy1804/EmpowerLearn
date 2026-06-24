package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.model.Professor;
import br.com.empowerlearn.empowerlearn_api.repository.ProfessorRepository;
import br.com.empowerlearn.empowerlearn_api.security.JwtService;
import br.com.empowerlearn.empowerlearn_api.service.CepService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/professores")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CepService cepService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> cadastrarProfessor(@RequestBody Professor professor) {
        if (professorRepository.existsByEmail(professor.getEmail())) {
            return new ResponseEntity<>("Erro: Este e-mail já está cadastrado.", HttpStatus.BAD_REQUEST);
        }
        if (professor.getSenha() == null || professor.getSenha().length() < 6) {
            return new ResponseEntity<>("Erro: A senha deve ter pelo menos 6 caracteres.", HttpStatus.BAD_REQUEST);
        }
        if (professor.getNome() == null || professor.getNome().trim().isEmpty()) {
            return new ResponseEntity<>("Erro: O nome é obrigatório.", HttpStatus.BAD_REQUEST);
        }
        if (professor.getDataNascimento() != null) {
            int idade = java.time.Period.between(professor.getDataNascimento(), java.time.LocalDate.now()).getYears();
            if (idade < 18) {
                return new ResponseEntity<>("Erro: É necessário ter pelo menos 18 anos.", HttpStatus.BAD_REQUEST);
            }
        }
        professor.setSenha(passwordEncoder.encode(professor.getSenha()));
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
        String token = jwtService.gerarToken(novoProfessor.getEmail(), "professor");
        return new ResponseEntity<>(Map.of("professor", novoProfessor, "token", token), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Professor credenciais) {
        if (credenciais.getEmail() == null || credenciais.getSenha() == null) {
            return new ResponseEntity<>("Email e senha são obrigatórios.", HttpStatus.BAD_REQUEST);
        }
        Professor professor = professorRepository.findByEmail(credenciais.getEmail());
        if (professor == null || !passwordEncoder.matches(credenciais.getSenha(), professor.getSenha())) {
            return new ResponseEntity<>("Credenciais inválidas.", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtService.gerarToken(professor.getEmail(), "professor");
        return new ResponseEntity<>(Map.of("professor", professor, "token", token), HttpStatus.OK);
    }

    @PostMapping("/{id}/upload-foto")
    public ResponseEntity<String> uploadFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return new ResponseEntity<>("Nenhum arquivo enviado.", HttpStatus.BAD_REQUEST);
        if (file.getSize() > 5 * 1024 * 1024) return new ResponseEntity<>("Máximo 5MB.", HttpStatus.BAD_REQUEST);
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) return new ResponseEntity<>("Apenas imagens.", HttpStatus.BAD_REQUEST);
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        try {
            Path directoryPath = Paths.get(uploadDir);
            Files.createDirectories(directoryPath);
            String fileName = id + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            Files.copy(file.getInputStream(), Paths.get(uploadDir + File.separator + fileName), StandardCopyOption.REPLACE_EXISTING);
            Professor professor = professorRepository.findById(id).orElseThrow(() -> new RuntimeException("Professor não encontrado."));
            professor.setFotoUrl("/uploads/" + fileName);
            professorRepository.save(professor);
            return new ResponseEntity<>("/uploads/" + fileName, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao salvar foto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/upload-curriculo")
    public ResponseEntity<String> uploadCurriculo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return new ResponseEntity<>("Nenhum arquivo enviado.", HttpStatus.BAD_REQUEST);
        if (file.getSize() > 10 * 1024 * 1024) return new ResponseEntity<>("Máximo 10MB.", HttpStatus.BAD_REQUEST);
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        try {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = "curriculo_" + id + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            Files.copy(file.getInputStream(), Paths.get(uploadDir + File.separator + fileName), StandardCopyOption.REPLACE_EXISTING);
            Professor professor = professorRepository.findById(id).orElseThrow(() -> new RuntimeException("Professor não encontrado."));
            professor.setCurriculoUrl("/uploads/" + fileName);
            professorRepository.save(professor);
            return new ResponseEntity<>("/uploads/" + fileName, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao salvar currículo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/biografia")
    public ResponseEntity<?> atualizarBiografia(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String novaBiografia = body.get("biografia");
        if (novaBiografia != null && novaBiografia.length() > 280) {
            return new ResponseEntity<>("Biografia excede 280 caracteres.", HttpStatus.BAD_REQUEST);
        }
        try {
            Professor professor = professorRepository.findById(id).orElseThrow(() -> new RuntimeException("Professor não encontrado."));
            professor.setBiografia(novaBiografia);
            professorRepository.save(professor);
            return new ResponseEntity<>(professor, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> buscarPorId(@PathVariable Long id) {
        return professorRepository.findById(id)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Professor>> buscarProfessores(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String especialidade,
            @RequestParam(required = false) String didatica) {
        List<Professor> professores = professorRepository.buscarProfessoresPorFiltro(nome, especialidade, didatica);
        if (professores.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(professores, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (professorRepository.existsById(id)) {
            professorRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public List<Professor> listarTodos() {
        return professorRepository.findAll();
    }
}