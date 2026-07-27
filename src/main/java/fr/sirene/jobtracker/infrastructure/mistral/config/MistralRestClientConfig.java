package fr.sirene.jobtracker.infrastructure.mistral.config;

import fr.sirene.jobtracker.domain.exception.ExtractionOffreIAException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class MistralRestClientConfig {

    @Bean
    public RestClient mistralApiRestClient(RestClient.Builder builder, MistralApiProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactoryAvecTimeouts(properties.connectTimeout(), properties.readTimeout()))
                .defaultHeader("Authorization", "Bearer " + properties.apiKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultStatusHandler(
                        status -> status.is4xxClientError(),
                        (request, response) -> {
                            throw new ExtractionOffreIAException(
                                    "Erreur client API Mistral [" + response.getStatusCode() + "]");
                        })
                .defaultStatusHandler(
                        status -> status.is5xxServerError(),
                        (request, response) -> {
                            throw new ExtractionOffreIAException(
                                    "Service Mistral indisponible [" + response.getStatusCode() + "]");
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
