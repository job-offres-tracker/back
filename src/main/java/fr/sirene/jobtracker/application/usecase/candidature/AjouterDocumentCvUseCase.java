package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.application.port.cv.CvRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentCv;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AjouterDocumentCvUseCase {

    private final CandidatureRepository candidatureRepository;
    private final CvRepository cvRepository;

    public AjouterDocumentCvUseCase(CandidatureRepository candidatureRepository, CvRepository cvRepository) {
        this.candidatureRepository = candidatureRepository;
        this.cvRepository = cvRepository;
    }

    public DocumentCandidature executer(Long candidatureId, String cvNomUnique) {
        candidatureRepository.trouverParId(candidatureId)
                .orElseThrow(() -> new CandidatureNonTrouveeException(candidatureId));

        Cv cv = cvRepository.trouverParNomUnique(cvNomUnique)
                .orElseThrow(() -> new CvNonTrouveException(cvNomUnique));

        DocumentCandidature document =
                new DocumentCv(null, cv.getNomOriginal(), cv.getNomUnique(), cv.getTailleOctets(), LocalDateTime.now());

        return candidatureRepository.ajouterDocument(candidatureId, document);
    }
}
