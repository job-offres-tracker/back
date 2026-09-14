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
        Candidature candidature = new CandidatureSpontanee(
                null,
                nomEntreprise,
                urlEntreprise,
                typeEntreprise,
                statut != null ? statut : StatutCandidatureSpontanee.ENVOYE,
                dateCandidature != null ? dateCandidature : LocalDateTime.now(),
                List.of(),
                List.of());
        return candidatureRepository.sauvegarder(candidature);
    }

    public Candidature creerPriseDeContact(
            String nomEntreprise, String urlEntreprise, TypeEntreprise typeEntreprise,
            StatutPriseDeContact statut, LocalDateTime dateCandidature) {
        Candidature candidature = new CandidaturePriseDeContact(
                null,
                nomEntreprise,
                urlEntreprise,
                typeEntreprise,
                statut != null ? statut : StatutPriseDeContact.ETABLI,
                dateCandidature != null ? dateCandidature : LocalDateTime.now(),
                List.of(),
                List.of());
        return candidatureRepository.sauvegarder(candidature);
    }
}
