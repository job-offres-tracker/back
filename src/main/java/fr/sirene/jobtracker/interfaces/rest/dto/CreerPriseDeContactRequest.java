package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.StatutPriseDeContact;
import fr.sirene.jobtracker.domain.model.TypeEntreprise;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Données de création d'une prise de contact (chasseur de tête ou entreprise)")
public record CreerPriseDeContactRequest(
        @Schema(description = "Nom de l'entreprise", example = "Acme SAS", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Le nom de l'entreprise est obligatoire")
        String nomEntreprise,

        @Schema(description = "URL du site de l'entreprise")
        String urlEntreprise,

        @Schema(description = "Type de l'entreprise", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Le type de l'entreprise est obligatoire")
        TypeEntreprise typeEntreprise,

        @Schema(description = "Statut initial ; \"ETABLI\" par défaut si absent")
        StatutPriseDeContact statut,

        @Schema(description = "Date de la prise de contact ; date courante utilisée si absente")
        LocalDateTime dateCandidature
) {}
