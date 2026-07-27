package fr.sirene.jobtracker.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.URL;

@Schema(description = "URL de la page d'offre d'emploi à importer")
public record ImporterOffreRequest(
        @Schema(description = "URL de l'offre (ex. HelloWork)", example = "https://www.hellowork.com/fr-fr/emplois/12345.html", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "L'URL est obligatoire")
        @URL(message = "L'URL fournie n'est pas valide")
        String url
) {}
