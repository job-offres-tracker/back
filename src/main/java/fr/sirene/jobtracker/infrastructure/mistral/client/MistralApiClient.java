package fr.sirene.jobtracker.infrastructure.mistral.client;

import fr.sirene.jobtracker.infrastructure.mistral.dto.RequeteChatMistral;
import fr.sirene.jobtracker.infrastructure.mistral.dto.ReponseChatMistral;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MistralApiClient {

    private final RestClient restClient;

    public MistralApiClient(RestClient mistralApiRestClient) {
        this.restClient = mistralApiRestClient;
    }

    public ReponseChatMistral completer(RequeteChatMistral requete) {
        return restClient.post()
                .uri("/v1/chat/completions")
                .body(requete)
                .retrieve()
                .body(ReponseChatMistral.class);
    }
}
