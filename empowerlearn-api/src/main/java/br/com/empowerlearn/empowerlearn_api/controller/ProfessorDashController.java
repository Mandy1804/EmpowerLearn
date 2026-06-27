package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.model.Aluno;
import br.com.empowerlearn.empowerlearn_api.model.Instituicao;
import br.com.empowerlearn.empowerlearn_api.repository.AlunoRepository;
import br.com.empowerlearn.empowerlearn_api.repository.InstituicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/painel-professor")
@CrossOrigin(origins = "*")
public class ProfessorDashController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @GetMapping("/clientes")
    public ResponseEntity<?> buscarClientes(
            @RequestParam(required = false) String tipoEnsino) {

        List<Map<String, Object>> resultado = new ArrayList<>();

        List<Aluno> alunos = alunoRepository.findAll();
        for (Aluno a : alunos) {
            if (tipoEnsino != null && !tipoEnsino.isEmpty() &&
                !tipoEnsino.equalsIgnoreCase(a.getTipoEnsino())) continue;
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("nome", a.getNome());
            m.put("email", a.getEmail());
            m.put("cidade", a.getCidade());
            m.put("estado", a.getEstado());
            m.put("fotoUrl", a.getFotoUrl());
            m.put("tipoEnsino", a.getTipoEnsino());
            m.put("tipo", "aluno");
            resultado.add(m);
        }

        List<Instituicao> instituicoes = instituicaoRepository.findAll();
        for (Instituicao i : instituicoes) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", i.getId());
            m.put("nome", i.getNome());
            m.put("email", i.getEmail());
            m.put("cidade", i.getCidade());
            m.put("estado", i.getEstado());
            m.put("fotoUrl", i.getFotoUrl());
            m.put("tipo", "instituicao");
            resultado.add(m);
        }

        return ResponseEntity.ok(resultado);
    }
}