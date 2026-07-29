package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.CommuneRecherche;
import fr.sirene.jobtracker.domain.model.ParametresRecherche;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paramètres de recherche utilisés pour la synchronisation des offres")
public record ParametresRechercheResponse(
        @Schema(description = "Mots-clés de recherche (une entrée = un appel de recherche distinct)")
        List<String> motsCles,

        @Schema(description = "Communes ciblées (5 maximum)")
        List<CommuneRecherche> communes,

        @Schema(description = "Type de contrat recherché", example = "CDI")
        String typeContrat
) {
    public static ParametresRechercheResponse fromDomain(ParametresRecherche parametres) {
        return new ParametresRechercheResponse(parametres.motsCles(), parametres.communes(), parametres.typeContrat());
    }
}
