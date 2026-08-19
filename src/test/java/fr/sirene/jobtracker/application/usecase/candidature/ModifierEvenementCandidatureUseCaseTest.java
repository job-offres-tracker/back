package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.exception.CandidatureNonTrouveeException;
import fr.sirene.jobtracker.domain.exception.EvenementNonTrouveException;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.Evenement;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.TypeEvenement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModifierEvenementCandidatureUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @InjectMocks
    private ModifierEvenementCandidatureUseCase useCase;

    @Test
    void leve_une_exception_quand_la_candidature_est_introuvable() {
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executer(1L, 10L, LocalDate.now(), TypeEvenement.MAIL, null))
                .isInstanceOf(CandidatureNonTrouveeException.class);
    }

    @Test
    void leve_une_exception_quand_l_evenement_n_appartient_pas_a_la_candidature() {
        Candidature candidature = Candidature.builder()
                .id(1L)
                .offre(Offre.builder().idExterne("123").build())
                .evenements(List.of(Evenement.builder().id(99L).date(LocalDate.now()).type(TypeEvenement.MAIL).build()))
                .build();
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));

        assertThatThrownBy(() -> useCase.executer(1L, 10L, LocalDate.now(), TypeEvenement.MAIL, null))
                .isInstanceOf(EvenementNonTrouveException.class);
    }

    @Test
    void modifie_l_evenement_existant() {
        Candidature candidature = Candidature.builder()
                .id(1L)
                .offre(Offre.builder().idExterne("123").build())
                .evenements(List.of(Evenement.builder().id(10L).date(LocalDate.now()).type(TypeEvenement.MAIL).build()))
                .build();
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(candidature));
        LocalDate nouvelleDate = LocalDate.of(2026, 8, 15);
        when(candidatureRepository.modifierEvenement(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq(10L), org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(2));

        Evenement evenement = useCase.executer(1L, 10L, nouvelleDate, TypeEvenement.ENTRETIEN, "Entretien reporté");

        assertThat(evenement.getDate()).isEqualTo(nouvelleDate);
        assertThat(evenement.getType()).isEqualTo(TypeEvenement.ENTRETIEN);
        assertThat(evenement.getDescription()).isEqualTo("Entretien reporté");
    }
}
