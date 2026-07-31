package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.infrastructure.persistence.entity.EvenementCandidatureEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EvenementCandidatureJpaRepository extends JpaRepository<EvenementCandidatureEntity, Long> {
}
