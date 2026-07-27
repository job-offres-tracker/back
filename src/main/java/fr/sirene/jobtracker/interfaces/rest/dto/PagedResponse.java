package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.ResultatPagine;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Réponse paginée générique")
public record PagedResponse<T>(
        @Schema(description = "Numéro de la page courante, à partir de 0")
        int page,

        @Schema(description = "Taille de page demandée")
        int taille,

        @Schema(description = "Nombre d'éléments effectivement présents dans cette page")
        int nombreResultats,

        @Schema(description = "Nombre total d'éléments sur l'ensemble des pages")
        long totalElements,

        @Schema(description = "Nombre total de pages")
        int totalPages,

        @Schema(description = "Éléments de la page courante")
        List<T> resultats
) {
    public static <T> PagedResponse<T> of(ResultatPagine<T> resultat) {
        int totalPages = resultat.taille() > 0
                ? (int) Math.ceil((double) resultat.total() / resultat.taille())
                : 0;
        return new PagedResponse<>(
                resultat.page(),
                resultat.taille(),
                resultat.elements().size(),
                resultat.total(),
                totalPages,
                resultat.elements());
    }
}
