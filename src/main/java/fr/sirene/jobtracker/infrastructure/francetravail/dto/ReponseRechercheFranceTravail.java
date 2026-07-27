package fr.sirene.jobtracker.infrastructure.francetravail.dto;

import java.util.List;

public record ReponseRechercheFranceTravail(
        List<OffreFranceTravail> resultats
) {}
