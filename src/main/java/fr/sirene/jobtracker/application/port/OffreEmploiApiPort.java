package fr.sirene.jobtracker.application.port;

import fr.sirene.jobtracker.domain.model.CritereRecherche;
import fr.sirene.jobtracker.domain.model.Offre;

import java.util.List;

public interface OffreEmploiApiPort {

    List<Offre> rechercherOffres(CritereRecherche critere);
}
