package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.StatutCandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.TypeEntreprise;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Données de création d'une candidature spontanée")
public record CreerCandidatureSpontaneeRequest(
        @Schema(description = "Nom de l'entreprise", example = "Acme SAS", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Le nom de l'entreprise est obligatoire")
        String nomEntreprise,

        @Schema(description = "URL du site de l'entreprise")
        String urlEntreprise,

        @Schema(description = "Type de l'entreprise", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Le type de l'entreprise est obligatoire")
        TypeEntreprise typeEntreprise,

        @Schema(description = "Statut initial ; \"ENVOYE\" par défaut si absent")
        StatutCandidatureSpontanee statut,

        @Schema(description = "Date de la candidature ; date courante utilisée si absente")
        LocalDateTime dateCandidature
) {}
