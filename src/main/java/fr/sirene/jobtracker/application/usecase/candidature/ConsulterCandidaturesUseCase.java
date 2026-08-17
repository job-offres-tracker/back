package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.ResultatPagine;

import org.springframework.stereotype.Service;

@Service
public class ConsulterCandidaturesUseCase {

    private final CandidatureRepository candidatureRepository;

    public ConsulterCandidaturesUseCase(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    public ResultatPagine<Candidature> executer(int page, int taille) {
        return candidatureRepository.lister(page, taille);
    }
}
