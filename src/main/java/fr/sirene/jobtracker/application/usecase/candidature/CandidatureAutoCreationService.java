package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidatureOffre;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.StatutCandidatureOffre;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CandidatureAutoCreationService {

    private final CandidatureRepository candidatureRepository;

    public CandidatureAutoCreationService(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    public void assurer(Offre offre) {
        if (candidatureRepository.existeParOffreIdExterne(offre.getIdExterne())) {
            return;
        }
        Candidature candidature = new CandidatureOffre(
                null, offre, StatutCandidatureOffre.POSTULE, LocalDateTime.now(), List.of(), List.of());
        candidatureRepository.sauvegarder(candidature);
    }
}
