package fr.sirene.jobtracker.domain.model;

import java.time.Instant;

public class Cv {

    private final String nomUnique;
    private final String nomOriginal;
    private final long tailleOctets;
    private final Instant dateUpload;

    private Cv(Builder builder) {
        this.nomUnique = builder.nomUnique;
        this.nomOriginal = builder.nomOriginal;
        this.tailleOctets = builder.tailleOctets;
        this.dateUpload = builder.dateUpload;
    }

    public String getNomUnique() { return nomUnique; }
    public String getNomOriginal() { return nomOriginal; }
    public long getTailleOctets() { return tailleOctets; }
    public Instant getDateUpload() { return dateUpload; }

    public static Builder builder() { return new Builder(); }

    public Builder toBuilder() {
        return new Builder()
                .nomUnique(this.nomUnique)
                .nomOriginal(this.nomOriginal)
                .tailleOctets(this.tailleOctets)
                .dateUpload(this.dateUpload);
    }

    public static final class Builder {
        private String nomUnique;
        private String nomOriginal;
        private long tailleOctets;
        private Instant dateUpload;

        public Builder nomUnique(String nomUnique) { this.nomUnique = nomUnique; return this; }
        public Builder nomOriginal(String nomOriginal) { this.nomOriginal = nomOriginal; return this; }
        public Builder tailleOctets(long tailleOctets) { this.tailleOctets = tailleOctets; return this; }
        public Builder dateUpload(Instant dateUpload) { this.dateUpload = dateUpload; return this; }

        public Cv build() {
            if (nomUnique == null) {
                throw new IllegalStateException("Le nom unique est obligatoire pour construire un Cv");
            }
            return new Cv(this);
        }
    }
}
