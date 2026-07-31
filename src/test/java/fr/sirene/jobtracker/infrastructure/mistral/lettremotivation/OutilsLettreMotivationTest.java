package fr.sirene.jobtracker.infrastructure.mistral.lettremotivation;

import fr.sirene.jobtracker.application.port.CvRepository;
import fr.sirene.jobtracker.application.port.CvStockagePort;
import fr.sirene.jobtracker.application.port.OffreStorageRepository;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.exception.ExtractionTexteCvException;
import fr.sirene.jobtracker.domain.exception.OffreNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.domain.model.Lieu;
import fr.sirene.jobtracker.domain.model.Offre;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutilsLettreMotivationTest {

    @Mock
    private OffreStorageRepository offreStorageRepository;

    @Mock
    private CvRepository cvRepository;

    @Mock
    private CvStockagePort cvStockagePort;

    @InjectMocks
    private OutilsLettreMotivation outils;

    @Test
    void recupererOffre_retourne_le_detail_formate_de_l_offre() {
        Offre offre = Offre.builder()
                .idExterne("123")
                .intitule("Développeur Java")
                .entreprise("Acme")
                .lieu(new Lieu("Paris", "75056", null, null, null))
                .typeContrat("CDI")
                .salaire("45K€")
                .description("Rejoignez notre équipe")
                .build();
        when(offreStorageRepository.trouverParIdExterne("123")).thenReturn(Optional.of(offre));

        String resultat = outils.recupererOffre("123");

        assertThat(resultat)
                .contains("Développeur Java")
                .contains("Acme")
                .contains("Paris")
                .contains("CDI")
                .contains("45K€")
                .contains("Rejoignez notre équipe");
    }

    @Test
    void recupererOffre_leve_une_exception_quand_l_offre_est_introuvable() {
        when(offreStorageRepository.trouverParIdExterne("inconnu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> outils.recupererOffre("inconnu"))
                .isInstanceOf(OffreNonTrouveeException.class);
    }

    @Test
    void recupererCv_extrait_le_texte_du_pdf() throws IOException {
        byte[] pdf = genererPdf("Jean Dupont - Développeur Java - 5 ans d'expérience");
        when(cvRepository.trouverParNomUnique("cv-1")).thenReturn(Optional.of(Cv.builder().nomUnique("cv-1").build()));
        when(cvStockagePort.lire("cv-1")).thenReturn(pdf);

        String resultat = outils.recupererCv("cv-1");

        assertThat(resultat).contains("Jean Dupont", "Développeur Java");
    }

    @Test
    void recupererCv_leve_une_exception_quand_le_cv_est_introuvable() {
        when(cvRepository.trouverParNomUnique("inconnu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> outils.recupererCv("inconnu"))
                .isInstanceOf(CvNonTrouveException.class);
    }

    @Test
    void recupererCv_leve_une_exception_quand_le_contenu_n_est_pas_un_pdf_lisible() {
        when(cvRepository.trouverParNomUnique("cv-1")).thenReturn(Optional.of(Cv.builder().nomUnique("cv-1").build()));
        when(cvStockagePort.lire("cv-1")).thenReturn("pas un pdf".getBytes());

        assertThatThrownBy(() -> outils.recupererCv("cv-1"))
                .isInstanceOf(ExtractionTexteCvException.class);
    }

    private byte[] genererPdf(String texte) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText(texte);
                contentStream.endText();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }
}
