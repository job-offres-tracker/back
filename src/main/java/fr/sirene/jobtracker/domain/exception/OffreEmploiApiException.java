package fr.sirene.jobtracker.domain.exception;

public class OffreEmploiApiException extends RuntimeException {

    public OffreEmploiApiException(String message) {
        super(message);
    }

    public OffreEmploiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
