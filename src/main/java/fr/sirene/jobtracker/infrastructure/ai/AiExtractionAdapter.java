package fr.sirene.jobtracker.infrastructure.ai;

import fr.sirene.jobtracker.application.port.offre.ExtractionOffreIAPort;
import fr.sirene.jobtracker.domain.exception.ExtractionOffreIAException;
import fr.sirene.jobtracker.domain.model.BrouillonOffre;
import fr.sirene.jobtracker.infrastructure.ai.dto.ExtractionOffreIA;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Repository;

import java.net.URI;

@Repository
@Slf4j
public class AiExtractionAdapter implements ExtractionOffreIAPort {

    private static final String PROMPT_SYSTEME = """
            Tu es un extracteur de données pour des offres d'emploi. À partir du texte brut d'une page web \
            fourni par l'utilisateur, extrais exactement ces champs (laisse le champ à null si l'information \
            est absente de la page) :
            - intitule : intitulé du poste
            - description : description complète de l'offre reformatée avec des sauts de ligne
            - entreprise : nom de l'entreprise qui recrute
            - lieuLibelle : ville ou lieu de travail
            - typeContrat : type de contrat (CDI, CDD, alternance, etc.)
            - salaire : salaire tel qu'indiqué sur la page
            - referenceExterne : référence ou numéro d'offre indiqué sur la page (ex. 'Réf. 12345'), en ne \
            gardant que le numéro
            - datePublication : date de publication de l'offre au format AAAA-MM-JJ si elle est déterminable, \
            sinon null""";

    private final ChatClient chatClient;

    public AiExtractionAdapter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public BrouillonOffre extraire(String contenuPage, String urlOrigine) {
        log.debug("urlOrigine: {}, contenu: {}", urlOrigine, contenuPage);

        ExtractionOffreIA extraction;
        try {
            extraction = chatClient.prompt()
                    .system(PROMPT_SYSTEME)
                    .user(contenuPage)
                    .options(ChatOptions.builder().temperature(0.0))
                    .call()
                    .entity(ExtractionOffreIA.class, spec -> spec.useProviderStructuredOutput());
        } catch (RuntimeException e) {
            throw new ExtractionOffreIAException("Échec de l'extraction des champs de l'offre par l'IA", e);
        }
        if (extraction == null) {
            throw new ExtractionOffreIAException("Réponse vide de l'API IA");
        }
        log.debug("Extraction : {}", extraction);

        return new BrouillonOffre(
                extraction.intitule(),
                extraction.description(),
                extraction.entreprise(),
                extraction.lieuLibelle(),
                extraction.typeContrat(),
                extraction.salaire(),
                urlOrigine,
                extraireProvenance(urlOrigine),
                extraction.referenceExterne(),
                extraction.datePublication());
    }

    private String extraireProvenance(String urlOrigine) {
        String hote = URI.create(urlOrigine).getHost();
        if (hote == null) {
            return null;
        }
        String sansWww = hote.startsWith("www.") ? hote.substring(4) : hote;
        int indexPremierPoint = sansWww.indexOf('.');
        return indexPremierPoint > 0 ? sansWww.substring(0, indexPremierPoint) : sansWww;
    }
}
