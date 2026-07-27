package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.infrastructure.persistence.entity.LieuEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LieuJpaRepository extends JpaRepository<LieuEntity, Long> {

    Optional<LieuEntity> findByLatitudeAndLongitude(Double latitude, Double longitude);
}
