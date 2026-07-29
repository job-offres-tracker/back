package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.CommuneRecherche;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Paramètres de recherche utilisés pour la synchronisation des offres")
public record ParametresRechercheRequest(
        @Schema(description = "Mots-clés de recherche (une entrée = un appel de recherche distinct)",
                example = "[\"Java, Back-end\", \"lead tech\"]")
        List<String> motsCles,

        @Schema(description = "Communes ciblées (5 maximum)")
        @Size(max = 5, message = "5 communes maximum")
        List<CommuneRecherche> communes,

        @Schema(description = "Type de contrat recherché", example = "CDI")
        String typeContrat
) {}
