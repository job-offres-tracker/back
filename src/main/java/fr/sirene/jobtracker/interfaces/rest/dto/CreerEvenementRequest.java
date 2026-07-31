package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.TypeEvenement;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Demande de création ou modification d'un événement de candidature")
public record CreerEvenementRequest(
        @Schema(description = "Date de l'événement", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "La date est obligatoire")
        LocalDate date,

        @Schema(description = "Type d'événement", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Le type est obligatoire")
        TypeEvenement type,

        @Schema(description = "Description libre de l'événement")
        String description
) {}
