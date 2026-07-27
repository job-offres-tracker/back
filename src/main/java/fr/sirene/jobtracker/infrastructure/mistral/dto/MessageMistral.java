package fr.sirene.jobtracker.infrastructure.mistral.dto;

public record MessageMistral(
        String role,
        String content
) {}
