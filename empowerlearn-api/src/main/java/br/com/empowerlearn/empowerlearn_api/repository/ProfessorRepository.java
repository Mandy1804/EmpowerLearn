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
    Professor findByEmailAndSenha(String email, String senha);

    // NOVO MÉTODO PARA BUSCA DINÂMICA
    @Query("SELECT p FROM Professor p WHERE " +
            // Filtra pelo Nome (se o parâmetro :nome NÃO for NULL)
            "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
            // Filtra pela Especialidade (se o parâmetro :especialidade NÃO for NULL)
            "(:especialidade IS NULL OR LOWER(p.especialidade) LIKE LOWER(CONCAT('%', :especialidade, '%')))")
    List<Professor> buscarProfessoresPorFiltro(
            @Param("nome") String nome,
            @Param("especialidade") String especialidade // Assumindo que a entidade Professor tem o campo 'especialidade'
    );
}