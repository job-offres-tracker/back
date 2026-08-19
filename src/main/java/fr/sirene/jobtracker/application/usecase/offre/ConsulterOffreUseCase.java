package fr.sirene.jobtracker.application.usecase.offre;

import fr.sirene.jobtracker.application.port.offre.OffreStorageRepository;
import fr.sirene.jobtracker.domain.exception.OffreNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Offre;

import org.springframework.stereotype.Service;

@Service
public class ConsulterOffreUseCase {

    private final OffreStorageRepository offreStorageRepository;

    public ConsulterOffreUseCase(OffreStorageRepository offreStorageRepository) {
        this.offreStorageRepository = offreStorageRepository;
    }

    public Offre executer(String idExterne) {
        return offreStorageRepository.trouverParIdExterne(idExterne)
                .orElseThrow(() -> new OffreNonTrouveeException(idExterne));
    }
}
