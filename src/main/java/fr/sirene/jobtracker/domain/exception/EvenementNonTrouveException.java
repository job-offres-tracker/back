package fr.sirene.jobtracker.domain.exception;

public class EvenementNonTrouveException extends RuntimeException {

    public EvenementNonTrouveException(Long candidatureId, Long evenementId) {
        super("Aucun événement trouvé pour l'identifiant %d sur la candidature %d".formatted(evenementId, candidatureId));
    }
}
