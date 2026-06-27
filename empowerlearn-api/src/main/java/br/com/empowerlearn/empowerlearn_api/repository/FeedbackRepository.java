package br.com.empowerlearn.empowerlearn_api.repository;

import br.com.empowerlearn.empowerlearn_api.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByProfessorId(Long professorId);

    Optional<Feedback> findByProfessorIdAndAutorIdAndAutorTipo(Long professorId, Long autorId, String autorTipo);

    @Query("SELECT AVG(f.nota) FROM Feedback f WHERE f.professorId = :professorId")
    Double calcularMedia(@Param("professorId") Long professorId);
}
