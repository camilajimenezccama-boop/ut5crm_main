package edu.altair.ut5crm.repositories;

import edu.altair.ut5crm.entities.Familia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamiliaRepository extends JpaRepository<Familia, Long> {
    Optional<Familia> findByEmailPadre(String emailPadre);
    Optional<Familia> findByEmailMadre(String emailMadre);
    boolean existsByEmailPadre(String emailPadre);
    boolean existsByEmailMadre(String emailMadre);
}