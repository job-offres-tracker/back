package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.OffreStorageRepository;
import fr.sirene.jobtracker.domain.exception.OffreDejaExistanteException;
import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Offre;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreerOffreManuelleUseCaseTest {

    @Mock
    private OffreStorageRepository offreStorageRepository;

    @Mock
    private CandidatureAutoCreationService candidatureAutoCreationService;

    @InjectMocks
    private CreerOffreManuelleUseCase useCase;

    @Test
    void genere_un_id_externe_prefixe_quand_aucun_n_est_fourni() {
        when(offreStorageRepository.trouverParIdExterne(any())).thenReturn(Optional.empty());

        Offre offre = useCase.executer(null, "Développeur Java", null, null, null, null, null, null, null, null, null);

        assertThat(offre.getIdExterne()).startsWith("MANUEL-");
    }

    @Test
    void conserve_l_id_externe_fourni() {
        when(offreStorageRepository.trouverParIdExterne("REF-123")).thenReturn(Optional.empty());

        Offre offre = useCase.executer("REF-123", "Développeur Java", null, null, null, null, null, null, null, null, null);

        assertThat(offre.getIdExterne()).isEqualTo("REF-123");
    }

    @Test
    void leve_une_exception_quand_l_id_externe_est_deja_utilise() {
        when(offreStorageRepository.trouverParIdExterne("REF-123"))
                .thenReturn(Optional.of(Offre.builder().idExterne("REF-123").build()));

        assertThatThrownBy(() ->
                useCase.executer("REF-123", "Développeur Java", null, null, null, null, null, null, null, null, null))
                .isInstanceOf(OffreDejaExistanteException.class)
                .hasMessageContaining("REF-123");
    }

    @Test
    void retombe_sur_manuelle_quand_la_provenance_est_absente() {
        when(offreStorageRepository.trouverParIdExterne(any())).thenReturn(Optional.empty());

        Offre offre = useCase.executer(null, "Développeur Java", null, null, null, null, null, null, null, "  ", null);

        assertThat(offre.getProvenance()).isEqualTo("MANUELLE");
    }

    @Test
    void conserve_la_provenance_fournie() {
        when(offreStorageRepository.trouverParIdExterne(any())).thenReturn(Optional.empty());

        Offre offre = useCase.executer(null, "Développeur Java", null, null, null, null, null, null, null, "LinkedIn", null);

        assertThat(offre.getProvenance()).isEqualTo("LinkedIn");
    }

    @Test
    void retombe_sur_non_lu_quand_l_etat_est_absent() {
        when(offreStorageRepository.trouverParIdExterne(any())).thenReturn(Optional.empty());

        Offre offre = useCase.executer(null, "Développeur Java", null, null, null, null, null, null, null, null, null);

        assertThat(offre.getEtat()).isEqualTo(EtatOffre.NON_LU);
    }

    @Test
    void conserve_l_etat_fourni() {
        when(offreStorageRepository.trouverParIdExterne(any())).thenReturn(Optional.empty());

        Offre offre = useCase.executer(
                null, "Développeur Java", null, null, null, null, null, null, null, null, EtatOffre.POSTULE);

        assertThat(offre.getEtat()).isEqualTo(EtatOffre.POSTULE);
    }

    @Test
    void initialise_la_date_de_creation_a_l_instant_present_quand_elle_est_absente() {
        when(offreStorageRepository.trouverParIdExterne(any())).thenReturn(Optional.empty());
        LocalDateTime avant = LocalDateTime.now();

        Offre offre = useCase.executer(null, "Développeur Java", null, null, null, null, null, null, null, null, null);

        assertThat(offre.getDateCreation()).isNotNull().isAfterOrEqualTo(avant);
    }

    @Test
    void persiste_l_offre_construite() {
        when(offreStorageRepository.trouverParIdExterne(any())).thenReturn(Optional.empty());

        Offre offre = useCase.executer(null, "Développeur Java", null, "ACME", null, "CDI", null, null, null, null, null);

        ArgumentCaptor<List<Offre>> captor = ArgumentCaptor.captor();
        verify(offreStorageRepository).sauvegarderTout(captor.capture());
        assertThat(captor.getValue()).containsExactly(offre);
    }

    @Test
    void assure_une_candidature_quand_l_etat_fourni_est_postule() {
        when(offreStorageRepository.trouverParIdExterne(any())).thenReturn(Optional.empty());

        Offre offre = useCase.executer(
                null, "Développeur Java", null, null, null, null, null, null, null, null, EtatOffre.POSTULE);

        verify(candidatureAutoCreationService).assurer(offre);
    }

    @Test
    void n_assure_pas_de_candidature_quand_l_etat_n_est_pas_postule() {
        when(offreStorageRepository.trouverParIdExterne(any())).thenReturn(Optional.empty());

        useCase.executer(null, "Développeur Java", null, null, null, null, null, null, null, null, null);

        verify(candidatureAutoCreationService, never()).assurer(any());
    }
}
