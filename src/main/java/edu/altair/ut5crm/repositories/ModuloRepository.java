package edu.altair.ut5crm.repositories;

import edu.altair.ut5crm.entities.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    Optional<Modulo> findByCodigo(String codigo);
    Optional<Modulo> findByDiminutivo(String diminutivo);
    boolean existsByCodigo(String codigo);
    boolean existsByDiminutivo(String diminutivo);
}