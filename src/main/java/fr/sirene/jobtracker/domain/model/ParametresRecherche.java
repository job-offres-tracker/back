package fr.sirene.jobtracker.domain.model;

import java.util.List;

public record ParametresRecherche(List<String> motsCles, List<CommuneRecherche> communes, String typeContrat) {

    public ParametresRecherche {
        motsCles = motsCles == null ? List.of() : List.copyOf(motsCles);
        communes = communes == null ? List.of() : List.copyOf(communes);
    }
}
