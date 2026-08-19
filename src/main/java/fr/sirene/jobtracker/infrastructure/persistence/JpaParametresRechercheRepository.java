package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.application.port.parametres.ParametresRechercheRepository;
import fr.sirene.jobtracker.domain.model.CommuneRecherche;
import fr.sirene.jobtracker.domain.model.ParametresRecherche;
import fr.sirene.jobtracker.infrastructure.persistence.entity.CommuneRechercheEmbeddable;
import fr.sirene.jobtracker.infrastructure.persistence.entity.ParametresRechercheEntity;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class JpaParametresRechercheRepository implements ParametresRechercheRepository {

    private final ParametresRechercheJpaRepository jpaRepository;

    public JpaParametresRechercheRepository(ParametresRechercheJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ParametresRecherche recuperer() {
        return jpaRepository.findTopByOrderByIdAsc()
                .map(entity -> new ParametresRecherche(
                        entity.getMotsCles(), toDomain(entity.getCommunes()), entity.getTypeContrat()))
                .orElseGet(() -> new ParametresRecherche(List.of(), List.of(), null));
    }

    @Override
    @Transactional
    public ParametresRecherche sauvegarder(ParametresRecherche parametres) {
        ParametresRechercheEntity entity = jpaRepository.findTopByOrderByIdAsc()
                .orElseGet(ParametresRechercheEntity::new);
        entity.setMotsCles(new ArrayList<>(parametres.motsCles()));
        entity.setCommunes(toEmbeddables(parametres.communes()));
        entity.setTypeContrat(parametres.typeContrat());
        ParametresRechercheEntity sauvegardee = jpaRepository.save(entity);
        return new ParametresRecherche(
                sauvegardee.getMotsCles(), toDomain(sauvegardee.getCommunes()), sauvegardee.getTypeContrat());
    }

    private List<CommuneRecherche> toDomain(List<CommuneRechercheEmbeddable> communes) {
        return communes.stream().map(c -> new CommuneRecherche(c.getCodeInsee(), c.getLibelle())).toList();
    }

    private List<CommuneRechercheEmbeddable> toEmbeddables(List<CommuneRecherche> communes) {
        return communes.stream()
                .map(c -> new CommuneRechercheEmbeddable(c.codeInsee(), c.libelle()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
