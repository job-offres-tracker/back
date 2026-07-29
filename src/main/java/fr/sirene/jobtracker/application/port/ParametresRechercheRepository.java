package fr.sirene.jobtracker.application.port;

import fr.sirene.jobtracker.domain.model.ParametresRecherche;

public interface ParametresRechercheRepository {

    ParametresRecherche recuperer();

    ParametresRecherche sauvegarder(ParametresRecherche parametres);
}
