package fr.sirene.jobtracker.application.usecase.offre;

import fr.sirene.jobtracker.application.port.offre.OffreEmploiApiPort;
import fr.sirene.jobtracker.application.port.offre.OffreStorageRepository;
import fr.sirene.jobtracker.application.port.parametres.ParametresRechercheRepository;
import fr.sirene.jobtracker.domain.exception.ParametresRechercheNonConfiguresException;
import fr.sirene.jobtracker.domain.model.CommuneRecherche;
import fr.sirene.jobtracker.domain.model.CritereRecherche;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.ParametresRecherche;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SynchroniserOffresUseCase {

    private final OffreEmploiApiPort offreEmploiApiPort;
    private final OffreStorageRepository offreStorageRepository;
    private final ParametresRechercheRepository parametresRechercheRepository;
    private final AdresseEnrichisseur adresseEnrichisseur;

    public SynchroniserOffresUseCase(
            OffreEmploiApiPort offreEmploiApiPort,
            OffreStorageRepository offreStorageRepository,
            ParametresRechercheRepository parametresRechercheRepository,
            AdresseEnrichisseur adresseEnrichisseur) {
        this.offreEmploiApiPort = offreEmploiApiPort;
        this.offreStorageRepository = offreStorageRepository;
        this.parametresRechercheRepository = parametresRechercheRepository;
        this.adresseEnrichisseur = adresseEnrichisseur;
    }

    public int executer() {
        ParametresRecherche parametres = parametresRechercheRepository.recuperer();
        if (parametres.motsCles().isEmpty() || parametres.communes().isEmpty()
                || parametres.typeContrat() == null || parametres.typeContrat().isBlank()) {
            throw new ParametresRechercheNonConfiguresException(
                    "Impossible de synchroniser : mots-clés, communes et type de contrat doivent être configurés. "
                            + "Configurez-les via PUT /api/v1/parametres/recherche");
        }
        String codeCommune = parametres.communes().stream()
                .map(CommuneRecherche::codeInsee)
                .collect(Collectors.joining(","));

        Map<String, Offre> offresParIdExterne = new LinkedHashMap<>();

        for (String motsCles : parametres.motsCles()) {
            CritereRecherche critere = new CritereRecherche(
                    motsCles,
                    parametres.typeContrat(),
                    codeCommune);

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
