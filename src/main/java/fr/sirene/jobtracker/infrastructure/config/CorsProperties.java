package fr.sirene.jobtracker.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "jobtracker.cors")
public record CorsProperties(
        List<String> allowedOrigins
) {}
