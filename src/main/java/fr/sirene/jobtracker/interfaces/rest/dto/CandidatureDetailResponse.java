package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.Candidature;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Détail d'une candidature")
public record CandidatureDetailResponse(
        @Schema(description = "Identifiant de la candidature")
        Long id,

        @Schema(description = "Date de création de la candidature")
        LocalDateTime dateCandidature,

        @Schema(description = "Offre liée à la candidature")
        OffreResponse offre,

        @Schema(description = "Événements de la candidature, du plus ancien au plus récent")
        List<EvenementResponse> evenements,

        @Schema(description = "Documents attachés à la candidature")
        List<DocumentCandidatureResponse> documents
) {
    public static CandidatureDetailResponse fromDomain(Candidature candidature) {
        return new CandidatureDetailResponse(
                candidature.getId(),
                candidature.getDateCandidature(),
                OffreResponse.fromDomain(candidature.getOffre()),
                candidature.getEvenements().stream().map(EvenementResponse::fromDomain).toList(),
                candidature.getDocuments().stream().map(DocumentCandidatureResponse::fromDomain).toList());
    }
}
