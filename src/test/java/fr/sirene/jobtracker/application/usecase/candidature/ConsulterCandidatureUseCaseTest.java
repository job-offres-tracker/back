package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidatureOffre;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.StatutCandidatureOffre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsulterCandidatureUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @InjectMocks
    private ConsulterCandidatureUseCase useCase;

    @Test
    void retourne_la_candidature_correspondant_a_l_id() {
        Candidature candidature = CandidatureOffre.builder()
                .id(1L)
                .offre(Offre.builder().idExterne("123").build())
                .statut(StatutCandidatureOffre.POSTULE)
                .dateCandidature(LocalDateTime.now())
                .evenements(List.of())
                .documents(List.of())
                .build();
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));

        Candidature obtenue = useCase.executer(1L);

        assertThat(obtenue).isEqualTo(candidature);
    }

    @Test
    void leve_une_exception_quand_la_candidature_est_introuvable() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer(1L)).isInstanceOf(CandidatureNonTrouveeException.class);
    }
}
