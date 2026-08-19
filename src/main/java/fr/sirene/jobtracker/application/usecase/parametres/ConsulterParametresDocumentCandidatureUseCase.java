package fr.sirene.jobtracker.application.usecase.parametres;

import fr.sirene.jobtracker.application.port.parametres.ParametresDocumentCandidatureRepository;
import fr.sirene.jobtracker.domain.model.ParametresDocumentCandidature;

import org.springframework.stereotype.Service;

@Service
public class ConsulterParametresDocumentCandidatureUseCase {

    private final ParametresDocumentCandidatureRepository parametresDocumentCandidatureRepository;

    public ConsulterParametresDocumentCandidatureUseCase(
            ParametresDocumentCandidatureRepository parametresDocumentCandidatureRepository) {
        this.parametresDocumentCandidatureRepository = parametresDocumentCandidatureRepository;
    }

    public ParametresDocumentCandidature executer() {
        return parametresDocumentCandidatureRepository.recuperer();
    }
}
