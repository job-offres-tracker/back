package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.application.port.candidature.DocumentCandidatureStockagePort;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidatureTelecharge;
import fr.sirene.jobtracker.domain.model.DocumentFichier;

import org.springframework.stereotype.Service;

@Service
public class TelechargerDocumentCandidatureUseCase {

    private final CandidatureRepository candidatureRepository;
    private final DocumentCandidatureStockagePort documentCandidatureStockagePort;

    public TelechargerDocumentCandidatureUseCase(
            CandidatureRepository candidatureRepository, DocumentCandidatureStockagePort documentCandidatureStockagePort) {
        this.candidatureRepository = candidatureRepository;
        this.documentCandidatureStockagePort = documentCandidatureStockagePort;
    }

    public DocumentCandidatureTelecharge executer(Long candidatureId, Long documentId) {
        Candidature candidature = candidatureRepository.trouverParId(candidatureId)
                .orElseThrow(() -> new CandidatureNonTrouveeException(candidatureId));

        DocumentCandidature document = candidature.getDocuments().stream()
                .filter(d -> d.id().equals(documentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucun document trouvé pour l'identifiant : " + documentId));

        if (!(document instanceof DocumentFichier fichier)) {
            throw new IllegalArgumentException("Ce document n'est pas un fichier téléchargeable");
        }

        byte[] contenu = documentCandidatureStockagePort.lire(candidatureId, fichier.nomStocke());
        return new DocumentCandidatureTelecharge(fichier, contenu);
    }
}
