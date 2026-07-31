package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.infrastructure.persistence.entity.DocumentCandidatureEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentCandidatureJpaRepository extends JpaRepository<DocumentCandidatureEntity, Long> {
}
