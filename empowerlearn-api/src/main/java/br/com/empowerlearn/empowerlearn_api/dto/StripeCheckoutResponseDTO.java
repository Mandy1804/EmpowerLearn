package br.com.empowerlearn.empowerlearn_api.dto;

public class StripeCheckoutResponseDTO {
    private Long assinaturaId;
    private String sessionId;
    private String checkoutUrl;
    private String status;
    private boolean modoDemonstracao;

    public StripeCheckoutResponseDTO(Long assinaturaId, String sessionId, String checkoutUrl, String status, boolean modoDemonstracao) {
        this.assinaturaId = assinaturaId;
        this.sessionId = sessionId;
        this.checkoutUrl = checkoutUrl;
        this.status = status;
        this.modoDemonstracao = modoDemonstracao;
    }

    public Long getAssinaturaId() { return assinaturaId; }
    public String getSessionId() { return sessionId; }
    public String getCheckoutUrl() { return checkoutUrl; }
    public String getStatus() { return status; }
    public boolean isModoDemonstracao() { return modoDemonstracao; }
}
