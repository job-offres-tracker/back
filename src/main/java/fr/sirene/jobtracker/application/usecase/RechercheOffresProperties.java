package fr.sirene.jobtracker.application.usecase;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "jobtracker.recherche")
public record RechercheOffresProperties(
        List<String> motsCles,
        String typeContrat,
        String codeCommune
) {}
