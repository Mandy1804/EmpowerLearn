package br.com.empowerlearn.empowerlearn_api.dto;

public class StripeStatusResponseDTO {
    private Long assinaturaId;
    private String planoCodigo;
    private String sessionId;
    private String status;
    private String paymentStatus;
    private String checkoutUrl;

    public StripeStatusResponseDTO(Long assinaturaId, String planoCodigo, String sessionId, String status, String paymentStatus, String checkoutUrl) {
        this.assinaturaId = assinaturaId;
        this.planoCodigo = planoCodigo;
        this.sessionId = sessionId;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.checkoutUrl = checkoutUrl;
    }

    public Long getAssinaturaId() { return assinaturaId; }
    public String getPlanoCodigo() { return planoCodigo; }
    public String getSessionId() { return sessionId; }
    public String getStatus() { return status; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getCheckoutUrl() { return checkoutUrl; }
}
