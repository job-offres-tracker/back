package fr.sirene.jobtracker.infrastructure.francetravail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "francetravail.api")
public record FranceTravailApiProperties(
        String baseUrl,
        Duration connectTimeout,
        Duration readTimeout
) {}
