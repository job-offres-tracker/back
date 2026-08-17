package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.application.port.candidature.DocumentCandidatureStockagePort;
import fr.sirene.jobtracker.application.port.parametres.ParametresDocumentCandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.exception.TailleFichierDepasseeException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentFichier;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.ParametresDocumentCandidature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AjouterDocumentFichierUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @Mock
    private DocumentCandidatureStockagePort documentCandidatureStockagePort;

    @Mock
    private ParametresDocumentCandidatureRepository parametresDocumentCandidatureRepository;

    @InjectMocks
    private AjouterDocumentFichierUseCase useCase;

    private static final Candidature CANDIDATURE = Candidature.builder()
            .id(1L)
            .offre(Offre.builder().idExterne("123").build())
            .build();

    @BeforeEach
    void setUp() {
        lenient().when(parametresDocumentCandidatureRepository.recuperer()).thenReturn(new ParametresDocumentCandidature(1_000L));
   
    }

    @Test
    void leve_une_exception_quand_la_candidature_est_introuvable() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer(1L, "Lettre de motivation", "application/pdf", new byte[10]))
                .isInstanceOf(CandidatureNonTrouveeException.class);
        verifyNoInteractions(documentCandidatureStockagePort);
    }

    @Test
    void rejette_un_fichier_qui_depasse_la_taille_max() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(CANDIDATURE));
        byte[] contenu = new byte[2_000];

        assertThatThrownBy(() -> useCase.executer(1L, "Lettre de motivation", "application/pdf", contenu))
                .isInstanceOf(TailleFichierDepasseeException.class);
        verify(documentCandidatureStockagePort, never()).ecrire(any(), any(), any());
    }

    @Test
    void ecrit_le_fichier_et_ajoute_le_document() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(CANDIDATURE));
        when(candidatureRepository.ajouterDocument(eq(1L), any())).thenAnswer(invocation -> invocation.getArgument(1));
        byte[] contenu = new byte[] {1, 2, 3};

        DocumentCandidature document = useCase.executer(1L, "Lettre de motivation", "application/pdf", contenu);

        assertThat(document).isInstanceOf(DocumentFichier.class);
        DocumentFichier fichier = (DocumentFichier) document;
        assertThat(fichier.libelle()).isEqualTo("Lettre de motivation");
        assertThat(fichier.tailleOctets()).isEqualTo(3);
        assertThat(fichier.contentType()).isEqualTo("application/pdf");
        assertThat(fichier.nomStocke()).isNotBlank();

        ArgumentCaptor<String> nomStockeCaptor = ArgumentCaptor.captor();
        verify(documentCandidatureStockagePort).ecrire(eq(1L), nomStockeCaptor.capture(), eq(contenu));
        assertThat(nomStockeCaptor.getValue()).isEqualTo(fichier.nomStocke());
    }
}
