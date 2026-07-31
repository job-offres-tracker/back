package fr.sirene.jobtracker.domain.exception;

public class ExtractionTexteCvException extends RuntimeException {

    public ExtractionTexteCvException(String message) {
        super(message);
    }

    public ExtractionTexteCvException(String message, Throwable cause) {
        super(message, cause);
    }
}
