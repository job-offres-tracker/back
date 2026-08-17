package fr.sirene.jobtracker.infrastructure.francetravail;

import fr.sirene.jobtracker.application.port.offre.OffreEmploiApiPort;
import fr.sirene.jobtracker.domain.model.CritereRecherche;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.infrastructure.francetravail.client.FranceTravailApiClient;
import fr.sirene.jobtracker.infrastructure.francetravail.client.FranceTravailAuthClient;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.OffreFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.ReponseRechercheFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.mapper.OffreMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FranceTravailOffreEmploiAdapter implements OffreEmploiApiPort {

    private static final int TAILLE_PAGE = 50;

    private final FranceTravailAuthClient authClient;
    private final FranceTravailApiClient apiClient;
    private final OffreMapper mapper;

    public FranceTravailOffreEmploiAdapter(
            FranceTravailAuthClient authClient, FranceTravailApiClient apiClient, OffreMapper mapper) {
        this.authClient = authClient;
        this.apiClient = apiClient;
        this.mapper = mapper;
    }

    @Override
    public List<Offre> rechercherOffres(CritereRecherche critere) {
        List<Offre> resultat = new ArrayList<>();
        int debut = 0;
        long total = Long.MAX_VALUE;

        while (debut < total) {
            int fin = debut + TAILLE_PAGE - 1;
            String token = authClient.obtenirToken();

            ResponseEntity<ReponseRechercheFranceTravail> reponse =
                    apiClient.rechercherOffres(
                            critere.motsCles(), critere.typeContrat(), critere.codeCommune(), debut, fin, token);

            ReponseRechercheFranceTravail corps = reponse.getBody();
            List<OffreFranceTravail> offresPage = corps != null ? corps.resultats() : null;
            if (offresPage == null || offresPage.isEmpty()) {
                break;
            }

            resultat.addAll(mapper.toDomainList(offresPage));
            total = extraireTotal(reponse.getHeaders().getFirst("Content-Range"), resultat.size());
            debut += TAILLE_PAGE;
        }

        return resultat;
    }

    private long extraireTotal(String contentRange, int tailleActuelle) {
        if (contentRange == null) {
            return tailleActuelle;
        }
        int indexSlash = contentRange.lastIndexOf('/');
        if (indexSlash == -1) {
            return tailleActuelle;
        }
        try {
            return Long.parseLong(contentRange.substring(indexSlash + 1));
        } catch (NumberFormatException e) {
            return tailleActuelle;
        }
    }
}
