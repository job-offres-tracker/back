package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.exception.EvenementNonTrouveException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.Evenement;
import fr.sirene.jobtracker.domain.model.TypeEvenement;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ModifierEvenementCandidatureUseCase {

    private final CandidatureRepository candidatureRepository;

    public ModifierEvenementCandidatureUseCase(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    public Evenement executer(Long candidatureId, Long evenementId, LocalDate date, TypeEvenement type, String description) {
        Candidature candidature = candidatureRepository.trouverParId(candidatureId)
                .orElseThrow(() -> new CandidatureNonTrouveeException(candidatureId));
        boolean evenementExiste = candidature.getEvenements().stream().anyMatch(e -> e.getId().equals(evenementId));
        if (!evenementExiste) {
            throw new EvenementNonTrouveException(candidatureId, evenementId);
        }

        Evenement evenement = Evenement.builder()
                .date(date)
                .type(type)
                .description(description)
                .build();
        return candidatureRepository.modifierEvenement(candidatureId, evenementId, evenement);
    }
}
