package fr.sirene.jobtracker.infrastructure.ban.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "ban.api")
public record BanApiProperties(
        String baseUrl,
        Duration connectTimeout,
        Duration readTimeout
) {}
