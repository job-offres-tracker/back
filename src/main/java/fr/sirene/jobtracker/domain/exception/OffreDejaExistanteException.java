package fr.sirene.jobtracker.domain.exception;

public class OffreDejaExistanteException extends RuntimeException {

    public OffreDejaExistanteException(String idExterne) {
        super("Une offre existe déjà pour l'identifiant externe : " + idExterne);
    }
}
