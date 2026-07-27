package fr.sirene.jobtracker.infrastructure.scraping.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "scraping")
public record ScrapingProperties(
        Duration connectTimeout,
        Duration readTimeout
) {}
