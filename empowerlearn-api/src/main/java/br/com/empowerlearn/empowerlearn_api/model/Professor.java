package br.com.empowerlearn.empowerlearn_api.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "professores")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    private String senha;
    private String cep;

    // ===================================
    // CAMPO NOVO: URL da Foto (para persistência)
    private String fotoUrl;
    private String curriculoUrl;
    @jakarta.persistence.Column(length = 280)
    private String biografia;
    private LocalDate dataNascimento;
    private String sexo;
    // ===================================

    // CAMPOS DE LOCALIZAÇÃO (CEP Service)
    private String cidade;
    private String estado;
    private String pais;

    private String especialidade;
    private String didatica;
    private int experiencia;
    private String status;
    private LocalDate dataCadastro;

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    // GETTER E SETTER DA FOTO
    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getCurriculoUrl() {
        return curriculoUrl;
    }

    public void setCurriculoUrl(String curriculoUrl) {
        this.curriculoUrl = curriculoUrl;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    // GETTERS E SETTERS DA LOCALIZAÇÃO
    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    // RESTANTE DOS GETTERS E SETTERS
    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getDidatica() {
        return didatica;
    }

    public void setDidatica(String didatica) {
        this.didatica = didatica;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}