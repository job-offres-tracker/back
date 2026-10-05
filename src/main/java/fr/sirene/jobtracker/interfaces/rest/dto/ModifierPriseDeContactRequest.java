package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.ChampModifie;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Modification partielle : Jackson n'appelle un setter que si la propriété figure dans le corps.
 * Un champ omis reste donc {@link ChampModifie#absent() absent} (inchangé), tandis qu'un champ
 * présent — y compris avec la valeur JSON {@code null} — est appliqué (et effacé si {@code null}).
 * <p>
 * Les getters renvoient un {@link ChampModifie} alors que le JSON porte une simple chaîne :
 * {@code @Schema(type = "string")} sur les getters évite que Swagger expose {@code ChampModifie}.
 */
@Schema(description = "Données modifiables d'une prise de contact (URL du site, poste visé, client final). "
        + "Seuls les champs présents dans le corps sont modifiés ; un champ à null est effacé.")
public class ModifierPriseDeContactRequest {

    private ChampModifie<String> urlEntreprise = ChampModifie.absent();
    private ChampModifie<String> poste = ChampModifie.absent();
    private ChampModifie<String> client = ChampModifie.absent();

    public void setUrlEntreprise(String urlEntreprise) {
        this.urlEntreprise = ChampModifie.de(urlEntreprise);
    }

    public void setPoste(String poste) {
        this.poste = ChampModifie.de(poste);
    }

    public void setClient(String client) {
        this.client = ChampModifie.de(client);
    }

    @Schema(description = "URL du site de l'entreprise", type = "string", nullable = true)
    public ChampModifie<String> getUrlEntreprise() { return urlEntreprise; }

    @Schema(description = "Poste visé par la prise de contact", type = "string", nullable = true)
    public ChampModifie<String> getPoste() { return poste; }

    @Schema(description = "Client final pour lequel le poste est à pourvoir "
            + "(uniquement pertinent si l'entreprise est une ESN ou un cabinet de recrutement)",
            type = "string", nullable = true)
    public ChampModifie<String> getClient() { return client; }
}
