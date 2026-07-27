package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Lieu;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Schema(description = "Données de création d'une offre saisie manuellement")
public record CreerOffreRequest(
        @Schema(description = "Identifiant externe ; généré si absent", example = "123ABCD")
        String idExterne,

        @Schema(description = "Intitulé du poste", example = "Développeur Java Back-end H/F", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "L'intitulé est obligatoire")
        String intitule,

        @Schema(description = "Description complète de l'offre")
        String description,

        @Schema(description = "Nom de l'entreprise qui recrute", example = "Acme SAS")
        String entreprise,

        @Schema(description = "Lieu de travail")
        Lieu lieu,

        @Schema(description = "Type de contrat", example = "CDI")
        String typeContrat,

        @Schema(description = "Salaire tel que communiqué", example = "45 000 - 55 000 € / an")
        String salaire,

        @Schema(description = "URL de l'offre sur le site d'origine")
        String urlOrigine,

        @Schema(description = "Date de création de l'offre ; date courante utilisée si absente")
        LocalDateTime dateCreation,

        @Schema(description = "Origine de l'offre ; \"MANUELLE\" par défaut", example = "MANUELLE")
        String provenance,

        @Schema(description = "État initial de l'offre ; \"NON_LU\" par défaut si absent")
        EtatOffre etat
) {}
