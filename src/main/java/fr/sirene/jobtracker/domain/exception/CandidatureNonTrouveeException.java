package fr.sirene.jobtracker.domain.exception;

public class CandidatureNonTrouveeException extends RuntimeException {

    public CandidatureNonTrouveeException(Long id) {
        super("Aucune candidature trouvée pour l'identifiant : " + id);
    }
}
