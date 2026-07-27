package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.OffreEmploiApiPort;
import fr.sirene.jobtracker.application.port.OffreStorageRepository;
import fr.sirene.jobtracker.domain.model.CritereRecherche;
import fr.sirene.jobtracker.domain.model.Offre;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SynchroniserOffresUseCase {

    private final OffreEmploiApiPort offreEmploiApiPort;
    private final OffreStorageRepository offreStorageRepository;
    private final RechercheOffresProperties rechercheOffresProperties;
    private final AdresseEnrichisseur adresseEnrichisseur;

    public SynchroniserOffresUseCase(
            OffreEmploiApiPort offreEmploiApiPort,
            OffreStorageRepository offreStorageRepository,
            RechercheOffresProperties rechercheOffresProperties,
            AdresseEnrichisseur adresseEnrichisseur) {
        this.offreEmploiApiPort = offreEmploiApiPort;
        this.offreStorageRepository = offreStorageRepository;
        this.rechercheOffresProperties = rechercheOffresProperties;
        this.adresseEnrichisseur = adresseEnrichisseur;
    }

    public int executer() {
        Map<String, Offre> offresParIdExterne = new LinkedHashMap<>();

        for (String motsCles : rechercheOffresProperties.motsCles()) {
            CritereRecherche critere = new CritereRecherche(
                    motsCles,
                    rechercheOffresProperties.typeContrat(),
                    rechercheOffresProperties.codeCommune());

            for (Offre offre : offreEmploiApiPort.rechercherOffres(critere)) {
                offresParIdExterne.put(offre.getIdExterne(), offre);
            }
        }

        List<Offre> offres = offresParIdExterne.values().stream()
                .map(adresseEnrichisseur::enrichir)
                .toList();
        offreStorageRepository.sauvegarderTout(offres);
        return offres.size();
    }
}
