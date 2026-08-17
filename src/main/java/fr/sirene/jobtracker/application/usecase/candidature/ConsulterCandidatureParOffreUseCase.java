package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Candidature;

import org.springframework.stereotype.Service;

@Service
public class ConsulterCandidatureParOffreUseCase {

    private final CandidatureRepository candidatureRepository;

    public ConsulterCandidatureParOffreUseCase(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    public Candidature executer(String idExterneOffre) {
        return candidatureRepository.trouverParOffreIdExterne(idExterneOffre)
                .orElseThrow(() -> new CandidatureNonTrouveeException(idExterneOffre));
    }
}
