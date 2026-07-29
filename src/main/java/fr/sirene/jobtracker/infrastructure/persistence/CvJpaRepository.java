package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.infrastructure.persistence.entity.CvEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CvJpaRepository extends JpaRepository<CvEntity, Long> {

    Optional<CvEntity> findByNomUnique(String nomUnique);

    List<CvEntity> findAllByOrderByDateUploadDesc();
}
