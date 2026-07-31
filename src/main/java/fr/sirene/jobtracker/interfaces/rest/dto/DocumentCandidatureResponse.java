package fr.sirene.jobtracker.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentCv;
import fr.sirene.jobtracker.domain.model.DocumentFichier;
import fr.sirene.jobtracker.domain.model.DocumentTexte;
import fr.sirene.jobtracker.domain.model.TypeDocument;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Document lié à une candidature (CV existant, fichier uploadé ou texte libre)")
public record DocumentCandidatureResponse(
        @Schema(description = "Identifiant du document")
        Long id,

        @Schema(description = "Type de document")
        TypeDocument type,

        @Schema(description = "Libellé d'affichage du document")
        String libelle,

        @Schema(description = "Nom unique du CV référencé (uniquement pour le type CV)")
        String cvNomUnique,

        @Schema(description = "Taille du fichier en octets (types CV et FICHIER)")
        Long tailleOctets,

        @Schema(description = "Type MIME du fichier (uniquement pour le type FICHIER)")
        String contentType,

        @Schema(description = "Contenu texte (uniquement pour le type TEXTE)")
        String contenuTexte,

        @Schema(description = "Date d'ajout du document")
        LocalDateTime dateAjout
) {
    public static DocumentCandidatureResponse fromDomain(DocumentCandidature document) {
        return switch (document) {
            case DocumentCv cv -> new DocumentCandidatureResponse(
                    cv.id(), TypeDocument.CV, cv.libelle(), cv.cvNomUnique(), cv.tailleOctets(), null, null, cv.dateAjout());
            case DocumentFichier fichier -> new DocumentCandidatureResponse(
                    fichier.id(), TypeDocument.FICHIER, fichier.libelle(), null, fichier.tailleOctets(),
                    fichier.contentType(), null, fichier.dateAjout());
            case DocumentTexte texte -> new DocumentCandidatureResponse(
                    texte.id(), TypeDocument.TEXTE, texte.libelle(), null, null, null, texte.contenuTexte(), texte.dateAjout());
        };
    }
}
