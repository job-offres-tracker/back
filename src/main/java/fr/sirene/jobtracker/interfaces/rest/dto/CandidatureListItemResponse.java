package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidatureOffre;
import fr.sirene.jobtracker.domain.model.CandidaturePriseDeContact;
import fr.sirene.jobtracker.domain.model.CandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Lieu;
import fr.sirene.jobtracker.domain.model.StatutCandidatureOffre;
import fr.sirene.jobtracker.domain.model.StatutCandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.StatutPriseDeContact;
import fr.sirene.jobtracker.domain.model.TypeCandidature;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Élément de la liste paginée des candidatures")
public record CandidatureListItemResponse(
        @Schema(description = "Identifiant de la candidature")
        Long id,

        @Schema(description = "Type de candidature")
        TypeCandidature type,

        @Schema(description = "Identifiant externe de l'offre liée (uniquement pour le type OFFRE)")
        String idExterne,

        @Schema(description = "Intitulé du poste (uniquement pour le type OFFRE)")
        String intitule,

        @Schema(description = "État actuel de l'offre liée (uniquement pour le type OFFRE)")
        EtatOffre etat,

        @Schema(description = "Nom de l'entreprise")
        String entreprise,

        @Schema(description = "Lieu de travail (uniquement pour le type OFFRE)")
        Lieu lieu,

        @Schema(description = "Statut de la candidature liée à une offre (uniquement pour le type OFFRE)")
        StatutCandidatureOffre statutCandidatureOffre,

        @Schema(description = "Statut de la candidature spontanée (uniquement pour le type SPONTANEE)")
        StatutCandidatureSpontanee statutCandidatureSpontanee,

        @Schema(description = "Statut de la prise de contact (uniquement pour le type PRISE_DE_CONTACT)")
        StatutPriseDeContact statutPriseDeContact,

        @Schema(description = "Date de création de la candidature")
        LocalDateTime dateCandidature
) {
    public static CandidatureListItemResponse fromDomain(Candidature candidature) {
        return switch (candidature) {
            case CandidatureOffre co -> new CandidatureListItemResponse(
                    co.id(), TypeCandidature.OFFRE, co.offre().getIdExterne(), co.offre().getIntitule(),
                    co.offre().getEtat(), co.offre().getEntreprise(), co.offre().getLieu(), co.statut(), null, null,
                    co.dateCandidature());
            case CandidatureSpontanee cs -> new CandidatureListItemResponse(
                    cs.id(), TypeCandidature.SPONTANEE, null, null, null, cs.nomEntreprise(), null, null,
                    cs.statut(), null, cs.dateCandidature());
            case CandidaturePriseDeContact cp -> new CandidatureListItemResponse(
                    cp.id(), TypeCandidature.PRISE_DE_CONTACT, null, null, null, cp.nomEntreprise(), null, null,
                    null, cp.statut(), cp.dateCandidature());
        };
    }
}
