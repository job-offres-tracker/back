package fr.sirene.jobtracker.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lettre de motivation générée pour une offre et un CV donnés")
public record LettreMotivationResponse(
        @Schema(description = "Texte de la lettre de motivation générée")
        String contenu
) {}
