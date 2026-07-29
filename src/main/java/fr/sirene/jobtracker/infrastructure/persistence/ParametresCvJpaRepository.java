package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.infrastructure.persistence.entity.ParametresCvEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametresCvJpaRepository extends JpaRepository<ParametresCvEntity, Long> {

    Optional<ParametresCvEntity> findTopByOrderByIdAsc();
}
