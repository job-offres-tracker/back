package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Candidature;

import org.springframework.stereotype.Service;

@Service
public class ConsulterCandidatureUseCase {

    private final CandidatureRepository candidatureRepository;

    public ConsulterCandidatureUseCase(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    public Candidature executer(Long id) {
        return candidatureRepository.trouverParId(id)
                .orElseThrow(() -> new CandidatureNonTrouveeException(id));
    }
}
