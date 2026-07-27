package fr.sirene.jobtracker.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI jobOffresTrackerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Job Offres Tracker API")
                        .description("Synchronisation planifiée des offres d'emploi France Travail, "
                                + "consultation, création manuelle et suivi d'état des candidatures.")
                        .version("v1")
                        .contact(new Contact().name("job-tracker").url("https://github.com/ogodineau/job-tracker")));
    }
}
