package fr.sirene.jobtracker.domain.exception;

public class CvNonTrouveException extends RuntimeException {

    public CvNonTrouveException(String nomUnique) {
        super("Aucun CV trouvé pour le nom : " + nomUnique);
    }
}
