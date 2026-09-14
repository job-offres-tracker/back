package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public sealed interface Candidature permits CandidatureOffre, CandidatureSpontanee, CandidaturePriseDeContact {

    Long id();

    LocalDateTime dateCandidature();

    List<Evenement> evenements();

    List<DocumentCandidature> documents();
}
