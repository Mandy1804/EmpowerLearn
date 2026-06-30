package br.com.empowerlearn.empowerlearn_api.dto;

import java.time.LocalDateTime;

public class NotificacaoResponseDTO {

    private String destino;
    private String titulo;
    private String mensagem;
    private LocalDateTime enviadaEm;

    public NotificacaoResponseDTO(String destino, String titulo, String mensagem, LocalDateTime enviadaEm) {
        this.destino = destino;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.enviadaEm = enviadaEm;
    }

    public String getDestino() {
        return destino;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getEnviadaEm() {
        return enviadaEm;
    }
}
