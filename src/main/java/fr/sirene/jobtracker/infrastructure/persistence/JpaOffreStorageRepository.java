package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.application.port.OffreStorageRepository;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Lieu;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.infrastructure.persistence.entity.LieuEntity;
import fr.sirene.jobtracker.infrastructure.persistence.entity.OffreEntity;

import org.springframework.data.core.TypedPropertyPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaOffreStorageRepository implements OffreStorageRepository {

    private final OffreJpaRepository offreJpaRepository;
    private final LieuJpaRepository lieuJpaRepository;

    public JpaOffreStorageRepository(OffreJpaRepository offreJpaRepository, LieuJpaRepository lieuJpaRepository) {
        this.offreJpaRepository = offreJpaRepository;
        this.lieuJpaRepository = lieuJpaRepository;
    }

    @Override
    @Transactional
    public void sauvegarderTout(List<Offre> offres) {
        List<OffreEntity> entites = offres.stream().map(this::toEntity).toList();
        offreJpaRepository.saveAll(entites);
    }

    @Override
    public List<Offre> rechercher(int page, int taille, List<EtatOffre> etats) {
        Pageable pageable = PageRequest.of(page, taille, Sort.by(Sort.Direction.DESC, TypedPropertyPath.path(OffreEntity::getDateCreation)));
        Page<OffreEntity> resultats = (etats == null || etats.isEmpty())
                ? offreJpaRepository.findAll(pageable)
                : offreJpaRepository.findByEtatIn(etats, pageable);
        return resultats.map(this::toDomain).toList();
    }

    @Override
    public long compter(List<EtatOffre> etats) {
        return (etats == null || etats.isEmpty())
                ? offreJpaRepository.count()
                : offreJpaRepository.countByEtatIn(etats);
    }

    @Override
    @Transactional
    public void mettreAJourEtat(List<String> idsExternes, EtatOffre etat) {
        offreJpaRepository.mettreAJourEtat(etat, idsExternes);
    }

    @Override
    public Optional<Offre> trouverParIdExterne(String idExterne) {
        return offreJpaRepository.findByIdExterne(idExterne).map(this::toDomain);
    }

    @Override
    public List<Offre> trouverParIdsExternes(List<String> idsExternes) {
        return offreJpaRepository.findByIdExterneIn(idsExternes).stream().map(this::toDomain).toList();
    }

    private OffreEntity toEntity(Offre offre) {
        OffreEntity entity = offreJpaRepository.findByIdExterne(offre.getIdExterne())
                .orElseGet(() -> {
                    OffreEntity nouvelle = new OffreEntity(offre.getIdExterne());
                    nouvelle.setDateImport(LocalDateTime.now());
                    nouvelle.setEtat(offre.getEtat());
                    return nouvelle;
                });
        entity.setIntitule(offre.getIntitule());
        entity.setDescription(offre.getDescription());
        entity.setEntreprise(offre.getEntreprise());
        entity.setLieu(resoudreLieuEntity(offre.getLieu()));
        entity.setTypeContrat(offre.getTypeContrat());
        entity.setSalaire(offre.getSalaire());
        entity.setUrlOrigine(offre.getUrlOrigine());
        entity.setDateCreation(offre.getDateCreation());
        entity.setProvenance(offre.getProvenance());
        return entity;
    }

    private LieuEntity resoudreLieuEntity(Lieu lieu) {
        if (lieu == null) {
            return null;
        }
        LieuEntity entity = lieu.latitude() != null && lieu.longitude() != null
                ? lieuJpaRepository.findByLatitudeAndLongitude(lieu.latitude(), lieu.longitude()).orElseGet(LieuEntity::new)
                : new LieuEntity();
        entity.setLibelle(lieu.libelle());
        entity.setCodeCommune(lieu.codeCommune());
        entity.setLatitude(lieu.latitude());
        entity.setLongitude(lieu.longitude());
        entity.setAdresse(lieu.adresse());
        return lieuJpaRepository.save(entity);
    }

    private Offre toDomain(OffreEntity entity) {
        LieuEntity lieuEntity = entity.getLieu();
        Lieu lieu = lieuEntity != null
                ? new Lieu(lieuEntity.getLibelle(), lieuEntity.getCodeCommune(), lieuEntity.getLatitude(),
                        lieuEntity.getLongitude(), lieuEntity.getAdresse())
                : null;
        return Offre.builder()
                .idExterne(entity.getIdExterne())
                .intitule(entity.getIntitule())
                .description(entity.getDescription())
                .entreprise(entity.getEntreprise())
                .lieu(lieu)
                .typeContrat(entity.getTypeContrat())
                .salaire(entity.getSalaire())
                .urlOrigine(entity.getUrlOrigine())
                .dateCreation(entity.getDateCreation())
                .etat(entity.getEtat())
                .provenance(entity.getProvenance())
                .build();
    }
}
