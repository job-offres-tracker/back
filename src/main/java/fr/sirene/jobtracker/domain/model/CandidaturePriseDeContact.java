package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public record CandidaturePriseDeContact(
        Long id,
        String nomEntreprise,
        String urlEntreprise,
        TypeEntreprise typeEntreprise,
        StatutPriseDeContact statut,
        LocalDateTime dateCandidature,
        List<Evenement> evenements,
        List<DocumentCandidature> documents
) implements Candidature {
}
