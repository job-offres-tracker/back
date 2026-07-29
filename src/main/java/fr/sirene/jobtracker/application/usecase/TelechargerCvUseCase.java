package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.CvRepository;
import fr.sirene.jobtracker.application.port.CvStockagePort;
import fr.sirene.jobtracker.domain.exception.CvNonTrouveException;
import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.domain.model.CvTelecharge;

import org.springframework.stereotype.Service;

@Service
public class TelechargerCvUseCase {

    private final CvRepository cvRepository;
    private final CvStockagePort cvStockagePort;

    public TelechargerCvUseCase(CvRepository cvRepository, CvStockagePort cvStockagePort) {
        this.cvRepository = cvRepository;
        this.cvStockagePort = cvStockagePort;
    }

    public CvTelecharge executer(String nomUnique) {
        Cv cv = cvRepository.trouverParNomUnique(nomUnique)
                .orElseThrow(() -> new CvNonTrouveException(nomUnique));
        byte[] contenu = cvStockagePort.lire(nomUnique);
        return new CvTelecharge(cv, contenu);
    }
}
