package fr.sirene.jobtracker.domain.exception;

public class TypeFichierNonAutoriseException extends RuntimeException {

    public TypeFichierNonAutoriseException(String message) {
        super(message);
    }
}
