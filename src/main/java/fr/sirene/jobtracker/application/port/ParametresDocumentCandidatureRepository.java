package fr.sirene.jobtracker.application.port;

import fr.sirene.jobtracker.domain.model.ParametresDocumentCandidature;

public interface ParametresDocumentCandidatureRepository {

    ParametresDocumentCandidature recuperer();

    ParametresDocumentCandidature sauvegarder(ParametresDocumentCandidature parametres);
}
