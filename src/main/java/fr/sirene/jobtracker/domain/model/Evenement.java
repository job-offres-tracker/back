package fr.sirene.jobtracker.domain.model;

import java.time.LocalDate;

public class Evenement {

    private final Long id;
    private final LocalDate date;
    private final TypeEvenement type;
    private final String description;

    private Evenement(Builder builder) {
        this.id = builder.id;
        this.date = builder.date;
        this.type = builder.type;
        this.description = builder.description;
    }

    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public TypeEvenement getType() { return type; }
    public String getDescription() { return description; }

    public static Builder builder() { return new Builder(); }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .date(this.date)
                .type(this.type)
                .description(this.description);
    }

    public static final class Builder {
        private Long id;
        private LocalDate date;
        private TypeEvenement type;
        private String description;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder date(LocalDate date) { this.date = date; return this; }
        public Builder type(TypeEvenement type) { this.type = type; return this; }
        public Builder description(String description) { this.description = description; return this; }

        public Evenement build() {
            if (date == null) {
                throw new IllegalStateException("La date est obligatoire pour construire un Evenement");
            }
            if (type == null) {
                throw new IllegalStateException("Le type est obligatoire pour construire un Evenement");
            }
            return new Evenement(this);
        }
    }
}
