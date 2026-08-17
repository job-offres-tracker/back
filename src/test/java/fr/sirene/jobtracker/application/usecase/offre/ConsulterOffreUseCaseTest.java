package fr.sirene.jobtracker.application.usecase.offre;

import fr.sirene.jobtracker.application.port.offre.OffreStorageRepository;
import fr.sirene.jobtracker.domain.exception.OffreNonTrouveeException;
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
class ConsulterOffreUseCaseTest {

    @Mock
    private OffreStorageRepository offreStorageRepository;

    @InjectMocks
    private ConsulterOffreUseCase useCase;

    @Test
    void retourne_l_offre_correspondant_a_l_id_externe() {
        Offre offre = Offre.builder().idExterne("123").build();
        when(offreStorageRepository.trouverParIdExterne("123")).thenReturn(Optional.of(offre));

        Offre resultat = useCase.executer("123");

        assertThat(resultat).isEqualTo(offre);
    }

    @Test
    void leve_une_exception_quand_aucune_offre_ne_correspond_a_l_id_externe() {
        when(offreStorageRepository.trouverParIdExterne("inconnu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer("inconnu"))
                .isInstanceOf(OffreNonTrouveeException.class)
                .hasMessageContaining("inconnu");
    }
}
