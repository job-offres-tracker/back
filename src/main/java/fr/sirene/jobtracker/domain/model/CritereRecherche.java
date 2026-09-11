package fr.sirene.jobtracker.domain.model;

import java.util.List;

public record CritereRecherche(
        String motsCles,
        String typeContrat,
        List<CommuneRecherche> communes
) {

    public CritereRecherche {
        communes = communes == null ? List.of() : List.copyOf(communes);
    }
}
