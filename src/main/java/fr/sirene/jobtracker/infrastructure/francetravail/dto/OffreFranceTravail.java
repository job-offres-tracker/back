package fr.sirene.jobtracker.infrastructure.francetravail.dto;

public record OffreFranceTravail(
        String id,
        String intitule,
        String description,
        String dateCreation,
        EntrepriseFranceTravail entreprise,
        LieuTravailFranceTravail lieuTravail,
        String typeContrat,
        SalaireFranceTravail salaire,
        OrigineOffreFranceTravail origineOffre
) {}
