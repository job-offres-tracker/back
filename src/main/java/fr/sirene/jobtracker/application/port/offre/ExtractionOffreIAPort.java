package fr.sirene.jobtracker.application.port.offre;

import fr.sirene.jobtracker.domain.model.BrouillonOffre;

public interface ExtractionOffreIAPort {

    BrouillonOffre extraire(String contenuPage, String urlOrigine);
}
