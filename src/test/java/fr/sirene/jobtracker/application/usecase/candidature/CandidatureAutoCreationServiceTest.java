package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.Offre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidatureAutoCreationServiceTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @InjectMocks
    private CandidatureAutoCreationService service;


    @Test
    void cree_une_candidature_quand_aucune_n_existe_pour_l_offre() {
        Offre offre = Offre.builder().idExterne("123").build();
        when(candidatureRepository.existeParOffreIdExterne("123")).thenReturn(false);

        service.assurer(offre);

        ArgumentCaptor<Candidature> captor = ArgumentCaptor.captor();
        verify(candidatureRepository).sauvegarder(captor.capture());
        assertThat(captor.getValue().getOffre()).isEqualTo(offre);
        assertThat(captor.getValue().getDateCandidature()).isNotNull();
    }

    @Test
    void ne_cree_pas_de_doublon_quand_une_candidature_existe_deja() {
        Offre offre = Offre.builder().idExterne("123").build();
        when(candidatureRepository.existeParOffreIdExterne("123")).thenReturn(true);

        service.assurer(offre);

        verify(candidatureRepository, never()).sauvegarder(org.mockito.ArgumentMatchers.any());
    }
}
