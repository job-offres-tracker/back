package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.exception.StatutCandidatureInvalideException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidatureOffre;
import fr.sirene.jobtracker.domain.model.CandidaturePriseDeContact;
import fr.sirene.jobtracker.domain.model.CandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.StatutCandidatureOffre;
import fr.sirene.jobtracker.domain.model.StatutCandidatureSpontanee;
import fr.sirene.jobtracker.domain.model.StatutPriseDeContact;
import fr.sirene.jobtracker.domain.model.TypeEntreprise;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModifierStatutCandidatureUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @InjectMocks
    private ModifierStatutCandidatureUseCase useCase;

    @Test
    void leve_une_exception_quand_la_candidature_est_introuvable() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer(1L, "ACCEPTE")).isInstanceOf(CandidatureNonTrouveeException.class);
    }

    @Nested
    class CandidatureDeTypeOffre {

        @Test
        void met_a_jour_le_statut() {
            Candidature candidature = CandidatureOffre.builder()
                    .id(1L)
                    .offre(Offre.builder().idExterne("123").build())
                    .statut(StatutCandidatureOffre.POSTULE)
                    .dateCandidature(LocalDateTime.now())
                    .evenements(List.of())
                    .documents(List.of())
                    .build();
            when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));
            when(candidatureRepository.mettreAJourStatut(any())).thenAnswer(invocation -> invocation.getArgument(0));

            Candidature resultat = useCase.executer(1L, "ACCEPTE");

            assertThat(((CandidatureOffre) resultat).getStatut()).isEqualTo(StatutCandidatureOffre.ACCEPTE);
            ArgumentCaptor<Candidature> captor = ArgumentCaptor.captor();
            verify(candidatureRepository).mettreAJourStatut(captor.capture());
            assertThat(((CandidatureOffre) captor.getValue()).getStatut()).isEqualTo(StatutCandidatureOffre.ACCEPTE);
        }

        @Test
        void leve_une_exception_quand_le_statut_est_invalide() {
            Candidature candidature = CandidatureOffre.builder()
                    .id(1L)
                    .offre(Offre.builder().idExterne("123").build())
                    .statut(StatutCandidatureOffre.POSTULE)
                    .dateCandidature(LocalDateTime.now())
                    .evenements(List.of())
                    .documents(List.of())
                    .build();
            when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));

            assertThatThrownBy(() -> useCase.executer(1L, "ETABLI"))
                    .isInstanceOf(StatutCandidatureInvalideException.class);
        }

        @Test
        void leve_une_exception_quand_le_statut_est_null() {
            Candidature candidature = CandidatureOffre.builder()
                    .id(1L)
                    .offre(Offre.builder().idExterne("123").build())
                    .statut(StatutCandidatureOffre.POSTULE)
                    .dateCandidature(LocalDateTime.now())
                    .evenements(List.of())
                    .documents(List.of())
                    .build();
            when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));

            assertThatThrownBy(() -> useCase.executer(1L, null))
                    .isInstanceOf(StatutCandidatureInvalideException.class);
        }
    }

    @Nested
    class CandidatureDeTypeSpontanee {

        @Test
        void met_a_jour_le_statut() {
            Candidature candidature = CandidatureSpontanee.builder()
                    .id(2L)
                    .nomEntreprise("Acme SAS")
                    .typeEntreprise(TypeEntreprise.ESN)
                    .statut(StatutCandidatureSpontanee.ENVOYE)
                    .dateCandidature(LocalDateTime.now())
                    .evenements(List.of())
                    .documents(List.of())
                    .build();
            when(candidatureRepository.trouverParId(2L)).thenReturn(Optional.of(candidature));
            when(candidatureRepository.mettreAJourStatut(any())).thenAnswer(invocation -> invocation.getArgument(0));

            Candidature resultat = useCase.executer(2L, "RECALE");

            assertThat(((CandidatureSpontanee) resultat).getStatut()).isEqualTo(StatutCandidatureSpontanee.RECALE);
        }

        @Test
        void leve_une_exception_quand_le_statut_est_invalide() {
            Candidature candidature = CandidatureSpontanee.builder()
                    .id(2L)
                    .nomEntreprise("Acme SAS")
                    .typeEntreprise(TypeEntreprise.ESN)
                    .statut(StatutCandidatureSpontanee.ENVOYE)
                    .dateCandidature(LocalDateTime.now())
                    .evenements(List.of())
                    .documents(List.of())
                    .build();
            when(candidatureRepository.trouverParId(2L)).thenReturn(Optional.of(candidature));

            assertThatThrownBy(() -> useCase.executer(2L, "POSTULE"))
                    .isInstanceOf(StatutCandidatureInvalideException.class);
        }
    }

    @Nested
    class CandidatureDeTypePriseDeContact {

        @Test
        void met_a_jour_le_statut() {
            Candidature candidature = CandidaturePriseDeContact.builder()
                    .id(3L)
                    .nomEntreprise("Acme SAS")
                    .typeEntreprise(TypeEntreprise.CABINET_RECRUTEMENT)
                    .statut(StatutPriseDeContact.ETABLI)
                    .dateCandidature(LocalDateTime.now())
                    .evenements(List.of())
                    .documents(List.of())
                    .build();
            when(candidatureRepository.trouverParId(3L)).thenReturn(Optional.of(candidature));
            when(candidatureRepository.mettreAJourStatut(any())).thenAnswer(invocation -> invocation.getArgument(0));

            Candidature resultat = useCase.executer(3L, "ACCEPTE");

            assertThat(((CandidaturePriseDeContact) resultat).getStatut()).isEqualTo(StatutPriseDeContact.ACCEPTE);
        }

        @Test
        void leve_une_exception_quand_le_statut_est_invalide() {
            Candidature candidature = CandidaturePriseDeContact.builder()
                    .id(3L)
                    .nomEntreprise("Acme SAS")
                    .typeEntreprise(TypeEntreprise.CABINET_RECRUTEMENT)
                    .statut(StatutPriseDeContact.ETABLI)
                    .dateCandidature(LocalDateTime.now())
                    .evenements(List.of())
                    .documents(List.of())
                    .build();
            when(candidatureRepository.trouverParId(3L)).thenReturn(Optional.of(candidature));

            assertThatThrownBy(() -> useCase.executer(3L, "ENVOYE"))
                    .isInstanceOf(StatutCandidatureInvalideException.class);
        }
    }
}
