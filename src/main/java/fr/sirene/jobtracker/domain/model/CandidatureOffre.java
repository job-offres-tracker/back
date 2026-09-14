package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public record CandidatureOffre(
        Long id,
        Offre offre,
        LocalDateTime dateCandidature,
        List<Evenement> evenements,
        List<DocumentCandidature> documents
) implements Candidature {
}
