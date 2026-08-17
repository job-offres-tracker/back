package fr.sirene.jobtracker.interfaces.rest;

import fr.sirene.jobtracker.application.usecase.candidature.AjouterDocumentCvUseCase;
import fr.sirene.jobtracker.application.usecase.candidature.AjouterDocumentFichierUseCase;
import fr.sirene.jobtracker.application.usecase.candidature.AjouterDocumentTexteUseCase;
import fr.sirene.jobtracker.application.usecase.candidature.AjouterEvenementCandidatureUseCase;
import fr.sirene.jobtracker.application.usecase.candidature.ConsulterCandidatureParOffreUseCase;
import fr.sirene.jobtracker.application.usecase.candidature.ConsulterCandidatureUseCase;
import fr.sirene.jobtracker.application.usecase.candidature.ConsulterCandidaturesUseCase;
import fr.sirene.jobtracker.application.usecase.candidature.ModifierEvenementCandidatureUseCase;
import fr.sirene.jobtracker.application.usecase.candidature.TelechargerDocumentCandidatureUseCase;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidatureTelecharge;
import fr.sirene.jobtracker.domain.model.DocumentCv;
import fr.sirene.jobtracker.domain.model.DocumentFichier;
import fr.sirene.jobtracker.domain.model.DocumentTexte;
import fr.sirene.jobtracker.domain.model.Evenement;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.ResultatPagine;
import fr.sirene.jobtracker.domain.model.TypeEvenement;
import fr.sirene.jobtracker.infrastructure.config.CorsConfig;
import fr.sirene.jobtracker.interfaces.rest.dto.AjouterDocumentCvRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.AjouterDocumentTexteRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.CreerEvenementRequest;
import jakarta.inject.Inject;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CandidatureController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CorsConfig.class))
class CandidatureControllerTest {

    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private MockMvc mockMvc;

    @MockitoBean
    private ConsulterCandidaturesUseCase consulterCandidaturesUseCase;
    @MockitoBean
    private ConsulterCandidatureUseCase consulterCandidatureUseCase;
    @MockitoBean
    private ConsulterCandidatureParOffreUseCase consulterCandidatureParOffreUseCase;
    @MockitoBean
    private AjouterEvenementCandidatureUseCase ajouterEvenementCandidatureUseCase;
    @MockitoBean
    private ModifierEvenementCandidatureUseCase modifierEvenementCandidatureUseCase;
    @MockitoBean
    private AjouterDocumentCvUseCase ajouterDocumentCvUseCase;
    @MockitoBean
    private AjouterDocumentFichierUseCase ajouterDocumentFichierUseCase;
    @MockitoBean
    private AjouterDocumentTexteUseCase ajouterDocumentTexteUseCase;
    @MockitoBean
    private TelechargerDocumentCandidatureUseCase telechargerDocumentCandidatureUseCase;

    private static final Offre OFFRE = Offre.builder().idExterne("123").intitule("Développeur Java").build();

    @Nested
    class ConsulterLesCandidatures {

