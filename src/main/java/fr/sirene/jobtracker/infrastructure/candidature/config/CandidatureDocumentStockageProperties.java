package fr.sirene.jobtracker.infrastructure.candidature.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jobtracker.candidature.document.stockage")
public record CandidatureDocumentStockageProperties(String repertoire) {}
