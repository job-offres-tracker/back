package fr.sirene.jobtracker.domain.model;

public record Lieu(
        String libelle,
        String codeCommune,
        Double latitude,
        Double longitude,
        String adresse
) {}
