package fr.sirene.jobtracker.infrastructure.ban;

import fr.sirene.jobtracker.application.port.offre.GeocodageAdressePort;
import fr.sirene.jobtracker.domain.exception.GeocodageAdresseException;
import fr.sirene.jobtracker.infrastructure.ban.client.BanAdresseApiClient;
import fr.sirene.jobtracker.infrastructure.ban.dto.FeatureBan;
import fr.sirene.jobtracker.infrastructure.ban.dto.PropertiesFeatureBan;
import fr.sirene.jobtracker.infrastructure.ban.dto.ReponseReverseBan;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class BanGeocodageAdapter implements GeocodageAdressePort {

    private final BanAdresseApiClient apiClient;

    public BanGeocodageAdapter(BanAdresseApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public Optional<String> resoudreAdresse(double latitude, double longitude) {
        try {
            ReponseReverseBan reponse = apiClient.rechercherAdresse(latitude, longitude);
            List<FeatureBan> features = reponse != null ? reponse.features() : null;
            if (features == null || features.isEmpty()) {
                return Optional.empty();
            }
            return Optional.ofNullable(features.get(0).properties())
                    .map(PropertiesFeatureBan::label);
        } catch (RestClientException | GeocodageAdresseException e) {
            log.warn("Échec de la résolution d'adresse BAN pour ({}, {}) : {}", latitude, longitude, e.getMessage());
            return Optional.empty();
        }
    }
}
