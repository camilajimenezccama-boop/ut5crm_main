package edu.altair.ut5crm.repositories;

import edu.altair.ut5crm.entities.Titulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TitulacionRepository extends JpaRepository<Titulacion, Long> {
    Optional<Titulacion> findByCodigo(String codigo);
    Optional<Titulacion> findByDiminutivo(String diminutivo);
    boolean existsByCodigo(String codigo);
    boolean existsByDiminutivo(String diminutivo);
}