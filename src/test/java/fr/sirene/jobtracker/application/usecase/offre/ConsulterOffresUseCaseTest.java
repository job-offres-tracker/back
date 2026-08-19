package fr.sirene.jobtracker.application.usecase.offre;

import fr.sirene.jobtracker.application.port.offre.OffreStorageRepository;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.ResultatPagine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsulterOffresUseCaseTest {

    @Mock
    private OffreStorageRepository offreStorageRepository;

    @InjectMocks
    private ConsulterOffresUseCase useCase;



    @Test
    void transmet_le_filtre_etats_a_la_recherche_et_au_comptage() {
        Offre offre = Offre.builder().idExterne("123").build();
        List<EtatOffre> etats = List.of(EtatOffre.LU, EtatOffre.POSTULE);
        when(offreStorageRepository.rechercher(0, 20, etats)).thenReturn(List.of(offre));
        when(offreStorageRepository.compter(etats)).thenReturn(1L);

        ResultatPagine<Offre> resultat = useCase.executer(0, 20, etats);

        assertThat(resultat.elements()).containsExactly(offre);
        assertThat(resultat.total()).isEqualTo(1L);
        verify(offreStorageRepository).rechercher(0, 20, etats);
        verify(offreStorageRepository).compter(etats);
    }

    @Test
    void fonctionne_sans_filtre_etats() {
        when(offreStorageRepository.rechercher(0, 20, null)).thenReturn(List.of());
        when(offreStorageRepository.compter(null)).thenReturn(0L);

        ResultatPagine<Offre> resultat = useCase.executer(0, 20, null);

        assertThat(resultat.elements()).isEmpty();
        assertThat(resultat.total()).isZero();
    }
}
