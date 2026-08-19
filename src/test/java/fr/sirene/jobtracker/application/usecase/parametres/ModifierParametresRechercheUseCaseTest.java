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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModifierParametresRechercheUseCaseTest {

    private static final CommuneRecherche NANTES = new CommuneRecherche("44109", "Nantes");

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
    void rejette_plus_de_5_communes() {
        List<CommuneRecherche> sixCommunes = List.of(
                new CommuneRecherche("44109", "Nantes"),
                new CommuneRecherche("44020", "Saint-Herblain"),
                new CommuneRecherche("85191", "Les Sables-d'Olonne"),
                new CommuneRecherche("85047", "Challans"),
                new CommuneRecherche("85194", "Talmont-Saint-Hilaire"),
                new CommuneRecherche("44000", "Nantes centre"));

        assertThatThrownBy(() -> useCase.executer(List.of("Java"), sixCommunes, "CDI"))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(parametresRechercheRepository);
    }
}
