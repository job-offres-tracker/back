package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.OffreStorageRepository;
import fr.sirene.jobtracker.domain.exception.TransitionEtatInvalideException;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Offre;

import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class MettreAJourEtatOffresUseCase {

    private static final Set<EtatOffre> ETATS_POST_CANDIDATURE =
            EnumSet.of(EtatOffre.POSTULE, EtatOffre.ENTRETIEN, EtatOffre.ACCEPTE, EtatOffre.RECALE);
    private static final Set<EtatOffre> ETATS_CIBLE_INTERDITS = EnumSet.of(EtatOffre.NON_LU, EtatOffre.LU);

    private final OffreStorageRepository offreStorageRepository;
    private final CandidatureAutoCreationService candidatureAutoCreationService;

    public MettreAJourEtatOffresUseCase(
            OffreStorageRepository offreStorageRepository,
            CandidatureAutoCreationService candidatureAutoCreationService) {
        this.offreStorageRepository = offreStorageRepository;
        this.candidatureAutoCreationService = candidatureAutoCreationService;
    }

    public void executer(List<String> idsExternes, EtatOffre etat) {
        List<Offre> offres = offreStorageRepository.trouverParIdsExternes(idsExternes);

        if (ETATS_CIBLE_INTERDITS.contains(etat)) {
            List<String> idsInvalides = offres.stream()
                    .filter(offre -> ETATS_POST_CANDIDATURE.contains(offre.getEtat()))
                    .map(Offre::getIdExterne)
                    .toList();
            if (!idsInvalides.isEmpty()) {
                throw new TransitionEtatInvalideException(idsInvalides, etat);
            }
        }

        offreStorageRepository.mettreAJourEtat(idsExternes, etat);

        if (etat == EtatOffre.POSTULE) {
            offres.forEach(candidatureAutoCreationService::assurer);
        }
    }
}
