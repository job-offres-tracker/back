package fr.sirene.jobtracker.application.usecase.offre;

import fr.sirene.jobtracker.application.port.offre.ExtractionOffreIAPort;
import fr.sirene.jobtracker.application.port.offre.RecuperationPageOffrePort;
import fr.sirene.jobtracker.domain.model.BrouillonOffre;

import org.springframework.stereotype.Service;

@Service
public class ImporterOffreDepuisUrlUseCase {

    private final RecuperationPageOffrePort recuperationPageOffrePort;
    private final ExtractionOffreIAPort extractionOffreIAPort;

    public ImporterOffreDepuisUrlUseCase(
            RecuperationPageOffrePort recuperationPageOffrePort, ExtractionOffreIAPort extractionOffreIAPort) {
        this.recuperationPageOffrePort = recuperationPageOffrePort;
        this.extractionOffreIAPort = extractionOffreIAPort;
    }

    public BrouillonOffre executer(String url) {
        String contenuPage = recuperationPageOffrePort.recuperer(url);
        return extractionOffreIAPort.extraire(contenuPage, url);
    }
}
