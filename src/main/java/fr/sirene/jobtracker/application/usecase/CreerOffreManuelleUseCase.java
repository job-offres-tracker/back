package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.OffreStorageRepository;
import fr.sirene.jobtracker.domain.exception.OffreDejaExistanteException;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Lieu;
import fr.sirene.jobtracker.domain.model.Offre;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CreerOffreManuelleUseCase {

    private static final String PROVENANCE_PAR_DEFAUT = "MANUELLE";

    private final OffreStorageRepository offreStorageRepository;
    private final CandidatureAutoCreationService candidatureAutoCreationService;

    public CreerOffreManuelleUseCase(
            OffreStorageRepository offreStorageRepository,
            CandidatureAutoCreationService candidatureAutoCreationService) {
        this.offreStorageRepository = offreStorageRepository;
        this.candidatureAutoCreationService = candidatureAutoCreationService;
    }

    public Offre executer(
            String idExterne,
            String intitule,
            String description,
            String entreprise,
            Lieu lieu,
            String typeContrat,
            String salaire,
            String urlOrigine,
            LocalDateTime dateCreation,
            String provenance,
            EtatOffre etat) {

        String idExterneRetenu = idExterne != null && !idExterne.isBlank()
                ? idExterne
                : "MANUEL-" + UUID.randomUUID();

        if (offreStorageRepository.trouverParIdExterne(idExterneRetenu).isPresent()) {
            throw new OffreDejaExistanteException(idExterneRetenu);
        }

        Offre offre = Offre.builder()
                .idExterne(idExterneRetenu)
                .intitule(intitule)
                .description(description)
                .entreprise(entreprise)
                .lieu(lieu)
                .typeContrat(typeContrat)
                .salaire(salaire)
                .urlOrigine(urlOrigine)
                .dateCreation(dateCreation != null ? dateCreation : LocalDateTime.now())
                .provenance(provenance != null && !provenance.isBlank() ? provenance : PROVENANCE_PAR_DEFAUT)
                .etat(etat != null ? etat : EtatOffre.NON_LU)
                .build();

        offreStorageRepository.sauvegarderTout(List.of(offre));
        if (offre.getEtat() == EtatOffre.POSTULE) {
            candidatureAutoCreationService.assurer(offre);
        }
        return offre;
    }
}
