package fr.sirene.jobtracker.infrastructure.ban.client;

import fr.sirene.jobtracker.infrastructure.ban.dto.ReponseReverseBan;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BanAdresseApiClient {

    private final RestClient restClient;

    public BanAdresseApiClient(RestClient banApiRestClient) {
        this.restClient = banApiRestClient;
    }

    public ReponseReverseBan rechercherAdresse(double latitude, double longitude) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse/")
                        .queryParam("lon", longitude)
                        .queryParam("lat", latitude)
                        .build())
                .retrieve()
                .body(ReponseReverseBan.class);
    }
}
