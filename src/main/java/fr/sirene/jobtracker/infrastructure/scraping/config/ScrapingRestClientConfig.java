package fr.sirene.jobtracker.infrastructure.scraping.config;

import fr.sirene.jobtracker.domain.exception.RecuperationPageException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class ScrapingRestClientConfig {

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36";

    @Bean
    public RestClient scrapingRestClient(RestClient.Builder builder, ScrapingProperties properties) {
        return builder
                .requestFactory(requestFactoryAvecTimeouts(properties.connectTimeout(), properties.readTimeout()))
                .defaultHeader("User-Agent", USER_AGENT)
                .defaultHeader("Accept", "text/html")
                .defaultStatusHandler(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            throw new RecuperationPageException(
                                    "Impossible de récupérer la page [" + response.getStatusCode() + "]");
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
