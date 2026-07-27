package fr.sirene.jobtracker.infrastructure.scraping.client;

import fr.sirene.jobtracker.domain.exception.RecuperationPageException;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

@Component
public class PageWebClient {

    private final RestClient restClient;

    public PageWebClient(RestClient scrapingRestClient) {
        this.restClient = scrapingRestClient;
    }

    public String telecharger(String url) {
        validerCibleAutorisee(url);
        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException | IllegalArgumentException e) {
            throw new RecuperationPageException("Impossible de récupérer la page à l'URL fournie", e);
        }
    }

    // Anti-SSRF : l'URL vient de l'appelant de l'API, elle ne doit pas cibler le réseau interne.
    private void validerCibleAutorisee(String url) {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL fournie invalide", e);
        }

        String scheme = uri.getScheme();
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("Seuls les schémas http et https sont autorisés");
        }

        String hote = uri.getHost();
        if (hote == null) {
            throw new IllegalArgumentException("URL fournie invalide");
        }

        try {
            for (InetAddress adresse : InetAddress.getAllByName(hote)) {
                if (estAdresseReseauInterne(adresse)) {
                    throw new IllegalArgumentException("L'URL fournie cible une ressource réseau non autorisée");
                }
            }
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Impossible de résoudre l'hôte de l'URL fournie", e);
        }
    }

    private boolean estAdresseReseauInterne(InetAddress adresse) {
        if (adresse.isLoopbackAddress() || adresse.isLinkLocalAddress()
                || adresse.isSiteLocalAddress() || adresse.isMulticastAddress()
                || adresse.isAnyLocalAddress()) {
            return true;
        }
        // Couvre les adresses IPv6 "unique local" (fc00::/7), non détectées par isSiteLocalAddress().
        byte[] octets = adresse.getAddress();
        return octets.length == 16 && (octets[0] & 0xfe) == 0xfc;
    }
}
