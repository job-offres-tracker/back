package fr.sirene.jobtracker.infrastructure.francetravail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "francetravail.auth")
public record FranceTravailAuthProperties(
        String tokenUrl,
        String clientId,
        String clientSecret,
        String scope,
        Duration connectTimeout,
        Duration readTimeout
) {}
