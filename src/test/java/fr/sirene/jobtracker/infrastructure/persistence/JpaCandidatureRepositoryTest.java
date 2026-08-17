package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.application.port.offre.OffreStorageRepository;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.DocumentCandidature;
import fr.sirene.jobtracker.domain.model.DocumentCv;
import fr.sirene.jobtracker.domain.model.DocumentTexte;
import fr.sirene.jobtracker.domain.model.Evenement;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.ResultatPagine;
import fr.sirene.jobtracker.domain.model.TypeEvenement;
import fr.sirene.jobtracker.infrastructure.persistence.entity.CandidatureEntity;
import fr.sirene.jobtracker.infrastructure.persistence.entity.CvEntity;
import fr.sirene.jobtracker.infrastructure.persistence.entity.DocumentCandidatureEntity;
import fr.sirene.jobtracker.infrastructure.persistence.entity.EvenementCandidatureEntity;
import fr.sirene.jobtracker.infrastructure.persistence.entity.OffreEntity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaCandidatureRepositoryTest {

    @Mock
    private CandidatureJpaRepository candidatureJpaRepository;
    @Mock
    private EvenementCandidatureJpaRepository evenementCandidatureJpaRepository;
    @Mock
    private DocumentCandidatureJpaRepository documentCandidatureJpaRepository;
    @Mock
    private OffreJpaRepository offreJpaRepository;
    @Mock
    private OffreStorageRepository offreStorageRepository;
    @Mock
    private CvJpaRepository cvJpaRepository;
    @InjectMocks
    private JpaCandidatureRepository repository;

    private static final Offre OFFRE = Offre.builder().idExterne("123").intitule("Développeur Java").build();

   
    private CandidatureEntity nouvelleCandidatureEntity(Long id) {
        OffreEntity offreEntity = new OffreEntity("123");
        CandidatureEntity entity = new CandidatureEntity(offreEntity);
        entity.setDateCandidature(LocalDateTime.now());
        if (id != null) {
            org.springframework.test.util.ReflectionTestUtils.setField(entity, "id", id);
        }
        return entity;
    }

    @Nested
    class Sauvegarder {

        @Test
        void resout_l_offre_et_persiste_la_candidature() {
            OffreEntity offreEntity = new OffreEntity("123");
            when(offreJpaRepository.findByIdExterne("123")).thenReturn(Optional.of(offreEntity));
            when(candidatureJpaRepository.save(any(CandidatureEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(offreStorageRepository.trouverParIdExterne("123")).thenReturn(Optional.of(OFFRE));

            Candidature candidature = Candidature.builder().offre(OFFRE).dateCandidature(LocalDateTime.now()).build();

            Candidature resultat = repository.sauvegarder(candidature);

            assertThat(resultat.getOffre().getIdExterne()).isEqualTo("123");
            ArgumentCaptor<CandidatureEntity> captor = ArgumentCaptor.captor();
            verify(candidatureJpaRepository).save(captor.capture());
            assertThat(captor.getValue().getOffre()).isEqualTo(offreEntity);
        }
    }

    @Nested
    class TrouverParId {

        @Test
        void restitue_la_candidature_avec_ses_evenements_et_documents() {
            CandidatureEntity entity = nouvelleCandidatureEntity(1L);
            EvenementCandidatureEntity evenementEntity = new EvenementCandidatureEntity(entity);
            evenementEntity.setDateEvenement(LocalDate.now());
            evenementEntity.setType(TypeEvenement.ENTRETIEN);
            entity.getEvenements().add(evenementEntity);
            when(candidatureJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(offreStorageRepository.trouverParIdExterne("123")).thenReturn(Optional.of(OFFRE));

            Optional<Candidature> resultat = repository.trouverParId(1L);

            assertThat(resultat).isPresent();
            assertThat(resultat.get().getEvenements()).hasSize(1);
            assertThat(resultat.get().getEvenements().get(0).getType()).isEqualTo(TypeEvenement.ENTRETIEN);
        }

        @Test
        void renvoie_vide_si_aucune_candidature_ne_correspond() {
            when(candidatureJpaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThat(repository.trouverParId(99L)).isEmpty();
        }
    }

    @Nested
    class TrouverParOffreIdExterne {

        @Test
        void restitue_la_candidature_correspondant_a_l_offre() {
            CandidatureEntity entity = nouvelleCandidatureEntity(1L);
            when(candidatureJpaRepository.findByOffreIdExterne("123")).thenReturn(Optional.of(entity));
            when(offreStorageRepository.trouverParIdExterne("123")).thenReturn(Optional.of(OFFRE));

            Optional<Candidature> resultat = repository.trouverParOffreIdExterne("123");

            assertThat(resultat).isPresent();
            assertThat(resultat.get().getId()).isEqualTo(1L);
            assertThat(resultat.get().getOffre().getIdExterne()).isEqualTo("123");
        }

        @Test
        void renvoie_vide_si_aucune_candidature_ne_correspond_a_l_offre() {
            when(candidatureJpaRepository.findByOffreIdExterne("999")).thenReturn(Optional.empty());

            assertThat(repository.trouverParOffreIdExterne("999")).isEmpty();
        }
    }

    @Nested
    class ExisteParOffreIdExterne {

        @Test
        void delegue_au_repository_jpa() {
            when(candidatureJpaRepository.existsByOffreIdExterne("123")).thenReturn(true);

            assertThat(repository.existeParOffreIdExterne("123")).isTrue();
        }
    }

    @Nested
    class Lister {

        @Test
        void restitue_une_page_de_candidatures() {
            CandidatureEntity entity = nouvelleCandidatureEntity(1L);
            Page<CandidatureEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 20), 1);
            when(candidatureJpaRepository.findAllByOrderByDateCandidatureDesc(any())).thenReturn(page);
            when(offreStorageRepository.trouverParIdExterne("123")).thenReturn(Optional.of(OFFRE));

            ResultatPagine<Candidature> resultat = repository.lister(0, 20);

            assertThat(resultat.elements()).hasSize(1);
            assertThat(resultat.total()).isEqualTo(1);
        }
    }

    @Nested
    class AjouterEvenement {

        @Test
        void attache_l_evenement_a_la_candidature() {
            CandidatureEntity entity = nouvelleCandidatureEntity(1L);
            when(candidatureJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(evenementCandidatureJpaRepository.save(any(EvenementCandidatureEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Evenement evenement = Evenement.builder().date(LocalDate.now()).type(TypeEvenement.MAIL).description("Relance").build();

            Evenement resultat = repository.ajouterEvenement(1L, evenement);

            assertThat(resultat.getType()).isEqualTo(TypeEvenement.MAIL);
            assertThat(resultat.getDescription()).isEqualTo("Relance");
        }
    }

    @Nested
    class ModifierEvenement {

        @Test
        void met_a_jour_l_evenement_existant() {
            CandidatureEntity entity = nouvelleCandidatureEntity(1L);
            EvenementCandidatureEntity evenementEntity = new EvenementCandidatureEntity(entity);
            evenementEntity.setDateEvenement(LocalDate.now());
            evenementEntity.setType(TypeEvenement.MAIL);
            org.springframework.test.util.ReflectionTestUtils.setField(evenementEntity, "id", 10L);
            when(evenementCandidatureJpaRepository.findById(10L)).thenReturn(Optional.of(evenementEntity));
            when(evenementCandidatureJpaRepository.save(any(EvenementCandidatureEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            LocalDate nouvelleDate = LocalDate.of(2026, 8, 15);
            Evenement evenementModifie = Evenement.builder().date(nouvelleDate).type(TypeEvenement.ENTRETIEN).description("Reporté").build();

            Evenement resultat = repository.modifierEvenement(1L, 10L, evenementModifie);

            assertThat(resultat.getDate()).isEqualTo(nouvelleDate);
            assertThat(resultat.getType()).isEqualTo(TypeEvenement.ENTRETIEN);
        }
    }

    @Nested
    class AjouterDocument {

        @Test
        void attache_un_document_texte_sans_resoudre_de_cv() {
            CandidatureEntity entity = nouvelleCandidatureEntity(1L);
            when(candidatureJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(documentCandidatureJpaRepository.save(any(DocumentCandidatureEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            DocumentCandidature document = new DocumentTexte(null, "Notes", "Contenu", LocalDateTime.now());

            DocumentCandidature resultat = repository.ajouterDocument(1L, document);

            assertThat(resultat).isInstanceOf(DocumentTexte.class);
            assertThat(((DocumentTexte) resultat).contenuTexte()).isEqualTo("Contenu");
            verify(cvJpaRepository, org.mockito.Mockito.never()).findByNomUnique(any());
        }

        @Test
        void resout_le_cv_reference_pour_un_document_de_type_cv() {
            CandidatureEntity entity = nouvelleCandidatureEntity(1L);
            when(candidatureJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
            CvEntity cvEntity = new CvEntity("abc.pdf");
            cvEntity.setTailleOctets(12_345L);
            when(cvJpaRepository.findByNomUnique("abc.pdf")).thenReturn(Optional.of(cvEntity));
            when(documentCandidatureJpaRepository.save(any(DocumentCandidatureEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            DocumentCandidature document = new DocumentCv(null, "cv.pdf", "abc.pdf", 12_345L, LocalDateTime.now());

            DocumentCandidature resultat = repository.ajouterDocument(1L, document);

            assertThat(resultat).isInstanceOf(DocumentCv.class);
            assertThat(((DocumentCv) resultat).cvNomUnique()).isEqualTo("abc.pdf");
            assertThat(((DocumentCv) resultat).tailleOctets()).isEqualTo(12_345L);
        }
    }
}
