package fr.sirene.jobtracker.infrastructure.scraping;

import fr.sirene.jobtracker.application.port.RecuperationPageOffrePort;
import fr.sirene.jobtracker.infrastructure.scraping.client.PageWebClient;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class PageWebOffreAdapter implements RecuperationPageOffrePort {

    private static final int TAILLE_MAX_CONTENU = 20_000;

    private final PageWebClient client;

    public PageWebOffreAdapter(PageWebClient client) {
        this.client = client;
    }

    @Override
    public String recuperer(String url) {
        log.debug("url: {}", url);
        String html = client.telecharger(url);
        String texte = Jsoup.parse(html).text();
        return texte.length() > TAILLE_MAX_CONTENU ? texte.substring(0, TAILLE_MAX_CONTENU) : texte;
    }
}
