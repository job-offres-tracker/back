package fr.sirene.jobtracker.interfaces.scheduler;

import fr.sirene.jobtracker.application.usecase.offre.SynchroniserOffresUseCase;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OffreSyncScheduler {

    private final SynchroniserOffresUseCase synchroniserOffresUseCase;

    public OffreSyncScheduler(SynchroniserOffresUseCase synchroniserOffresUseCase) {
        this.synchroniserOffresUseCase = synchroniserOffresUseCase;
    }

    @Scheduled(cron = "${jobtracker.sync.cron}")
    public void synchroniser() {
        try {
            int nombreOffres = synchroniserOffresUseCase.executer();
            log.info("Synchronisation France Travail terminée : {} offre(s) traitée(s)", nombreOffres);
        } catch (Exception e) {
            log.error("Échec de la synchronisation France Travail", e);
        }
    }
}
