package fr.sirene.jobtracker.infrastructure.francetravail.client;

import fr.sirene.jobtracker.infrastructure.francetravail.config.FranceTravailAuthProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class FranceTravailAuthClientTest {

    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

    private final FranceTravailAuthProperties properties = new FranceTravailAuthProperties(
            "https://auth.example.com/token", "id-123", "secret-456", "scope-a scope-b",
            Duration.ofSeconds(5), Duration.ofSeconds(10));

    private final FranceTravailAuthClient client = new FranceTravailAuthClient(builder.build(), properties);

    @Test
    void recupere_un_token_et_transmet_les_identifiants() {
        server.expect(requestTo("https://auth.example.com/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(containsString("client_id=id-123")))
                .andExpect(content().string(containsString("client_secret=secret-456")))
                .andExpect(content().string(containsString("grant_type=client_credentials")))
                .andRespond(withSuccess(
                        "{\"access_token\":\"jeton-abc\",\"expires_in\":1200,\"token_type\":\"Bearer\"}",
                        MediaType.APPLICATION_JSON));

        String token = client.obtenirToken();

        assertThat(token).isEqualTo("jeton-abc");
        server.verify();
    }

    @Test
    void reutilise_le_token_en_cache_tant_qu_il_n_est_pas_expire() {
        server.expect(requestTo("https://auth.example.com/token"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        "{\"access_token\":\"jeton-abc\",\"expires_in\":1200,\"token_type\":\"Bearer\"}",
                        MediaType.APPLICATION_JSON));

        String premier = client.obtenirToken();
        String second = client.obtenirToken();

        assertThat(premier).isEqualTo("jeton-abc");
        assertThat(second).isEqualTo("jeton-abc");
        server.verify();
    }
}
