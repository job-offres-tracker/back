package fr.sirene.jobtracker.domain.model;

import java.util.Arrays;
import java.util.Objects;

public record DocumentCandidatureTelecharge(DocumentFichier document, byte[] contenu) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentCandidatureTelecharge other)) {
            return false;
        }
        return Objects.equals(document, other.document) && Arrays.equals(contenu, other.contenu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document, Arrays.hashCode(contenu));
    }
}
