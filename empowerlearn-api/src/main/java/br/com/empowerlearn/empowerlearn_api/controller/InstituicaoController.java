package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.model.Instituicao;
import br.com.empowerlearn.empowerlearn_api.repository.InstituicaoRepository;
import br.com.empowerlearn.empowerlearn_api.service.CepService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/instituicoes")
// Métodos necessários: GET, POST, DELETE, PUT
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
    // 2. LOGIN (POST) - Rota: /api/instituicoes/login
    // ========================================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Instituicao credenciais) {
        Instituicao instituicao = instituicaoRepository.findByEmailAndSenha(credenciais.getEmail(), credenciais.getSenha());
        if (instituicao != null) {
            return new ResponseEntity<>(instituicao, HttpStatus.OK); // Retorna o objeto completo
        } else {
            return new ResponseEntity<>("Credenciais inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }

    // ========================================================
    // 3. BUSCAR POR ID (GET) - Rota: /api/instituicoes/{id}
    // Necessário para a página ver-perfil.html
    // ========================================================
    @GetMapping("/{id}")
    public ResponseEntity<Instituicao> buscarPorId(@PathVariable Long id) {
        return instituicaoRepository.findById(id)
                .map(instituicao -> new ResponseEntity<>(instituicao, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ========================================================
    // 4. EXCLUSÃO (DELETE) - Rota: /api/instituicoes/{id}
    // Necessário para a função de exclusão de perfil do Front-End
    // ========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (instituicaoRepository.existsById(id)) {
            instituicaoRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204: Sucesso, sem corpo
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
    }

    // ========================================================
    // 5. LISTAR TODOS (GET) - Opcional
    // ========================================================
    @GetMapping
    public List<Instituicao> listarTodos() {
        return instituicaoRepository.findAll();
    }
}