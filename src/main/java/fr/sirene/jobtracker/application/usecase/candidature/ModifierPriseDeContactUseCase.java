package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidaturePriseDeContact;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModifierPriseDeContactUseCase {

    private final CandidatureRepository candidatureRepository;

    public ModifierPriseDeContactUseCase(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    @Transactional
    public Candidature executer(long id, String urlEntreprise, String poste, String client) {
        Candidature candidature = candidatureRepository.trouverParId(id)
                .orElseThrow(() -> new CandidatureNonTrouveeException(id));

        if (!(candidature instanceof CandidaturePriseDeContact cp)) {
            throw new IllegalArgumentException(
                    "La candidature %d n'est pas une prise de contact".formatted(id));
        }

        Candidature miseAJour = cp.toBuilder()
                .urlEntreprise(urlEntreprise)
                .poste(poste)
                .client(client)
                .build();

        return candidatureRepository.sauvegarder(miseAJour);
    }
}
