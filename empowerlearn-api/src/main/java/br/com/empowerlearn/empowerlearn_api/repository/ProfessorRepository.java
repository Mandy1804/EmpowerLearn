package br.com.empowerlearn.empowerlearn_api.repository;

import br.com.empowerlearn.empowerlearn_api.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    boolean existsByEmail(String email);
    Professor findByEmail(String email);
    Professor findByEmailAndSenha(String email, String senha);

    @Query("SELECT p FROM Professor p WHERE " +
            "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            "(:especialidade IS NULL OR LOWER(p.especialidade) LIKE LOWER(CONCAT('%', :especialidade, '%'))) AND " +
            "(:didatica IS NULL OR LOWER(p.didatica) LIKE LOWER(CONCAT('%', :didatica, '%')))")
    List<Professor> buscarProfessoresPorFiltro(
            @Param("nome") String nome,
            @Param("especialidade") String especialidade,
            @Param("didatica") String didatica
    );
}