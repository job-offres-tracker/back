package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.domain.model.Cv;
import fr.sirene.jobtracker.infrastructure.persistence.entity.CvEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaCvRepositoryTest {

    @Mock
    private CvJpaRepository cvJpaRepository;

    private JpaCvRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaCvRepository(cvJpaRepository);
    }

    @Nested
    class Sauvegarder {

        @Test
        void transmet_les_metadonnees_du_domaine_vers_l_entite() {
            when(cvJpaRepository.save(org.mockito.ArgumentMatchers.any(CvEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Cv cv = Cv.builder().nomUnique("abc.pdf").nomOriginal("cv-jean.pdf").tailleOctets(42).dateUpload(Instant.now()).build();

            Cv resultat = repository.sauvegarder(cv);

            ArgumentCaptor<CvEntity> captor = ArgumentCaptor.captor();
            verify(cvJpaRepository).save(captor.capture());
            assertThat(captor.getValue().getNomUnique()).isEqualTo("abc.pdf");
            assertThat(captor.getValue().getNomOriginal()).isEqualTo("cv-jean.pdf");
            assertThat(resultat.getNomUnique()).isEqualTo("abc.pdf");
        }
    }

    @Nested
    class TrouverParNomUnique {

        @Test
        void restitue_le_cv_correspondant() {
            CvEntity entity = new CvEntity("abc.pdf");
            entity.setNomOriginal("cv-jean.pdf");
            entity.setTailleOctets(42);
            entity.setDateUpload(Instant.now());
            when(cvJpaRepository.findByNomUnique("abc.pdf")).thenReturn(Optional.of(entity));

            Optional<Cv> resultat = repository.trouverParNomUnique("abc.pdf");

            assertThat(resultat).isPresent();
            assertThat(resultat.get().getNomOriginal()).isEqualTo("cv-jean.pdf");
        }

        @Test
        void renvoie_vide_si_aucun_cv_ne_correspond() {
            when(cvJpaRepository.findByNomUnique("inconnu.pdf")).thenReturn(Optional.empty());

            assertThat(repository.trouverParNomUnique("inconnu.pdf")).isEmpty();
        }
    }

    @Nested
    class ListerTout {

        @Test
        void renvoie_les_cv_du_plus_recent_au_plus_ancien() {
            CvEntity entity = new CvEntity("abc.pdf");
            entity.setNomOriginal("cv-jean.pdf");
            entity.setDateUpload(Instant.now());
            when(cvJpaRepository.findAllByOrderByDateUploadDesc()).thenReturn(List.of(entity));

            List<Cv> resultat = repository.listerTout();

            assertThat(resultat).hasSize(1);
            assertThat(resultat.get(0).getNomUnique()).isEqualTo("abc.pdf");
        }
    }
}
