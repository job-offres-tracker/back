package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.application.port.offre.LieuRepository;
import fr.sirene.jobtracker.infrastructure.persistence.entity.LieuEntity;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaLieuRepository implements LieuRepository {

    private final LieuJpaRepository lieuJpaRepository;

    public JpaLieuRepository(LieuJpaRepository lieuJpaRepository) {
        this.lieuJpaRepository = lieuJpaRepository;
    }

    @Override
    public Optional<String> rechercherAdresse(double latitude, double longitude) {
        return lieuJpaRepository.findByLatitudeAndLongitude(latitude, longitude)
                .map(LieuEntity::getAdresse)
                .filter(adresse -> adresse != null && !adresse.isBlank());
    }

    @Override
    public void enregistrerAdresse(double latitude, double longitude, String adresse) {
        LieuEntity entity = lieuJpaRepository.findByLatitudeAndLongitude(latitude, longitude)
                .orElseGet(LieuEntity::new);
        entity.setLatitude(latitude);
        entity.setLongitude(longitude);
        entity.setAdresse(adresse);
        lieuJpaRepository.save(entity);
    }
}
