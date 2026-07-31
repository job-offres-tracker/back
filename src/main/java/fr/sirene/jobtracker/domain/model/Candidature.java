package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Candidature {

    private final Long id;
    private final Offre offre;
    private final LocalDateTime dateCandidature;
    private final List<Evenement> evenements;
    private final List<DocumentCandidature> documents;

    private Candidature(Builder builder) {
        this.id = builder.id;
        this.offre = builder.offre;
        this.dateCandidature = builder.dateCandidature;
        this.evenements = builder.evenements;
        this.documents = builder.documents;
    }

    public Long getId() { return id; }
    public Offre getOffre() { return offre; }
    public LocalDateTime getDateCandidature() { return dateCandidature; }
    public List<Evenement> getEvenements() { return evenements; }
    public List<DocumentCandidature> getDocuments() { return documents; }

    public static Builder builder() { return new Builder(); }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .offre(this.offre)
                .dateCandidature(this.dateCandidature)
                .evenements(this.evenements)
                .documents(this.documents);
    }

    public static final class Builder {
        private Long id;
        private Offre offre;
        private LocalDateTime dateCandidature;
        private List<Evenement> evenements = List.of();
        private List<DocumentCandidature> documents = List.of();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder offre(Offre offre) { this.offre = offre; return this; }
        public Builder dateCandidature(LocalDateTime dateCandidature) { this.dateCandidature = dateCandidature; return this; }
        public Builder evenements(List<Evenement> evenements) { this.evenements = evenements != null ? evenements : List.of(); return this; }
        public Builder documents(List<DocumentCandidature> documents) { this.documents = documents != null ? documents : List.of(); return this; }

        public Candidature build() {
            if (offre == null) {
                throw new IllegalStateException("L'offre est obligatoire pour construire une Candidature");
            }
            return new Candidature(this);
        }
    }
}
