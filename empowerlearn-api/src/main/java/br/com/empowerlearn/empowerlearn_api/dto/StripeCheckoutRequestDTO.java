package br.com.empowerlearn.empowerlearn_api.dto;

public class StripeCheckoutRequestDTO {
    private String planoCodigo;
    private Long usuarioId;
    private String usuarioTipo;
    private String email;
    private String nome;
    private String backBaseUrl;

    public String getPlanoCodigo() { return planoCodigo; }
    public void setPlanoCodigo(String planoCodigo) { this.planoCodigo = planoCodigo; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getUsuarioTipo() { return usuarioTipo; }
    public void setUsuarioTipo(String usuarioTipo) { this.usuarioTipo = usuarioTipo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getBackBaseUrl() { return backBaseUrl; }
    public void setBackBaseUrl(String backBaseUrl) { this.backBaseUrl = backBaseUrl; }
}
