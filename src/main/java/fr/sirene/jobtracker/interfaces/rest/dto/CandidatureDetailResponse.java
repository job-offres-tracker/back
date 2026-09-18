package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidatureOffre;
import fr.sirene.jobtracker.domain.model.CandidaturePriseDeContact;
import fr.sirene.jobtracker.domain.model.CandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.StatutCandidatureOffre;
import fr.sirene.jobtracker.domain.model.StatutCandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.StatutPriseDeContact;
import fr.sirene.jobtracker.domain.model.TypeCandidature;
import fr.sirene.jobtracker.domain.model.TypeEntreprise;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Détail d'une candidature")
public record CandidatureDetailResponse(
        @Schema(description = "Identifiant de la candidature")
        Long id,

        @Schema(description = "Type de candidature")
        TypeCandidature type,

        @Schema(description = "Date de création de la candidature")
        LocalDateTime dateCandidature,

        @Schema(description = "Offre liée à la candidature (uniquement pour le type OFFRE)")
        OffreResponse offre,

        @Schema(description = "Statut de la candidature liée à une offre (uniquement pour le type OFFRE)")
        StatutCandidatureOffre statutCandidatureOffre,

        @Schema(description = "Nom de l'entreprise (uniquement pour SPONTANEE/PRISE_DE_CONTACT)")
        String nomEntreprise,

        @Schema(description = "URL du site de l'entreprise (uniquement pour SPONTANEE/PRISE_DE_CONTACT)")
        String urlEntreprise,

        @Schema(description = "Type de l'entreprise (uniquement pour SPONTANEE/PRISE_DE_CONTACT)")
        TypeEntreprise typeEntreprise,

        @Schema(description = "Statut de la candidature spontanée (uniquement pour le type SPONTANEE)")
        StatutCandidatureSpontanee statutCandidatureSpontanee,

        @Schema(description = "Statut de la prise de contact (uniquement pour le type PRISE_DE_CONTACT)")
        StatutPriseDeContact statutPriseDeContact,

        @Schema(description = "Événements de la candidature, du plus ancien au plus récent")
        List<EvenementResponse> evenements,

        @Schema(description = "Documents attachés à la candidature")
        List<DocumentCandidatureResponse> documents
) {
    public static CandidatureDetailResponse fromDomain(Candidature candidature) {
        List<EvenementResponse> evenements = candidature.getEvenements().stream().map(EvenementResponse::fromDomain).toList();
        List<DocumentCandidatureResponse> documents = candidature.getDocuments().stream().map(DocumentCandidatureResponse::fromDomain).toList();
        return switch (candidature) {
            case CandidatureOffre co -> new CandidatureDetailResponse(
                    co.getId(), TypeCandidature.OFFRE, co.getDateCandidature(), OffreResponse.fromDomain(co.getOffre()), co.getStatut(),
                    null, null, null, null, null, evenements, documents);
            case CandidatureSpontanee cs -> new CandidatureDetailResponse(
                    cs.getId(), TypeCandidature.SPONTANEE, cs.getDateCandidature(), null, null,
                    cs.getNomEntreprise(), cs.getUrlEntreprise(), cs.getTypeEntreprise(), cs.getStatut(), null, evenements, documents);
            case CandidaturePriseDeContact cp -> new CandidatureDetailResponse(
                    cp.getId(), TypeCandidature.PRISE_DE_CONTACT, cp.getDateCandidature(), null, null,
                    cp.getNomEntreprise(), cp.getUrlEntreprise(), cp.getTypeEntreprise(), null, cp.getStatut(), evenements, documents);
        };
    }
}
