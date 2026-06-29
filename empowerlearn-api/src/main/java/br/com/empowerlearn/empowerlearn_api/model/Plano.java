package br.com.empowerlearn.empowerlearn_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "planos")
public class Plano {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 20)
    private String periodicidade;

    @Column(nullable = false, length = 1000)
    private String recursos;

    @Column(nullable = false)
    private Integer limiteContratacoes;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getPeriodicidade() { return periodicidade; }
    public void setPeriodicidade(String periodicidade) { this.periodicidade = periodicidade; }
    public String getRecursos() { return recursos; }
    public void setRecursos(String recursos) { this.recursos = recursos; }
    public Integer getLimiteContratacoes() { return limiteContratacoes; }
    public void setLimiteContratacoes(Integer limiteContratacoes) { this.limiteContratacoes = limiteContratacoes; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
