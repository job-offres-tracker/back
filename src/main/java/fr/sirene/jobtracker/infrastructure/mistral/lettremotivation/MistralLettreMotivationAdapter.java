package fr.sirene.jobtracker.infrastructure.mistral.lettremotivation;

import fr.sirene.jobtracker.application.port.offre.GenerationLettreMotivationPort;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.exception.ExtractionTexteCvException;
import fr.sirene.jobtracker.domain.exception.GenerationLettreMotivationException;
import fr.sirene.jobtracker.domain.exception.OffreNonTrouveeException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class MistralLettreMotivationAdapter implements GenerationLettreMotivationPort {

    private static final String PROMPT_SYSTEME = """
            Tu es un rédacteur spécialisé en lettres de motivation en français. Utilise l'outil recupererOffre \
            pour obtenir le détail de l'offre d'emploi et l'outil recupererCv pour obtenir le contenu du CV du \
            candidat. Rédige ensuite une lettre de motivation personnalisée d'environ 300 à 400 mots, qui met en \
            avant les expériences et compétences du CV réellement pertinentes pour l'offre. N'invente aucune \
            expérience ni compétence qui ne figure pas dans le CV. Laisse les placeholders [Votre nom], \
            [Votre adresse], [Ville, le date du jour] pour les coordonnées de l'expéditeur, que tu ne connais pas, \
            et termine par une formule de politesse suivie du placeholder [Votre signature]. Ne retourne que le \
            texte de la lettre, sans commentaire ni introduction.""";

    private final ChatClient chatClient;

    public MistralLettreMotivationAdapter(ChatClient lettreMotivationChatClient) {
        this.chatClient = lettreMotivationChatClient;
    }

    @Override
    public String genererLettre(String idExterneOffre, String cvNomUnique) {
        try {
            String user = """
                    Rédige une lettre de motivation pour l'offre d'identifiant externe '%s' en t'appuyant \
                    sur le CV de nom unique '%s'.""".formatted(idExterneOffre, cvNomUnique);
            return chatClient.prompt()
                    .system(PROMPT_SYSTEME)
                    .user(user)
                    .call()
                    .content();
        } catch (ToolExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof OffreNonTrouveeException
                    || cause instanceof CvNonTrouveException
                    || cause instanceof ExtractionTexteCvException) {
                throw (RuntimeException) cause;
            }
            log.debug("Échec de la génération de la lettre de motivation", e);
            throw new GenerationLettreMotivationException("Échec de la génération de la lettre de motivation", e);
        } catch (RuntimeException e) {
            log.debug("Échec de la génération de la lettre de motivation", e);
            throw new GenerationLettreMotivationException("Échec de la génération de la lettre de motivation", e);
        }
    }
}
