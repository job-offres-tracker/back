package fr.sirene.jobtracker.domain.model;

public record ParametresDocumentCandidature(long tailleMaxOctets) {

    public ParametresDocumentCandidature {
        if (tailleMaxOctets <= 0) {
            throw new IllegalArgumentException("La taille maximale doit être strictement positive");
        }
    }
}
