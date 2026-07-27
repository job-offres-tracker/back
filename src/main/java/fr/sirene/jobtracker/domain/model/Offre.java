package fr.sirene.jobtracker.domain.model;

import java.time.LocalDateTime;

public class Offre {

    private final String idExterne;
    private final String intitule;
    private final String description;
    private final String entreprise;
    private final Lieu lieu;
    private final String typeContrat;
    private final String salaire;
    private final String urlOrigine;
    private final LocalDateTime dateCreation;
    private final EtatOffre etat;
    private final String provenance;

    private Offre(Builder builder) {
        this.idExterne = builder.idExterne;
        this.intitule = builder.intitule;
        this.description = builder.description;
        this.entreprise = builder.entreprise;
        this.lieu = builder.lieu;
        this.typeContrat = builder.typeContrat;
        this.salaire = builder.salaire;
        this.urlOrigine = builder.urlOrigine;
        this.dateCreation = builder.dateCreation;
        this.etat = builder.etat;
        this.provenance = builder.provenance;
    }

    public String getIdExterne() { return idExterne; }
    public String getIntitule() { return intitule; }
    public String getDescription() { return description; }
    public String getEntreprise() { return entreprise; }
    public Lieu getLieu() { return lieu; }
    public String getTypeContrat() { return typeContrat; }
    public String getSalaire() { return salaire; }
    public String getUrlOrigine() { return urlOrigine; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public EtatOffre getEtat() { return etat; }
    public String getProvenance() { return provenance; }

    public static Builder builder() { return new Builder(); }

    public Builder toBuilder() {
        return new Builder()
                .idExterne(this.idExterne)
                .intitule(this.intitule)
                .description(this.description)
                .entreprise(this.entreprise)
                .lieu(this.lieu)
                .typeContrat(this.typeContrat)
                .salaire(this.salaire)
                .urlOrigine(this.urlOrigine)
                .dateCreation(this.dateCreation)
                .etat(this.etat)
                .provenance(this.provenance);
    }

    public static final class Builder {
        private String idExterne;
        private String intitule;
        private String description;
        private String entreprise;
        private Lieu lieu;
        private String typeContrat;
        private String salaire;
        private String urlOrigine;
        private LocalDateTime dateCreation;
        private EtatOffre etat = EtatOffre.NON_LU;
        private String provenance = "FRANCE_TRAVAIL";

        public Builder idExterne(String idExterne) { this.idExterne = idExterne; return this; }
        public Builder intitule(String intitule) { this.intitule = intitule; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder entreprise(String entreprise) { this.entreprise = entreprise; return this; }
        public Builder lieu(Lieu lieu) { this.lieu = lieu; return this; }
        public Builder typeContrat(String typeContrat) { this.typeContrat = typeContrat; return this; }
        public Builder salaire(String salaire) { this.salaire = salaire; return this; }
        public Builder urlOrigine(String urlOrigine) { this.urlOrigine = urlOrigine; return this; }
        public Builder dateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; return this; }
        public Builder etat(EtatOffre etat) { this.etat = etat; return this; }
        public Builder provenance(String provenance) { this.provenance = provenance; return this; }

        public Offre build() {
            if (idExterne == null) {
                throw new IllegalStateException("L'identifiant externe est obligatoire pour construire une Offre");
            }
            return new Offre(this);
        }
    }
}
