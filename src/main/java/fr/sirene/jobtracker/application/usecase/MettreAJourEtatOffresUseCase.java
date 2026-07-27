package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.OffreStorageRepository;
import fr.sirene.jobtracker.domain.model.EtatOffre;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MettreAJourEtatOffresUseCase {

    private final OffreStorageRepository offreStorageRepository;

    public MettreAJourEtatOffresUseCase(OffreStorageRepository offreStorageRepository) {
        this.offreStorageRepository = offreStorageRepository;
    }

    public void executer(List<String> idsExternes, EtatOffre etat) {
        offreStorageRepository.mettreAJourEtat(idsExternes, etat);
    }
}
