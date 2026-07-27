package fr.sirene.jobtracker.infrastructure.geo.client;

import fr.sirene.jobtracker.infrastructure.geo.dto.CommuneGeo;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GeoApiClient {

    private static final int LIMITE_RESULTATS = 10;

    private final RestClient restClient;

    public GeoApiClient(RestClient geoApiRestClient) {
        this.restClient = geoApiRestClient;
    }

    public List<CommuneGeo> rechercherCommunes(String nom) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/communes")
                        .queryParam("nom", nom)
                        .queryParam("fields", "nom,code,codesPostaux")
                        .queryParam("boost", "population")
                        .queryParam("limit", LIMITE_RESULTATS)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommuneGeo>>() {});
    }
}
