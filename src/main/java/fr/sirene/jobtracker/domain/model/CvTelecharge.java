package fr.sirene.jobtracker.domain.model;

import java.util.Arrays;
import java.util.Objects;

public record CvTelecharge(Cv cv, byte[] contenu) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CvTelecharge other)) {
            return false;
        }
        return Objects.equals(cv, other.cv) && Arrays.equals(contenu, other.contenu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cv, Arrays.hashCode(contenu));
    }
}
