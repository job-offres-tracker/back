package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.Evenement;
import fr.sirene.jobtracker.domain.model.TypeEvenement;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Événement lié à une candidature")
public record EvenementResponse(
        @Schema(description = "Identifiant de l'événement")
        Long id,

        @Schema(description = "Date de l'événement")
        LocalDate date,

        @Schema(description = "Type d'événement")
        TypeEvenement type,

        @Schema(description = "Description libre de l'événement")
        String description
) {
    public static EvenementResponse fromDomain(Evenement evenement) {
        return new EvenementResponse(evenement.getId(), evenement.getDate(), evenement.getType(), evenement.getDescription());
    }
}
