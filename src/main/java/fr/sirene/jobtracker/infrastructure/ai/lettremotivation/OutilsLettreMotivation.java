package fr.sirene.jobtracker.infrastructure.ai.lettremotivation;

import fr.sirene.jobtracker.application.port.cv.CvRepository;
import fr.sirene.jobtracker.application.port.cv.CvStockagePort;
import fr.sirene.jobtracker.application.port.offre.OffreStorageRepository;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.exception.ExtractionTexteCvException;
import fr.sirene.jobtracker.domain.exception.OffreNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Offre;
import lombok.extern.slf4j.Slf4j;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OutilsLettreMotivation {

    private final OffreStorageRepository offreStorageRepository;
    private final CvRepository cvRepository;
    private final CvStockagePort cvStockagePort;

    public OutilsLettreMotivation(
            OffreStorageRepository offreStorageRepository,
            CvRepository cvRepository,
            CvStockagePort cvStockagePort) {
        this.offreStorageRepository = offreStorageRepository;
        this.cvRepository = cvRepository;
        this.cvStockagePort = cvStockagePort;
    }

    @Tool(description = "Récupère le détail d'une offre d'emploi (intitulé, entreprise, lieu, type de contrat, "
            + "salaire, description) à partir de son identifiant externe")
    public String recupererOffre(
            @ToolParam(description = "Identifiant externe de l'offre") String idExterneOffre) {
        Offre offre = offreStorageRepository.trouverParIdExterne(idExterneOffre)
                .orElseThrow(() -> new OffreNonTrouveeException(idExterneOffre));
        return formatterOffre(offre);
    }

    @Tool(description = "Récupère le contenu texte intégral d'un CV à partir de son nom unique")
    public String recupererCv(@ToolParam(description = "Nom unique du CV") String cvNomUnique) {
        cvRepository.trouverParNomUnique(cvNomUnique)
                .orElseThrow(() -> new CvNonTrouveException(cvNomUnique));
        byte[] contenu = cvStockagePort.lire(cvNomUnique);
        String text = extraireTexte(contenu);
        log.debug("Texte extrait du cv : {}", text);
        return text;
    }

    private String formatterOffre(Offre offre) {
        String formatted = """
                Intitulé : %s
                Entreprise : %s
                Lieu : %s
                Type de contrat : %s
                Salaire : %s
                Description :
                %s""".formatted(
                offre.getIntitule(),
                offre.getEntreprise(),
                offre.getLieu() != null ? offre.getLieu().libelle() : null,
                offre.getTypeContrat(),
                offre.getSalaire(),
                offre.getDescription());
        log.debug("Offre formattée :{}", formatted);
        return formatted;
    }

    private String extraireTexte(byte[] contenuPdf) {
        try (PDDocument document = Loader.loadPDF(contenuPdf)) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new ExtractionTexteCvException("Impossible d'extraire le texte du CV", e);
        }
    }
}
