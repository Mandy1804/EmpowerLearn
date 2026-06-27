package br.com.empowerlearn.empowerlearn_api.dto;

import br.com.empowerlearn.empowerlearn_api.model.Professor;

public class ProfessorResponseDTO {
    public Long id;
    public String nome;
    public String email;
    public String cidade;
    public String estado;
    public String especialidade;
    public String didatica;
    public Integer experiencia;
    public String fotoUrl;
    public String biografia;
    public String status;

    public ProfessorResponseDTO(Professor p) {
        this.id = p.getId();
        this.nome = p.getNome();
        this.email = p.getEmail();
        this.cidade = p.getCidade();
        this.estado = p.getEstado();
        this.especialidade = p.getEspecialidade();
        this.didatica = p.getDidatica();
        this.experiencia = p.getExperiencia();
        this.fotoUrl = p.getFotoUrl();
        this.biografia = p.getBiografia();
        this.status = p.getStatus();
    }
}
