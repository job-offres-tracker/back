package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.ParametresCv;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Paramètres d'upload/téléchargement des CV")
public record ParametresCvResponse(
        @Schema(description = "Taille maximale autorisée pour un CV, en octets", example = "5242880")
        long tailleMaxOctets
) {
    public static ParametresCvResponse fromDomain(ParametresCv parametres) {
        return new ParametresCvResponse(parametres.tailleMaxOctets());
    }
}
