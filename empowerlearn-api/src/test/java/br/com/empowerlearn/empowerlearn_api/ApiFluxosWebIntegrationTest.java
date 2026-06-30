package br.com.empowerlearn.empowerlearn_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiFluxosWebIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deveListarPlanosFreemium() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/planos", String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("GRATUITO");
        assertThat(response.getBody()).contains("BASICO");
        assertThat(response.getBody()).contains("PRO");
        assertThat(response.getBody()).contains("PREMIUM");
    }

    @Test
    void deveAtivarPlanoGratuitoSemChamarStripe() {
        Map<String, Object> body = Map.of(
                "planoCodigo", "GRATUITO",
                "usuarioId", 999999,
                "usuarioTipo", "ALUNO",
                "nome", "Usuario Teste Automatizado",
                "email", "teste.automatizado@empowerlearn.com.br"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/pagamentos/stripe/checkout",
                body,
                String.class
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("GRATUITO_ATIVO");
        assertThat(response.getBody()).contains("gratuito_");
        assertThat(response.getBody()).contains("pagamento-retorno.html");
    }

    @Test
    void deveEnviarNotificacaoWebSocketViaApi() {
        Map<String, Object> body = Map.of(
                "destinoTipo", "professor",
                "destinoId", 1,
                "titulo", "Teste automatizado WebSocket",
                "mensagem", "Mensagem enviada pelo teste automatizado"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/notificacoes/enviar",
                body,
                String.class
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("professor-1");
        assertThat(response.getBody()).contains("Teste automatizado WebSocket");
    }
}
