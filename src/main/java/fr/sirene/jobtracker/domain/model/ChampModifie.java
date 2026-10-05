package fr.sirene.jobtracker.domain.model;

/**
 * Valeur d'un champ dans une modification partielle : soit absente (le champ reste inchangé),
 * soit présente (le champ prend la valeur fournie, éventuellement {@code null} pour l'effacer).
 */
public record ChampModifie<T>(boolean present, T valeur) {

    public static <T> ChampModifie<T> absent() {
        return new ChampModifie<>(false, null);
    }

    public static <T> ChampModifie<T> de(T valeur) {
        return new ChampModifie<>(true, valeur);
    }

    public T appliquerA(T valeurActuelle) {
        return present ? valeur : valeurActuelle;
    }
}
