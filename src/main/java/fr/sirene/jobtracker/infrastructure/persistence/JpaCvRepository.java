package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.application.port.cv.CvRepository;
import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.infrastructure.persistence.entity.CvEntity;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCvRepository implements CvRepository {

    private final CvJpaRepository cvJpaRepository;

    public JpaCvRepository(CvJpaRepository cvJpaRepository) {
        this.cvJpaRepository = cvJpaRepository;
    }

    @Override
    @Transactional
    public Cv sauvegarder(Cv cv) {
        CvEntity entity = new CvEntity(cv.getNomUnique());
        entity.setNomOriginal(cv.getNomOriginal());
        entity.setTailleOctets(cv.getTailleOctets());
        entity.setDateUpload(cv.getDateUpload());
        return toDomain(cvJpaRepository.save(entity));
    }

    @Override
    public List<Cv> listerTout() {
        return cvJpaRepository.findAllByOrderByDateUploadDesc().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Cv> trouverParNomUnique(String nomUnique) {
        return cvJpaRepository.findByNomUnique(nomUnique).map(this::toDomain);
    }

    private Cv toDomain(CvEntity entity) {
        return Cv.builder()
                .nomUnique(entity.getNomUnique())
                .nomOriginal(entity.getNomOriginal())
                .tailleOctets(entity.getTailleOctets())
                .dateUpload(entity.getDateUpload())
                .build();
    }
}
