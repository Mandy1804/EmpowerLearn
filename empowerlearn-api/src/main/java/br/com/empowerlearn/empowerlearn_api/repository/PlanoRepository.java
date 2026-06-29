package br.com.empowerlearn.empowerlearn_api.repository;

import br.com.empowerlearn.empowerlearn_api.model.Plano;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoRepository extends JpaRepository<Plano, Long> {
    Optional<Plano> findByCodigo(String codigo);
    List<Plano> findByAtivoTrueOrderByValorAsc();
    boolean existsByCodigo(String codigo);
}
