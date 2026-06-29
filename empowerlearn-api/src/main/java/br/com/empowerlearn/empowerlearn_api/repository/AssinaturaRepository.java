package br.com.empowerlearn.empowerlearn_api.repository;

import br.com.empowerlearn.empowerlearn_api.model.Assinatura;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {
    Optional<Assinatura> findBySessionId(String sessionId);
}
