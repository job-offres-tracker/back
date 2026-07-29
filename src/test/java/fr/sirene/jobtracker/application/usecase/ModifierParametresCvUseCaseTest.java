package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.ParametresCvRepository;
import fr.sirene.jobtracker.domain.model.ParametresCv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModifierParametresCvUseCaseTest {

    @Mock
    private ParametresCvRepository parametresCvRepository;

    private ModifierParametresCvUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ModifierParametresCvUseCase(parametresCvRepository);
    }

    @Test
    void sauvegarde_la_taille_max_fournie() {
        ParametresCv parametres = new ParametresCv(1_000L);
        when(parametresCvRepository.sauvegarder(parametres)).thenReturn(parametres);

        ParametresCv resultat = useCase.executer(1_000L);

        assertThat(resultat.tailleMaxOctets()).isEqualTo(1_000L);
        verify(parametresCvRepository).sauvegarder(parametres);
    }

    @Test
    void rejette_une_taille_negative_ou_nulle() {
        assertThatThrownBy(() -> useCase.executer(0L))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(parametresCvRepository);
    }
}
