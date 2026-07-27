package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.RechercheCommunePort;
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
