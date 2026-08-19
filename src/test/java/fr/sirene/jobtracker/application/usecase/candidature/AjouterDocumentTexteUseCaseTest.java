package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentTexte;
import fr.sirene.jobtracker.domain.model.Offre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AjouterDocumentTexteUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @InjectMocks
    private AjouterDocumentTexteUseCase useCase;

    @Test
    void leve_une_exception_quand_la_candidature_est_introuvable() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer(1L, "Notes", "Contenu"))
                .isInstanceOf(CandidatureNonTrouveeException.class);
    }

    @Test
    void ajoute_le_document_texte() {
        Candidature candidature = Candidature.builder().id(1L).offre(Offre.builder().idExterne("123").build()).build();
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));
        when(candidatureRepository.ajouterDocument(eq(1L), any())).thenAnswer(invocation -> invocation.getArgument(1));

        DocumentCandidature document = useCase.executer(1L, "Notes entretien", "Bon feeling général");

        assertThat(document).isInstanceOf(DocumentTexte.class);
        assertThat(document.libelle()).isEqualTo("Notes entretien");
        assertThat(((DocumentTexte) document).contenuTexte()).isEqualTo("Bon feeling général");
    }
}
