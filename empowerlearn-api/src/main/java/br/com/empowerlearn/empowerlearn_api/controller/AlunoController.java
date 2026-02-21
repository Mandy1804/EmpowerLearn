package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.model.Aluno;
import br.com.empowerlearn.empowerlearn_api.repository.AlunoRepository;
import br.com.empowerlearn.empowerlearn_api.service.CepService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}) // Adicionado DELETE
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
    // 2. BUSCAR POR ID (GET) - Para ver-perfil.html
    // ========================================================
    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        return alunoRepository.findById(id)
                .map(aluno -> new ResponseEntity<>(aluno, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ========================================================
    // 3. LOGIN (POST)
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
    // 4. EXCLUSÃO DE PERFIL (DELETE) - Rota: /api/alunos/{id}
    // ========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (alunoRepository.existsById(id)) {
            alunoRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204: Sucesso, sem corpo
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
    }
}