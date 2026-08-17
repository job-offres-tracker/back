package fr.sirene.jobtracker.infrastructure.geo;

import fr.sirene.jobtracker.application.port.commune.RechercheCommunePort;
import fr.sirene.jobtracker.domain.model.Commune;
import fr.sirene.jobtracker.infrastructure.geo.client.GeoApiClient;
import fr.sirene.jobtracker.infrastructure.geo.dto.CommuneGeo;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GeoApiCommuneAdapter implements RechercheCommunePort {

    private final GeoApiClient apiClient;

    public GeoApiCommuneAdapter(GeoApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<Commune> rechercher(String nom) {
        List<CommuneGeo> communes = apiClient.rechercherCommunes(nom);
        if (communes == null) {
            return List.of();
        }
        return communes.stream()
                .map(commune -> new Commune(commune.nom(), commune.code(), commune.codesPostaux()))
                .toList();
    }
}
