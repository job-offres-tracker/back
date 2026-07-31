package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.CandidatureRepository;
import fr.sirene.jobtracker.application.port.DocumentCandidatureStockagePort;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidatureTelecharge;
import fr.sirene.jobtracker.domain.model.DocumentFichier;
import fr.sirene.jobtracker.domain.model.DocumentTexte;
import fr.sirene.jobtracker.domain.model.Offre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelechargerDocumentCandidatureUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @Mock
    private DocumentCandidatureStockagePort documentCandidatureStockagePort;

    @InjectMocks
    private TelechargerDocumentCandidatureUseCase useCase;

    @Test
    void leve_une_exception_quand_la_candidature_est_introuvable() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer(1L, 10L)).isInstanceOf(CandidatureNonTrouveeException.class);
    }

    @Test
    void leve_une_exception_quand_le_document_est_introuvable() {
        Candidature candidature = Candidature.builder().id(1L).offre(Offre.builder().idExterne("123").build()).build();
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));

        assertThatThrownBy(() -> useCase.executer(1L, 10L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leve_une_exception_quand_le_document_n_est_pas_un_fichier() {
        DocumentCandidature documentTexte = new DocumentTexte(10L, "Notes", "...", LocalDateTime.now());
        Candidature candidature = Candidature.builder()
                .id(1L).offre(Offre.builder().idExterne("123").build()).documents(List.of(documentTexte)).build();
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));

        assertThatThrownBy(() -> useCase.executer(1L, 10L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void retourne_le_contenu_du_document_fichier() {
        DocumentCandidature documentFichier =
                new DocumentFichier(10L, "Lettre.pdf", "abc-123", 3, "application/pdf", LocalDateTime.now());
        Candidature candidature = Candidature.builder()
                .id(1L).offre(Offre.builder().idExterne("123").build()).documents(List.of(documentFichier)).build();
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));
        when(documentCandidatureStockagePort.lire(1L, "abc-123")).thenReturn(new byte[] {1, 2, 3});

        DocumentCandidatureTelecharge resultat = useCase.executer(1L, 10L);

        assertThat(resultat.document().libelle()).isEqualTo("Lettre.pdf");
        assertThat(resultat.contenu()).containsExactly(1, 2, 3);
    }
}
