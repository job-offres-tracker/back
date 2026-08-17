package fr.sirene.jobtracker.application.port.parametres;

import fr.sirene.jobtracker.domain.model.ParametresRecherche;

public interface ParametresRechercheRepository {

    ParametresRecherche recuperer();

    ParametresRecherche sauvegarder(ParametresRecherche parametres);
}
