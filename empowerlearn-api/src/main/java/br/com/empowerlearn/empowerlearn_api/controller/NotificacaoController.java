package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.dto.NotificacaoRequestDTO;
import br.com.empowerlearn.empowerlearn_api.dto.NotificacaoResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificacaoController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/enviar")
    public ResponseEntity<NotificacaoResponseDTO> enviar(@RequestBody NotificacaoRequestDTO request) {
        String destino = request.getDestinoTipo() + "-" + request.getDestinoId();

        NotificacaoResponseDTO notificacao = new NotificacaoResponseDTO(
                destino,
                request.getTitulo(),
                request.getMensagem(),
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/topic/notificacoes/" + destino, notificacao);

        return ResponseEntity.ok(notificacao);
    }
}
