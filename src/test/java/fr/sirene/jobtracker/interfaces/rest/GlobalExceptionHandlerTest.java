package fr.sirene.jobtracker.interfaces.rest;

import fr.sirene.jobtracker.domain.exception.GeocodageAdresseException;
import fr.sirene.jobtracker.domain.exception.OffreDejaExistanteException;
import fr.sirene.jobtracker.domain.exception.OffreNonTrouveeException;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void renvoie_404_quand_l_offre_est_introuvable() {
        ProblemDetail detail = handler.handleOffreNonTrouvee(new OffreNonTrouveeException("inconnu"));

        assertThat(detail.getStatus()).isEqualTo(404);
        assertThat(detail.getDetail()).contains("inconnu");
    }

    @Test
    void renvoie_409_quand_l_offre_existe_deja() {
        ProblemDetail detail = handler.handleOffreDejaExistante(new OffreDejaExistanteException("MANUEL-1"));

        assertThat(detail.getStatus()).isEqualTo(409);
        assertThat(detail.getDetail()).contains("MANUEL-1");
    }

    @Test
    void renvoie_502_quand_le_geocodage_ban_echoue() {
        ProblemDetail detail = handler.handleGeocodageAdresseException(
                new GeocodageAdresseException("Service de géocodage BAN indisponible [503 SERVICE_UNAVAILABLE]"));

        assertThat(detail.getStatus()).isEqualTo(502);
        assertThat(detail.getDetail()).contains("BAN");
    }

    @Test
    void renvoie_400_pour_un_argument_illegal() {
        ProblemDetail detail = handler.handleIllegalArgument(
                new IllegalArgumentException("L'URL fournie cible une ressource réseau non autorisée"));

        assertThat(detail.getStatus()).isEqualTo(400);
        assertThat(detail.getDetail()).contains("non autorisée");
    }

    @Test
    void liste_les_valeurs_autorisees_quand_le_type_cible_est_un_enum() throws NoSuchMethodException {
        Method methode = OffreController.class.getMethod("consulterLesOffres", int.class, int.class, List.class);
        MethodParameter parametre = new MethodParameter(methode, 2);
        ConversionFailedException cause = new ConversionFailedException(
                TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(EtatOffre.class), "REJETE",
                new IllegalArgumentException("No enum constant"));
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("REJETE", List.class, "etats", parametre, cause);

        ProblemDetail detail = handler.handleMethodArgumentTypeMismatch(ex);

        assertThat(detail.getStatus()).isEqualTo(400);
        assertThat(detail.getDetail()).contains("REJETE", "etats", "NON_LU", "POSTULE");
    }

    @Test
    void ne_plante_pas_et_renvoie_un_message_generique_quand_le_type_cible_n_est_pas_un_enum() throws NoSuchMethodException {
        Method methode = OffreController.class.getMethod("consulterLesOffres", int.class, int.class, List.class);
        MethodParameter parametre = new MethodParameter(methode, 0);
        ConversionFailedException cause = new ConversionFailedException(
                TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Integer.class), "abc",
                new NumberFormatException("For input string: \"abc\""));
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", int.class, "page", parametre, cause);

        ProblemDetail detail = handler.handleMethodArgumentTypeMismatch(ex);

        assertThat(detail.getStatus()).isEqualTo(400);
        assertThat(detail.getDetail()).isEqualTo("La valeur 'abc' du paramètre 'page' est invalide");
    }
}
