package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.Offre;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        Candidature candidature = Candidature.builder()
                .offre(offre)
                .dateCandidature(LocalDateTime.now())
                .build();
        candidatureRepository.sauvegarder(candidature);
    }
}
