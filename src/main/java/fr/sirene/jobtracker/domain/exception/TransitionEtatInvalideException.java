package fr.sirene.jobtracker.domain.exception;

import fr.sirene.jobtracker.domain.model.EtatOffre;

import java.util.List;

public class TransitionEtatInvalideException extends RuntimeException {

    public TransitionEtatInvalideException(List<String> idsExternes, EtatOffre etatCible) {
        super("Impossible de repasser à l'état %s pour les offres suivantes, déjà engagées dans une candidature : %s"
                .formatted(etatCible, idsExternes));
    }
}
