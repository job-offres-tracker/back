package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class DocumentTexte implements DocumentCandidature {

    private final Long id;
    private final String libelle;
    private final String contenuTexte;
    private final LocalDateTime dateAjout;

    private DocumentTexte(Builder builder) {
        this.id = builder.id;
        this.libelle = builder.libelle;
        this.contenuTexte = builder.contenuTexte;
        this.dateAjout = builder.dateAjout;
    }

    public Long getId() { return id; }
    public String getLibelle() { return libelle; }
    public String getContenuTexte() { return contenuTexte; }
    public LocalDateTime getDateAjout() { return dateAjout; }

    public static Builder builder() { return new Builder(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentTexte other)) {
            return false;
        }
        return Objects.equals(id, other.id)
                && Objects.equals(libelle, other.libelle)
                && Objects.equals(contenuTexte, other.contenuTexte)
                && Objects.equals(dateAjout, other.dateAjout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, libelle, contenuTexte, dateAjout);
    }

    @Override
    public String toString() {
        return "DocumentTexte[id=" + id + ", libelle=" + libelle + ", contenuTexte=" + contenuTexte
                + ", dateAjout=" + dateAjout + "]";
    }

    public static final class Builder {
        private Long id;
        private String libelle;
        private String contenuTexte;
        private LocalDateTime dateAjout;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder libelle(String libelle) { this.libelle = libelle; return this; }
        public Builder contenuTexte(String contenuTexte) { this.contenuTexte = contenuTexte; return this; }
        public Builder dateAjout(LocalDateTime dateAjout) { this.dateAjout = dateAjout; return this; }

        public DocumentTexte build() {
            if (libelle == null || libelle.isBlank()) {
                throw new IllegalArgumentException("Le libellé est obligatoire pour construire un DocumentTexte");
            }
            if (contenuTexte == null || contenuTexte.isBlank()) {
                throw new IllegalArgumentException("Le contenu est obligatoire pour construire un DocumentTexte");
            }
            return new DocumentTexte(this);
        }
    }
}
