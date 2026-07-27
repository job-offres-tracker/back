package fr.sirene.jobtracker.domain.model;

public record CritereRecherche(
        String motsCles,
        String typeContrat,
        String codeCommune
) {}
