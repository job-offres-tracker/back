package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.application.port.cv.CvRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentCv;
import fr.sirene.jobtracker.domain.model.Offre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AjouterDocumentCvUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @Mock
    private CvRepository cvRepository;

    @InjectMocks
    private AjouterDocumentCvUseCase useCase;

    private static final Candidature CANDIDATURE = Candidature.builder()
            .id(1L)
            .offre(Offre.builder().idExterne("123").build())
            .build();

    @Test
    void leve_une_exception_quand_la_candidature_est_introuvable() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer(1L, "abc.pdf")).isInstanceOf(CandidatureNonTrouveeException.class);
        verifyNoInteractions(cvRepository);
    }

    @Test
    void leve_une_exception_quand_le_cv_est_introuvable() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(CANDIDATURE));
        when(cvRepository.trouverParNomUnique("abc.pdf")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer(1L, "abc.pdf")).isInstanceOf(CvNonTrouveException.class);
    }

    @Test
    void attache_le_cv_a_la_candidature() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(CANDIDATURE));
        Cv cv = Cv.builder().nomUnique("abc.pdf").nomOriginal("cv-jean.pdf").tailleOctets(10).dateUpload(Instant.now()).build();
        when(cvRepository.trouverParNomUnique("abc.pdf")).thenReturn(Optional.of(cv));
        when(candidatureRepository.ajouterDocument(eq(1L), any())).thenAnswer(invocation -> invocation.getArgument(1));

        DocumentCandidature document = useCase.executer(1L, "abc.pdf");

        assertThat(document).isInstanceOf(DocumentCv.class);
        assertThat(document.libelle()).isEqualTo("cv-jean.pdf");
        assertThat(((DocumentCv) document).cvNomUnique()).isEqualTo("abc.pdf");
        assertThat(((DocumentCv) document).tailleOctets()).isEqualTo(10);
    }
}
