package fr.sirene.jobtracker.infrastructure.geo.dto;

import java.util.List;

public record CommuneGeo(
        String nom,
        String code,
        List<String> codesPostaux
) {}
