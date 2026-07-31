package fr.sirene.jobtracker.interfaces.rest;

import fr.sirene.jobtracker.application.usecase.AjouterDocumentCvUseCase;
import fr.sirene.jobtracker.application.usecase.AjouterDocumentFichierUseCase;
import fr.sirene.jobtracker.application.usecase.AjouterDocumentTexteUseCase;
import fr.sirene.jobtracker.application.usecase.AjouterEvenementCandidatureUseCase;
import fr.sirene.jobtracker.application.usecase.ConsulterCandidatureUseCase;
import fr.sirene.jobtracker.application.usecase.ConsulterCandidaturesUseCase;
import fr.sirene.jobtracker.application.usecase.ModifierEvenementCandidatureUseCase;
import fr.sirene.jobtracker.application.usecase.TelechargerDocumentCandidatureUseCase;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidatureTelecharge;
import fr.sirene.jobtracker.domain.model.Evenement;
import fr.sirene.jobtracker.domain.model.ResultatPagine;
import fr.sirene.jobtracker.interfaces.rest.dto.AjouterDocumentCvRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.AjouterDocumentTexteRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.CandidatureDetailResponse;
import fr.sirene.jobtracker.interfaces.rest.dto.CandidatureListItemResponse;
import fr.sirene.jobtracker.interfaces.rest.dto.CreerEvenementRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.DocumentCandidatureResponse;
import fr.sirene.jobtracker.interfaces.rest.dto.EvenementResponse;
import fr.sirene.jobtracker.interfaces.rest.dto.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/candidatures")
@Validated
@Tag(name = "Candidatures", description = "Consultation et suivi des candidatures (documents, événements)")
public class CandidatureController {

    private static final int TAILLE_PAGE_MAX = 100;

    private final ConsulterCandidaturesUseCase consulterCandidaturesUseCase;
    private final ConsulterCandidatureUseCase consulterCandidatureUseCase;
    private final AjouterEvenementCandidatureUseCase ajouterEvenementCandidatureUseCase;
    private final ModifierEvenementCandidatureUseCase modifierEvenementCandidatureUseCase;
    private final AjouterDocumentCvUseCase ajouterDocumentCvUseCase;
    private final AjouterDocumentFichierUseCase ajouterDocumentFichierUseCase;
    private final AjouterDocumentTexteUseCase ajouterDocumentTexteUseCase;
    private final TelechargerDocumentCandidatureUseCase telechargerDocumentCandidatureUseCase;

    public CandidatureController(
            ConsulterCandidaturesUseCase consulterCandidaturesUseCase,
            ConsulterCandidatureUseCase consulterCandidatureUseCase,
            AjouterEvenementCandidatureUseCase ajouterEvenementCandidatureUseCase,
            ModifierEvenementCandidatureUseCase modifierEvenementCandidatureUseCase,
            AjouterDocumentCvUseCase ajouterDocumentCvUseCase,
            AjouterDocumentFichierUseCase ajouterDocumentFichierUseCase,
            AjouterDocumentTexteUseCase ajouterDocumentTexteUseCase,
            TelechargerDocumentCandidatureUseCase telechargerDocumentCandidatureUseCase) {
        this.consulterCandidaturesUseCase = consulterCandidaturesUseCase;
        this.consulterCandidatureUseCase = consulterCandidatureUseCase;
        this.ajouterEvenementCandidatureUseCase = ajouterEvenementCandidatureUseCase;
        this.modifierEvenementCandidatureUseCase = modifierEvenementCandidatureUseCase;
        this.ajouterDocumentCvUseCase = ajouterDocumentCvUseCase;
        this.ajouterDocumentFichierUseCase = ajouterDocumentFichierUseCase;
        this.ajouterDocumentTexteUseCase = ajouterDocumentTexteUseCase;
        this.telechargerDocumentCandidatureUseCase = telechargerDocumentCandidatureUseCase;
    }

