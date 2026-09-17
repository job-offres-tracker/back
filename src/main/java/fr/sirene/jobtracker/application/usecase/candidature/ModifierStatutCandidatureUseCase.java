package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.exception.StatutCandidatureInvalideException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidatureOffre;
import fr.sirene.jobtracker.domain.model.CandidaturePriseDeContact;
import fr.sirene.jobtracker.domain.model.CandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.StatutCandidatureOffre;
import fr.sirene.jobtracker.domain.model.StatutCandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.StatutPriseDeContact;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ModifierStatutCandidatureUseCase {

    private final CandidatureRepository candidatureRepository;

    public ModifierStatutCandidatureUseCase(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    public Candidature executer(Long id, String statut) {
        Candidature candidature = candidatureRepository.trouverParId(id)
                .orElseThrow(() -> new CandidatureNonTrouveeException(id));

        Candidature miseAJour = switch (candidature) {
            case CandidatureOffre co -> new CandidatureOffre(
                    co.id(), co.offre(), parseStatutOffre(statut), co.dateCandidature(), co.evenements(), co.documents());
            case CandidatureSpontanee cs -> new CandidatureSpontanee(
                    cs.id(), cs.nomEntreprise(), cs.urlEntreprise(), cs.typeEntreprise(), parseStatutSpontanee(statut),
                    cs.dateCandidature(), cs.evenements(), cs.documents());
            case CandidaturePriseDeContact cp -> new CandidaturePriseDeContact(
                    cp.id(), cp.nomEntreprise(), cp.urlEntreprise(), cp.typeEntreprise(), parseStatutPriseDeContact(statut),
                    cp.dateCandidature(), cp.evenements(), cp.documents());
        };

        return candidatureRepository.sauvegarder(miseAJour);
    }

    private StatutCandidatureOffre parseStatutOffre(String statut) {
        try {
            return StatutCandidatureOffre.valueOf(statut);
        } catch (IllegalArgumentException e) {
            throw new StatutCandidatureInvalideException(messageInvalide(statut, StatutCandidatureOffre.values()), e);
        }
    }

    private StatutCandidatureSpontanee parseStatutSpontanee(String statut) {
        try {
            return StatutCandidatureSpontanee.valueOf(statut);
        } catch (IllegalArgumentException e) {
            throw new StatutCandidatureInvalideException(messageInvalide(statut, StatutCandidatureSpontanee.values()), e);
        }
    }

    private StatutPriseDeContact parseStatutPriseDeContact(String statut) {
        try {
            return StatutPriseDeContact.valueOf(statut);
        } catch (IllegalArgumentException e) {
            throw new StatutCandidatureInvalideException(messageInvalide(statut, StatutPriseDeContact.values()), e);
        }
    }

    private String messageInvalide(String statut, Enum<?>[] valeursAutorisees) {
        return "Statut '%s' invalide. Valeurs autorisées : %s".formatted(statut, Arrays.toString(valeursAutorisees));
    }
}
