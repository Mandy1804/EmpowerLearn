package br.com.empowerlearn.empowerlearn_api.repository;

import br.com.empowerlearn.empowerlearn_api.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    boolean existsByEmail(String email);
    Aluno findByEmailAndSenha(String email, String senha);
}