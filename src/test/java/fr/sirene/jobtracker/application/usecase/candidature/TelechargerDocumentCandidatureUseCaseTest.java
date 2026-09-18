package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.application.port.candidature.DocumentCandidatureStockagePort;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidatureOffre;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidatureTelecharge;
import fr.sirene.jobtracker.domain.model.DocumentFichier;
import fr.sirene.jobtracker.domain.model.DocumentTexte;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.StatutCandidatureOffre;
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
        Candidature candidature = CandidatureOffre.builder()
                .id(1L)
                .offre(Offre.builder().idExterne("123").build())
                .statut(StatutCandidatureOffre.POSTULE)
                .dateCandidature(LocalDateTime.now())
                .evenements(List.of())
                .documents(List.of())
                .build();
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));

        assertThatThrownBy(() -> useCase.executer(1L, 10L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leve_une_exception_quand_le_document_n_est_pas_un_fichier() {
        DocumentCandidature documentTexte = DocumentTexte.builder()
                .id(10L).libelle("Notes").contenuTexte("...").dateAjout(LocalDateTime.now()).build();
        Candidature candidature = CandidatureOffre.builder()
                .id(1L)
                .offre(Offre.builder().idExterne("123").build())
                .statut(StatutCandidatureOffre.POSTULE)
                .dateCandidature(LocalDateTime.now())
                .evenements(List.of())
                .documents(List.of(documentTexte))
                .build();
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));

        assertThatThrownBy(() -> useCase.executer(1L, 10L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void retourne_le_contenu_du_document_fichier() {
        DocumentCandidature documentFichier = DocumentFichier.builder()
                .id(10L).libelle("Lettre.pdf").nomStocke("abc-123").tailleOctets(3)
                .contentType("application/pdf").dateAjout(LocalDateTime.now()).build();
        Candidature candidature = CandidatureOffre.builder()
                .id(1L)
                .offre(Offre.builder().idExterne("123").build())
                .statut(StatutCandidatureOffre.POSTULE)
                .dateCandidature(LocalDateTime.now())
                .evenements(List.of())
                .documents(List.of(documentFichier))
                .build();
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));
        when(documentCandidatureStockagePort.lire(1L, "abc-123")).thenReturn(new byte[] {1, 2, 3});

        DocumentCandidatureTelecharge resultat = useCase.executer(1L, 10L);

        assertThat(resultat.document().getLibelle()).isEqualTo("Lettre.pdf");
        assertThat(resultat.contenu()).containsExactly(1, 2, 3);
    }
}
