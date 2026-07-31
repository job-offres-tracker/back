package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;

public record DocumentFichier(
        Long id, String libelle, String nomStocke, long tailleOctets, String contentType, LocalDateTime dateAjout)
        implements DocumentCandidature {

    public DocumentFichier {
        if (libelle == null || libelle.isBlank()) {
            throw new IllegalArgumentException("Le libellé est obligatoire pour construire un DocumentFichier");
        }
        if (nomStocke == null || nomStocke.isBlank()) {
            throw new IllegalArgumentException("Le nom de stockage est obligatoire pour construire un DocumentFichier");
        }
    }
}
