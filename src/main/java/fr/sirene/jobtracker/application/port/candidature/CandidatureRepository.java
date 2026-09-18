package fr.sirene.jobtracker.application.port.candidature;

import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.Evenement;
import fr.sirene.jobtracker.domain.model.ResultatPagine;

import java.util.Optional;

public interface CandidatureRepository {

    Candidature sauvegarder(Candidature candidature);

    Candidature mettreAJourStatut(Candidature candidature);

    Optional<Candidature> trouverParId(long id);

    Optional<Candidature> trouverParOffreIdExterne(String idExterneOffre);

    boolean existeParOffreIdExterne(String idExterneOffre);

    ResultatPagine<Candidature> lister(int page, int taille);

    Evenement ajouterEvenement(long candidatureId, Evenement evenement);

    Evenement modifierEvenement(long candidatureId, long evenementId, Evenement evenement);

    DocumentCandidature ajouterDocument(long candidatureId, DocumentCandidature document);
}
