package fr.sirene.jobtracker.interfaces.rest;

import fr.sirene.jobtracker.application.usecase.ConsulterOffreUseCase;
import fr.sirene.jobtracker.application.usecase.ConsulterOffresUseCase;
import fr.sirene.jobtracker.application.usecase.CreerOffreManuelleUseCase;
import fr.sirene.jobtracker.application.usecase.ImporterOffreDepuisUrlUseCase;
import fr.sirene.jobtracker.application.usecase.MettreAJourEtatOffresUseCase;
import fr.sirene.jobtracker.application.usecase.SynchroniserOffresUseCase;
import fr.sirene.jobtracker.domain.model.BrouillonOffre;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.ResultatPagine;
import fr.sirene.jobtracker.interfaces.rest.dto.CreerOffreRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.ImporterOffreRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.MettreAJourEtatRequest;
import fr.sirene.jobtracker.interfaces.rest.dto.OffreExtraiteResponse;
import fr.sirene.jobtracker.interfaces.rest.dto.OffreResponse;
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
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offres")
@Validated
@Slf4j
@Tag(name = "Offres", description = "Consultation, création et suivi des offres d'emploi")
public class OffreController {

    private static final String PROVENANCE_HELLOWORK = "hellowork";
    private static final int TAILLE_PAGE_MAX = 100;

    private final ConsulterOffresUseCase consulterOffresUseCase;
    private final ConsulterOffreUseCase consulterOffreUseCase;
    private final CreerOffreManuelleUseCase creerOffreManuelleUseCase;
    private final SynchroniserOffresUseCase synchroniserOffresUseCase;
    private final MettreAJourEtatOffresUseCase mettreAJourEtatOffresUseCase;
    private final ImporterOffreDepuisUrlUseCase importerOffreDepuisUrlUseCase;

    public OffreController(
            ConsulterOffresUseCase consulterOffresUseCase,
            ConsulterOffreUseCase consulterOffreUseCase,
            CreerOffreManuelleUseCase creerOffreManuelleUseCase,
            SynchroniserOffresUseCase synchroniserOffresUseCase,
            MettreAJourEtatOffresUseCase mettreAJourEtatOffresUseCase,
            ImporterOffreDepuisUrlUseCase importerOffreDepuisUrlUseCase) {
        this.consulterOffresUseCase = consulterOffresUseCase;
        this.consulterOffreUseCase = consulterOffreUseCase;
        this.creerOffreManuelleUseCase = creerOffreManuelleUseCase;
        this.synchroniserOffresUseCase = synchroniserOffresUseCase;
        this.mettreAJourEtatOffresUseCase = mettreAJourEtatOffresUseCase;
        this.importerOffreDepuisUrlUseCase = importerOffreDepuisUrlUseCase;
    }

    @Operation(
            summary = "Lister les offres",
            description = "Retourne les offres d'emploi de manière paginée, avec filtrage optionnel par état(s).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page d'offres renvoyée avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètre de pagination ou état invalide",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping
    public ResponseEntity<PagedResponse<OffreResponse>> consulterLesOffres(
            @Parameter(description = "Numéro de la page, à partir de 0")
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "La page doit être >= 0")
            int page,

            @Parameter(description = "Nombre d'offres par page")
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "La taille doit être >= 1")
            @Max(value = TAILLE_PAGE_MAX, message = "La taille doit être <= " + TAILLE_PAGE_MAX)
            int taille,

            @Parameter(description = "Filtre optionnel sur un ou plusieurs états de l'offre")
            @RequestParam(required = false)
            List<EtatOffre> etats) {

        ResultatPagine<Offre> resultat = consulterOffresUseCase.executer(page, taille, etats);
        return ResponseEntity.ok(PagedResponse.of(resultat.map(OffreResponse::fromDomain)));
    }

    @Operation(
            summary = "Consulter une offre",
            description = "Retourne le détail d'une offre à partir de son identifiant externe (France Travail ou saisi manuellement).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Offre trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune offre ne correspond à cet identifiant externe",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{idExterne}")
    public ResponseEntity<OffreResponse> consulterUneOffre(
            @Parameter(description = "Identifiant externe de l'offre", required = true)
            @PathVariable String idExterne) {
        Offre offre = consulterOffreUseCase.executer(idExterne);
        return ResponseEntity.ok(OffreResponse.fromDomain(offre));
    }

    @Operation(
            summary = "Créer une offre manuellement",
            description = "Ajoute une offre saisie à la main, en dehors de la synchronisation France Travail.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Offre créée"),
            @ApiResponse(responseCode = "400", description = "Corps de requête invalide (intitulé manquant, etc.)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Une offre existe déjà avec cet identifiant externe",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<OffreResponse> creer(@Valid @RequestBody CreerOffreRequest requete) {
        Offre offre = creerOffreManuelleUseCase.executer(
                requete.idExterne(), requete.intitule(), requete.description(), requete.entreprise(),
                requete.lieu(), requete.typeContrat(), requete.salaire(), requete.urlOrigine(),
                requete.dateCreation(), requete.provenance(), requete.etat());
        return ResponseEntity.status(HttpStatus.CREATED).body(OffreResponse.fromDomain(offre));
    }

    @Operation(
            summary = "Déclencher une synchronisation France Travail",
            description = "Récupère et enregistre les dernières offres depuis l'API France Travail "
                    + "(la même synchronisation que celle exécutée automatiquement par le planificateur).")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Synchronisation exécutée et traitée"),
            @ApiResponse(responseCode = "400", description = "Aucun mot-clé ou aucune commune n'est configuré(e)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Erreur lors de l'appel à l'API France Travail",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/synchroniser")
    public ResponseEntity<Void> synchroniser() {
        var nbOffres = synchroniserOffresUseCase.executer();
        log.info("Synchronisation France Travail terminée : {} offre(s) traitée(s)", nbOffres);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Mettre à jour l'état d'offres",
            description = "Met à jour en masse l'état (lu, postulé, refusé, ...) d'une liste d'offres identifiées par leur identifiant externe.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "État(s) mis à jour"),
            @ApiResponse(responseCode = "400", description = "Liste d'identifiants vide ou état manquant",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/etat")
    public ResponseEntity<Void> mettreAJourEtat(@Valid @RequestBody MettreAJourEtatRequest requete) {
        mettreAJourEtatOffresUseCase.executer(requete.idsExternes(), requete.etat());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Importer une offre depuis une URL",
            description = "Récupère le contenu d'une page d'offre d'emploi (ex. HelloWork) et en extrait les champs "
                    + "via une IA. Ne crée PAS l'offre : les champs extraits sont renvoyés pour être vérifiés/corrigés "
                    + "par l'utilisateur avant soumission via l'endpoint de création.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Champs extraits avec succès"),
            @ApiResponse(responseCode = "400", description = "URL manquante ou invalide",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Échec de récupération de la page ou de l'appel à l'IA",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/importer")
    public ResponseEntity<OffreExtraiteResponse> importer(@Valid @RequestBody ImporterOffreRequest requete) {
        log.debug("requete: {}", requete);
        BrouillonOffre brouillon = importerOffreDepuisUrlUseCase.executer(requete.url());
        return ResponseEntity.ok(OffreExtraiteResponse.fromDomain(brouillon, PROVENANCE_HELLOWORK));
    }
}
