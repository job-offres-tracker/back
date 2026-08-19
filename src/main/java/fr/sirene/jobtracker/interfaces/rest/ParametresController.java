package fr.sirene.jobtracker.interfaces.rest;

import fr.sirene.jobtracker.application.usecase.parametres.ConsulterParametresCvUseCase;
import fr.sirene.jobtracker.application.usecase.parametres.ConsulterParametresDocumentCandidatureUseCase;
import fr.sirene.jobtracker.application.usecase.parametres.ConsulterParametresRechercheUseCase;
import fr.sirene.jobtracker.application.usecase.parametres.ModifierParametresCvUseCase;
import fr.sirene.jobtracker.application.usecase.parametres.ModifierParametresDocumentCandidatureUseCase;
import fr.sirene.jobtracker.application.usecase.parametres.ModifierParametresRechercheUseCase;
import fr.sirene.jobtracker.domain.model.ParametresCv;
import fr.sirene.jobtracker.domain.model.ParametresDocumentCandidature;
import fr.sirene.jobtracker.domain.model.ParametresRecherche;
import fr.sirene.jobtracker.interfaces.rest.dto.ParametresCvRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.ParametresCvResponse;
import fr.sirene.jobtracker.interfaces.rest.dto.ParametresDocumentCandidatureRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.ParametresDocumentCandidatureResponse;
import fr.sirene.jobtracker.interfaces.rest.dto.ParametresRechercheRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.ParametresRechercheResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parametres")
@Validated
@Tag(name = "Paramètres", description = "Gestion des paramètres de recherche des offres et d'upload des CV")
public class ParametresController {

    private final ConsulterParametresRechercheUseCase consulterParametresRechercheUseCase;
    private final ModifierParametresRechercheUseCase modifierParametresRechercheUseCase;
    private final ConsulterParametresCvUseCase consulterParametresCvUseCase;
    private final ModifierParametresCvUseCase modifierParametresCvUseCase;
    private final ConsulterParametresDocumentCandidatureUseCase consulterParametresDocumentCandidatureUseCase;
    private final ModifierParametresDocumentCandidatureUseCase modifierParametresDocumentCandidatureUseCase;

    public ParametresController(
            ConsulterParametresRechercheUseCase consulterParametresRechercheUseCase,
            ModifierParametresRechercheUseCase modifierParametresRechercheUseCase,
            ConsulterParametresCvUseCase consulterParametresCvUseCase,
            ModifierParametresCvUseCase modifierParametresCvUseCase,
            ConsulterParametresDocumentCandidatureUseCase consulterParametresDocumentCandidatureUseCase,
            ModifierParametresDocumentCandidatureUseCase modifierParametresDocumentCandidatureUseCase) {
        this.consulterParametresRechercheUseCase = consulterParametresRechercheUseCase;
        this.modifierParametresRechercheUseCase = modifierParametresRechercheUseCase;
        this.consulterParametresCvUseCase = consulterParametresCvUseCase;
        this.modifierParametresCvUseCase = modifierParametresCvUseCase;
        this.consulterParametresDocumentCandidatureUseCase = consulterParametresDocumentCandidatureUseCase;
        this.modifierParametresDocumentCandidatureUseCase = modifierParametresDocumentCandidatureUseCase;
    }

    @Operation(
            summary = "Consulter les paramètres de recherche",
            description = "Retourne les mots-clés et communes actuellement configurés pour la synchronisation des offres.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paramètres renvoyés avec succès")
    })
    @GetMapping("/recherche")
    public ResponseEntity<ParametresRechercheResponse> consulterRecherche() {
        ParametresRecherche parametres = consulterParametresRechercheUseCase.executer();
        return ResponseEntity.ok(ParametresRechercheResponse.fromDomain(parametres));
    }

    @Operation(
            summary = "Modifier les paramètres de recherche",
            description = "Remplace intégralement les mots-clés et communes utilisés pour la synchronisation des offres "
                    + "(5 communes maximum).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paramètres mis à jour"),
            @ApiResponse(responseCode = "400", description = "Plus de 5 communes fournies",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/recherche")
    public ResponseEntity<ParametresRechercheResponse> modifierRecherche(@Valid @RequestBody ParametresRechercheRequest requete) {
        ParametresRecherche parametres = modifierParametresRechercheUseCase.executer(
                requete.motsCles(), requete.communes(), requete.typeContrat());
        return ResponseEntity.ok(ParametresRechercheResponse.fromDomain(parametres));
    }

    @Operation(
            summary = "Consulter les paramètres CV",
            description = "Retourne la taille maximale actuellement autorisée pour l'upload d'un CV.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paramètres renvoyés avec succès")
    })
    @GetMapping("/cv")
    public ResponseEntity<ParametresCvResponse> consulterCv() {
        ParametresCv parametres = consulterParametresCvUseCase.executer();
        return ResponseEntity.ok(ParametresCvResponse.fromDomain(parametres));
    }

    @Operation(
            summary = "Modifier les paramètres CV",
            description = "Met à jour la taille maximale autorisée pour l'upload d'un CV.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paramètres mis à jour"),
            @ApiResponse(responseCode = "400", description = "Taille maximale invalide (doit être positive)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/cv")
    public ResponseEntity<ParametresCvResponse> modifierCv(@Valid @RequestBody ParametresCvRequest requete) {
        ParametresCv parametres = modifierParametresCvUseCase.executer(requete.tailleMaxOctets());
        return ResponseEntity.ok(ParametresCvResponse.fromDomain(parametres));
    }

    @Operation(
            summary = "Consulter les paramètres des documents de candidature",
            description = "Retourne la taille maximale actuellement autorisée pour l'upload d'un document de candidature.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paramètres renvoyés avec succès")
    })
    @GetMapping("/document-candidature")
    public ResponseEntity<ParametresDocumentCandidatureResponse> consulterDocumentCandidature() {
        ParametresDocumentCandidature parametres = consulterParametresDocumentCandidatureUseCase.executer();
        return ResponseEntity.ok(ParametresDocumentCandidatureResponse.fromDomain(parametres));
    }

    @Operation(
            summary = "Modifier les paramètres des documents de candidature",
            description = "Met à jour la taille maximale autorisée pour l'upload d'un document de candidature.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paramètres mis à jour"),
            @ApiResponse(responseCode = "400", description = "Taille maximale invalide (doit être positive)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/document-candidature")
    public ResponseEntity<ParametresDocumentCandidatureResponse> modifierDocumentCandidature(
            @Valid @RequestBody ParametresDocumentCandidatureRequest requete) {
        ParametresDocumentCandidature parametres =
                modifierParametresDocumentCandidatureUseCase.executer(requete.tailleMaxOctets());
        return ResponseEntity.ok(ParametresDocumentCandidatureResponse.fromDomain(parametres));
    }
}
