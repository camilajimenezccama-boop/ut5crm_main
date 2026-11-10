package edu.altair.ut5crm.repositories;

import edu.altair.ut5crm.entities.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findByDni(String dni);
    Optional<Alumno> findByNumeroSS(String numeroSS);
    boolean existsByDni(String dni);
    boolean existsByNumeroSS(String numeroSS); // Corregido para que no falle el servicio
}