package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Lieu;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Élément de la liste paginée des candidatures")
public record CandidatureListItemResponse(
        @Schema(description = "Identifiant de la candidature")
        Long id,

        @Schema(description = "Identifiant externe de l'offre liée")
        String idExterne,

        @Schema(description = "Intitulé du poste")
        String intitule,

        @Schema(description = "État actuel de l'offre liée")
        EtatOffre etat,

        @Schema(description = "Nom de l'entreprise")
        String entreprise,

        @Schema(description = "Lieu de travail")
        Lieu lieu,

        @Schema(description = "Date de création de la candidature")
        LocalDateTime dateCandidature
) {
    public static CandidatureListItemResponse fromDomain(Candidature candidature) {
        return new CandidatureListItemResponse(
                candidature.getId(),
                candidature.getOffre().getIdExterne(),
                candidature.getOffre().getIntitule(),
                candidature.getOffre().getEtat(),
                candidature.getOffre().getEntreprise(),
                candidature.getOffre().getLieu(),
                candidature.getDateCandidature());
    }
}
