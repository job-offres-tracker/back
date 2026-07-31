package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.ParametresDocumentCandidatureRepository;
import fr.sirene.jobtracker.domain.model.ParametresDocumentCandidature;

import org.springframework.stereotype.Service;

@Service
public class ModifierParametresDocumentCandidatureUseCase {

    private final ParametresDocumentCandidatureRepository parametresDocumentCandidatureRepository;

    public ModifierParametresDocumentCandidatureUseCase(
            ParametresDocumentCandidatureRepository parametresDocumentCandidatureRepository) {
        this.parametresDocumentCandidatureRepository = parametresDocumentCandidatureRepository;
    }

    public ParametresDocumentCandidature executer(long tailleMaxOctets) {
        ParametresDocumentCandidature parametres = new ParametresDocumentCandidature(tailleMaxOctets);
        return parametresDocumentCandidatureRepository.sauvegarder(parametres);
    }
}
