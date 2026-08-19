package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.Offre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsulterCandidatureParOffreUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @InjectMocks
    private ConsulterCandidatureParOffreUseCase useCase;

    @Test
    void retourne_la_candidature_correspondant_a_l_offre() {
        Candidature candidature = Candidature.builder()
                .id(1L)
                .offre(Offre.builder().idExterne("123").build())
                .build();
        when(candidatureRepository.trouverParOffreIdExterne("123")).thenReturn(Optional.of(candidature));

        Candidature obtenue = useCase.executer("123");

        assertThat(obtenue).isEqualTo(candidature);
    }

    @Test
    void leve_une_exception_quand_aucune_candidature_ne_correspond_a_l_offre() {
        when(candidatureRepository.trouverParOffreIdExterne("123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer("123")).isInstanceOf(CandidatureNonTrouveeException.class);
    }
}
