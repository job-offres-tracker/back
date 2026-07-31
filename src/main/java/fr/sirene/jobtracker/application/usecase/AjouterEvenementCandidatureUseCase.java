package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Evenement;
import fr.sirene.jobtracker.domain.model.TypeEvenement;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AjouterEvenementCandidatureUseCase {

    private final CandidatureRepository candidatureRepository;

    public AjouterEvenementCandidatureUseCase(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    public Evenement executer(Long candidatureId, LocalDate date, TypeEvenement type, String description) {
        candidatureRepository.trouverParId(candidatureId)
                .orElseThrow(() -> new CandidatureNonTrouveeException(candidatureId));

        Evenement evenement = Evenement.builder()
                .date(date)
                .type(type)
                .description(description)
                .build();
        return candidatureRepository.ajouterEvenement(candidatureId, evenement);
    }
}
