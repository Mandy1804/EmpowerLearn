package br.com.empowerlearn.empowerlearn_api.repository;


import br.com.empowerlearn.empowerlearn_api.model.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {

    // Regra para verificar se o email já existe
    boolean existsByEmail(String email);

    // Regra para buscar uma instituição por email E senha (usada no login)
    Instituicao findByEmailAndSenha(String email, String senha);
}