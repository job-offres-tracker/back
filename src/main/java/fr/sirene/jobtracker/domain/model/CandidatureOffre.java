package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public final class CandidatureOffre implements Candidature {

    private final Long id;
    private final Offre offre;
    private final StatutCandidatureOffre statut;
    private final LocalDateTime dateCandidature;
    private final List<Evenement> evenements;
    private final List<DocumentCandidature> documents;

    private CandidatureOffre(Builder builder) {
        this.id = builder.id;
        this.offre = builder.offre;
        this.statut = builder.statut;
        this.dateCandidature = builder.dateCandidature;
        this.evenements = builder.evenements;
        this.documents = builder.documents;
    }

    public Long getId() { return id; }
    public Offre getOffre() { return offre; }
    public StatutCandidatureOffre getStatut() { return statut; }
    public LocalDateTime getDateCandidature() { return dateCandidature; }
    public List<Evenement> getEvenements() { return evenements; }
    public List<DocumentCandidature> getDocuments() { return documents; }

    public static Builder builder() { return new Builder(); }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .offre(this.offre)
                .statut(this.statut)
                .dateCandidature(this.dateCandidature)
                .evenements(this.evenements)
                .documents(this.documents);
    }

    public static final class Builder {
        private Long id;
        private Offre offre;
        private StatutCandidatureOffre statut;
        private LocalDateTime dateCandidature;
        private List<Evenement> evenements;
        private List<DocumentCandidature> documents;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder offre(Offre offre) { this.offre = offre; return this; }
        public Builder statut(StatutCandidatureOffre statut) { this.statut = statut; return this; }
        public Builder dateCandidature(LocalDateTime dateCandidature) { this.dateCandidature = dateCandidature; return this; }
        public Builder evenements(List<Evenement> evenements) { this.evenements = evenements; return this; }
        public Builder documents(List<DocumentCandidature> documents) { this.documents = documents; return this; }

        public CandidatureOffre build() {
            if (offre == null) {
                throw new IllegalArgumentException("L'offre est obligatoire pour construire une CandidatureOffre");
            }
            return new CandidatureOffre(this);
        }
    }
}
