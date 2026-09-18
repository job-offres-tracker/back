package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidaturePriseDeContact;
import fr.sirene.jobtracker.domain.model.CandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.StatutCandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.StatutPriseDeContact;
import fr.sirene.jobtracker.domain.model.TypeEntreprise;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CreerCandidatureUseCase {

    private final CandidatureRepository candidatureRepository;

    public CreerCandidatureUseCase(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    public Candidature creerSpontanee(
            String nomEntreprise, String urlEntreprise, TypeEntreprise typeEntreprise,
            StatutCandidatureSpontanee statut, LocalDateTime dateCandidature) {
        Candidature candidature = CandidatureSpontanee.builder()
                .nomEntreprise(nomEntreprise)
                .urlEntreprise(urlEntreprise)
                .typeEntreprise(typeEntreprise)
                .statut(statut != null ? statut : StatutCandidatureSpontanee.ENVOYE)
                .dateCandidature(dateCandidature != null ? dateCandidature : LocalDateTime.now())
                .evenements(List.of())
                .documents(List.of())
                .build();
        return candidatureRepository.sauvegarder(candidature);
    }

    public Candidature creerPriseDeContact(
            String nomEntreprise, String urlEntreprise, TypeEntreprise typeEntreprise,
            StatutPriseDeContact statut, LocalDateTime dateCandidature) {
        Candidature candidature = CandidaturePriseDeContact.builder()
                .nomEntreprise(nomEntreprise)
                .urlEntreprise(urlEntreprise)
                .typeEntreprise(typeEntreprise)
                .statut(statut != null ? statut : StatutPriseDeContact.ETABLI)
                .dateCandidature(dateCandidature != null ? dateCandidature : LocalDateTime.now())
                .evenements(List.of())
                .documents(List.of())
                .build();
        return candidatureRepository.sauvegarder(candidature);
    }
}
