package fr.sirene.jobtracker.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données modifiables d'une prise de contact (URL du site, poste visé, client final)")
public record ModifierPriseDeContactRequest(
        @Schema(description = "URL du site de l'entreprise")
        String urlEntreprise,

        @Schema(description = "Poste visé par la prise de contact")
        String poste,

        @Schema(description = "Client final pour lequel le poste est à pourvoir "
                + "(uniquement pertinent si l'entreprise est une ESN ou un cabinet de recrutement)")
        String client
) {}
