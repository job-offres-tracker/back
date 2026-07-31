package fr.sirene.jobtracker.interfaces.rest;

import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.exception.EvenementNonTrouveException;
import fr.sirene.jobtracker.domain.exception.ExtractionOffreIAException;
import fr.sirene.jobtracker.domain.exception.GeocodageAdresseException;
import fr.sirene.jobtracker.domain.exception.OffreDejaExistanteException;
import fr.sirene.jobtracker.domain.exception.OffreEmploiApiException;
import fr.sirene.jobtracker.domain.exception.OffreNonTrouveeException;
import fr.sirene.jobtracker.domain.exception.ParametresRechercheNonConfiguresException;
import fr.sirene.jobtracker.domain.exception.RechercheCommuneException;
import fr.sirene.jobtracker.domain.exception.RecuperationPageException;
import fr.sirene.jobtracker.domain.exception.StockageFichierException;
import fr.sirene.jobtracker.domain.exception.TailleFichierDepasseeException;
import fr.sirene.jobtracker.domain.exception.TransitionEtatInvalideException;
import fr.sirene.jobtracker.domain.exception.TypeFichierNonAutoriseException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Le corps de la requête est invalide ou mal formé");
        detail.setTitle("Requête invalide");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> typeCible = determinerTypeEnumCible(ex);
        String message = typeCible != null
                ? "La valeur '%s' du paramètre '%s' est invalide. Valeurs autorisées : %s".formatted(
                        ex.getValue(), ex.getName(), joindreConstantesEnum(typeCible))
                : "La valeur '%s' du paramètre '%s' est invalide".formatted(ex.getValue(), ex.getName());

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        detail.setTitle("Paramètre invalide");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    private Class<?> determinerTypeEnumCible(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            return ex.getRequiredType();
        }
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause instanceof ConversionFailedException conversionFailedException
                    && conversionFailedException.getTargetType().getType().isEnum()) {
                return conversionFailedException.getTargetType().getType();
            }
            cause = cause.getCause();
        }
        return null;
    }

    private String joindreConstantesEnum(Class<?> typeEnum) {
        return Arrays.stream(typeEnum.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        detail.setTitle("Validation échouée");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Validation échouée");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(OffreNonTrouveeException.class)
    public ProblemDetail handleOffreNonTrouvee(OffreNonTrouveeException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Offre introuvable");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(OffreDejaExistanteException.class)
    public ProblemDetail handleOffreDejaExistante(OffreDejaExistanteException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setTitle("Offre déjà existante");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(OffreEmploiApiException.class)
    public ProblemDetail handleOffreEmploiApiException(OffreEmploiApiException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        detail.setTitle("Erreur API France Travail");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(RechercheCommuneException.class)
    public ProblemDetail handleRechercheCommuneException(RechercheCommuneException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        detail.setTitle("Erreur API Géo");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(RecuperationPageException.class)
    public ProblemDetail handleRecuperationPageException(RecuperationPageException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        detail.setTitle("Erreur de récupération de la page");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(GeocodageAdresseException.class)
    public ProblemDetail handleGeocodageAdresseException(GeocodageAdresseException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        detail.setTitle("Erreur API BAN");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Requête invalide");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ParametresRechercheNonConfiguresException.class)
    public ProblemDetail handleParametresRechercheNonConfigures(ParametresRechercheNonConfiguresException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Synchronisation impossible");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ExtractionOffreIAException.class)
    public ProblemDetail handleExtractionOffreIAException(ExtractionOffreIAException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        detail.setTitle("Erreur API Mistral");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(CvNonTrouveException.class)
    public ProblemDetail handleCvNonTrouve(CvNonTrouveException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("CV introuvable");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(CandidatureNonTrouveeException.class)
    public ProblemDetail handleCandidatureNonTrouvee(CandidatureNonTrouveeException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Candidature introuvable");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(EvenementNonTrouveException.class)
    public ProblemDetail handleEvenementNonTrouve(EvenementNonTrouveException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Événement introuvable");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(TransitionEtatInvalideException.class)
    public ProblemDetail handleTransitionEtatInvalide(TransitionEtatInvalideException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setTitle("Transition d'état invalide");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(TypeFichierNonAutoriseException.class)
    public ProblemDetail handleTypeFichierNonAutorise(TypeFichierNonAutoriseException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Type de fichier non autorisé");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(TailleFichierDepasseeException.class)
    public ProblemDetail handleTailleFichierDepassee(TailleFichierDepasseeException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Fichier trop volumineux");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler({MissingServletRequestPartException.class, MissingServletRequestParameterException.class})
    public ProblemDetail handleRequeteIncomplete(Exception ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Requête incomplète");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Le fichier envoyé dépasse la taille maximale autorisée par le serveur");
        detail.setTitle("Fichier trop volumineux");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(StockageFichierException.class)
    public ProblemDetail handleStockageFichier(StockageFichierException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        detail.setTitle("Erreur de stockage");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.debug("Exception inattendue : {}", ex.getMessage(), ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur inattendue est survenue");
        detail.setTitle("Erreur interne");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }
}
