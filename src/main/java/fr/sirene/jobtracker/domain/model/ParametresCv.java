package fr.sirene.jobtracker.domain.model;

public record ParametresCv(long tailleMaxOctets) {

    public ParametresCv {
        if (tailleMaxOctets <= 0) {
            throw new IllegalArgumentException("La taille maximale doit être strictement positive");
        }
    }
}
