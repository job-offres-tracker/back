package fr.sirene.jobtracker.infrastructure.francetravail.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenFranceTravail(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") Integer expiresIn,
        @JsonProperty("token_type") String tokenType
) {}
