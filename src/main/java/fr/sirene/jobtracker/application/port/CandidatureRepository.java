package fr.sirene.jobtracker.application.port;

import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.Evenement;
import fr.sirene.jobtracker.domain.model.ResultatPagine;

import java.util.Optional;

public interface CandidatureRepository {

    Candidature sauvegarder(Candidature candidature);

    Optional<Candidature> trouverParId(Long id);

    Optional<Candidature> trouverParOffreIdExterne(String idExterneOffre);

    boolean existeParOffreIdExterne(String idExterneOffre);

    ResultatPagine<Candidature> lister(int page, int taille);

    Evenement ajouterEvenement(Long candidatureId, Evenement evenement);

    Evenement modifierEvenement(Long candidatureId, Long evenementId, Evenement evenement);

    DocumentCandidature ajouterDocument(Long candidatureId, DocumentCandidature document);
}
