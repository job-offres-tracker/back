package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.ParametresDocumentCandidatureRepository;
import fr.sirene.jobtracker.domain.model.ParametresDocumentCandidature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModifierParametresDocumentCandidatureUseCaseTest {

    @Mock
    private ParametresDocumentCandidatureRepository parametresDocumentCandidatureRepository;

    @InjectMocks
    private ModifierParametresDocumentCandidatureUseCase useCase;


    @Test
    void sauvegarde_la_taille_max_fournie() {
        ParametresDocumentCandidature parametres = new ParametresDocumentCandidature(1_000L);
        when(parametresDocumentCandidatureRepository.sauvegarder(parametres)).thenReturn(parametres);

        ParametresDocumentCandidature resultat = useCase.executer(1_000L);

        assertThat(resultat.tailleMaxOctets()).isEqualTo(1_000L);
        verify(parametresDocumentCandidatureRepository).sauvegarder(parametres);
    }

    @Test
    void rejette_une_taille_negative_ou_nulle() {
        assertThatThrownBy(() -> useCase.executer(0L))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(parametresDocumentCandidatureRepository);
    }
}
