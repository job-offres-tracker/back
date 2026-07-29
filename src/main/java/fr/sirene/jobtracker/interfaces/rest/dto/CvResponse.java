package fr.sirene.jobtracker.interfaces.rest.dto;

import fr.sirene.jobtracker.domain.model.Cv;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Métadonnées d'un CV persisté")
public record CvResponse(
        @Schema(description = "Nom de fichier unique généré côté serveur, à utiliser pour le téléchargement",
                example = "3f1b2c4a-...-pdf")
        String nomUnique,

        @Schema(description = "Nom de fichier original fourni lors de l'upload", example = "cv-jean-dupont.pdf")
        String nomOriginal,

        @Schema(description = "Taille du fichier en octets", example = "245678")
        long tailleOctets,

        @Schema(description = "Date et heure de l'upload")
        Instant dateUpload
) {
    public static CvResponse fromDomain(Cv cv) {
        return new CvResponse(cv.getNomUnique(), cv.getNomOriginal(), cv.getTailleOctets(), cv.getDateUpload());
    }
}
