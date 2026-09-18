package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentTexte;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AjouterDocumentTexteUseCase {

    private final CandidatureRepository candidatureRepository;

    public AjouterDocumentTexteUseCase(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    public DocumentCandidature executer(long candidatureId, String libelle, String contenuTexte) {
        candidatureRepository.trouverParId(candidatureId)
                .orElseThrow(() -> new CandidatureNonTrouveeException(candidatureId));

        DocumentCandidature document = DocumentTexte.builder()
                .libelle(libelle)
                .contenuTexte(contenuTexte)
                .dateAjout(LocalDateTime.now())
                .build();

        return candidatureRepository.ajouterDocument(candidatureId, document);
    }
}
