package fr.sirene.jobtracker.infrastructure.ai.dto;

public record ExtractionOffreIA(
        String intitule,
        String description,
        String entreprise,
        String lieuLibelle,
        String typeContrat,
        String salaire,
        String referenceExterne,
        String datePublication
) {}
