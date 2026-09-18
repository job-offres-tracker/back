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
        CandidatureEntity entity = candidature.getId() != null
                ? candidatureJpaRepository.findById(candidature.getId())
                        .orElseThrow(() -> new IllegalStateException("Candidature introuvable : " + candidature.getId()))
                : nouvelleEntite(candidature);

        switch (candidature) {
            case CandidatureOffre co -> {
                entity.setOffre(resoudreOffre(co.getOffre().getIdExterne()));
                entity.setStatutOffre(co.getStatut());
            }
            case CandidatureSpontanee cs -> {
                entity.setNomEntreprise(cs.getNomEntreprise());
                entity.setUrlEntreprise(cs.getUrlEntreprise());
                entity.setTypeEntreprise(cs.getTypeEntreprise());
                entity.setStatutCandidatureSpontanee(cs.getStatut());
            }
            case CandidaturePriseDeContact cp -> {
                entity.setNomEntreprise(cp.getNomEntreprise());
                entity.setUrlEntreprise(cp.getUrlEntreprise());
                entity.setTypeEntreprise(cp.getTypeEntreprise());
                entity.setStatutPriseDeContact(cp.getStatut());
            }
        }
        entity.setDateCandidature(candidature.getDateCandidature());
        return toDomain(candidatureJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public Candidature mettreAJourStatut(Candidature candidature) {
        CandidatureEntity entity = candidatureJpaRepository.findById(candidature.getId())
                .orElseThrow(() -> new IllegalStateException("Candidature introuvable : " + candidature.getId()));

        switch (candidature) {
            case CandidatureOffre co -> entity.setStatutOffre(co.getStatut());
            case CandidatureSpontanee cs -> entity.setStatutCandidatureSpontanee(cs.getStatut());
            case CandidaturePriseDeContact cp -> entity.setStatutPriseDeContact(cp.getStatut());
        }
        candidatureJpaRepository.save(entity);
        return candidature;
    }

    private CandidatureEntity nouvelleEntite(Candidature candidature) {
        return switch (candidature) {
            case CandidatureOffre co -> new CandidatureEntity(resoudreOffre(co.getOffre().getIdExterne()));
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
        entity.setLibelle(document.getLibelle());
        entity.setDateAjout(document.getDateAjout());

        switch (document) {
            case DocumentCv cv -> {
                entity.setType(TypeDocument.CV);
                CvEntity cvEntity = cvJpaRepository.findByNomUnique(cv.getCvNomUnique())
                        .orElseThrow(() -> new IllegalStateException("CV introuvable : " + cv.getCvNomUnique()));
                entity.setCv(cvEntity);
            }
            case DocumentFichier fichier -> {
                entity.setType(TypeDocument.FICHIER);
                entity.setNomStocke(fichier.getNomStocke());
                entity.setTailleOctets(fichier.getTailleOctets());
                entity.setContentType(fichier.getContentType());
            }
            case DocumentTexte texte -> {
                entity.setType(TypeDocument.TEXTE);
                entity.setContenuTexte(texte.getContenuTexte());
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
                yield CandidatureOffre.builder()
                        .id(entity.getId())
                        .offre(offre)
                        .statut(entity.getStatutOffre())
                        .dateCandidature(entity.getDateCandidature())
                        .evenements(evenements)
                        .documents(documents)
                        .build();
            }
            case SPONTANEE -> CandidatureSpontanee.builder()
                    .id(entity.getId())
                    .nomEntreprise(entity.getNomEntreprise())
                    .urlEntreprise(entity.getUrlEntreprise())
                    .typeEntreprise(entity.getTypeEntreprise())
                    .statut(entity.getStatutCandidatureSpontanee())
                    .dateCandidature(entity.getDateCandidature())
                    .evenements(evenements)
                    .documents(documents)
                    .build();
            case PRISE_DE_CONTACT -> CandidaturePriseDeContact.builder()
                    .id(entity.getId())
                    .nomEntreprise(entity.getNomEntreprise())
                    .urlEntreprise(entity.getUrlEntreprise())
                    .typeEntreprise(entity.getTypeEntreprise())
                    .statut(entity.getStatutPriseDeContact())
                    .dateCandidature(entity.getDateCandidature())
                    .evenements(evenements)
                    .documents(documents)
                    .build();
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
            case CV -> DocumentCv.builder()
                    .id(entity.getId())
                    .libelle(entity.getLibelle())
                    .cvNomUnique(entity.getCv().getNomUnique())
                    .tailleOctets(entity.getCv().getTailleOctets())
                    .dateAjout(entity.getDateAjout())
                    .build();
            case FICHIER -> DocumentFichier.builder()
                    .id(entity.getId())
                    .libelle(entity.getLibelle())
                    .nomStocke(entity.getNomStocke())
                    .tailleOctets(entity.getTailleOctets())
                    .contentType(entity.getContentType())
                    .dateAjout(entity.getDateAjout())
                    .build();
            case TEXTE -> DocumentTexte.builder()
                    .id(entity.getId())
                    .libelle(entity.getLibelle())
                    .contenuTexte(entity.getContenuTexte())
                    .dateAjout(entity.getDateAjout())
                    .build();
        };
    }
}
