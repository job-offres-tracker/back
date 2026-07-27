package fr.sirene.jobtracker.infrastructure.ban.dto;

import java.util.List;

public record ReponseReverseBan(
        List<FeatureBan> features
) {}
