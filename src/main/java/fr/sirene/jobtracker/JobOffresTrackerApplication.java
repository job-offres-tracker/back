package fr.sirene.jobtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class JobOffresTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobOffresTrackerApplication.class, args);
    }
}
