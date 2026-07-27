package fr.sirene.jobtracker.application.port;

import fr.sirene.jobtracker.domain.model.BrouillonOffre;

public interface ExtractionOffreIAPort {

    BrouillonOffre extraire(String contenuPage, String urlOrigine);
}
