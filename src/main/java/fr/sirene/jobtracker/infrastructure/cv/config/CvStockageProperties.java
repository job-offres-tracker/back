package fr.sirene.jobtracker.infrastructure.cv.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jobtracker.cv.stockage")
public record CvStockageProperties(String repertoire) {}
