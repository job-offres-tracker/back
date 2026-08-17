package fr.sirene.jobtracker.application.port.cv;

import fr.sirene.jobtracker.domain.model.Cv;

import java.util.List;
import java.util.Optional;

public interface CvRepository {

    Cv sauvegarder(Cv cv);

    List<Cv> listerTout();

    Optional<Cv> trouverParNomUnique(String nomUnique);
}
