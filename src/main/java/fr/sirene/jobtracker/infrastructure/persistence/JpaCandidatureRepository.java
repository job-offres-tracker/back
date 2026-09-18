package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.application.port.offre.OffreStorageRepository;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidatureOffre;
import fr.sirene.jobtracker.domain.model.CandidaturePriseDeContact;
import fr.sirene.jobtracker.domain.model.CandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentCv;
import fr.sirene.jobtracker.domain.model.DocumentFichier;
import fr.sirene.jobtracker.domain.model.DocumentTexte;
import fr.sirene.jobtracker.domain.model.Evenement;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.ResultatPagine;
import fr.sirene.jobtracker.domain.model.TypeCandidature;
import fr.sirene.jobtracker.domain.model.TypeDocument;
import fr.sirene.jobtracker.infrastructure.persistence.entity.CandidatureEntity;
import fr.sirene.jobtracker.infrastructure.persistence.entity.CvEntity;
import fr.sirene.jobtracker.infrastructure.persistence.entity.DocumentCandidatureEntity;
import fr.sirene.jobtracker.infrastructure.persistence.entity.EvenementCandidatureEntity;
import fr.sirene.jobtracker.infrastructure.persistence.entity.OffreEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCandidatureRepository implements CandidatureRepository {

    private final CandidatureJpaRepository candidatureJpaRepository;
    private final EvenementCandidatureJpaRepository evenementCandidatureJpaRepository;
    private final DocumentCandidatureJpaRepository documentCandidatureJpaRepository;
    private final OffreJpaRepository offreJpaRepository;
    private final OffreStorageRepository offreStorageRepository;
    private final CvJpaRepository cvJpaRepository;

    public JpaCandidatureRepository(
            CandidatureJpaRepository candidatureJpaRepository,
            EvenementCandidatureJpaRepository evenementCandidatureJpaRepository,
            DocumentCandidatureJpaRepository documentCandidatureJpaRepository,
            OffreJpaRepository offreJpaRepository,
            OffreStorageRepository offreStorageRepository,
            CvJpaRepository cvJpaRepository) {
        this.candidatureJpaRepository = candidatureJpaRepository;
        this.evenementCandidatureJpaRepository = evenementCandidatureJpaRepository;
        this.documentCandidatureJpaRepository = documentCandidatureJpaRepository;
        this.offreJpaRepository = offreJpaRepository;
        this.offreStorageRepository = offreStorageRepository;
        this.cvJpaRepository = cvJpaRepository;
    }

    @Override
    @Transactional
    public Candidature sauvegarder(Candidature candidature) {
        CandidatureEntity entity = candidature.id() != null
                ? candidatureJpaRepository.findById(candidature.id())
                        .orElseThrow(() -> new IllegalStateException("Candidature introuvable : " + candidature.id()))
                : nouvelleEntite(candidature);

        switch (candidature) {
            case CandidatureOffre co -> {
                entity.setOffre(resoudreOffre(co.offre().getIdExterne()));
                entity.setStatutOffre(co.statut());
            }
            case CandidatureSpontanee cs -> {
                entity.setNomEntreprise(cs.nomEntreprise());
                entity.setUrlEntreprise(cs.urlEntreprise());
                entity.setTypeEntreprise(cs.typeEntreprise());
                entity.setStatutCandidatureSpontanee(cs.statut());
            }
            case CandidaturePriseDeContact cp -> {
                entity.setNomEntreprise(cp.nomEntreprise());
                entity.setUrlEntreprise(cp.urlEntreprise());
                entity.setTypeEntreprise(cp.typeEntreprise());
                entity.setStatutPriseDeContact(cp.statut());
            }
        }
        entity.setDateCandidature(candidature.dateCandidature());
        return toDomain(candidatureJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public Candidature mettreAJourStatut(Candidature candidature) {
        CandidatureEntity entity = candidatureJpaRepository.findById(candidature.id())
                .orElseThrow(() -> new IllegalStateException("Candidature introuvable : " + candidature.id()));

        switch (candidature) {
            case CandidatureOffre co -> entity.setStatutOffre(co.statut());
            case CandidatureSpontanee cs -> entity.setStatutCandidatureSpontanee(cs.statut());
            case CandidaturePriseDeContact cp -> entity.setStatutPriseDeContact(cp.statut());
        }
        candidatureJpaRepository.save(entity);
        return candidature;
    }

    private CandidatureEntity nouvelleEntite(Candidature candidature) {
        return switch (candidature) {
            case CandidatureOffre co -> new CandidatureEntity(resoudreOffre(co.offre().getIdExterne()));
            case CandidatureSpontanee _ -> new CandidatureEntity(TypeCandidature.SPONTANEE);
            case CandidaturePriseDeContact _ -> new CandidatureEntity(TypeCandidature.PRISE_DE_CONTACT);
        };
    }

    private OffreEntity resoudreOffre(String idExterne) {
        return offreJpaRepository.findByIdExterne(idExterne)
                .orElseThrow(() -> new IllegalStateException("Offre introuvable pour l'identifiant externe : " + idExterne));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Candidature> trouverParId(Long id) {
        return candidatureJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Candidature> trouverParOffreIdExterne(String idExterneOffre) {
        return candidatureJpaRepository.findByOffreIdExterne(idExterneOffre).map(this::toDomain);
    }

    @Override
    public boolean existeParOffreIdExterne(String idExterneOffre) {
        return candidatureJpaRepository.existsByOffreIdExterne(idExterneOffre);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultatPagine<Candidature> lister(int page, int taille) {
        Pageable pageable = PageRequest.of(page, taille);
        Page<CandidatureEntity> resultats = candidatureJpaRepository.findAllByOrderByDateCandidatureDesc(pageable);
        return new ResultatPagine<>(resultats.map(this::toDomain).toList(), page, taille, resultats.getTotalElements());
    }

    @Override
    @Transactional
    public Evenement ajouterEvenement(Long candidatureId, Evenement evenement) {
        CandidatureEntity candidature = candidatureJpaRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalStateException("Candidature introuvable : " + candidatureId));
        EvenementCandidatureEntity entity = new EvenementCandidatureEntity(candidature);
        entity.setDateEvenement(evenement.getDate());
        entity.setType(evenement.getType());
        entity.setDescription(evenement.getDescription());
        return toDomain(evenementCandidatureJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public Evenement modifierEvenement(Long candidatureId, Long evenementId, Evenement evenement) {
        EvenementCandidatureEntity entity = evenementCandidatureJpaRepository.findById(evenementId)
                .filter(e -> e.getCandidature().getId().equals(candidatureId))
                .orElseThrow(() -> new IllegalStateException(
                        "Événement introuvable : %d pour la candidature %d".formatted(evenementId, candidatureId)));
        entity.setDateEvenement(evenement.getDate());
        entity.setType(evenement.getType());
        entity.setDescription(evenement.getDescription());
        return toDomain(evenementCandidatureJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public DocumentCandidature ajouterDocument(Long candidatureId, DocumentCandidature document) {
        CandidatureEntity candidature = candidatureJpaRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalStateException("Candidature introuvable : " + candidatureId));

        DocumentCandidatureEntity entity = new DocumentCandidatureEntity(candidature);
        entity.setLibelle(document.libelle());
        entity.setDateAjout(document.dateAjout());

        switch (document) {
            case DocumentCv cv -> {
                entity.setType(TypeDocument.CV);
                CvEntity cvEntity = cvJpaRepository.findByNomUnique(cv.cvNomUnique())
                        .orElseThrow(() -> new IllegalStateException("CV introuvable : " + cv.cvNomUnique()));
                entity.setCv(cvEntity);
            }
            case DocumentFichier fichier -> {
                entity.setType(TypeDocument.FICHIER);
                entity.setNomStocke(fichier.nomStocke());
                entity.setTailleOctets(fichier.tailleOctets());
                entity.setContentType(fichier.contentType());
            }
            case DocumentTexte texte -> {
                entity.setType(TypeDocument.TEXTE);
                entity.setContenuTexte(texte.contenuTexte());
            }
        }

        return toDomain(documentCandidatureJpaRepository.save(entity));
    }

    private Candidature toDomain(CandidatureEntity entity) {
        List<Evenement> evenements = entity.getEvenements().stream().map(this::toDomain).toList();
        List<DocumentCandidature> documents = entity.getDocuments().stream().map(this::toDomain).toList();
        return switch (entity.getType()) {
            case OFFRE -> {
                Offre offre = offreStorageRepository.trouverParIdExterne(entity.getOffre().getIdExterne())
                        .orElseThrow(() -> new IllegalStateException(
                                "Offre introuvable pour l'identifiant externe : " + entity.getOffre().getIdExterne()));
                yield new CandidatureOffre(
                        entity.getId(), offre, entity.getStatutOffre(), entity.getDateCandidature(), evenements, documents);
            }
            case SPONTANEE -> new CandidatureSpontanee(
                    entity.getId(), entity.getNomEntreprise(), entity.getUrlEntreprise(), entity.getTypeEntreprise(),
                    entity.getStatutCandidatureSpontanee(), entity.getDateCandidature(), evenements, documents);
            case PRISE_DE_CONTACT -> new CandidaturePriseDeContact(
                    entity.getId(), entity.getNomEntreprise(), entity.getUrlEntreprise(), entity.getTypeEntreprise(),
                    entity.getStatutPriseDeContact(), entity.getDateCandidature(), evenements, documents);
        };
    }

    private Evenement toDomain(EvenementCandidatureEntity entity) {
        return Evenement.builder()
                .id(entity.getId())
                .date(entity.getDateEvenement())
                .type(entity.getType())
                .description(entity.getDescription())
                .build();
    }

    private DocumentCandidature toDomain(DocumentCandidatureEntity entity) {
        return switch (entity.getType()) {
            case CV -> new DocumentCv(
                    entity.getId(), entity.getLibelle(), entity.getCv().getNomUnique(),
                    entity.getCv().getTailleOctets(), entity.getDateAjout());
            case FICHIER -> new DocumentFichier(
                    entity.getId(), entity.getLibelle(), entity.getNomStocke(), entity.getTailleOctets(),
                    entity.getContentType(), entity.getDateAjout());
            case TEXTE -> new DocumentTexte(entity.getId(), entity.getLibelle(), entity.getContenuTexte(), entity.getDateAjout());
        };
    }
}
