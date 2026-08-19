package fr.sirene.jobtracker.application.usecase.offre;

import fr.sirene.jobtracker.application.port.cv.CvRepository;
import fr.sirene.jobtracker.application.port.offre.GenerationLettreMotivationPort;
import fr.sirene.jobtracker.application.port.offre.OffreStorageRepository;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.exception.OffreNonTrouveeException;

import org.springframework.stereotype.Service;

@Service
public class GenererLettreMotivationUseCase {

    private final OffreStorageRepository offreStorageRepository;
    private final CvRepository cvRepository;
    private final GenerationLettreMotivationPort generationLettreMotivationPort;

    public GenererLettreMotivationUseCase(
            OffreStorageRepository offreStorageRepository,
            CvRepository cvRepository,
            GenerationLettreMotivationPort generationLettreMotivationPort) {
        this.offreStorageRepository = offreStorageRepository;
        this.cvRepository = cvRepository;
        this.generationLettreMotivationPort = generationLettreMotivationPort;
    }

    public String executer(String idExterneOffre, String cvNomUnique) {
        offreStorageRepository.trouverParIdExterne(idExterneOffre)
                .orElseThrow(() -> new OffreNonTrouveeException(idExterneOffre));
        cvRepository.trouverParNomUnique(cvNomUnique)
                .orElseThrow(() -> new CvNonTrouveException(cvNomUnique));
        return generationLettreMotivationPort.genererLettre(idExterneOffre, cvNomUnique);
    }
}
