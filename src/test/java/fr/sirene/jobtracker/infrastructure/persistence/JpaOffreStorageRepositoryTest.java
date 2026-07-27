package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.infrastructure.persistence.entity.OffreEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaOffreStorageRepositoryTest {

    @Mock
    private OffreJpaRepository offreJpaRepository;

    @Mock
    private LieuJpaRepository lieuJpaRepository;

    private JpaOffreStorageRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaOffreStorageRepository(offreJpaRepository, lieuJpaRepository);
    }

    @Nested
    class SauvegarderTout {

        @Test
        void ne_modifie_pas_l_etat_d_une_offre_deja_persistee_lors_d_un_resync() {
            OffreEntity entiteExistante = new OffreEntity("123");
            entiteExistante.setEtat(EtatOffre.POSTULE);
            when(offreJpaRepository.findByIdExterne("123")).thenReturn(Optional.of(entiteExistante));

            Offre offreResynchronisee = Offre.builder().idExterne("123").intitule("Développeur Java").build();
            assertThat(offreResynchronisee.getEtat()).isEqualTo(EtatOffre.NON_LU);

            repository.sauvegarderTout(List.of(offreResynchronisee));

            ArgumentCaptor<List<OffreEntity>> captor = ArgumentCaptor.captor();
            verify(offreJpaRepository).saveAll(captor.capture());
            assertThat(captor.getValue().get(0).getEtat()).isEqualTo(EtatOffre.POSTULE);
        }

        @Test
        void initialise_l_etat_d_une_nouvelle_offre_avec_la_valeur_fournie_par_le_domaine() {
            when(offreJpaRepository.findByIdExterne("456")).thenReturn(Optional.empty());

            Offre nouvelleOffre = Offre.builder().idExterne("456").intitule("Développeur Java").build();

            repository.sauvegarderTout(List.of(nouvelleOffre));

            ArgumentCaptor<List<OffreEntity>> captor = ArgumentCaptor.captor();
            verify(offreJpaRepository).saveAll(captor.capture());
            assertThat(captor.getValue().get(0).getEtat()).isEqualTo(EtatOffre.NON_LU);
        }

        @Test
        void transmet_la_provenance_de_l_offre_vers_l_entite() {
            when(offreJpaRepository.findByIdExterne("MANUEL-1")).thenReturn(Optional.empty());

            Offre offreManuelle = Offre.builder()
                    .idExterne("MANUEL-1")
                    .intitule("Développeur Java")
                    .provenance("LinkedIn")
                    .build();

            repository.sauvegarderTout(List.of(offreManuelle));

            ArgumentCaptor<List<OffreEntity>> captor = ArgumentCaptor.captor();
            verify(offreJpaRepository).saveAll(captor.capture());
            assertThat(captor.getValue().get(0).getProvenance()).isEqualTo("LinkedIn");
        }
    }

    @Nested
    class TrouverParIdExterne {

        @Test
        void restitue_la_provenance_de_l_entite_vers_le_domaine() {
            OffreEntity entite = new OffreEntity("123");
            entite.setEtat(EtatOffre.NON_LU);
            entite.setProvenance("FRANCE_TRAVAIL");
            when(offreJpaRepository.findByIdExterne("123")).thenReturn(Optional.of(entite));

            Optional<Offre> offre = repository.trouverParIdExterne("123");

            assertThat(offre).isPresent();
            assertThat(offre.get().getProvenance()).isEqualTo("FRANCE_TRAVAIL");
        }
    }
}
