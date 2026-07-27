package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.EtatOffre;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Demande de mise à jour groupée de l'état d'offres")
public record MettreAJourEtatRequest(
        @Schema(description = "Identifiants externes des offres à mettre à jour", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "La liste des identifiants externes ne doit pas être vide")
        List<String> idsExternes,

        @Schema(description = "Nouvel état à appliquer aux offres", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "L'état est obligatoire")
        EtatOffre etat
) {}
