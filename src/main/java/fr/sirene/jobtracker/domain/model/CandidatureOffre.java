package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public record CandidatureOffre(
        Long id,
        Offre offre,
        StatutCandidatureOffre statut,
        LocalDateTime dateCandidature,
        List<Evenement> evenements,
        List<DocumentCandidature> documents
) implements Candidature {

    public CandidatureOffre {
        if (offre == null) {
            throw new IllegalArgumentException("L'offre est obligatoire pour construire une CandidatureOffre");
        }
    }
}
