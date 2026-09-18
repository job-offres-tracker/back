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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class ModifierStatutCandidatureUseCase {

    private final CandidatureRepository candidatureRepository;

    public ModifierStatutCandidatureUseCase(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    @Transactional
    public Candidature executer(Long id, String statut) {
        Candidature candidature = candidatureRepository.trouverParId(id)
                .orElseThrow(() -> new CandidatureNonTrouveeException(id));

        Candidature miseAJour = switch (candidature) {
            case CandidatureOffre co -> new CandidatureOffre(
                    co.id(), co.offre(), parseStatut(statut, StatutCandidatureOffre.class), co.dateCandidature(),
                    co.evenements(), co.documents());
            case CandidatureSpontanee cs -> new CandidatureSpontanee(
                    cs.id(), cs.nomEntreprise(), cs.urlEntreprise(), cs.typeEntreprise(),
                    parseStatut(statut, StatutCandidatureSpontanee.class), cs.dateCandidature(), cs.evenements(), cs.documents());
            case CandidaturePriseDeContact cp -> new CandidaturePriseDeContact(
                    cp.id(), cp.nomEntreprise(), cp.urlEntreprise(), cp.typeEntreprise(),
                    parseStatut(statut, StatutPriseDeContact.class), cp.dateCandidature(), cp.evenements(), cp.documents());
        };

        return candidatureRepository.mettreAJourStatut(miseAJour);
    }

    private <E extends Enum<E>> E parseStatut(String statut, Class<E> type) {
        if (statut == null) {
            throw new StatutCandidatureInvalideException(
                    "Statut manquant. Valeurs autorisées : %s".formatted(Arrays.toString(type.getEnumConstants())));
        }
        try {
            return Enum.valueOf(type, statut);
        } catch (IllegalArgumentException e) {
            throw new StatutCandidatureInvalideException(
                    "Statut '%s' invalide. Valeurs autorisées : %s".formatted(statut, Arrays.toString(type.getEnumConstants())), e);
        }
    }
}
