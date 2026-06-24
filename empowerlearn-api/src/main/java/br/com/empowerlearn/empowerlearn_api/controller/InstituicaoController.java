package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.model.Instituicao;
import br.com.empowerlearn.empowerlearn_api.repository.InstituicaoRepository;
import br.com.empowerlearn.empowerlearn_api.security.JwtService;
import br.com.empowerlearn.empowerlearn_api.service.CepService;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instituicoes")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class InstituicaoController {

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private CepService cepService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Instituicao instituicao) {
        if (instituicaoRepository.existsByEmail(instituicao.getEmail())) {
            return new ResponseEntity<>("Erro: E-mail já cadastrado.", HttpStatus.BAD_REQUEST);
        }
        if (instituicao.getSenha() == null || instituicao.getSenha().length() < 6) {
            return new ResponseEntity<>("Erro: Senha deve ter pelo menos 6 caracteres.", HttpStatus.BAD_REQUEST);
        }
        if (instituicao.getNome() == null || instituicao.getNome().trim().isEmpty()) {
            return new ResponseEntity<>("Erro: Nome obrigatório.", HttpStatus.BAD_REQUEST);
        }
        instituicao.setSenha(passwordEncoder.encode(instituicao.getSenha()));
        try {
            JsonNode endereco = cepService.buscarEnderecoPorCep(instituicao.getCep());
            if (endereco == null) return new ResponseEntity<>("Erro: CEP inválido.", HttpStatus.BAD_REQUEST);
            instituicao.setCidade(endereco.get("localidade").asText());
            instituicao.setEstado(endereco.get("uf").asText());
            instituicao.setPais("BRASIL");
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao processar CEP.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Instituicao nova = instituicaoRepository.save(instituicao);
        String token = jwtService.gerarToken(nova.getEmail(), "instituicao");
        return new ResponseEntity<>(Map.of("instituicao", nova, "token", token), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Instituicao credenciais) {
        if (credenciais.getEmail() == null || credenciais.getSenha() == null) {
            return new ResponseEntity<>("Email e senha obrigatórios.", HttpStatus.BAD_REQUEST);
        }
        Instituicao inst = instituicaoRepository.findByEmail(credenciais.getEmail());
        if (inst == null || !passwordEncoder.matches(credenciais.getSenha(), inst.getSenha())) {
            return new ResponseEntity<>("Credenciais inválidas.", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtService.gerarToken(inst.getEmail(), "instituicao");
        return new ResponseEntity<>(Map.of("instituicao", inst, "token", token), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Instituicao> buscarPorId(@PathVariable Long id) {
        return instituicaoRepository.findById(id)
                .map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (instituicaoRepository.existsById(id)) {
            instituicaoRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public List<Instituicao> listarTodos() {
        return instituicaoRepository.findAll();
    }
}