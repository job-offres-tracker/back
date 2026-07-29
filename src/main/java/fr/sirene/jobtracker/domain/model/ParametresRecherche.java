package fr.sirene.jobtracker.domain.model;

import java.util.List;

public record ParametresRecherche(List<String> motsCles, List<CommuneRecherche> communes, String typeContrat) {

    private static final int NB_COMMUNES_MAX = 5;

    public ParametresRecherche {
        motsCles = motsCles == null ? List.of() : List.copyOf(motsCles);
        communes = communes == null ? List.of() : List.copyOf(communes);
        if (communes.size() > NB_COMMUNES_MAX) {
            throw new IllegalArgumentException(
                    "%d communes maximum autorisées, %d fournie(s)".formatted(NB_COMMUNES_MAX, communes.size()));
        }
    }
}
