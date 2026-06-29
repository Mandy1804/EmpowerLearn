package br.com.empowerlearn.empowerlearn_api.dto;

import br.com.empowerlearn.empowerlearn_api.model.Plano;
import java.math.BigDecimal;

public class PlanoResponseDTO {
    private Long id;
    private String codigo;
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private String periodicidade;
    private String recursos;
    private Integer limiteContratacoes;

    public PlanoResponseDTO(Plano plano) {
        this.id = plano.getId();
        this.codigo = plano.getCodigo();
        this.nome = plano.getNome();
        this.descricao = plano.getDescricao();
        this.valor = plano.getValor();
        this.periodicidade = plano.getPeriodicidade();
        this.recursos = plano.getRecursos();
        this.limiteContratacoes = plano.getLimiteContratacoes();
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public String getPeriodicidade() { return periodicidade; }
    public String getRecursos() { return recursos; }
    public Integer getLimiteContratacoes() { return limiteContratacoes; }
}
