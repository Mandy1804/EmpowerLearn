package br.com.empowerlearn.empowerlearn_api.controller;

import br.com.empowerlearn.empowerlearn_api.dto.StripeCheckoutRequestDTO;
import br.com.empowerlearn.empowerlearn_api.dto.StripeCheckoutResponseDTO;
import br.com.empowerlearn.empowerlearn_api.dto.StripeStatusResponseDTO;
import br.com.empowerlearn.empowerlearn_api.service.StripePagamentoService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagamentos/stripe")
public class StripePagamentoController {

    private final StripePagamentoService stripePagamentoService;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    public StripePagamentoController(StripePagamentoService stripePagamentoService) {
        this.stripePagamentoService = stripePagamentoService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> criarCheckout(@RequestBody StripeCheckoutRequestDTO request) {
        try {
            StripeCheckoutResponseDTO response = stripePagamentoService.criarCheckout(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", "Erro ao criar checkout Stripe: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{sessionId}")
    public ResponseEntity<?> consultarStatus(@PathVariable String sessionId) {
        try {
            StripeStatusResponseDTO response = stripePagamentoService.consultarStatus(sessionId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", "Erro ao consultar status Stripe: " + e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> receberWebhook(
        @RequestBody String payload,
        @RequestHeader(value = "Stripe-Signature", required = false) String signature
    ) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("recebido", true);

            if (webhookSecret != null && !webhookSecret.isBlank()) {
                Event event = Webhook.constructEvent(payload, signature, webhookSecret);
                response.put("tipo", event.getType());
                response.put("verificado", true);
            } else {
                response.put("tipo", "webhook_recebido_sem_verificacao_local");
                response.put("verificado", false);
            }

            return ResponseEntity.ok(response);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Assinatura do webhook Stripe inválida."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Erro no webhook Stripe: " + e.getMessage()));
        }
    }
}
