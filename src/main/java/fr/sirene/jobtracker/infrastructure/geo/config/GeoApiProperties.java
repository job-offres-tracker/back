package fr.sirene.jobtracker.infrastructure.geo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "geo.api")
public record GeoApiProperties(
        String baseUrl,
        Duration connectTimeout,
        Duration readTimeout
) {}
