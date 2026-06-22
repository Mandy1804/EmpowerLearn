package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.model.Instituicao;
import br.com.empowerlearn.empowerlearn_api.repository.InstituicaoRepository;
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
import java.util.List;

@RestController
@RequestMapping("/api/instituicoes")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class InstituicaoController {

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private CepService cepService;

    // ========================================================
    // 1. CADASTRO (POST) - Rota: /api/instituicoes
    // ========================================================
    @PostMapping
    public ResponseEntity<?> cadastrarInstituicao(@RequestBody Instituicao instituicao) {
        if (instituicaoRepository.existsByEmail(instituicao.getEmail())) {
            return new ResponseEntity<>("Erro: Este e-mail já está cadastrado.", HttpStatus.BAD_REQUEST);
        }

        try {
            JsonNode endereco = cepService.buscarEnderecoPorCep(instituicao.getCep());
            if (endereco == null) {
                return new ResponseEntity<>("Erro: CEP inválido ou não encontrado.", HttpStatus.BAD_REQUEST);
            }

            instituicao.setCidade(endereco.get("localidade").asText());
            instituicao.setEstado(endereco.get("uf").asText());
            instituicao.setPais("BRASIL");
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno ao processar CEP.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Instituicao novaInstituicao = instituicaoRepository.save(instituicao);
        return new ResponseEntity<>(novaInstituicao, HttpStatus.CREATED);
    }

    // ========================================================
    // 2. ENDPOINT: UPLOAD DE FOTO (POST /api/instituicoes/{id}/upload-foto)
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

            Instituicao instituicao = instituicaoRepository.findById(id).orElseThrow(() -> new RuntimeException("Instituição não encontrada."));

            String fotoUrl = "/uploads/" + fileName;
            instituicao.setFotoUrl(fotoUrl);
            instituicaoRepository.save(instituicao);

            return new ResponseEntity<>(fotoUrl, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Erro durante o upload: " + e.getMessage());
            return new ResponseEntity<>("Erro ao salvar foto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ========================================================
    // 3. LOGIN (POST) - Rota: /api/instituicoes/login
    // ========================================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Instituicao credenciais) {
        Instituicao instituicao = instituicaoRepository.findByEmailAndSenha(credenciais.getEmail(), credenciais.getSenha());
        if (instituicao != null) {
            return new ResponseEntity<>(instituicao, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Credenciais inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }

    // ========================================================
    // 4. BUSCAR POR ID (GET) - Rota: /api/instituicoes/{id}
    // ========================================================
    @GetMapping("/{id}")
    public ResponseEntity<Instituicao> buscarPorId(@PathVariable Long id) {
        return instituicaoRepository.findById(id)
                .map(instituicao -> new ResponseEntity<>(instituicao, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ========================================================
    // 5. EXCLUSÃO (DELETE) - Rota: /api/instituicoes/{id}
    // ========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (instituicaoRepository.existsById(id)) {
            instituicaoRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ========================================================
    // 6. LISTAR TODOS (GET) - Opcional
    // ========================================================
    @GetMapping
    public List<Instituicao> listarTodos() {
        return instituicaoRepository.findAll();
    }
}