package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.Evenement;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.TypeEvenement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AjouterEvenementCandidatureUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @InjectMocks
    private AjouterEvenementCandidatureUseCase useCase;

    @Test
    void leve_une_exception_quand_la_candidature_est_introuvable() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer(1L, LocalDate.now(), TypeEvenement.ENTRETIEN, "Entretien RH"))
                .isInstanceOf(CandidatureNonTrouveeException.class);
    }

    @Test
    void ajoute_l_evenement_a_la_candidature() {
        Candidature candidature = Candidature.builder().id(1L).offre(Offre.builder().idExterne("123").build()).build();
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));
        LocalDate date = LocalDate.of(2026, 8, 1);
        when(candidatureRepository.ajouterEvenement(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(1));

        Evenement evenement = useCase.executer(1L, date, TypeEvenement.RELANCE, "Relance mail");

        assertThat(evenement.getDate()).isEqualTo(date);
        assertThat(evenement.getType()).isEqualTo(TypeEvenement.RELANCE);
        assertThat(evenement.getDescription()).isEqualTo("Relance mail");

        ArgumentCaptor<Evenement> captor = ArgumentCaptor.captor();
        verify(candidatureRepository).ajouterEvenement(org.mockito.ArgumentMatchers.eq(1L), captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo(TypeEvenement.RELANCE);
    }
}
