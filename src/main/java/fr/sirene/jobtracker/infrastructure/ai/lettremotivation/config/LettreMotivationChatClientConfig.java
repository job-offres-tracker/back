package fr.sirene.jobtracker.infrastructure.ai.lettremotivation.config;

import fr.sirene.jobtracker.infrastructure.ai.lettremotivation.OutilsLettreMotivation;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LettreMotivationChatClientConfig {

    @Bean
    public ChatClient lettreMotivationChatClient(
            ChatClient.Builder chatClientBuilder, OutilsLettreMotivation outilsLettreMotivation) {
        return chatClientBuilder.defaultTools(outilsLettreMotivation).build();
    }
}
