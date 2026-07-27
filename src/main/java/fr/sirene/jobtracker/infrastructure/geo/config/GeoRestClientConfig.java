package fr.sirene.jobtracker.infrastructure.geo.config;

import fr.sirene.jobtracker.domain.exception.RechercheCommuneException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class GeoRestClientConfig {

    @Bean
    public RestClient geoApiRestClient(RestClient.Builder builder, GeoApiProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactoryAvecTimeouts(properties.connectTimeout(), properties.readTimeout()))
                .defaultHeader("Accept", "application/json")
                .defaultStatusHandler(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            throw new RechercheCommuneException(
                                    "Service de recherche de communes indisponible [" + response.getStatusCode() + "]");
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
