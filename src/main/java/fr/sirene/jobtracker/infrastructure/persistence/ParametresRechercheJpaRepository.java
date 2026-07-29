package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.infrastructure.persistence.entity.ParametresRechercheEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametresRechercheJpaRepository extends JpaRepository<ParametresRechercheEntity, Long> {

    Optional<ParametresRechercheEntity> findTopByOrderByIdAsc();
}
