package fr.sirene.jobtracker.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Positive;

@Schema(description = "Paramètres d'upload des documents de candidature")
public record ParametresDocumentCandidatureRequest(
        @Schema(description = "Taille maximale autorisée pour un document, en octets", example = "10485760",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Positive(message = "La taille maximale doit être strictement positive")
        long tailleMaxOctets
) {}
