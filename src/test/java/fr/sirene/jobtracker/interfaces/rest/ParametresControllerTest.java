package fr.sirene.jobtracker.interfaces.rest;

import fr.sirene.jobtracker.application.usecase.ConsulterParametresCvUseCase;
import fr.sirene.jobtracker.application.usecase.ConsulterParametresRechercheUseCase;
import fr.sirene.jobtracker.application.usecase.ModifierParametresCvUseCase;
import fr.sirene.jobtracker.application.usecase.ModifierParametresRechercheUseCase;
import fr.sirene.jobtracker.domain.model.CommuneRecherche;
import fr.sirene.jobtracker.domain.model.ParametresCv;
import fr.sirene.jobtracker.domain.model.ParametresRecherche;
import fr.sirene.jobtracker.infrastructure.config.CorsConfig;
import fr.sirene.jobtracker.interfaces.rest.dto.ParametresCvRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.ParametresRechercheRequest;
import jakarta.inject.Inject;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ParametresController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CorsConfig.class))
class ParametresControllerTest {

    private static final CommuneRecherche NANTES = new CommuneRecherche("44109", "Nantes");

    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private MockMvc mockMvc;

    @MockitoBean
    private ConsulterParametresRechercheUseCase consulterParametresRechercheUseCase;

    @MockitoBean
    private ModifierParametresRechercheUseCase modifierParametresRechercheUseCase;

    @MockitoBean
    private ConsulterParametresCvUseCase consulterParametresCvUseCase;

    @MockitoBean
    private ModifierParametresCvUseCase modifierParametresCvUseCase;

    @Nested
    class ConsulterRecherche {

        @Test
        void renvoie_200_avec_les_parametres_configures() throws Exception {
            when(consulterParametresRechercheUseCase.executer())
                    .thenReturn(new ParametresRecherche(List.of("Java"), List.of(NANTES), "CDI"));

            mockMvc.perform(get("/api/v1/parametres/recherche"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.motsCles[0]").value("Java"))
                    .andExpect(jsonPath("$.communes[0].codeInsee").value("44109"))
                    .andExpect(jsonPath("$.communes[0].libelle").value("Nantes"))
                    .andExpect(jsonPath("$.typeContrat").value("CDI"));
        }
    }

    @Nested
    class ModifierRecherche {

        @Test
        void renvoie_200_avec_les_parametres_mis_a_jour() throws Exception {
            ParametresRecherche resultat = new ParametresRecherche(List.of("Java"), List.of(NANTES), "CDI");
            when(modifierParametresRechercheUseCase.executer(eq(List.of("Java")), eq(List.of(NANTES)), eq("CDI")))
                    .thenReturn(resultat);

            ParametresRechercheRequest requete = new ParametresRechercheRequest(List.of("Java"), List.of(NANTES), "CDI");

            mockMvc.perform(put("/api/v1/parametres/recherche")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.motsCles[0]").value("Java"))
                    .andExpect(jsonPath("$.typeContrat").value("CDI"));

            verify(modifierParametresRechercheUseCase).executer(List.of("Java"), List.of(NANTES), "CDI");
        }

        @Test
        void renvoie_400_quand_plus_de_5_communes_sont_fournies() throws Exception {
            List<CommuneRecherche> sixCommunes = List.of(
                    new CommuneRecherche("44109", "Nantes"),
                    new CommuneRecherche("44020", "Saint-Herblain"),
                    new CommuneRecherche("85191", "Les Sables-d'Olonne"),
                    new CommuneRecherche("85047", "Challans"),
                    new CommuneRecherche("85194", "Talmont-Saint-Hilaire"),
                    new CommuneRecherche("44000", "Nantes centre"));
            ParametresRechercheRequest requete = new ParametresRechercheRequest(List.of("Java"), sixCommunes, "CDI");

            mockMvc.perform(put("/api/v1/parametres/recherche")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ConsulterCv {

        @Test
        void renvoie_200_avec_la_taille_max_configuree() throws Exception {
            when(consulterParametresCvUseCase.executer()).thenReturn(new ParametresCv(5_242_880L));

            mockMvc.perform(get("/api/v1/parametres/cv"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tailleMaxOctets").value(5_242_880L));
        }
    }

    @Nested
    class ModifierCv {

        @Test
        void renvoie_200_avec_la_taille_max_mise_a_jour() throws Exception {
            when(modifierParametresCvUseCase.executer(1_000_000L)).thenReturn(new ParametresCv(1_000_000L));

            ParametresCvRequest requete = new ParametresCvRequest(1_000_000L);

            mockMvc.perform(put("/api/v1/parametres/cv")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tailleMaxOctets").value(1_000_000L));

            verify(modifierParametresCvUseCase).executer(1_000_000L);
        }

        @Test
        void renvoie_400_quand_la_taille_max_n_est_pas_positive() throws Exception {
            ParametresCvRequest requete = new ParametresCvRequest(0L);

            mockMvc.perform(put("/api/v1/parametres/cv")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isBadRequest());
        }
    }
}
