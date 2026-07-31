package fr.sirene.jobtracker.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(description = "CV à utiliser pour générer la lettre de motivation")
public record GenererLettreMotivationRequest(
        @Schema(description = "Nom unique du CV (paramétrage CV)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Le nom unique du CV est obligatoire")
        String cvNomUnique
) {}
