package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.CvRepository;
import fr.sirene.jobtracker.application.port.CvStockagePort;
import fr.sirene.jobtracker.application.port.ParametresCvRepository;
import fr.sirene.jobtracker.domain.exception.TailleFichierDepasseeException;
import fr.sirene.jobtracker.domain.exception.TypeFichierNonAutoriseException;
import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.domain.model.ParametresCv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UploaderCvUseCaseTest {

    @Mock
    private CvRepository cvRepository;

    @Mock
    private CvStockagePort cvStockagePort;

    @Mock
    private ParametresCvRepository parametresCvRepository;

    private UploaderCvUseCase useCase;

    @BeforeEach
    void setUp() {
        lenient().when(parametresCvRepository.recuperer()).thenReturn(new ParametresCv(1_000L));
        lenient().when(cvRepository.sauvegarder(any(Cv.class))).thenAnswer(invocation -> invocation.getArgument(0));
        useCase = new UploaderCvUseCase(cvRepository, cvStockagePort, parametresCvRepository);
    }

    @Test
    void rejette_un_fichier_qui_nest_pas_un_pdf() {
        assertThatThrownBy(() -> useCase.executer("cv.docx", "application/msword", new byte[10]))
                .isInstanceOf(TypeFichierNonAutoriseException.class);
        verifyNoInteractions(cvStockagePort, cvRepository);
    }

    @Test
    void rejette_un_fichier_qui_depasse_la_taille_max() {
        byte[] contenu = new byte[2_000];

        assertThatThrownBy(() -> useCase.executer("cv.pdf", "application/pdf", contenu))
                .isInstanceOf(TailleFichierDepasseeException.class);
        verify(cvStockagePort, never()).ecrire(anyString(), any());
    }

    @Test
    void genere_un_nom_unique_et_sauvegarde_le_cv() {
        byte[] contenu = new byte[100];

        Cv cv = useCase.executer("cv-jean-dupont.pdf", "application/pdf", contenu);

        assertThat(cv.getNomUnique()).endsWith(".pdf");
        assertThat(cv.getNomOriginal()).isEqualTo("cv-jean-dupont.pdf");
        assertThat(cv.getTailleOctets()).isEqualTo(100);

        ArgumentCaptor<String> nomUniqueCaptor = ArgumentCaptor.captor();
        verify(cvStockagePort).ecrire(nomUniqueCaptor.capture(), org.mockito.ArgumentMatchers.eq(contenu));
        assertThat(nomUniqueCaptor.getValue()).isEqualTo(cv.getNomUnique());
        verify(cvRepository).sauvegarder(any(Cv.class));
    }

    @Test
    void ajoute_l_extension_pdf_si_le_nom_fourni_ne_la_contient_pas() {
        Cv cv = useCase.executer("Mon CV", "application/pdf", new byte[10]);

        assertThat(cv.getNomOriginal()).isEqualTo("Mon CV.pdf");
    }
}
