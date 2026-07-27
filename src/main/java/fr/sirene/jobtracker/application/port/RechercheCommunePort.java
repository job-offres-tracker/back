package fr.sirene.jobtracker.application.port;

import fr.sirene.jobtracker.domain.model.Commune;

import java.util.List;

public interface RechercheCommunePort {

    List<Commune> rechercher(String nom);
}
