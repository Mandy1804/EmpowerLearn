package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.model.Feedback;
import br.com.empowerlearn.empowerlearn_api.repository.FeedbackRepository;
import br.com.empowerlearn.empowerlearn_api.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> darFeedback(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {

        String token = authHeader.replace("Bearer ", "");
        String tipo = jwtService.extrairTipo(token);

        if ("professor".equals(tipo)) {
            return new ResponseEntity<>("Professores não podem dar feedback a outros professores.", HttpStatus.FORBIDDEN);
        }

        Long professorId = Long.valueOf(body.get("professorId").toString());
        Long autorId = Long.valueOf(body.get("autorId").toString());
        Integer nota = Integer.valueOf(body.get("nota").toString());

        if (nota < 1 || nota > 5) {
            return new ResponseEntity<>("Nota deve ser entre 1 e 5.", HttpStatus.BAD_REQUEST);
        }

        Optional<Feedback> existente = feedbackRepository
                .findByProfessorIdAndAutorIdAndAutorTipo(professorId, autorId, tipo);

        Feedback feedback = existente.orElse(new Feedback());
        feedback.setProfessorId(professorId);
        feedback.setAutorId(autorId);
        feedback.setAutorTipo(tipo);
        feedback.setNota(nota);

        feedbackRepository.save(feedback);
        return new ResponseEntity<>(Map.of("mensagem", "Feedback salvo!", "nota", nota), HttpStatus.OK);
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<?> getFeedbacks(@PathVariable Long professorId) {
        List<Feedback> feedbacks = feedbackRepository.findByProfessorId(professorId);
        Double media = feedbackRepository.calcularMedia(professorId);
        return new ResponseEntity<>(Map.of(
                "feedbacks", feedbacks,
                "media", media != null ? Math.round(media * 10.0) / 10.0 : 0,
                "total", feedbacks.size()
        ), HttpStatus.OK);
    }

    @GetMapping("/meu-feedback")
    public ResponseEntity<?> getMeuFeedback(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long professorId,
            @RequestParam Long autorId) {

        String token = authHeader.replace("Bearer ", "");
        String tipo = jwtService.extrairTipo(token);

        Optional<Feedback> feedback = feedbackRepository
                .findByProfessorIdAndAutorIdAndAutorTipo(professorId, autorId, tipo);

        return feedback
                .map(f -> new ResponseEntity<>(Map.of("nota", f.getNota()), HttpStatus.OK))
                .orElse(new ResponseEntity<>(Map.of("nota", 0), HttpStatus.OK));
    }
}
