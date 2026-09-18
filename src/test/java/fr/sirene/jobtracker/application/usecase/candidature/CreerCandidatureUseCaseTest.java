package fr.sirene.jobtracker.application.usecase.candidature;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidaturePriseDeContact;
import fr.sirene.jobtracker.domain.model.CandidatureSpontanee;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreerCandidatureUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    @InjectMocks
    private CreerCandidatureUseCase useCase;

    @Nested
    class CreerSpontanee {

        @Test
        void applique_le_statut_envoye_par_defaut() {
            when(candidatureRepository.sauvegarder(any())).thenAnswer(invocation -> invocation.getArgument(0));

            Candidature candidature = useCase.creerSpontanee("Acme SAS", "https://acme.example", TypeEntreprise.ESN, null, null);

            assertThat(candidature).isInstanceOf(CandidatureSpontanee.class);
            CandidatureSpontanee spontanee = (CandidatureSpontanee) candidature;
            assertThat(spontanee.getNomEntreprise()).isEqualTo("Acme SAS");
            assertThat(spontanee.getUrlEntreprise()).isEqualTo("https://acme.example");
            assertThat(spontanee.getTypeEntreprise()).isEqualTo(TypeEntreprise.ESN);
            assertThat(spontanee.getStatut()).isEqualTo(StatutCandidatureSpontanee.ENVOYE);
            assertThat(spontanee.getDateCandidature()).isNotNull();
        }

        @Test
        void conserve_le_statut_et_la_date_fournis() {
            when(candidatureRepository.sauvegarder(any())).thenAnswer(invocation -> invocation.getArgument(0));
            LocalDateTime date = LocalDateTime.of(2026, 1, 1, 10, 0);

            Candidature candidature = useCase.creerSpontanee(
                    "Acme SAS", null, TypeEntreprise.EDITEUR, StatutCandidatureSpontanee.ACCEPTE, date);

            CandidatureSpontanee spontanee = (CandidatureSpontanee) candidature;
            assertThat(spontanee.getStatut()).isEqualTo(StatutCandidatureSpontanee.ACCEPTE);
            assertThat(spontanee.getDateCandidature()).isEqualTo(date);

            ArgumentCaptor<Candidature> captor = ArgumentCaptor.captor();
            verify(candidatureRepository).sauvegarder(captor.capture());
            assertThat(captor.getValue()).isInstanceOf(CandidatureSpontanee.class);
        }
    }

    @Nested
    class CreerPriseDeContact {

        @Test
        void applique_le_statut_etabli_par_defaut() {
            when(candidatureRepository.sauvegarder(any())).thenAnswer(invocation -> invocation.getArgument(0));

            Candidature candidature = useCase.creerPriseDeContact(
                    "Acme SAS", null, TypeEntreprise.CABINET_RECRUTEMENT, null, null);

            assertThat(candidature).isInstanceOf(CandidaturePriseDeContact.class);
            CandidaturePriseDeContact priseDeContact = (CandidaturePriseDeContact) candidature;
            assertThat(priseDeContact.getNomEntreprise()).isEqualTo("Acme SAS");
            assertThat(priseDeContact.getTypeEntreprise()).isEqualTo(TypeEntreprise.CABINET_RECRUTEMENT);
            assertThat(priseDeContact.getStatut()).isEqualTo(StatutPriseDeContact.ETABLI);
            assertThat(priseDeContact.getDateCandidature()).isNotNull();
        }

        @Test
        void conserve_le_statut_et_la_date_fournis() {
            when(candidatureRepository.sauvegarder(any())).thenAnswer(invocation -> invocation.getArgument(0));
            LocalDateTime date = LocalDateTime.of(2026, 1, 1, 10, 0);

            Candidature candidature = useCase.creerPriseDeContact(
                    "Acme SAS", "https://acme.example", TypeEntreprise.ESN, StatutPriseDeContact.REFUSE, date);

            CandidaturePriseDeContact priseDeContact = (CandidaturePriseDeContact) candidature;
            assertThat(priseDeContact.getStatut()).isEqualTo(StatutPriseDeContact.REFUSE);
            assertThat(priseDeContact.getDateCandidature()).isEqualTo(date);
        }
    }
}
