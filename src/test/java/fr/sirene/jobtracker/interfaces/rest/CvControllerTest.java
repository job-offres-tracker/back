package fr.sirene.jobtracker.interfaces.rest;

import fr.sirene.jobtracker.application.usecase.ListerCvUseCase;
import fr.sirene.jobtracker.application.usecase.TelechargerCvUseCase;
import fr.sirene.jobtracker.application.usecase.UploaderCvUseCase;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.exception.TailleFichierDepasseeException;
import fr.sirene.jobtracker.domain.exception.TypeFichierNonAutoriseException;
import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.domain.model.CvTelecharge;
import fr.sirene.jobtracker.infrastructure.config.CorsConfig;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CvController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CorsConfig.class))
class CvControllerTest {

    @Inject
    private MockMvc mockMvc;

    @MockitoBean
    private UploaderCvUseCase uploaderCvUseCase;

    @MockitoBean
    private ListerCvUseCase listerCvUseCase;

    @MockitoBean
    private TelechargerCvUseCase telechargerCvUseCase;

    @Nested
    class Uploader {

        @Test
        void renvoie_201_a_l_upload_d_un_pdf() throws Exception {
            Cv cv = Cv.builder().nomUnique("abc.pdf").nomOriginal("cv.pdf").tailleOctets(4).dateUpload(Instant.now()).build();
            when(uploaderCvUseCase.executer(anyString(), anyString(), any())).thenReturn(cv);

            MockMultipartFile fichier = new MockMultipartFile("file", "cv.pdf", "application/pdf", new byte[] {1, 2, 3, 4});

            mockMvc.perform(multipart("/api/v1/cvs").file(fichier))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nomUnique").value("abc.pdf"));
        }

        @Test
        void utilise_le_nom_du_fichier_quand_aucun_nom_n_est_fourni() throws Exception {
            Cv cv = Cv.builder().nomUnique("abc.pdf").nomOriginal("cv.pdf").tailleOctets(4).dateUpload(Instant.now()).build();
            when(uploaderCvUseCase.executer(anyString(), anyString(), any())).thenReturn(cv);

            MockMultipartFile fichier = new MockMultipartFile("file", "cv.pdf", "application/pdf", new byte[] {1, 2, 3, 4});

            mockMvc.perform(multipart("/api/v1/cvs").file(fichier))
                    .andExpect(status().isCreated());

            verify(uploaderCvUseCase).executer(eq("cv.pdf"), anyString(), any());
        }

        @Test
        void transmet_le_nom_personnalise_fourni_au_use_case() throws Exception {
            Cv cv = Cv.builder().nomUnique("abc.pdf").nomOriginal("Mon CV.pdf").tailleOctets(4).dateUpload(Instant.now()).build();
            when(uploaderCvUseCase.executer(eq("Mon CV"), anyString(), any())).thenReturn(cv);

            MockMultipartFile fichier = new MockMultipartFile("file", "cv.pdf", "application/pdf", new byte[] {1, 2, 3, 4});

            mockMvc.perform(multipart("/api/v1/cvs").file(fichier).param("nom", "Mon CV"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nomOriginal").value("Mon CV.pdf"));

            verify(uploaderCvUseCase).executer(eq("Mon CV"), anyString(), any());
        }

        @Test
        void renvoie_400_quand_ni_nom_ni_nom_de_fichier_ne_sont_fournis() throws Exception {
            MockMultipartFile fichier = new MockMultipartFile("file", null, "application/pdf", new byte[] {1, 2, 3, 4});

            mockMvc.perform(multipart("/api/v1/cvs").file(fichier))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void renvoie_400_quand_la_partie_fichier_est_absente() throws Exception {
            mockMvc.perform(multipart("/api/v1/cvs"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void renvoie_400_quand_le_fichier_n_est_pas_un_pdf() throws Exception {
            when(uploaderCvUseCase.executer(anyString(), anyString(), any()))
                    .thenThrow(new TypeFichierNonAutoriseException("Seuls les fichiers PDF sont acceptés"));

            MockMultipartFile fichier = new MockMultipartFile("file", "cv.docx", "application/msword", new byte[] {1});

            mockMvc.perform(multipart("/api/v1/cvs").file(fichier))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void renvoie_400_quand_le_fichier_depasse_la_taille_max() throws Exception {
            when(uploaderCvUseCase.executer(anyString(), anyString(), any()))
                    .thenThrow(new TailleFichierDepasseeException("trop volumineux"));

            MockMultipartFile fichier = new MockMultipartFile("file", "cv.pdf", "application/pdf", new byte[] {1});

            mockMvc.perform(multipart("/api/v1/cvs").file(fichier))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class Lister {

        @Test
        void renvoie_200_avec_la_liste_des_cv() throws Exception {
            Cv cv = Cv.builder().nomUnique("abc.pdf").nomOriginal("cv.pdf").tailleOctets(4).dateUpload(Instant.now()).build();
            when(listerCvUseCase.executer()).thenReturn(List.of(cv));

            mockMvc.perform(get("/api/v1/cvs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nomUnique").value("abc.pdf"));
        }
    }

    @Nested
    class Telecharger {

        @Test
        void renvoie_200_avec_le_contenu_et_le_nom_original() throws Exception {
            Cv cv = Cv.builder().nomUnique("abc.pdf").nomOriginal("cv-jean.pdf").tailleOctets(3).dateUpload(Instant.now()).build();
            when(telechargerCvUseCase.executer("abc.pdf")).thenReturn(new CvTelecharge(cv, new byte[] {1, 2, 3}));

            mockMvc.perform(get("/api/v1/cvs/abc.pdf"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition",
                            "attachment; filename=\"cv-jean.pdf\"; filename*=UTF-8''cv-jean.pdf"));
        }

        @Test
        void renvoie_404_quand_le_cv_est_introuvable() throws Exception {
            when(telechargerCvUseCase.executer("inconnu.pdf")).thenThrow(new CvNonTrouveException("inconnu.pdf"));

            mockMvc.perform(get("/api/v1/cvs/inconnu.pdf"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void echappe_les_guillemets_dans_le_nom_de_fichier() throws Exception {
            Cv cv = Cv.builder().nomUnique("abc.pdf").nomOriginal("cv \"perso\".pdf").tailleOctets(3).dateUpload(Instant.now()).build();
            when(telechargerCvUseCase.executer("abc.pdf")).thenReturn(new CvTelecharge(cv, new byte[] {1, 2, 3}));

            mockMvc.perform(get("/api/v1/cvs/abc.pdf"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition",
                            "attachment; filename=\"cv \\\"perso\\\".pdf\"; filename*=UTF-8''cv%20%22perso%22.pdf"));
        }
    }
}
