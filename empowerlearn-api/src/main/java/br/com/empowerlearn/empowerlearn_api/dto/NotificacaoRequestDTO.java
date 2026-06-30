package br.com.empowerlearn.empowerlearn_api.dto;

public class NotificacaoRequestDTO {

    private String destinoTipo;
    private Long destinoId;
    private String titulo;
    private String mensagem;

    public String getDestinoTipo() {
        return destinoTipo;
    }

    public void setDestinoTipo(String destinoTipo) {
        this.destinoTipo = destinoTipo;
    }

    public Long getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(Long destinoId) {
        this.destinoId = destinoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
