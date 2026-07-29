package fr.sirene.jobtracker.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Positive;

@Schema(description = "Paramètres d'upload/téléchargement des CV")
public record ParametresCvRequest(
        @Schema(description = "Taille maximale autorisée pour un CV, en octets", example = "5242880",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Positive(message = "La taille maximale doit être strictement positive")
        long tailleMaxOctets
) {}
