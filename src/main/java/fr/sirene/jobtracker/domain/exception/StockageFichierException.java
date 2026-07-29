package fr.sirene.jobtracker.domain.exception;

public class StockageFichierException extends RuntimeException {

    public StockageFichierException(String message) {
        super(message);
    }

    public StockageFichierException(String message, Throwable cause) {
        super(message, cause);
    }
}
