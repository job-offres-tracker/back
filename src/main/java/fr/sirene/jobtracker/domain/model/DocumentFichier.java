package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;

public final class DocumentFichier implements DocumentCandidature {

    private final Long id;
    private final String libelle;
    private final String nomStocke;
    private final long tailleOctets;
    private final String contentType;
    private final LocalDateTime dateAjout;

    private DocumentFichier(Builder builder) {
        this.id = builder.id;
        this.libelle = builder.libelle;
        this.nomStocke = builder.nomStocke;
        this.tailleOctets = builder.tailleOctets;
        this.contentType = builder.contentType;
        this.dateAjout = builder.dateAjout;
    }

    public Long getId() { return id; }
    public String getLibelle() { return libelle; }
    public String getNomStocke() { return nomStocke; }
    public long getTailleOctets() { return tailleOctets; }
    public String getContentType() { return contentType; }
    public LocalDateTime getDateAjout() { return dateAjout; }

    public static Builder builder() { return new Builder(); }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .libelle(this.libelle)
                .nomStocke(this.nomStocke)
                .tailleOctets(this.tailleOctets)
                .contentType(this.contentType)
                .dateAjout(this.dateAjout);
    }

    public static final class Builder {
        private Long id;
        private String libelle;
        private String nomStocke;
        private long tailleOctets;
        private String contentType;
        private LocalDateTime dateAjout;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder libelle(String libelle) { this.libelle = libelle; return this; }
        public Builder nomStocke(String nomStocke) { this.nomStocke = nomStocke; return this; }
        public Builder tailleOctets(long tailleOctets) { this.tailleOctets = tailleOctets; return this; }
        public Builder contentType(String contentType) { this.contentType = contentType; return this; }
        public Builder dateAjout(LocalDateTime dateAjout) { this.dateAjout = dateAjout; return this; }

        public DocumentFichier build() {
            if (libelle == null || libelle.isBlank()) {
                throw new IllegalArgumentException("Le libellé est obligatoire pour construire un DocumentFichier");
            }
            if (nomStocke == null || nomStocke.isBlank()) {
                throw new IllegalArgumentException("Le nom de stockage est obligatoire pour construire un DocumentFichier");
            }
            return new DocumentFichier(this);
        }
    }
}
