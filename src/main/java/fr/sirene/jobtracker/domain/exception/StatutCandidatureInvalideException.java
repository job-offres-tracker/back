package fr.sirene.jobtracker.domain.exception;

public class StatutCandidatureInvalideException extends RuntimeException {

    public StatutCandidatureInvalideException(String message) {
        super(message);
    }

    public StatutCandidatureInvalideException(String message, Throwable cause) {
        super(message, cause);
    }
}
