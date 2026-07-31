package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.CandidatureRepository;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.ResultatPagine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsulterCandidaturesUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @InjectMocks
    private ConsulterCandidaturesUseCase useCase;

    @Test
    void delegue_la_recherche_paginee_au_repository() {
        Candidature candidature = Candidature.builder().offre(Offre.builder().idExterne("123").build()).build();
        ResultatPagine<Candidature> resultat = new ResultatPagine<>(List.of(candidature), 0, 20, 1);
        when(candidatureRepository.lister(0, 20)).thenReturn(resultat);

        ResultatPagine<Candidature> obtenu = useCase.executer(0, 20);

        assertThat(obtenu).isEqualTo(resultat);
    }
}
