package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.domain.model.CommuneRecherche;
import fr.sirene.jobtracker.domain.model.ParametresRecherche;
import fr.sirene.jobtracker.infrastructure.persistence.entity.CommuneRechercheEmbeddable;
import fr.sirene.jobtracker.infrastructure.persistence.entity.ParametresRechercheEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaParametresRechercheRepositoryTest {

    @Mock
    private ParametresRechercheJpaRepository jpaRepository;

    private JpaParametresRechercheRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaParametresRechercheRepository(jpaRepository);
    }

    @Nested
    class Recuperer {

        @Test
        void renvoie_des_listes_vides_et_un_type_de_contrat_nul_si_rien_n_a_jamais_ete_sauvegarde() {
            when(jpaRepository.findTopByOrderByIdAsc()).thenReturn(Optional.empty());

            ParametresRecherche parametres = repository.recuperer();

            assertThat(parametres.motsCles()).isEmpty();
            assertThat(parametres.communes()).isEmpty();
            assertThat(parametres.typeContrat()).isNull();
        }

        @Test
        void restitue_les_valeurs_persistees() {
            ParametresRechercheEntity entity = new ParametresRechercheEntity();
            entity.setMotsCles(List.of("Java"));
            entity.setCommunes(List.of(new CommuneRechercheEmbeddable("44109", "Nantes")));
            entity.setTypeContrat("CDI");
            when(jpaRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(entity));

            ParametresRecherche parametres = repository.recuperer();

            assertThat(parametres.motsCles()).containsExactly("Java");
            assertThat(parametres.communes()).containsExactly(new CommuneRecherche("44109", "Nantes"));
            assertThat(parametres.typeContrat()).isEqualTo("CDI");
        }
    }

    @Nested
    class Sauvegarder {

        @Test
        void cree_une_nouvelle_ligne_si_aucune_n_existe() {
            when(jpaRepository.findTopByOrderByIdAsc()).thenReturn(Optional.empty());
            when(jpaRepository.save(org.mockito.ArgumentMatchers.any(ParametresRechercheEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            ParametresRecherche resultat = repository.sauvegarder(
                    new ParametresRecherche(List.of("Java"), List.of(new CommuneRecherche("44109", "Nantes")), "CDI"));

            assertThat(resultat.motsCles()).containsExactly("Java");
            assertThat(resultat.communes()).containsExactly(new CommuneRecherche("44109", "Nantes"));
            assertThat(resultat.typeContrat()).isEqualTo("CDI");
        }

        @Test
        void met_a_jour_la_ligne_existante_plutot_que_d_en_creer_une_nouvelle() {
            ParametresRechercheEntity existante = new ParametresRechercheEntity();
            existante.setMotsCles(List.of("ancien"));
            existante.setTypeContrat("CDD");
            when(jpaRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(existante));
            when(jpaRepository.save(org.mockito.ArgumentMatchers.any(ParametresRechercheEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            repository.sauvegarder(
                    new ParametresRecherche(List.of("nouveau"), List.of(new CommuneRecherche("44109", "Nantes")), "CDI"));

            verify(jpaRepository).save(existante);
            assertThat(existante.getMotsCles()).containsExactly("nouveau");
            assertThat(existante.getTypeContrat()).isEqualTo("CDI");
        }
    }
}
