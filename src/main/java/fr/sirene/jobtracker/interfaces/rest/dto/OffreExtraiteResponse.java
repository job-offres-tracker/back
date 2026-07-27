package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.BrouillonOffre;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Champs d'offre extraits automatiquement depuis une page web, à valider avant création")
public record OffreExtraiteResponse(
        @Schema(description = "Intitulé du poste extrait", example = "Développeur Java Back-end H/F")
        String intitule,

        @Schema(description = "Description extraite de l'offre")
        String description,

        @Schema(description = "Nom de l'entreprise extrait", example = "Acme SAS")
        String entreprise,

        @Schema(description = "Libellé du lieu de travail extrait", example = "Nantes")
        String lieuLibelle,

        @Schema(description = "Type de contrat extrait", example = "CDI")
        String typeContrat,

        @Schema(description = "Salaire extrait tel qu'indiqué sur la page")
        String salaire,

        @Schema(description = "URL d'origine de l'offre importée")
        String urlOrigine,

        @Schema(description = "Provenance suggérée pour l'offre", example = "HELLOWORK")
        String provenance,

        @Schema(description = "Référence externe de l'offre extraite (numéro/réf. affiché sur la page)", example = "12345")
        String referenceExterne,

        @Schema(description = "Date de publication extraite, au format AAAA-MM-JJ si déterminable", example = "2026-07-20")
        String datePublication
) {
    public static OffreExtraiteResponse fromDomain(BrouillonOffre brouillon, String provenance) {
        return new OffreExtraiteResponse(
                brouillon.intitule(),
                brouillon.description(),
                brouillon.entreprise(),
                brouillon.lieuLibelle(),
                brouillon.typeContrat(),
                brouillon.salaire(),
                brouillon.urlOrigine(),
                provenance,
                brouillon.referenceExterne(),
                brouillon.datePublication());
    }
}
