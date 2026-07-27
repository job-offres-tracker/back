package fr.sirene.jobtracker.domain.exception;

public class GeocodageAdresseException extends RuntimeException {

    public GeocodageAdresseException(String message) {
        super(message);
    }

    public GeocodageAdresseException(String message, Throwable cause) {
        super(message, cause);
    }
}