    @Operation(
            summary = "Lister les candidatures",
            description = "Retourne les candidatures de manière paginée, de la plus récente à la plus ancienne.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page de candidatures renvoyée avec succès")
    })
    @GetMapping
    public ResponseEntity<PagedResponse<CandidatureListItemResponse>> consulterLesCandidatures(
            @Parameter(description = "Numéro de la page, à partir de 0")
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "La page doit être >= 0")
            int page,

            @Parameter(description = "Nombre de candidatures par page")
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "La taille doit être >= 1")
            @Max(value = TAILLE_PAGE_MAX, message = "La taille doit être <= " + TAILLE_PAGE_MAX)
            int taille) {
        ResultatPagine<Candidature> resultat = consulterCandidaturesUseCase.executer(page, taille);
        return ResponseEntity.ok(PagedResponse.of(resultat.map(CandidatureListItemResponse::fromDomain)));
    }

    @Operation(
            summary = "Consulter une candidature",
            description = "Retourne le détail d'une candidature : offre liée, événements et documents.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidature trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune candidature ne correspond à cet identifiant",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CandidatureDetailResponse> consulterUneCandidature(@PathVariable Long id) {
        Candidature candidature = consulterCandidatureUseCase.executer(id);
        return ResponseEntity.ok(CandidatureDetailResponse.fromDomain(candidature));
    }

    @Operation(
            summary = "Ajouter un événement",
            description = "Ajoute un événement (entretien, relance, mail) à une candidature.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Événement ajouté"),
            @ApiResponse(responseCode = "404", description = "Candidature introuvable",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/evenements")
    public ResponseEntity<EvenementResponse> ajouterEvenement(
            @PathVariable Long id, @Valid @RequestBody CreerEvenementRequest requete) {
        Evenement evenement = ajouterEvenementCandidatureUseCase.executer(id, requete.date(), requete.type(), requete.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(EvenementResponse.fromDomain(evenement));
    }

    @Operation(
            summary = "Modifier un événement",
            description = "Modifie un événement existant d'une candidature.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Événement modifié"),
            @ApiResponse(responseCode = "404", description = "Candidature ou événement introuvable",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}/evenements/{evenementId}")
    public ResponseEntity<EvenementResponse> modifierEvenement(
            @PathVariable Long id, @PathVariable Long evenementId, @Valid @RequestBody CreerEvenementRequest requete) {
        Evenement evenement = modifierEvenementCandidatureUseCase.executer(
                id, evenementId, requete.date(), requete.type(), requete.description());
        return ResponseEntity.ok(EvenementResponse.fromDomain(evenement));
    }

    @Operation(
            summary = "Attacher un CV existant",
            description = "Attache un CV du paramétrage à une candidature.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Document ajouté"),
            @ApiResponse(responseCode = "404", description = "Candidature ou CV introuvable",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/documents/cv")
    public ResponseEntity<DocumentCandidatureResponse> ajouterDocumentCv(
            @PathVariable Long id, @Valid @RequestBody AjouterDocumentCvRequest requete) {
        DocumentCandidature document = ajouterDocumentCvUseCase.executer(id, requete.cvNomUnique());
        return ResponseEntity.status(HttpStatus.CREATED).body(DocumentCandidatureResponse.fromDomain(document));
    }

    @Operation(
            summary = "Uploader un document",
            description = "Ajoute un document quelconque (tout type de fichier) à une candidature.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Document ajouté"),
            @ApiResponse(responseCode = "404", description = "Candidature introuvable",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(value = "/{id}/documents/fichier", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentCandidatureResponse> ajouterDocumentFichier(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("libelle") String libelle) {
        DocumentCandidature document = ajouterDocumentFichierUseCase.executer(
                id, libelle, file.getContentType(), lireContenu(file));
        return ResponseEntity.status(HttpStatus.CREATED).body(DocumentCandidatureResponse.fromDomain(document));
    }

    @Operation(
            summary = "Ajouter un document texte",
            description = "Ajoute un document texte libre (libellé + contenu) à une candidature, sans upload de fichier.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Document ajouté"),
            @ApiResponse(responseCode = "404", description = "Candidature introuvable",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/documents/texte")
    public ResponseEntity<DocumentCandidatureResponse> ajouterDocumentTexte(
            @PathVariable Long id, @Valid @RequestBody AjouterDocumentTexteRequest requete) {
        DocumentCandidature document = ajouterDocumentTexteUseCase.executer(id, requete.libelle(), requete.contenu());
        return ResponseEntity.status(HttpStatus.CREATED).body(DocumentCandidatureResponse.fromDomain(document));
    }

    @Operation(
            summary = "Télécharger un document",
            description = "Retourne le contenu binaire d'un document de type FICHIER attaché à une candidature.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contenu du document renvoyé"),
            @ApiResponse(responseCode = "400", description = "Document introuvable ou non téléchargeable (types CV/TEXTE)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Candidature introuvable",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}/documents/{documentId}/fichier")
    public ResponseEntity<byte[]> telechargerDocument(@PathVariable Long id, @PathVariable Long documentId) {
        DocumentCandidatureTelecharge resultat = telechargerDocumentCandidatureUseCase.executer(id, documentId);
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(resultat.document().libelle(), StandardCharsets.UTF_8)
                .build();
        MediaType typeContenu = resultat.document().contentType() != null
                ? MediaType.parseMediaType(resultat.document().contentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .contentType(typeContenu)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resultat.contenu());
    }

    private byte[] lireContenu(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de lire le fichier envoyé", e);
        }
    }
}
