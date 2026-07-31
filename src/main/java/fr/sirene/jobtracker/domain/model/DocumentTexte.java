package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;

public record DocumentTexte(Long id, String libelle, String contenuTexte, LocalDateTime dateAjout) implements DocumentCandidature {

    public DocumentTexte {
        if (libelle == null || libelle.isBlank()) {
            throw new IllegalArgumentException("Le libellé est obligatoire pour construire un DocumentTexte");
        }
        if (contenuTexte == null || contenuTexte.isBlank()) {
            throw new IllegalArgumentException("Le contenu est obligatoire pour construire un DocumentTexte");
        }
    }
}
