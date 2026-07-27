package fr.sirene.jobtracker.infrastructure.scraping.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PageWebClientTest {

    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    private final PageWebClient client = new PageWebClient(builder.build());

    @Test
    void telecharge_le_contenu_d_une_url_publique_valide() {
        server.expect(requestTo("http://93.184.216.34/offre"))
                .andRespond(withSuccess("<html>contenu</html>", MediaType.TEXT_HTML));

        String contenu = client.telecharger("http://93.184.216.34/offre");

        assertThat(contenu).isEqualTo("<html>contenu</html>");
        server.verify();
    }

    @Test
    void rejette_un_schema_different_de_http_ou_https() {
        assertThatThrownBy(() -> client.telecharger("ftp://93.184.216.34/offre"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("http et https");
    }

    @Test
    void rejette_une_url_malformee() {
        assertThatThrownBy(() -> client.telecharger("http://"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejette_une_cible_loopback_ipv4() {
        assertThatThrownBy(() -> client.telecharger("http://127.0.0.1/admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non autorisée");
    }

    @Test
    void rejette_une_cible_loopback_ipv6() {
        assertThatThrownBy(() -> client.telecharger("http://[::1]/admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non autorisée");
    }

    @Test
    void rejette_l_adresse_de_metadonnees_cloud_lien_local() {
        assertThatThrownBy(() -> client.telecharger("http://169.254.169.254/latest/meta-data/"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non autorisée");
    }

    @Test
    void rejette_un_reseau_prive_ipv4() {
        assertThatThrownBy(() -> client.telecharger("http://192.168.1.10/"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non autorisée");
    }

    @Test
    void rejette_une_adresse_ipv6_unique_local() {
        assertThatThrownBy(() -> client.telecharger("http://[fd00::1]/"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non autorisée");
    }
}
