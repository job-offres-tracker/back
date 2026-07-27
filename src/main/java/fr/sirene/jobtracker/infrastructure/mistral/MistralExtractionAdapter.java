package fr.sirene.jobtracker.infrastructure.mistral;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import fr.sirene.jobtracker.application.port.ExtractionOffreIAPort;
import fr.sirene.jobtracker.domain.exception.ExtractionOffreIAException;
import fr.sirene.jobtracker.domain.model.BrouillonOffre;
import fr.sirene.jobtracker.infrastructure.mistral.client.MistralApiClient;
import fr.sirene.jobtracker.infrastructure.mistral.config.MistralApiProperties;
import fr.sirene.jobtracker.infrastructure.mistral.dto.ChoixMistral;
import fr.sirene.jobtracker.infrastructure.mistral.dto.ExtractionOffreIA;
import fr.sirene.jobtracker.infrastructure.mistral.dto.MessageMistral;
import fr.sirene.jobtracker.infrastructure.mistral.dto.ReponseChatMistral;
import fr.sirene.jobtracker.infrastructure.mistral.dto.ReponseFormatMistral;
import fr.sirene.jobtracker.infrastructure.mistral.dto.RequeteChatMistral;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class MistralExtractionAdapter implements ExtractionOffreIAPort {

    private static final String PROMPT_SYSTEME = """
            Tu es un extracteur de données pour des offres d'emploi. À partir du texte brut d'une page web \
            fourni par l'utilisateur, retourne UNIQUEMENT un objet JSON strict avec exactement ces champs \
            (chaîne de caractères, ou null si l'information est absente de la page) :
            {
              "intitule": "intitulé du poste",
              "description": "description complète de l'offre reformattée avec des sauts de ligne",
              "entreprise": "nom de l'entreprise qui recrute",
              "lieuLibelle": "ville ou lieu de travail",
              "typeContrat": "type de contrat (CDI, CDD, alternance, etc.)",
              "salaire": "salaire tel qu'indiqué sur la page",
              "referenceExterne": "référence ou numéro d'offre indiqué sur la page (ex. 'Réf. 12345'), en ne gardant que le numéro",
              "datePublication": "date de publication de l'offre au format AAAA-MM-JJ si elle est déterminable, sinon null"
            }
            Ne retourne aucun texte en dehors de ce JSON.""";

    private final MistralApiClient apiClient;
    private final MistralApiProperties properties;
    private final ObjectMapper objectMapper;

    public MistralExtractionAdapter(MistralApiClient apiClient, MistralApiProperties properties, ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public BrouillonOffre extraire(String contenuPage, String urlOrigine) {
        log.debug("urlOrigine: {}, contenu: {}", urlOrigine, contenuPage);
        RequeteChatMistral requete = new RequeteChatMistral(
                properties.model(),
                List.of(
                        new MessageMistral("system", PROMPT_SYSTEME),
                        new MessageMistral("user", contenuPage)),
                0.0,
                new ReponseFormatMistral("json_object"));

        ReponseChatMistral reponse = apiClient.completer(requete);
        log.debug("Réponse mistral : {}", reponse);
        String contenuJson = extraireContenu(reponse);
        ExtractionOffreIA extraction = parser(contenuJson);

        return new BrouillonOffre(
                extraction.intitule(),
                extraction.description(),
                extraction.entreprise(),
                extraction.lieuLibelle(),
                extraction.typeContrat(),
                extraction.salaire(),
                urlOrigine,
                extraction.referenceExterne(),
                extraction.datePublication());
    }

    private String extraireContenu(ReponseChatMistral reponse) {
        List<ChoixMistral> choix = reponse != null ? reponse.choices() : null;
        if (choix == null || choix.isEmpty() || choix.get(0).message() == null) {
            throw new ExtractionOffreIAException("Réponse vide de l'API Mistral");
        }
        return choix.get(0).message().content();
    }

    private ExtractionOffreIA parser(String contenuJson) {
        try {
            return objectMapper.readValue(contenuJson, ExtractionOffreIA.class);
        } catch (JacksonException e) {
            throw new ExtractionOffreIAException("Réponse de l'IA illisible", e);
        }
    }
}
