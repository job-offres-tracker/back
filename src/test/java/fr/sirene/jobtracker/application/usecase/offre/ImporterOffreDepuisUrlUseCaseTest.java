package fr.sirene.jobtracker.application.usecase.offre;

import fr.sirene.jobtracker.application.port.offre.ExtractionOffreIAPort;
import fr.sirene.jobtracker.application.port.offre.RecuperationPageOffrePort;
import fr.sirene.jobtracker.domain.model.BrouillonOffre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImporterOffreDepuisUrlUseCaseTest {

    @Mock
    private RecuperationPageOffrePort recuperationPageOffrePort;

    @Mock
    private ExtractionOffreIAPort extractionOffreIAPort;

    @InjectMocks
    private ImporterOffreDepuisUrlUseCase useCase;

    @Test
    void recupere_la_page_puis_transmet_son_contenu_a_l_extraction_ia() {
        String url = "https://www.hellowork.com/fr-fr/emplois/12345.html";
        when(recuperationPageOffrePort.recuperer(url)).thenReturn("contenu de la page");
        BrouillonOffre brouillon = new BrouillonOffre(
                "Développeur Java", "description", "Acme", "Nantes", "CDI", "45K€", url, "REF-123", "2026-07-20");
        when(extractionOffreIAPort.extraire("contenu de la page", url)).thenReturn(brouillon);

        BrouillonOffre resultat = useCase.executer(url);

        assertThat(resultat).isEqualTo(brouillon);
        verify(extractionOffreIAPort).extraire("contenu de la page", url);
    }
}
