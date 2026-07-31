package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.infrastructure.persistence.entity.ParametresDocumentCandidatureEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametresDocumentCandidatureJpaRepository extends JpaRepository<ParametresDocumentCandidatureEntity, Long> {

    Optional<ParametresDocumentCandidatureEntity> findTopByOrderByIdAsc();
}
