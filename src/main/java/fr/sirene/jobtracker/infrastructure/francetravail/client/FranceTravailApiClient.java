package fr.sirene.jobtracker.infrastructure.francetravail.client;

import fr.sirene.jobtracker.infrastructure.francetravail.dto.ReponseRechercheFranceTravail;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class FranceTravailApiClient {

    private final RestClient restClient;

    public FranceTravailApiClient(RestClient franceTravailApiRestClient) {
        this.restClient = franceTravailApiRestClient;
    }

    public ResponseEntity<ReponseRechercheFranceTravail> rechercherOffres(
            String motsCles, String typeContrat, String codeCommune, int debut, int fin, String bearerToken) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/offres/search")
                        .queryParam("motsCles", motsCles)
                        .queryParam("typeContrat", typeContrat)
                        .queryParamIfPresent("commune", Optional.ofNullable(codeCommune)
                                .filter(commune -> !commune.isBlank()))
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                .header("Range", "resultats=" + debut + "-" + fin)
                .retrieve()
                .toEntity(ReponseRechercheFranceTravail.class);
    }
}
