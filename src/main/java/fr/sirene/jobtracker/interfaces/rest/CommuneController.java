package fr.sirene.jobtracker.interfaces.rest;

import fr.sirene.jobtracker.application.usecase.RechercherCommunesUseCase;
import fr.sirene.jobtracker.domain.model.Commune;
import fr.sirene.jobtracker.interfaces.rest.dto.CommuneResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Size;

import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/communes")
@Validated
@Tag(name = "Communes", description = "Recherche de communes françaises par nom, pour associer un code INSEE à une offre")
public class CommuneController {

    private final RechercherCommunesUseCase rechercherCommunesUseCase;

    public CommuneController(RechercherCommunesUseCase rechercherCommunesUseCase) {
        this.rechercherCommunesUseCase = rechercherCommunesUseCase;
    }

    @Operation(
            summary = "Rechercher des communes par nom",
            description = "Retourne les communes dont le nom correspond à la recherche, avec leur code INSEE, "
                    + "pour permettre à l'utilisateur de sélectionner un lieu précis lors de la saisie d'une offre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des communes correspondantes (peut être vide)"),
            @ApiResponse(responseCode = "400", description = "Recherche trop courte (moins de 2 caractères)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Service de recherche de communes indisponible",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping
    public ResponseEntity<List<CommuneResponse>> rechercher(
            @Parameter(description = "Nom (ou début de nom) de la commune recherchée", required = true)
            @RequestParam
            @Size(min = 2, message = "La recherche doit contenir au moins 2 caractères")
            String q) {

        List<Commune> communes = rechercherCommunesUseCase.executer(q);
        return ResponseEntity.ok(communes.stream().map(CommuneResponse::fromDomain).toList());
    }
}
