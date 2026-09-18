package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class DocumentCv implements DocumentCandidature {

    private final Long id;
    private final String libelle;
    private final String cvNomUnique;
    private final long tailleOctets;
    private final LocalDateTime dateAjout;

    private DocumentCv(Builder builder) {
        this.id = builder.id;
        this.libelle = builder.libelle;
        this.cvNomUnique = builder.cvNomUnique;
        this.tailleOctets = builder.tailleOctets;
        this.dateAjout = builder.dateAjout;
    }

    public Long getId() { return id; }
    public String getLibelle() { return libelle; }
    public String getCvNomUnique() { return cvNomUnique; }
    public long getTailleOctets() { return tailleOctets; }
    public LocalDateTime getDateAjout() { return dateAjout; }

    public static Builder builder() { return new Builder(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentCv other)) {
            return false;
        }
        return tailleOctets == other.tailleOctets
                && Objects.equals(id, other.id)
                && Objects.equals(libelle, other.libelle)
                && Objects.equals(cvNomUnique, other.cvNomUnique)
                && Objects.equals(dateAjout, other.dateAjout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, libelle, cvNomUnique, tailleOctets, dateAjout);
    }

    @Override
    public String toString() {
        return "DocumentCv[id=" + id + ", libelle=" + libelle + ", cvNomUnique=" + cvNomUnique
                + ", tailleOctets=" + tailleOctets + ", dateAjout=" + dateAjout + "]";
    }

    public static final class Builder {
        private Long id;
        private String libelle;
        private String cvNomUnique;
        private long tailleOctets;
        private LocalDateTime dateAjout;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder libelle(String libelle) { this.libelle = libelle; return this; }
        public Builder cvNomUnique(String cvNomUnique) { this.cvNomUnique = cvNomUnique; return this; }
        public Builder tailleOctets(long tailleOctets) { this.tailleOctets = tailleOctets; return this; }
        public Builder dateAjout(LocalDateTime dateAjout) { this.dateAjout = dateAjout; return this; }

        public DocumentCv build() {
            if (libelle == null || libelle.isBlank()) {
                throw new IllegalArgumentException("Le libellé est obligatoire pour construire un DocumentCv");
            }
            if (cvNomUnique == null || cvNomUnique.isBlank()) {
                throw new IllegalArgumentException("Le nom unique du CV est obligatoire pour construire un DocumentCv");
            }
            return new DocumentCv(this);
        }
    }
}
