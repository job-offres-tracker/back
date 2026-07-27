package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.Commune;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Commune retournée par la recherche par nom")
public record CommuneResponse(
        @Schema(description = "Nom de la commune", example = "Nantes")
        String nom,

        @Schema(description = "Code INSEE de la commune", example = "44109")
        String codeInsee,

        @Schema(description = "Codes postaux associés à la commune", example = "[\"44000\", \"44100\"]")
        List<String> codesPostaux
) {
    public static CommuneResponse fromDomain(Commune commune) {
        return new CommuneResponse(commune.nom(), commune.codeInsee(), commune.codesPostaux());
    }
}