        @Test
        void renvoie_200_avec_la_liste_paginee() throws Exception {
            Candidature candidature = Candidature.builder().id(1L).offre(OFFRE).dateCandidature(LocalDateTime.now()).build();
            when(consulterCandidaturesUseCase.executer(0, 20))
                    .thenReturn(new ResultatPagine<>(List.of(candidature), 0, 20, 1));

            mockMvc.perform(get("/api/v1/candidatures"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.resultats[0].idExterne").value("123"));
        }
    }

    @Nested
    class ConsulterUneCandidature {

        @Test
        void renvoie_200_avec_le_detail() throws Exception {
            Candidature candidature = Candidature.builder().id(1L).offre(OFFRE).dateCandidature(LocalDateTime.now()).build();
            when(consulterCandidatureUseCase.executer(1L)).thenReturn(candidature);

            mockMvc.perform(get("/api/v1/candidatures/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.offre.idExterne").value("123"));
        }

        @Test
        void renvoie_404_quand_la_candidature_est_introuvable() throws Exception {
            when(consulterCandidatureUseCase.executer(99L)).thenThrow(new CandidatureNonTrouveeException(99L));

            mockMvc.perform(get("/api/v1/candidatures/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class ConsulterLaCandidatureDUneOffre {

        @Test
        void renvoie_200_avec_le_detail() throws Exception {
            Candidature candidature = Candidature.builder().id(1L).offre(OFFRE).dateCandidature(LocalDateTime.now()).build();
            when(consulterCandidatureParOffreUseCase.executer("123")).thenReturn(candidature);

            mockMvc.perform(get("/api/v1/candidatures/par-offre/123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.offre.idExterne").value("123"));
        }

        @Test
        void renvoie_404_quand_aucune_candidature_ne_correspond_a_l_offre() throws Exception {
            when(consulterCandidatureParOffreUseCase.executer("999")).thenThrow(new CandidatureNonTrouveeException("999"));

            mockMvc.perform(get("/api/v1/candidatures/par-offre/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class AjouterEvenement {

        @Test
        void renvoie_201_a_l_ajout_d_un_evenement() throws Exception {
            Evenement evenement = Evenement.builder().id(5L).date(LocalDate.now()).type(TypeEvenement.ENTRETIEN).build();
            when(ajouterEvenementCandidatureUseCase.executer(eq(1L), any(), eq(TypeEvenement.ENTRETIEN), any()))
                    .thenReturn(evenement);

            CreerEvenementRequest requete = new CreerEvenementRequest(LocalDate.now(), TypeEvenement.ENTRETIEN, "Entretien RH");

            mockMvc.perform(post("/api/v1/candidatures/1/evenements")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(5));
        }

        @Test
        void renvoie_400_quand_le_type_est_absent() throws Exception {
            mockMvc.perform(post("/api/v1/candidatures/1/evenements")
                            .contentType("application/json")
                            .content("{\"date\":\"2026-08-01\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ModifierEvenement {

        @Test
        void renvoie_200_a_la_modification_d_un_evenement() throws Exception {
            Evenement evenement = Evenement.builder().id(5L).date(LocalDate.now()).type(TypeEvenement.RELANCE).build();
            when(modifierEvenementCandidatureUseCase.executer(eq(1L), eq(5L), any(), eq(TypeEvenement.RELANCE), any()))
                    .thenReturn(evenement);

            CreerEvenementRequest requete = new CreerEvenementRequest(LocalDate.now(), TypeEvenement.RELANCE, "Relance mail");

            mockMvc.perform(put("/api/v1/candidatures/1/evenements/5")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class AjouterDocumentCv {

        @Test
        void renvoie_201_a_l_attachement_d_un_cv() throws Exception {
            DocumentCandidature document = new DocumentCv(2L, "cv.pdf", "abc.pdf", 12_345L, LocalDateTime.now());
            when(ajouterDocumentCvUseCase.executer(1L, "abc.pdf")).thenReturn(document);

            mockMvc.perform(post("/api/v1/candidatures/1/documents/cv")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(new AjouterDocumentCvRequest("abc.pdf"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cvNomUnique").value("abc.pdf"))
                    .andExpect(jsonPath("$.tailleOctets").value(12_345L));
        }
    }

    @Nested
    class AjouterDocumentFichier {

        @Test
        void renvoie_201_a_l_upload_d_un_fichier() throws Exception {
            DocumentCandidature document = new DocumentFichier(3L, "Lettre", "xyz", 3, null, LocalDateTime.now());
            when(ajouterDocumentFichierUseCase.executer(eq(1L), eq("Lettre"), any(), any())).thenReturn(document);

            MockMultipartFile fichier = new MockMultipartFile("file", "lettre.docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", new byte[] {1, 2, 3});

            mockMvc.perform(multipart("/api/v1/candidatures/1/documents/fichier").file(fichier).param("libelle", "Lettre"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.libelle").value("Lettre"));
        }
    }

    @Nested
    class AjouterDocumentTexte {

        @Test
        void renvoie_201_a_l_ajout_d_un_document_texte() throws Exception {
            DocumentCandidature document = new DocumentTexte(4L, "Notes", "Bon feeling", LocalDateTime.now());
            when(ajouterDocumentTexteUseCase.executer(1L, "Notes", "Bon feeling")).thenReturn(document);

            mockMvc.perform(post("/api/v1/candidatures/1/documents/texte")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(new AjouterDocumentTexteRequest("Notes", "Bon feeling"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.contenuTexte").value("Bon feeling"));
        }

        @Test
        void renvoie_400_quand_le_contenu_est_absent() throws Exception {
            mockMvc.perform(post("/api/v1/candidatures/1/documents/texte")
                            .contentType("application/json")
                            .content("{\"libelle\":\"Notes\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void renvoie_400_quand_le_contenu_depasse_10000_caracteres() throws Exception {
            String contenuTropLong = "a".repeat(10_001);
            AjouterDocumentTexteRequest requete = new AjouterDocumentTexteRequest("Notes", contenuTropLong);

            mockMvc.perform(post("/api/v1/candidatures/1/documents/texte")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(requete)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class TelechargerDocument {

        @Test
        void renvoie_200_avec_le_contenu_du_fichier() throws Exception {
            DocumentFichier document = new DocumentFichier(3L, "lettre.pdf", "xyz", 3, "application/pdf", LocalDateTime.now());
            when(telechargerDocumentCandidatureUseCase.executer(1L, 3L))
                    .thenReturn(new DocumentCandidatureTelecharge(document, new byte[] {1, 2, 3}));

            mockMvc.perform(get("/api/v1/candidatures/1/documents/3/fichier"))
                    .andExpect(status().isOk());
        }

        @Test
        void renvoie_400_quand_le_document_n_est_pas_telechargeable() throws Exception {
            when(telechargerDocumentCandidatureUseCase.executer(1L, 3L))
                    .thenThrow(new IllegalArgumentException("Ce document n'est pas un fichier téléchargeable"));

            mockMvc.perform(get("/api/v1/candidatures/1/documents/3/fichier"))
                    .andExpect(status().isBadRequest());
        }
    }
}
