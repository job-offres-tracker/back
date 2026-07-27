package fr.sirene.jobtracker.infrastructure.mistral.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RequeteChatMistral(
        String model,
        List<MessageMistral> messages,
        Double temperature,
        @JsonProperty("response_format") ReponseFormatMistral responseFormat
) {}
