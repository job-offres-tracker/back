package fr.sirene.jobtracker.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class CandidaturePriseDeContactTest {

    private static CandidaturePriseDeContact.Builder builder(TypeEntreprise type, String client) {
        return CandidaturePriseDeContact.builder()
                .nomEntreprise("Acme SAS").typeEntreprise(type).client(client)
                .statut(StatutPriseDeContact.ETABLI).dateCandidature(LocalDateTime.now())
                .evenements(List.of()).documents(List.of());
    }

    @Test
    void un_client_blanc_est_normalise_en_null_pour_une_esn() {
        CandidaturePriseDeContact cp = builder(TypeEntreprise.ESN, "  ").build();

        assertThat(cp.getClient()).isNull();
    }

    @Test
    void un_client_vide_est_normalise_en_null_au_lieu_d_etre_refuse_pour_un_editeur() {
        CandidaturePriseDeContact cp = builder(TypeEntreprise.EDITEUR, "").build();

        assertThat(cp.getClient()).isNull();
    }

    @Test
    void refuse_un_client_renseigne_pour_une_entreprise_qui_n_est_ni_esn_ni_cabinet() {
        assertThatThrownBy(() -> builder(TypeEntreprise.EDITEUR, "Société Générale").build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void accepte_un_client_renseigne_pour_une_esn() {
        CandidaturePriseDeContact cp = builder(TypeEntreprise.ESN, "Société Générale").build();

        assertThat(cp.getClient()).isEqualTo("Société Générale");
    }
}
