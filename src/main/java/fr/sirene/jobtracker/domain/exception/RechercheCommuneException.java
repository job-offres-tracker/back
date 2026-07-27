package fr.sirene.jobtracker.domain.exception;

public class RechercheCommuneException extends RuntimeException {

    public RechercheCommuneException(String message) {
        super(message);
    }

    public RechercheCommuneException(String message, Throwable cause) {
        super(message, cause);
    }
}
