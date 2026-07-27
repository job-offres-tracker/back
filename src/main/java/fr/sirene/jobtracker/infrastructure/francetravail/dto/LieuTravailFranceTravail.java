package fr.sirene.jobtracker.infrastructure.francetravail.dto;

public record LieuTravailFranceTravail(
        String libelle,
        String commune,
        Double latitude,
        Double longitude
) {}
