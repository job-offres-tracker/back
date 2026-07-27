package fr.sirene.jobtracker.application.port;

import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Offre;

import java.util.List;
import java.util.Optional;

public interface OffreStorageRepository {

    void sauvegarderTout(List<Offre> offres);

    List<Offre> rechercher(int page, int taille, List<EtatOffre> etats);

    long compter(List<EtatOffre> etats);

    void mettreAJourEtat(List<String> idsExternes, EtatOffre etat);

    Optional<Offre> trouverParIdExterne(String idExterne);
}
