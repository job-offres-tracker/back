package fr.sirene.jobtracker.interfaces.rest;

import fr.sirene.jobtracker.application.usecase.ListerCvUseCase;
import fr.sirene.jobtracker.application.usecase.TelechargerCvUseCase;
import fr.sirene.jobtracker.application.usecase.UploaderCvUseCase;
import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.domain.model.CvTelecharge;
import fr.sirene.jobtracker.interfaces.rest.dto.CvResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cvs")
@Slf4j
@Tag(name = "CV", description = "Upload, listing et téléchargement des CV")
public class CvController {

    private final UploaderCvUseCase uploaderCvUseCase;
    private final ListerCvUseCase listerCvUseCase;
    private final TelechargerCvUseCase telechargerCvUseCase;

    public CvController(
            UploaderCvUseCase uploaderCvUseCase, ListerCvUseCase listerCvUseCase, TelechargerCvUseCase telechargerCvUseCase) {
        this.uploaderCvUseCase = uploaderCvUseCase;
        this.listerCvUseCase = listerCvUseCase;
        this.telechargerCvUseCase = telechargerCvUseCase;
    }

    @Operation(
            summary = "Uploader un CV",
            description = "Persiste un CV au format PDF. Un nom de fichier unique est généré côté serveur ; "
                    + "le nom affiché (téléchargement/liste) est celui du fichier envoyé, sauf si un nom est fourni explicitement.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CV enregistré"),
            @ApiResponse(responseCode = "400", description = "Fichier absent, non-PDF ou trop volumineux",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CvResponse> uploader(
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Nom d'affichage du CV ; nom du fichier envoyé utilisé par défaut si absent")
            @RequestParam(value = "nom", required = false) String nom) {
        String nomAffiche = nom != null && !nom.isBlank() ? nom : file.getOriginalFilename();
        if (nomAffiche == null || nomAffiche.isBlank()) {
            throw new IllegalArgumentException(
                    "Le nom du CV est requis : fournissez le paramètre 'nom' ou un fichier avec un nom");
        }
        Cv cv = uploaderCvUseCase.executer(nomAffiche, file.getContentType(), lireContenu(file));
        return ResponseEntity.status(HttpStatus.CREATED).body(CvResponse.fromDomain(cv));
    }

    @Operation(
            summary = "Lister les CV",
            description = "Retourne les métadonnées de tous les CV persistés, du plus récent au plus ancien.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste renvoyée avec succès")
    })
    @GetMapping
    public ResponseEntity<List<CvResponse>> lister() {
        List<CvResponse> reponse = listerCvUseCase.executer().stream().map(CvResponse::fromDomain).toList();
        return ResponseEntity.ok(reponse);
    }

    @Operation(
            summary = "Télécharger un CV",
            description = "Retourne le contenu binaire d'un CV à partir de son nom de fichier unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contenu du CV renvoyé"),
            @ApiResponse(responseCode = "404", description = "Aucun CV ne correspond à ce nom unique",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{nomUnique}")
    public ResponseEntity<byte[]> telecharger(@PathVariable String nomUnique) {
        CvTelecharge resultat = telechargerCvUseCase.executer(nomUnique);
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(resultat.cv().getNomOriginal(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
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
