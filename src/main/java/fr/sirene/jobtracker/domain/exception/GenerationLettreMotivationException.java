package fr.sirene.jobtracker.domain.exception;

public class GenerationLettreMotivationException extends RuntimeException {

    public GenerationLettreMotivationException(String message) {
        super(message);
    }

    public GenerationLettreMotivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
