package fr.sirene.jobtracker.infrastructure.francetravail.client;

import fr.sirene.jobtracker.infrastructure.francetravail.dto.ReponseRechercheFranceTravail;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class FranceTravailApiClientTest {

    private final RestClient.Builder builder = RestClient.builder().baseUrl("https://api.example.com");
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    private final FranceTravailApiClient client = new FranceTravailApiClient(builder.build());

    @Test
    void construit_la_requete_avec_les_parametres_et_les_en_tetes_attendus() {
        server.expect(requestTo("https://api.example.com/offres/search?motsCles=Java&typeContrat=CDI&commune=44109"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer jeton-abc"))
                .andExpect(header("Range", "resultats=0-49"))
                .andRespond(withSuccess("{\"resultats\":[]}", MediaType.APPLICATION_JSON));

        ResponseEntity<ReponseRechercheFranceTravail> reponse =
                client.rechercherOffres("Java", "CDI", "44109", 0, 49, "jeton-abc");

        assertThat(reponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(reponse.getBody().resultats()).isEmpty();
        server.verify();
    }

    @Test
    void omet_le_parametre_commune_quand_le_code_commune_est_absent() {
        server.expect(requestTo("https://api.example.com/offres/search?motsCles=Java&typeContrat=CDI"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"resultats\":[]}", MediaType.APPLICATION_JSON));

        client.rechercherOffres("Java", "CDI", null, 0, 49, "jeton-abc");

        server.verify();
    }
}
