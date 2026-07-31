package fr.sirene.jobtracker.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Demande d'ajout d'un document texte libre à une candidature")
public record AjouterDocumentTexteRequest(
        @Schema(description = "Libellé du document", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Le libellé est obligatoire")
        String libelle,

        @Schema(description = "Contenu texte du document", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Le contenu est obligatoire")
        @Size(max = 10_000, message = "Le contenu ne doit pas dépasser 10 000 caractères")
        String contenu
) {}
