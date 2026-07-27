package fr.sirene.jobtracker.domain.exception;

public class RecuperationPageException extends RuntimeException {

    public RecuperationPageException(String message) {
        super(message);
    }

    public RecuperationPageException(String message, Throwable cause) {
        super(message, cause);
    }
}
