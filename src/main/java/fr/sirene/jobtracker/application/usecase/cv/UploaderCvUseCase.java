package fr.sirene.jobtracker.application.usecase.cv;

import fr.sirene.jobtracker.application.port.cv.CvRepository;
import fr.sirene.jobtracker.application.port.cv.CvStockagePort;
import fr.sirene.jobtracker.application.port.parametres.ParametresCvRepository;
import fr.sirene.jobtracker.domain.exception.TailleFichierDepasseeException;
import fr.sirene.jobtracker.domain.exception.TypeFichierNonAutoriseException;
import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.domain.model.ParametresCv;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class UploaderCvUseCase {

    private static final String TYPE_PDF = "application/pdf";

    private final CvRepository cvRepository;
    private final CvStockagePort cvStockagePort;
    private final ParametresCvRepository parametresCvRepository;

    public UploaderCvUseCase(
            CvRepository cvRepository, CvStockagePort cvStockagePort, ParametresCvRepository parametresCvRepository) {
        this.cvRepository = cvRepository;
        this.cvStockagePort = cvStockagePort;
        this.parametresCvRepository = parametresCvRepository;
    }

    public Cv executer(String nomOriginal, String typeContenu, byte[] contenu) {
        if (!TYPE_PDF.equals(typeContenu)) {
            throw new TypeFichierNonAutoriseException("Seuls les fichiers PDF sont acceptés");
        }

        ParametresCv parametres = parametresCvRepository.recuperer();
        if (contenu.length > parametres.tailleMaxOctets()) {
            throw new TailleFichierDepasseeException(
                    "Le fichier dépasse la taille maximale autorisée de %d octets".formatted(parametres.tailleMaxOctets()));
        }

        String nomUnique = UUID.randomUUID() + ".pdf";
        cvStockagePort.ecrire(nomUnique, contenu);

        Cv cv = Cv.builder()
                .nomUnique(nomUnique)
                .nomOriginal(avecExtensionPdf(nomOriginal))
                .tailleOctets(contenu.length)
                .dateUpload(Instant.now())
                .build();
        return cvRepository.sauvegarder(cv);
    }

    private String avecExtensionPdf(String nom) {
        return nom.toLowerCase(Locale.ROOT).endsWith(".pdf") ? nom : nom + ".pdf";
    }
}
