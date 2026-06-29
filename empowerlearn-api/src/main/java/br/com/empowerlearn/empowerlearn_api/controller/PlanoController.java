package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.dto.PlanoResponseDTO;
import br.com.empowerlearn.empowerlearn_api.repository.PlanoRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/planos")
public class PlanoController {

    private final PlanoRepository planoRepository;
    private static final Map<String, Integer> ORDEM_PLANOS = Map.of(
        "GRATUITO", 0,
        "BASICO", 1,
        "PRO", 2,
        "PREMIUM", 3
    );

    public PlanoController(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    @GetMapping
    public List<PlanoResponseDTO> listarPlanos() {
        return planoRepository.findByAtivoTrueOrderByValorAsc()
            .stream()
            .sorted(Comparator.comparingInt(plano -> ORDEM_PLANOS.getOrDefault(plano.getCodigo(), 99)))
            .map(PlanoResponseDTO::new)
            .toList();
    }
}
