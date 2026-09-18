package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class CandidaturePriseDeContact implements Candidature {

    private final Long id;
    private final String nomEntreprise;
    private final String urlEntreprise;
    private final TypeEntreprise typeEntreprise;
    private final StatutPriseDeContact statut;
    private final LocalDateTime dateCandidature;
    private final List<Evenement> evenements;
    private final List<DocumentCandidature> documents;

    private CandidaturePriseDeContact(Builder builder) {
        this.id = builder.id;
        this.nomEntreprise = builder.nomEntreprise;
        this.urlEntreprise = builder.urlEntreprise;
        this.typeEntreprise = builder.typeEntreprise;
        this.statut = builder.statut;
        this.dateCandidature = builder.dateCandidature;
        this.evenements = builder.evenements;
        this.documents = builder.documents;
    }

    public Long getId() { return id; }
    public String getNomEntreprise() { return nomEntreprise; }
    public String getUrlEntreprise() { return urlEntreprise; }
    public TypeEntreprise getTypeEntreprise() { return typeEntreprise; }
    public StatutPriseDeContact getStatut() { return statut; }
    public LocalDateTime getDateCandidature() { return dateCandidature; }
    public List<Evenement> getEvenements() { return evenements; }
    public List<DocumentCandidature> getDocuments() { return documents; }

    public static Builder builder() { return new Builder(); }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .nomEntreprise(this.nomEntreprise)
                .urlEntreprise(this.urlEntreprise)
                .typeEntreprise(this.typeEntreprise)
                .statut(this.statut)
                .dateCandidature(this.dateCandidature)
                .evenements(this.evenements)
                .documents(this.documents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CandidaturePriseDeContact other)) {
            return false;
        }
        return Objects.equals(id, other.id)
                && Objects.equals(nomEntreprise, other.nomEntreprise)
                && Objects.equals(urlEntreprise, other.urlEntreprise)
                && typeEntreprise == other.typeEntreprise
                && statut == other.statut
                && Objects.equals(dateCandidature, other.dateCandidature)
                && Objects.equals(evenements, other.evenements)
                && Objects.equals(documents, other.documents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomEntreprise, urlEntreprise, typeEntreprise, statut,
                dateCandidature, evenements, documents);
    }

    @Override
    public String toString() {
        return "CandidaturePriseDeContact[id=" + id + ", nomEntreprise=" + nomEntreprise
                + ", urlEntreprise=" + urlEntreprise + ", typeEntreprise=" + typeEntreprise
                + ", statut=" + statut + ", dateCandidature=" + dateCandidature
                + ", evenements=" + evenements + ", documents=" + documents + "]";
    }

    public static final class Builder {
        private Long id;
        private String nomEntreprise;
        private String urlEntreprise;
        private TypeEntreprise typeEntreprise;
        private StatutPriseDeContact statut;
        private LocalDateTime dateCandidature;
        private List<Evenement> evenements;
        private List<DocumentCandidature> documents;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder nomEntreprise(String nomEntreprise) { this.nomEntreprise = nomEntreprise; return this; }
        public Builder urlEntreprise(String urlEntreprise) { this.urlEntreprise = urlEntreprise; return this; }
        public Builder typeEntreprise(TypeEntreprise typeEntreprise) { this.typeEntreprise = typeEntreprise; return this; }
        public Builder statut(StatutPriseDeContact statut) { this.statut = statut; return this; }
        public Builder dateCandidature(LocalDateTime dateCandidature) { this.dateCandidature = dateCandidature; return this; }
        public Builder evenements(List<Evenement> evenements) { this.evenements = evenements; return this; }
        public Builder documents(List<DocumentCandidature> documents) { this.documents = documents; return this; }

        public CandidaturePriseDeContact build() {
            return new CandidaturePriseDeContact(this);
        }
    }
}
