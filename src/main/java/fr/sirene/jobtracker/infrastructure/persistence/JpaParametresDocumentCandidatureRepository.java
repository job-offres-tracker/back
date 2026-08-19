package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.application.port.parametres.ParametresDocumentCandidatureRepository;
import fr.sirene.jobtracker.domain.model.ParametresDocumentCandidature;
import fr.sirene.jobtracker.infrastructure.persistence.entity.ParametresDocumentCandidatureEntity;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaParametresDocumentCandidatureRepository implements ParametresDocumentCandidatureRepository {

    private static final long TAILLE_MAX_OCTETS_PAR_DEFAUT = 10_485_760L;

    private final ParametresDocumentCandidatureJpaRepository jpaRepository;

    public JpaParametresDocumentCandidatureRepository(ParametresDocumentCandidatureJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ParametresDocumentCandidature recuperer() {
        return jpaRepository.findTopByOrderByIdAsc()
                .map(entity -> new ParametresDocumentCandidature(entity.getTailleMaxOctets()))
                .orElseGet(() -> new ParametresDocumentCandidature(TAILLE_MAX_OCTETS_PAR_DEFAUT));
    }

    @Override
    @Transactional
    public ParametresDocumentCandidature sauvegarder(ParametresDocumentCandidature parametres) {
        ParametresDocumentCandidatureEntity entity =
                jpaRepository.findTopByOrderByIdAsc().orElseGet(ParametresDocumentCandidatureEntity::new);
        entity.setTailleMaxOctets(parametres.tailleMaxOctets());
        ParametresDocumentCandidatureEntity sauvegardee = jpaRepository.save(entity);
        return new ParametresDocumentCandidature(sauvegardee.getTailleMaxOctets());
    }
}
