package fr.sirene.jobtracker.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Lieu;
import fr.sirene.jobtracker.domain.model.Offre;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Représentation d'une offre d'emploi")
public record OffreResponse(
        @Schema(description = "Identifiant externe (France Travail ou saisi manuellement)", example = "123ABCD")
        String idExterne,

        @Schema(description = "Intitulé du poste", example = "Développeur Java Back-end H/F")
        String intitule,

        @Schema(description = "Description complète de l'offre")
        String description,

        @Schema(description = "Nom de l'entreprise qui recrute", example = "Acme SAS")
        String entreprise,

        @Schema(description = "Lieu de travail")
        Lieu lieu,

        @Schema(description = "Type de contrat", example = "CDI")
        String typeContrat,

        @Schema(description = "Salaire tel que communiqué par la source", example = "45 000 - 55 000 € / an")
        String salaire,

        @Schema(description = "URL de l'offre sur le site d'origine")
        String urlOrigine,

        @Schema(description = "Date de création de l'offre")
        LocalDateTime dateCreation,

        @Schema(description = "État de suivi de la candidature")
        EtatOffre etat,

        @Schema(description = "Origine de l'offre", example = "FRANCE_TRAVAIL")
        String provenance
) {
    public static OffreResponse fromDomain(Offre offre) {
        return new OffreResponse(
                offre.getIdExterne(),
                offre.getIntitule(),
                offre.getDescription(),
                offre.getEntreprise(),
                offre.getLieu(),
                offre.getTypeContrat(),
                offre.getSalaire(),
                offre.getUrlOrigine(),
                offre.getDateCreation(),
                offre.getEtat(),
                offre.getProvenance()
        );
    }
}
