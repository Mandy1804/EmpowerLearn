package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.model.Aluno;
import br.com.empowerlearn.empowerlearn_api.repository.AlunoRepository;
import br.com.empowerlearn.empowerlearn_api.security.JwtService;
import br.com.empowerlearn.empowerlearn_api.service.CepService;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CepService cepService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> cadastrarAluno(@RequestBody Aluno aluno) {
        if (alunoRepository.existsByEmail(aluno.getEmail())) {
            return new ResponseEntity<>("Erro: E-mail já cadastrado.", HttpStatus.BAD_REQUEST);
        }
        if (aluno.getSenha() == null || aluno.getSenha().length() < 6) {
            return new ResponseEntity<>("Erro: Senha deve ter pelo menos 6 caracteres.", HttpStatus.BAD_REQUEST);
        }
        if (aluno.getNome() == null || aluno.getNome().trim().isEmpty()) {
            return new ResponseEntity<>("Erro: Nome obrigatório.", HttpStatus.BAD_REQUEST);
        }
        aluno.setSenha(passwordEncoder.encode(aluno.getSenha()));
        if (aluno.getNome() != null) {
            String nome = aluno.getNome().trim().toLowerCase();
            String[] palavras = nome.split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (String p : palavras) {
                if (p.length() > 0) sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");
            }
            aluno.setNome(sb.toString().trim());
        }
        try {
            JsonNode endereco = cepService.buscarEnderecoPorCep(aluno.getCep());
            if (endereco == null) return new ResponseEntity<>("Erro: CEP inválido.", HttpStatus.BAD_REQUEST);
            aluno.setCidade(endereco.get("localidade").asText());
            aluno.setEstado(endereco.get("uf").asText());
            aluno.setPais("BRASIL");
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao processar CEP.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Aluno novoAluno = alunoRepository.save(aluno);
        String token = jwtService.gerarToken(novoAluno.getEmail(), "aluno");
        return new ResponseEntity<>(Map.of("aluno", novoAluno, "token", token), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Aluno credenciais) {
        if (credenciais.getEmail() == null || credenciais.getSenha() == null) {
            return new ResponseEntity<>("Email e senha obrigatórios.", HttpStatus.BAD_REQUEST);
        }
        Aluno aluno = alunoRepository.findByEmail(credenciais.getEmail());
        if (aluno == null || !passwordEncoder.matches(credenciais.getSenha(), aluno.getSenha())) {
            return new ResponseEntity<>("Credenciais inválidas.", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtService.gerarToken(aluno.getEmail(), "aluno");
        return new ResponseEntity<>(Map.of("aluno", aluno, "token", token), HttpStatus.OK);
    }

    @PostMapping("/{id}/upload-foto")
    public ResponseEntity<String> uploadFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return new ResponseEntity<>("Nenhum arquivo enviado.", HttpStatus.BAD_REQUEST);
        if (file.getSize() > 5 * 1024 * 1024) return new ResponseEntity<>("Máximo 5MB.", HttpStatus.BAD_REQUEST);
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        try {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = "aluno_" + id + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            Files.copy(file.getInputStream(), Paths.get(uploadDir + File.separator + fileName), StandardCopyOption.REPLACE_EXISTING);
            Aluno a = alunoRepository.findById(id).orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
            a.setFotoUrl("/uploads/" + fileName);
            alunoRepository.save(a);
            return new ResponseEntity<>("/uploads/" + fileName, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        return alunoRepository.findById(id)
                .map(a -> new ResponseEntity<>(a, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (alunoRepository.existsById(id)) {
            alunoRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }
}