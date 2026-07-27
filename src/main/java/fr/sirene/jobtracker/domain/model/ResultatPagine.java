package fr.sirene.jobtracker.domain.model;

import java.util.List;
import java.util.function.Function;

public record ResultatPagine<T>(
        List<T> elements,
        int page,
        int taille,
        long total
) {
    public <R> ResultatPagine<R> map(Function<T, R> mapper) {
        return new ResultatPagine<>(elements.stream().map(mapper).toList(), page, taille, total);
    }
}
