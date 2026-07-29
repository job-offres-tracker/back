package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.CvRepository;
import fr.sirene.jobtracker.application.port.CvStockagePort;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.domain.model.CvTelecharge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelechargerCvUseCaseTest {

    @Mock
    private CvRepository cvRepository;

    @Mock
    private CvStockagePort cvStockagePort;

    private TelechargerCvUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new TelechargerCvUseCase(cvRepository, cvStockagePort);
    }

    @Test
    void leve_une_exception_si_le_cv_est_introuvable() {
        when(cvRepository.trouverParNomUnique("inconnu.pdf")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer("inconnu.pdf"))
                .isInstanceOf(CvNonTrouveException.class);
        verifyNoInteractions(cvStockagePort);
    }

    @Test
    void retourne_le_contenu_et_le_nom_original() {
        Cv cv = Cv.builder().nomUnique("abc.pdf").nomOriginal("cv-jean.pdf").tailleOctets(3).dateUpload(Instant.now()).build();
        when(cvRepository.trouverParNomUnique("abc.pdf")).thenReturn(Optional.of(cv));
        when(cvStockagePort.lire("abc.pdf")).thenReturn(new byte[] {1, 2, 3});

        CvTelecharge resultat = useCase.executer("abc.pdf");

        assertThat(resultat.cv().getNomOriginal()).isEqualTo("cv-jean.pdf");
        assertThat(resultat.contenu()).containsExactly(1, 2, 3);
    }
}
