package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.infrastructure.persistence.entity.OffreEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OffreJpaRepository extends JpaRepository<OffreEntity, Long> {

    Optional<OffreEntity> findByIdExterne(String idExterne);

    List<OffreEntity> findByIdExterneIn(List<String> idsExternes);

    Page<OffreEntity> findByEtatIn(List<EtatOffre> etats, Pageable pageable);

    long countByEtatIn(List<EtatOffre> etats);

    @Modifying
    @Query("UPDATE OffreEntity o SET o.etat = :etat WHERE o.idExterne IN :idsExternes")
    int mettreAJourEtat(@Param("etat") EtatOffre etat, @Param("idsExternes") List<String> idsExternes);
}
