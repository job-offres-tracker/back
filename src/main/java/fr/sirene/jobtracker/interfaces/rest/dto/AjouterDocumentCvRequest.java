package fr.sirene.jobtracker.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(description = "Demande d'attachement d'un CV existant à une candidature")
public record AjouterDocumentCvRequest(
        @Schema(description = "Nom unique du CV (paramétrage CV)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Le nom unique du CV est obligatoire")
        String cvNomUnique
) {}
