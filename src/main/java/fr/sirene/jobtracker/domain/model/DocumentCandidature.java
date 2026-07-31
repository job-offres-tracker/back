package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;

public sealed interface DocumentCandidature permits DocumentCv, DocumentFichier, DocumentTexte {

    Long id();

    String libelle();

    LocalDateTime dateAjout();
}
