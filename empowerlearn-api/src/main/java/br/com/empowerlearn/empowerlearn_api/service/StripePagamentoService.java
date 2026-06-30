package br.com.empowerlearn.empowerlearn_api.service;

import br.com.empowerlearn.empowerlearn_api.dto.StripeCheckoutRequestDTO;
import br.com.empowerlearn.empowerlearn_api.dto.StripeCheckoutResponseDTO;
import br.com.empowerlearn.empowerlearn_api.dto.StripeStatusResponseDTO;
import br.com.empowerlearn.empowerlearn_api.model.Assinatura;
import br.com.empowerlearn.empowerlearn_api.model.Plano;
import br.com.empowerlearn.empowerlearn_api.repository.AssinaturaRepository;
import br.com.empowerlearn.empowerlearn_api.repository.PlanoRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StripePagamentoService {

    private final PlanoRepository planoRepository;
    private final AssinaturaRepository assinaturaRepository;

    @Value("${stripe.secret.key:}")
    private String stripeSecretKey;

    @Value("${app.frontend.base-url:http://127.0.0.1:5500/EmpowerLearn}")
    private String frontendBaseUrl;

    public StripePagamentoService(PlanoRepository planoRepository, AssinaturaRepository assinaturaRepository) {
        this.planoRepository = planoRepository;
        this.assinaturaRepository = assinaturaRepository;
    }

    @Transactional
    public StripeCheckoutResponseDTO criarCheckout(StripeCheckoutRequestDTO request) throws Exception {
        String codigo = normalizarCodigoPlano(request.getPlanoCodigo());
        Plano plano = planoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado: " + codigo));

        Assinatura assinatura = new Assinatura();
        assinatura.setPlanoCodigo(plano.getCodigo());
        assinatura.setUsuarioId(request.getUsuarioId());
        assinatura.setUsuarioTipo(request.getUsuarioTipo());
        assinatura.setValor(plano.getValor());
        assinatura.setProvider("STRIPE");
        assinatura.setStatus("PENDENTE");
        assinatura.setCriadoEm(LocalDateTime.now());
        assinatura = assinaturaRepository.save(assinatura);

        if (plano.getValor() == null || BigDecimal.ZERO.compareTo(plano.getValor()) == 0) {
            String gratuitoUrl = montarBaseFront(request.getBackBaseUrl()) + "/pagamento-retorno.html?status=gratuito&assinaturaId=" + assinatura.getId();
            assinatura.setSessionId("gratuito_" + assinatura.getId());
            assinatura.setCheckoutUrl(gratuitoUrl);
            assinatura.setStatus("GRATUITO_ATIVO");
            assinatura.setAtualizadoEm(LocalDateTime.now());
            assinaturaRepository.save(assinatura);
            return new StripeCheckoutResponseDTO(assinatura.getId(), assinatura.getSessionId(), gratuitoUrl, assinatura.getStatus(), true);
        }

        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            String demoUrl = montarBaseFront(request.getBackBaseUrl()) + "/pagamento-retorno.html?status=demo&assinaturaId=" + assinatura.getId();
            assinatura.setSessionId("demo_" + assinatura.getId());
            assinatura.setCheckoutUrl(demoUrl);
            assinatura.setStatus("DEMONSTRACAO");
            assinatura.setAtualizadoEm(LocalDateTime.now());
            assinaturaRepository.save(assinatura);
            return new StripeCheckoutResponseDTO(assinatura.getId(), assinatura.getSessionId(), demoUrl, assinatura.getStatus(), true);
        }

        Stripe.apiKey = stripeSecretKey;

        String baseFront = montarBaseFront(request.getBackBaseUrl());
        String successUrl = baseFront + "/pagamento-retorno.html?status=sucesso&session_id={CHECKOUT_SESSION_ID}";
        String cancelUrl = baseFront + "/pagamento-retorno.html?status=cancelado";

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(successUrl)
            .setCancelUrl(cancelUrl)
            .putMetadata("assinaturaId", String.valueOf(assinatura.getId()))
            .putMetadata("planoCodigo", plano.getCodigo())
            .putMetadata("usuarioId", String.valueOf(request.getUsuarioId() == null ? 0 : request.getUsuarioId()))
            .putMetadata("usuarioTipo", request.getUsuarioTipo() == null ? "" : request.getUsuarioTipo())
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("brl")
                            .setUnitAmount(valorEmCentavos(plano.getValor()))
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("EmpowerLearn - Plano " + plano.getNome())
                                    .setDescription(plano.getDescricao())
                                    .build()
                            )
                            .build()
                    )
                    .build()
            );

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            paramsBuilder.setCustomerEmail(request.getEmail());
        }

        Session session = Session.create(paramsBuilder.build());

        assinatura.setSessionId(session.getId());
        assinatura.setCheckoutUrl(session.getUrl());
        assinatura.setStatus("CHECKOUT_CRIADO");
        assinatura.setAtualizadoEm(LocalDateTime.now());
        assinaturaRepository.save(assinatura);

        return new StripeCheckoutResponseDTO(assinatura.getId(), session.getId(), session.getUrl(), assinatura.getStatus(), false);
    }

    @Transactional
    public StripeStatusResponseDTO consultarStatus(String sessionId) throws Exception {
        Assinatura assinatura = assinaturaRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada: " + sessionId));

        String paymentStatus = null;
        if (stripeSecretKey != null && !stripeSecretKey.isBlank() && sessionId != null && !sessionId.startsWith("demo_")) {
            Stripe.apiKey = stripeSecretKey;
            Session session = Session.retrieve(sessionId);
            paymentStatus = session.getPaymentStatus();
            assinatura.setPaymentIntentId(session.getPaymentIntent() == null ? null : session.getPaymentIntent());

            if ("paid".equalsIgnoreCase(paymentStatus)) {
                assinatura.setStatus("PAGO");
            } else if ("unpaid".equalsIgnoreCase(paymentStatus)) {
                assinatura.setStatus("PENDENTE");
            }
            assinatura.setAtualizadoEm(LocalDateTime.now());
            assinaturaRepository.save(assinatura);
        }

        return new StripeStatusResponseDTO(
            assinatura.getId(),
            assinatura.getPlanoCodigo(),
            assinatura.getSessionId(),
            assinatura.getStatus(),
            paymentStatus,
            assinatura.getCheckoutUrl()
        );
    }

    private String normalizarCodigoPlano(String planoCodigo) {
        if (planoCodigo == null || planoCodigo.isBlank()) return "PRO";
        return planoCodigo.trim().toUpperCase();
    }

    private String montarBaseFront(String backBaseUrl) {
        String base = (backBaseUrl == null || backBaseUrl.isBlank()) ? frontendBaseUrl : backBaseUrl;
        while (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        return base;
    }

    private Long valorEmCentavos(BigDecimal valor) {
        return valor.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}
