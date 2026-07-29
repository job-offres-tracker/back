package fr.sirene.jobtracker.infrastructure.persistence;

import fr.sirene.jobtracker.domain.model.ParametresCv;
import fr.sirene.jobtracker.infrastructure.persistence.entity.ParametresCvEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaParametresCvRepositoryTest {

    @Mock
    private ParametresCvJpaRepository jpaRepository;

    private JpaParametresCvRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaParametresCvRepository(jpaRepository);
    }

    @Nested
    class Recuperer {

        @Test
        void renvoie_une_valeur_par_defaut_si_rien_n_a_jamais_ete_sauvegarde() {
            when(jpaRepository.findTopByOrderByIdAsc()).thenReturn(Optional.empty());

            ParametresCv parametres = repository.recuperer();

            assertThat(parametres.tailleMaxOctets()).isPositive();
        }

        @Test
        void restitue_la_valeur_persistee() {
            ParametresCvEntity entity = new ParametresCvEntity();
            entity.setTailleMaxOctets(1_000_000L);
            when(jpaRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(entity));

            ParametresCv parametres = repository.recuperer();

            assertThat(parametres.tailleMaxOctets()).isEqualTo(1_000_000L);
        }
    }

    @Nested
    class Sauvegarder {

        @Test
        void met_a_jour_la_ligne_existante_plutot_que_d_en_creer_une_nouvelle() {
            ParametresCvEntity existante = new ParametresCvEntity();
            existante.setTailleMaxOctets(5_000L);
            when(jpaRepository.findTopByOrderByIdAsc()).thenReturn(Optional.of(existante));
            when(jpaRepository.save(org.mockito.ArgumentMatchers.any(ParametresCvEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            repository.sauvegarder(new ParametresCv(1_000_000L));

            verify(jpaRepository).save(existante);
            assertThat(existante.getTailleMaxOctets()).isEqualTo(1_000_000L);
        }
    }
}
