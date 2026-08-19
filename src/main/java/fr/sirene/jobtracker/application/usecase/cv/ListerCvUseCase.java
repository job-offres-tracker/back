package fr.sirene.jobtracker.application.usecase.cv;

import fr.sirene.jobtracker.application.port.cv.CvRepository;
import fr.sirene.jobtracker.domain.model.Cv;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListerCvUseCase {

    private final CvRepository cvRepository;

    public ListerCvUseCase(CvRepository cvRepository) {
        this.cvRepository = cvRepository;
    }

    public List<Cv> executer() {
        return cvRepository.listerTout();
    }
}
