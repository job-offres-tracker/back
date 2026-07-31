package fr.sirene.jobtracker.infrastructure.mistral.lettremotivation;

import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.exception.ExtractionTexteCvException;
import fr.sirene.jobtracker.domain.exception.GenerationLettreMotivationException;
import fr.sirene.jobtracker.domain.exception.OffreNonTrouveeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.execution.ToolExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MistralLettreMotivationAdapterTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    @InjectMocks
    private MistralLettreMotivationAdapter adapter;

    @BeforeEach
    void setUp() {
        simulerAppelReussi();
    }

    @Test
    void retourne_le_contenu_genere_par_le_modele() {
        when(responseSpec.content()).thenReturn("Madame, Monsieur, ...");

        String resultat = adapter.genererLettre("123", "cv-1");

        assertThat(resultat).isEqualTo("Madame, Monsieur, ...");
    }

    @Test
    void relance_l_exception_d_origine_quand_l_offre_est_introuvable_dans_l_outil() {
        OffreNonTrouveeException causeMetier = new OffreNonTrouveeException("inconnu");
        when(responseSpec.content()).thenThrow(outilEnErreur(causeMetier));

        assertThatThrownBy(() -> adapter.genererLettre("inconnu", "cv-1"))
                .isSameAs(causeMetier);
    }

    @Test
    void relance_l_exception_d_origine_quand_le_cv_est_introuvable_dans_l_outil() {
        CvNonTrouveException causeMetier = new CvNonTrouveException("inconnu");
        when(responseSpec.content()).thenThrow(outilEnErreur(causeMetier));

        assertThatThrownBy(() -> adapter.genererLettre("123", "inconnu"))
                .isSameAs(causeMetier);
    }

    @Test
    void relance_l_exception_d_origine_quand_le_texte_du_cv_n_est_pas_extractible() {
        ExtractionTexteCvException causeMetier =
                new ExtractionTexteCvException("Impossible d'extraire le texte du CV");
        when(responseSpec.content()).thenThrow(outilEnErreur(causeMetier));

        assertThatThrownBy(() -> adapter.genererLettre("123", "cv-1"))
                .isSameAs(causeMetier);
    }

    @Test
    void enveloppe_dans_generation_lettre_motivation_exception_quand_l_appel_ia_echoue() {
        when(responseSpec.content()).thenThrow(new RuntimeException("Service Mistral indisponible"));

        assertThatThrownBy(() -> adapter.genererLettre("123", "cv-1"))
                .isInstanceOf(GenerationLettreMotivationException.class);
    }

    private void simulerAppelReussi() {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
    }

    private ToolExecutionException outilEnErreur(RuntimeException causeMetier) {
        ToolDefinition toolDefinition = ToolDefinition.builder()
                .name("recupererCv")
                .description("outil")
                .inputSchema("{}")
                .build();
        return new ToolExecutionException(toolDefinition, causeMetier);
    }
}
