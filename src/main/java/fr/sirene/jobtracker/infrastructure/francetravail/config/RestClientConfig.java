package fr.sirene.jobtracker.infrastructure.francetravail.config;

import fr.sirene.jobtracker.domain.exception.OffreEmploiApiException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient franceTravailAuthRestClient(RestClient.Builder builder, FranceTravailAuthProperties properties) {
        return builder
                .requestFactory(requestFactoryAvecTimeouts(properties.connectTimeout(), properties.readTimeout()))
                .defaultStatusHandler(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            throw new OffreEmploiApiException(
                                    "Erreur lors de l'authentification France Travail [" + response.getStatusCode() + "]");
                        })
                .build();
    }

    @Bean
    public RestClient franceTravailApiRestClient(RestClient.Builder builder, FranceTravailApiProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactoryAvecTimeouts(properties.connectTimeout(), properties.readTimeout()))
                .defaultHeader("Accept", "application/json")
                .defaultStatusHandler(
                        status -> status.is4xxClientError(),
                        (request, response) -> {
                            throw new OffreEmploiApiException(
                                    "Erreur client France Travail [" + response.getStatusCode() + "] : " + response.getStatusText());
                        })
                .defaultStatusHandler(
                        status -> status.is5xxServerError(),
                        (request, response) -> {
                            throw new OffreEmploiApiException(
                                    "Service France Travail indisponible [" + response.getStatusCode() + "]");
                        })
                .build();
    }

    private static ClientHttpRequestFactory requestFactoryAvecTimeouts(Duration connectTimeout, Duration readTimeout) {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(connectTimeout).build());
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
