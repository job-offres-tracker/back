package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.CommuneRecherche;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Paramètres de recherche utilisés pour la synchronisation des offres")
public record ParametresRechercheRequest(
        @Schema(description = "Mots-clés de recherche (une entrée = un appel de recherche distinct)",
                example = "[\"Java, Back-end\", \"lead tech\"]")
        List<String> motsCles,

        @ArraySchema(arraySchema = @Schema(description = "Communes ciblées"), maxItems = 20)
        @Size(max = 20, message = "Le nombre de communes ne peut pas dépasser 20")
        List<CommuneRecherche> communes,

        @Schema(description = "Type de contrat recherché", example = "CDI")
        String typeContrat
) {}
