package fr.sirene.jobtracker.infrastructure.francetravail.client;

import fr.sirene.jobtracker.infrastructure.francetravail.config.FranceTravailAuthProperties;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.TokenFranceTravail;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Component
public class FranceTravailAuthClient {

    private static final long MARGE_EXPIRATION_SECONDES = 30;

    private final RestClient restClient;
    private final FranceTravailAuthProperties properties;

    private volatile String tokenCourant;
    private volatile Instant expirationToken = Instant.EPOCH;

    public FranceTravailAuthClient(RestClient franceTravailAuthRestClient, FranceTravailAuthProperties properties) {
        this.restClient = franceTravailAuthRestClient;
        this.properties = properties;
    }

    public synchronized String obtenirToken() {
        if (tokenCourant != null && Instant.now().isBefore(expirationToken)) {
            return tokenCourant;
        }

        MultiValueMap<String, String> corps = new LinkedMultiValueMap<>();
        corps.add("grant_type", "client_credentials");
        corps.add("client_id", properties.clientId());
        corps.add("client_secret", properties.clientSecret());
        corps.add("scope", properties.scope());

        TokenFranceTravail token = restClient.post()
                .uri(properties.tokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(corps)
                .retrieve()
                .body(TokenFranceTravail.class);

        this.tokenCourant = token.accessToken();
        this.expirationToken = Instant.now().plusSeconds(token.expiresIn() - MARGE_EXPIRATION_SECONDES);
        return tokenCourant;
    }
}
