package fr.sirene.jobtracker.application.usecase.candidature;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import fr.sirene.jobtracker.application.port.candidature.CandidatureRepository;
import fr.sirene.jobtracker.domain.model.Candidature;
import fr.sirene.jobtracker.domain.model.CandidaturePriseDeContact;
import fr.sirene.jobtracker.domain.model.ChampModifie;
import fr.sirene.jobtracker.domain.model.StatutPriseDeContact;
import fr.sirene.jobtracker.domain.model.TypeEntreprise;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModifierPriseDeContactUseCaseTest {

    @Mock
    private CandidatureRepository candidatureRepository;

    private ModifierPriseDeContactUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ModifierPriseDeContactUseCase(candidatureRepository);
        when(candidatureRepository.trouverParId(1L)).thenReturn(Optional.of(priseDeContact()));
        when(candidatureRepository.sauvegarder(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private static CandidaturePriseDeContact priseDeContact() {
        return CandidaturePriseDeContact.builder()
                .id(1L).nomEntreprise("Acme SAS").urlEntreprise("https://acme.example")
                .typeEntreprise(TypeEntreprise.ESN).poste("Lead Developer").client("Société Générale")
                .statut(StatutPriseDeContact.ETABLI).dateCandidature(LocalDateTime.now())
                .evenements(List.of()).documents(List.of()).build();
    }

    @Test
    void ne_modifie_que_les_champs_presents_et_laisse_les_autres_en_l_etat() {
        Candidature resultat = useCase.executer(1L, ChampModifie.absent(), ChampModifie.de("Architecte"),
                ChampModifie.absent());

        CandidaturePriseDeContact cp = (CandidaturePriseDeContact) resultat;
        assertThat(cp.getPoste()).isEqualTo("Architecte");
        assertThat(cp.getUrlEntreprise()).isEqualTo("https://acme.example");
        assertThat(cp.getClient()).isEqualTo("Société Générale");
    }

    @Test
    void efface_un_champ_present_avec_une_valeur_nulle() {
        Candidature resultat = useCase.executer(1L, ChampModifie.de(null), ChampModifie.absent(),
                ChampModifie.absent());

        CandidaturePriseDeContact cp = (CandidaturePriseDeContact) resultat;
        assertThat(cp.getUrlEntreprise()).isNull();
        assertThat(cp.getPoste()).isEqualTo("Lead Developer");
    }
}
