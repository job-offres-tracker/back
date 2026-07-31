package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.infrastructure.persistence.entity.CandidatureEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatureJpaRepository extends JpaRepository<CandidatureEntity, Long> {

    boolean existsByOffreIdExterne(String idExterne);

    Page<CandidatureEntity> findAllByOrderByDateCandidatureDesc(Pageable pageable);
}
