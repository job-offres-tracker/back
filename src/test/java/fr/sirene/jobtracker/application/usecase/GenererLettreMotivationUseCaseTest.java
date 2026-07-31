package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.CvRepository;
import fr.sirene.jobtracker.application.port.GenerationLettreMotivationPort;
import fr.sirene.jobtracker.application.port.OffreStorageRepository;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.exception.OffreNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.domain.model.Offre;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenererLettreMotivationUseCaseTest {

    @Mock
    private OffreStorageRepository offreStorageRepository;

    @Mock
    private CvRepository cvRepository;

    @Mock
    private GenerationLettreMotivationPort generationLettreMotivationPort;

    @InjectMocks
    private GenererLettreMotivationUseCase useCase;

    @Test
    void delegue_la_generation_au_port_quand_l_offre_et_le_cv_existent() {
        when(offreStorageRepository.trouverParIdExterne("123"))
                .thenReturn(Optional.of(Offre.builder().idExterne("123").build()));
        when(cvRepository.trouverParNomUnique("cv-1"))
                .thenReturn(Optional.of(Cv.builder().nomUnique("cv-1").build()));
        when(generationLettreMotivationPort.genererLettre("123", "cv-1")).thenReturn("Lettre générée");

        String resultat = useCase.executer("123", "cv-1");

        assertThat(resultat).isEqualTo("Lettre générée");
    }

    @Test
    void leve_une_exception_quand_l_offre_est_introuvable() {
        when(offreStorageRepository.trouverParIdExterne("inconnu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer("inconnu", "cv-1"))
                .isInstanceOf(OffreNonTrouveeException.class)
                .hasMessageContaining("inconnu");

        verify(cvRepository, never()).trouverParNomUnique(any());
        verify(generationLettreMotivationPort, never()).genererLettre(any(), any());
    }

    @Test
    void leve_une_exception_quand_le_cv_est_introuvable() {
        when(offreStorageRepository.trouverParIdExterne("123"))
                .thenReturn(Optional.of(Offre.builder().idExterne("123").build()));
        when(cvRepository.trouverParNomUnique("inconnu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer("123", "inconnu"))
                .isInstanceOf(CvNonTrouveException.class)
                .hasMessageContaining("inconnu");

        verify(generationLettreMotivationPort, never()).genererLettre(any(), any());
    }
}
