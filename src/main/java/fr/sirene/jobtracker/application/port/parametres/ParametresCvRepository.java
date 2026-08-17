package fr.sirene.jobtracker.application.port.parametres;

import fr.sirene.jobtracker.domain.model.ParametresCv;

public interface ParametresCvRepository {

    ParametresCv recuperer();

    ParametresCv sauvegarder(ParametresCv parametres);
}
