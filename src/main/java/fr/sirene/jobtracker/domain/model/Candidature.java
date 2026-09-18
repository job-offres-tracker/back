package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public sealed interface Candidature permits CandidatureOffre, CandidatureSpontanee, CandidaturePriseDeContact {

    Long getId();

    LocalDateTime getDateCandidature();

    List<Evenement> getEvenements();

    List<DocumentCandidature> getDocuments();
}
