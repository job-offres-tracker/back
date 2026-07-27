package fr.sirene.jobtracker.domain.model;

public record BrouillonOffre(
        String intitule,
        String description,
        String entreprise,
        String lieuLibelle,
        String typeContrat,
        String salaire,
        String urlOrigine,
        String referenceExterne,
        String datePublication
) {}
