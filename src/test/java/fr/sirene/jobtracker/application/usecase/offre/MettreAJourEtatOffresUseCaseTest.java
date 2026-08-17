package fr.sirene.jobtracker.application.usecase.offre;

import fr.sirene.jobtracker.application.port.offre.OffreStorageRepository;
import fr.sirene.jobtracker.application.usecase.candidature.CandidatureAutoCreationService;
import fr.sirene.jobtracker.domain.exception.TransitionEtatInvalideException;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Offre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MettreAJourEtatOffresUseCaseTest {

    @Mock
    private OffreStorageRepository offreStorageRepository;

    @Mock
    private CandidatureAutoCreationService candidatureAutoCreationService;

    @InjectMocks
    private MettreAJourEtatOffresUseCase useCase;

    @Test
    void delegue_la_mise_a_jour_au_repository() {
        when(offreStorageRepository.trouverParIdsExternes(List.of("123", "456"))).thenReturn(List.of());

        useCase.executer(List.of("123", "456"), EtatOffre.LU);

        verify(offreStorageRepository).mettreAJourEtat(List.of("123", "456"), EtatOffre.LU);
    }

    @Test
    void cree_une_candidature_pour_chaque_offre_quand_l_etat_cible_est_postule() {
        Offre offre = Offre.builder().idExterne("123").etat(EtatOffre.NON_LU).build();
        when(offreStorageRepository.trouverParIdsExternes(List.of("123"))).thenReturn(List.of(offre));

        useCase.executer(List.of("123"), EtatOffre.POSTULE);

        verify(candidatureAutoCreationService).assurer(offre);
    }

    @Test
    void n_appelle_pas_l_auto_creation_de_candidature_pour_un_etat_cible_different_de_postule() {
        Offre offre = Offre.builder().idExterne("123").etat(EtatOffre.NON_LU).build();
        when(offreStorageRepository.trouverParIdsExternes(List.of("123"))).thenReturn(List.of(offre));

        useCase.executer(List.of("123"), EtatOffre.LU);

        verify(candidatureAutoCreationService, never()).assurer(any());
    }

    @Test
    void rejette_la_transition_vers_non_lu_quand_l_offre_est_deja_postulee() {
        Offre offre = Offre.builder().idExterne("123").etat(EtatOffre.POSTULE).build();
        when(offreStorageRepository.trouverParIdsExternes(List.of("123"))).thenReturn(List.of(offre));

        assertThatThrownBy(() -> useCase.executer(List.of("123"), EtatOffre.NON_LU))
                .isInstanceOf(TransitionEtatInvalideException.class)
                .hasMessageContaining("123");

        verify(offreStorageRepository, never()).mettreAJourEtat(any(), any());
    }

    @Test
    void rejette_la_transition_vers_lu_quand_l_offre_est_en_entretien() {
        Offre offre = Offre.builder().idExterne("123").etat(EtatOffre.ENTRETIEN).build();
        when(offreStorageRepository.trouverParIdsExternes(List.of("123"))).thenReturn(List.of(offre));

        assertThatThrownBy(() -> useCase.executer(List.of("123"), EtatOffre.LU))
                .isInstanceOf(TransitionEtatInvalideException.class);
    }

    @Test
    void autorise_la_transition_vers_refuse_meme_apres_une_candidature() {
        Offre offre = Offre.builder().idExterne("123").etat(EtatOffre.ENTRETIEN).build();
        when(offreStorageRepository.trouverParIdsExternes(List.of("123"))).thenReturn(List.of(offre));

        useCase.executer(List.of("123"), EtatOffre.REFUSE);

        verify(offreStorageRepository).mettreAJourEtat(List.of("123"), EtatOffre.REFUSE);
    }

    @Test
    void autorise_la_transition_vers_non_lu_quand_l_offre_n_est_pas_encore_engagee() {
        Offre offre = Offre.builder().idExterne("123").etat(EtatOffre.LU).build();
        when(offreStorageRepository.trouverParIdsExternes(List.of("123"))).thenReturn(List.of(offre));

        useCase.executer(List.of("123"), EtatOffre.NON_LU);

        verify(offreStorageRepository).mettreAJourEtat(List.of("123"), EtatOffre.NON_LU);
    }
}
