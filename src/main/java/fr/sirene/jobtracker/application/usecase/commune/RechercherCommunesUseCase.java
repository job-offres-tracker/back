package fr.sirene.jobtracker.application.usecase.commune;

import fr.sirene.jobtracker.application.port.commune.RechercheCommunePort;
import fr.sirene.jobtracker.domain.model.Commune;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RechercherCommunesUseCase {

    private final RechercheCommunePort rechercheCommunePort;

    public RechercherCommunesUseCase(RechercheCommunePort rechercheCommunePort) {
        this.rechercheCommunePort = rechercheCommunePort;
    }

    public List<Commune> executer(String nom) {
        return rechercheCommunePort.rechercher(nom);
    }
}
