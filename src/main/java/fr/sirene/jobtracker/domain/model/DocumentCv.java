package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;

public record DocumentCv(Long id, String libelle, String cvNomUnique, long tailleOctets, LocalDateTime dateAjout)
        implements DocumentCandidature {

    public DocumentCv {
        if (libelle == null || libelle.isBlank()) {
            throw new IllegalArgumentException("Le libellé est obligatoire pour construire un DocumentCv");
        }
        if (cvNomUnique == null || cvNomUnique.isBlank()) {
            throw new IllegalArgumentException("Le nom unique du CV est obligatoire pour construire un DocumentCv");
        }
    }
}
