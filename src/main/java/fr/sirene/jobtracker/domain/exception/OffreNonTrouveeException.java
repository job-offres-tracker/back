package fr.sirene.jobtracker.domain.exception;

public class OffreNonTrouveeException extends RuntimeException {

    public OffreNonTrouveeException(String idExterne) {
        super("Aucune offre trouvée pour l'identifiant externe : " + idExterne);
    }
}
