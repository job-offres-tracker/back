package fr.sirene.jobtracker.domain.model;

import java.util.List;

public record Commune(
        String nom,
        String codeInsee,
        List<String> codesPostaux
) {}
