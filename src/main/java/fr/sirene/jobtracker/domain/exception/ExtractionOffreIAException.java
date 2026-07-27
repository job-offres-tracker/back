package fr.sirene.jobtracker.domain.exception;

public class ExtractionOffreIAException extends RuntimeException {

    public ExtractionOffreIAException(String message) {
        super(message);
    }

    public ExtractionOffreIAException(String message, Throwable cause) {
        super(message, cause);
    }
}
