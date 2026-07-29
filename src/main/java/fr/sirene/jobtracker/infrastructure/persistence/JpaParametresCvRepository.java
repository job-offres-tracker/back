package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.application.port.ParametresCvRepository;
import fr.sirene.jobtracker.domain.model.ParametresCv;
import fr.sirene.jobtracker.infrastructure.persistence.entity.ParametresCvEntity;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaParametresCvRepository implements ParametresCvRepository {

    private static final long TAILLE_MAX_OCTETS_PAR_DEFAUT = 5_242_880L;

    private final ParametresCvJpaRepository jpaRepository;

    public JpaParametresCvRepository(ParametresCvJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ParametresCv recuperer() {
        return jpaRepository.findTopByOrderByIdAsc()
                .map(entity -> new ParametresCv(entity.getTailleMaxOctets()))
                .orElseGet(() -> new ParametresCv(TAILLE_MAX_OCTETS_PAR_DEFAUT));
    }

    @Override
    @Transactional
    public ParametresCv sauvegarder(ParametresCv parametres) {
        ParametresCvEntity entity = jpaRepository.findTopByOrderByIdAsc().orElseGet(ParametresCvEntity::new);
        entity.setTailleMaxOctets(parametres.tailleMaxOctets());
        ParametresCvEntity sauvegardee = jpaRepository.save(entity);
        return new ParametresCv(sauvegardee.getTailleMaxOctets());
    }
}
