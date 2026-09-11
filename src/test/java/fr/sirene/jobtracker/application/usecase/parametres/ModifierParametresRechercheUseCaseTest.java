package fr.sirene.jobtracker.application.usecase.parametres;

import fr.sirene.jobtracker.application.port.parametres.ParametresRechercheRepository;
import fr.sirene.jobtracker.domain.model.CommuneRecherche;
import fr.sirene.jobtracker.domain.model.ParametresRecherche;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static fr.sirene.jobtracker.domain.model.CommuneRechercheFixtures.NANTES;
import static fr.sirene.jobtracker.domain.model.CommuneRechercheFixtures.sixCommunesValides;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModifierParametresRechercheUseCaseTest {

    @Mock
    private ParametresRechercheRepository parametresRechercheRepository;

    private ModifierParametresRechercheUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ModifierParametresRechercheUseCase(parametresRechercheRepository);
    }

    @Test
    void sauvegarde_les_parametres_fournis() {
        ParametresRecherche parametres = new ParametresRecherche(List.of("Java"), List.of(NANTES), "CDI");
        when(parametresRechercheRepository.sauvegarder(parametres)).thenReturn(parametres);

        ParametresRecherche resultat = useCase.executer(List.of("Java"), List.of(NANTES), "CDI");

        assertThat(resultat).isEqualTo(parametres);
        verify(parametresRechercheRepository).sauvegarder(parametres);
    }

    @Test
    void accepte_un_nombre_de_communes_superieur_a_la_limite_de_batching_france_travail() {
        List<CommuneRecherche> sixCommunes = sixCommunesValides();
        ParametresRecherche parametres = new ParametresRecherche(List.of("Java"), sixCommunes, "CDI");
        when(parametresRechercheRepository.sauvegarder(parametres)).thenReturn(parametres);

        ParametresRecherche resultat = useCase.executer(List.of("Java"), sixCommunes, "CDI");

        assertThat(resultat.communes()).hasSize(6);
        verify(parametresRechercheRepository).sauvegarder(parametres);
    }
}
