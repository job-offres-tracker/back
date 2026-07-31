package fr.sirene.jobtracker.interfaces.rest;

import fr.sirene.jobtracker.application.usecase.ConsulterOffreUseCase;
import fr.sirene.jobtracker.application.usecase.ConsulterOffresUseCase;
import fr.sirene.jobtracker.application.usecase.CreerOffreManuelleUseCase;
import fr.sirene.jobtracker.application.usecase.GenererLettreMotivationUseCase;
import fr.sirene.jobtracker.application.usecase.ImporterOffreDepuisUrlUseCase;
import fr.sirene.jobtracker.application.usecase.MettreAJourEtatOffresUseCase;
import fr.sirene.jobtracker.application.usecase.SynchroniserOffresUseCase;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.exception.OffreDejaExistanteException;
import fr.sirene.jobtracker.domain.exception.OffreNonTrouveeException;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.ResultatPagine;
import fr.sirene.jobtracker.infrastructure.config.CorsConfig;
import fr.sirene.jobtracker.interfaces.rest.dto.CreerOffreRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.GenererLettreMotivationRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.MettreAJourEtatRequest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OffreController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CorsConfig.class))
class OffreControllerTest {

    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private MockMvc mockMvc;

    @MockitoBean
    private ConsulterOffresUseCase consulterOffresUseCase;

    @MockitoBean
    private ConsulterOffreUseCase consulterOffreUseCase;

    @MockitoBean
    private CreerOffreManuelleUseCase creerOffreManuelleUseCase;

    @MockitoBean
    private SynchroniserOffresUseCase synchroniserOffresUseCase;

    @MockitoBean
    private MettreAJourEtatOffresUseCase mettreAJourEtatOffresUseCase;
    @MockitoBean
    private ImporterOffreDepuisUrlUseCase importerOffreDepuisUrlUseCase;
    @MockitoBean
    private GenererLettreMotivationUseCase genererLettreMotivationUseCase;

    @Nested
    class ConsulterLesOffres {

        @Test
        void renvoie_200_avec_la_liste_paginee() throws Exception {
            Offre offre = Offre.builder().idExterne("123").intitule("Développeur Java").build();
            when(consulterOffresUseCase.executer(0, 20, null))
                    .thenReturn(new ResultatPagine<>(List.of(offre), 0, 20, 1));

            mockMvc.perform(get("/api/v1/offres"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.resultats[0].idExterne").value("123"));
        }

        @Test
        void renvoie_400_quand_la_page_est_negative() throws Exception {
            mockMvc.perform(get("/api/v1/offres").param("page", "-1"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void renvoie_400_quand_la_taille_depasse_le_maximum() throws Exception {
            mockMvc.perform(get("/api/v1/offres").param("taille", "101"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ConsulterUneOffre {

        @Test
        void renvoie_200_avec_le_detail_d_une_offre() throws Exception {
            Offre offre = Offre.builder().idExterne("123").intitule("Développeur Java").build();
            when(consulterOffreUseCase.executer("123")).thenReturn(offre);

            mockMvc.perform(get("/api/v1/offres/123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idExterne").value("123"));
        }

        @Test
        void renvoie_404_quand_l_offre_est_introuvable() throws Exception {
            when(consulterOffreUseCase.executer("inconnu")).thenThrow(new OffreNonTrouveeException("inconnu"));

            mockMvc.perform(get("/api/v1/offres/inconnu"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class Creer {

        @Test
        void renvoie_201_a_la_creation_d_une_offre() throws Exception {
            Offre offre = Offre.builder().idExterne("MANUEL-1").intitule("Développeur Java").build();
            when(creerOffreManuelleUseCase.executer(
                    isNull(), eq("Développeur Java"), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull()))
                    .thenReturn(offre);

            CreerOffreRequest requete = new CreerOffreRequest(
                    null, "Développeur Java", null, null, null, null, null, null, null, null, null);

            mockMvc.perform(post("/api/v1/offres")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idExterne").value("MANUEL-1"));
        }

        @Test
        void renvoie_400_quand_l_intitule_est_absent() throws Exception {
            mockMvc.perform(post("/api/v1/offres")
                            .contentType("application/json")
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void renvoie_409_quand_l_offre_existe_deja() throws Exception {
            when(creerOffreManuelleUseCase.executer(
                    eq("REF-123"), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                    .thenThrow(new OffreDejaExistanteException("REF-123"));

            CreerOffreRequest requete = new CreerOffreRequest(
                    "REF-123", "Développeur Java", null, null, null, null, null, null, null, null, null);

            mockMvc.perform(post("/api/v1/offres")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    class Synchroniser {

        @Test
        void renvoie_202_a_la_synchronisation() throws Exception {
            when(synchroniserOffresUseCase.executer()).thenReturn(5);

            mockMvc.perform(post("/api/v1/offres/synchroniser"))
                    .andExpect(status().isAccepted());
        }
    }

    @Nested
    class MettreAJourEtat {

        @Test
        void renvoie_204_a_la_mise_a_jour_d_etat() throws Exception {
            MettreAJourEtatRequest requete = new MettreAJourEtatRequest(List.of("123", "456"), EtatOffre.POSTULE);

            mockMvc.perform(patch("/api/v1/offres/etat")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isNoContent());

            verify(mettreAJourEtatOffresUseCase).executer(List.of("123", "456"), EtatOffre.POSTULE);
        }

        @Test
        void renvoie_400_quand_les_ids_sont_absents() throws Exception {
            mockMvc.perform(patch("/api/v1/offres/etat")
                            .contentType("application/json")
                            .content("{\"etat\":\"POSTULE\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GenererLettreMotivation {

        @Test
        void renvoie_200_avec_la_lettre_generee() throws Exception {
            when(genererLettreMotivationUseCase.executer("123", "cv-1")).thenReturn("Madame, Monsieur, ...");

            GenererLettreMotivationRequest requete = new GenererLettreMotivationRequest("cv-1");

            mockMvc.perform(post("/api/v1/offres/123/generer-lettre-motivation")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contenu").value("Madame, Monsieur, ..."));
        }

        @Test
        void renvoie_400_quand_le_nom_du_cv_est_absent() throws Exception {
            mockMvc.perform(post("/api/v1/offres/123/generer-lettre-motivation")
                            .contentType("application/json")
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void renvoie_404_quand_l_offre_est_introuvable() throws Exception {
            when(genererLettreMotivationUseCase.executer("inconnu", "cv-1"))
                    .thenThrow(new OffreNonTrouveeException("inconnu"));

            GenererLettreMotivationRequest requete = new GenererLettreMotivationRequest("cv-1");

            mockMvc.perform(post("/api/v1/offres/inconnu/generer-lettre-motivation")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void renvoie_404_quand_le_cv_est_introuvable() throws Exception {
            when(genererLettreMotivationUseCase.executer("123", "inconnu"))
                    .thenThrow(new CvNonTrouveException("inconnu"));

            GenererLettreMotivationRequest requete = new GenererLettreMotivationRequest("inconnu");

            mockMvc.perform(post("/api/v1/offres/123/generer-lettre-motivation")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isNotFound());
        }
    }
}
