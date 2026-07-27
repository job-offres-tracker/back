package fr.sirene.jobtracker.infrastructure.mistral.dto;

import java.util.List;

public record ReponseChatMistral(
        List<ChoixMistral> choices
) {}
