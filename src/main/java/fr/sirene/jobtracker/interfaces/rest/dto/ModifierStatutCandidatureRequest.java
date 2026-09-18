package fr.sirene.jobtracker.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requête de modification du statut d'une candidature")
public record ModifierStatutCandidatureRequest(
        @Schema(description = "Nouveau statut, dont les valeurs autorisées dépendent du type de la candidature "
                + "(StatutCandidatureOffre, StatutCandidatureSpontanee ou StatutPriseDeContact)", example = "ACCEPTE")
        @NotBlank
        String statut
) {
}
