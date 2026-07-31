package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.ParametresDocumentCandidature;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Paramètres d'upload des documents de candidature")
public record ParametresDocumentCandidatureResponse(
        @Schema(description = "Taille maximale autorisée pour un document, en octets", example = "10485760")
        long tailleMaxOctets
) {
    public static ParametresDocumentCandidatureResponse fromDomain(ParametresDocumentCandidature parametres) {
        return new ParametresDocumentCandidatureResponse(parametres.tailleMaxOctets());
    }
}
