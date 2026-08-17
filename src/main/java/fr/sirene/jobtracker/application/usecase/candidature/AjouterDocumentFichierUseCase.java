package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.application.port.candidature.DocumentCandidatureStockagePort;
import fr.sirene.jobtracker.application.port.parametres.ParametresDocumentCandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.exception.TailleFichierDepasseeException;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentFichier;
import fr.sirene.jobtracker.domain.model.ParametresDocumentCandidature;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AjouterDocumentFichierUseCase {

    private final CandidatureRepository candidatureRepository;
    private final DocumentCandidatureStockagePort documentCandidatureStockagePort;
    private final ParametresDocumentCandidatureRepository parametresDocumentCandidatureRepository;

    public AjouterDocumentFichierUseCase(
            CandidatureRepository candidatureRepository,
            DocumentCandidatureStockagePort documentCandidatureStockagePort,
            ParametresDocumentCandidatureRepository parametresDocumentCandidatureRepository) {
        this.candidatureRepository = candidatureRepository;
        this.documentCandidatureStockagePort = documentCandidatureStockagePort;
        this.parametresDocumentCandidatureRepository = parametresDocumentCandidatureRepository;
    }

    public DocumentCandidature executer(Long candidatureId, String libelle, String contentType, byte[] contenu) {
        candidatureRepository.trouverParId(candidatureId)
                .orElseThrow(() -> new CandidatureNonTrouveeException(candidatureId));

        ParametresDocumentCandidature parametres = parametresDocumentCandidatureRepository.recuperer();
        if (contenu.length > parametres.tailleMaxOctets()) {
            throw new TailleFichierDepasseeException(
                    "Le fichier dépasse la taille maximale autorisée de %d octets".formatted(parametres.tailleMaxOctets()));
        }

        String nomStocke = UUID.randomUUID().toString();
        documentCandidatureStockagePort.ecrire(candidatureId, nomStocke, contenu);

        DocumentCandidature document =
                new DocumentFichier(null, libelle, nomStocke, contenu.length, contentType, LocalDateTime.now());

        return candidatureRepository.ajouterDocument(candidatureId, document);
    }
}
