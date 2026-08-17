package fr.sirene.jobtracker.interfaces.scheduler;

import fr.sirene.jobtracker.application.usecase.offre.SynchroniserOffresUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OffreSyncSchedulerTest {

    @Mock
    private SynchroniserOffresUseCase synchroniserOffresUseCase;

    @InjectMocks
    private OffreSyncScheduler scheduler;

    @Test
    void delegue_la_synchronisation_au_use_case() {
        when(synchroniserOffresUseCase.executer()).thenReturn(5);

        scheduler.synchroniser();

        verify(synchroniserOffresUseCase).executer();
    }

    @Test
    void n_echoue_pas_quand_le_use_case_leve_une_exception() {
        when(synchroniserOffresUseCase.executer()).thenThrow(new RuntimeException("France Travail indisponible"));

        assertThatCode(() -> scheduler.synchroniser()).doesNotThrowAnyException();
    }
}
