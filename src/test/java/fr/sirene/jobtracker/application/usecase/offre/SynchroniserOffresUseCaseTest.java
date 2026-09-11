package fr.sirene.jobtracker.application.usecase.offre;

import fr.sirene.jobtracker.application.port.offre.OffreEmploiApiPort;
import fr.sirene.jobtracker.application.port.offre.OffreStorageRepository;
import fr.sirene.jobtracker.application.port.parametres.ParametresRechercheRepository;
import fr.sirene.jobtracker.domain.exception.ParametresRechercheNonConfiguresException;
import fr.sirene.jobtracker.domain.model.CommuneRecherche;
import fr.sirene.jobtracker.domain.model.CritereRecherche;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.domain.model.ParametresRecherche;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SynchroniserOffresUseCaseTest {

    private static final CommuneRecherche NANTES = new CommuneRecherche("44109", "Nantes");
    private static final CommuneRecherche SAINT_HERBLAIN = new CommuneRecherche("44020", "Saint-Herblain");
    private static final CommuneRecherche REZE = new CommuneRecherche("44143", "Rezé");
    private static final CommuneRecherche ORVAULT = new CommuneRecherche("44114", "Orvault");
    private static final CommuneRecherche COUERON = new CommuneRecherche("44047", "Couëron");
    private static final CommuneRecherche VERTOU = new CommuneRecherche("44215", "Vertou");
    private static final CommuneRecherche BOUGUENAIS = new CommuneRecherche("44018", "Bouguenais");

    @Mock
    private OffreEmploiApiPort offreEmploiApiPort;

    @Mock
    private OffreStorageRepository offreStorageRepository;

    @Mock
    private ParametresRechercheRepository parametresRechercheRepository;

    @Mock
    private AdresseEnrichisseur adresseEnrichisseur;

    private SynchroniserOffresUseCase useCase;

    @BeforeEach
    void setUp() {
        lenient().when(adresseEnrichisseur.enrichir(any())).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(parametresRechercheRepository.recuperer())
                .thenReturn(new ParametresRecherche(List.of("Java,développeur"), List.of(NANTES), "CDI"));
        useCase = new SynchroniserOffresUseCase(
                offreEmploiApiPort, offreStorageRepository, parametresRechercheRepository, adresseEnrichisseur);
    }

    @Test
    void recherche_puis_sauvegarde_les_offres_trouvees() {
        Offre offre = Offre.builder().idExterne("123").intitule("Développeur Java").build();
        when(offreEmploiApiPort.rechercherOffres(any(CritereRecherche.class)))
                .thenReturn(List.of(offre));

        int nombre = useCase.executer();

        assertThat(nombre).isEqualTo(1);
        verify(offreStorageRepository).sauvegarderTout(List.of(offre));
    }

    @Test
    void construit_le_critere_de_recherche_a_partir_des_parametres_configures() {
        when(offreEmploiApiPort.rechercherOffres(any(CritereRecherche.class))).thenReturn(List.of());

        useCase.executer();

        verify(offreEmploiApiPort).rechercherOffres(new CritereRecherche("Java,développeur", "CDI", List.of(NANTES)));
    }

    @Test
    void transmet_toutes_les_communes_configurees_en_un_seul_critere_de_recherche() {
        List<CommuneRecherche> septCommunes =
                List.of(NANTES, SAINT_HERBLAIN, REZE, ORVAULT, COUERON, VERTOU, BOUGUENAIS);
        when(parametresRechercheRepository.recuperer())
                .thenReturn(new ParametresRecherche(List.of("Java,développeur"), septCommunes, "CDI"));
        when(offreEmploiApiPort.rechercherOffres(any(CritereRecherche.class))).thenReturn(List.of());

        useCase.executer();

        verify(offreEmploiApiPort, times(1)).rechercherOffres(any(CritereRecherche.class));
        verify(offreEmploiApiPort).rechercherOffres(new CritereRecherche("Java,développeur", "CDI", septCommunes));
    }

    @Test
    void croise_toutes_les_communes_avec_chaque_entree_de_mots_cles() {
        List<CommuneRecherche> septCommunes =
                List.of(NANTES, SAINT_HERBLAIN, REZE, ORVAULT, COUERON, VERTOU, BOUGUENAIS);
        when(parametresRechercheRepository.recuperer())
                .thenReturn(new ParametresRecherche(
                        List.of("Java,développeur", "lead dev, lead tech"), septCommunes, "CDI"));
        when(offreEmploiApiPort.rechercherOffres(any(CritereRecherche.class))).thenReturn(List.of());

        useCase.executer();

        verify(offreEmploiApiPort, times(2)).rechercherOffres(any(CritereRecherche.class));
        verify(offreEmploiApiPort).rechercherOffres(new CritereRecherche("Java,développeur", "CDI", septCommunes));
        verify(offreEmploiApiPort).rechercherOffres(
                new CritereRecherche("lead dev, lead tech", "CDI", septCommunes));
    }

    @Test
    void ne_sauvegarde_rien_si_aucune_offre_trouvee() {
        when(offreEmploiApiPort.rechercherOffres(any(CritereRecherche.class))).thenReturn(List.of());

        int nombre = useCase.executer();

        assertThat(nombre).isZero();
        verify(offreStorageRepository).sauvegarderTout(List.of());
    }

    @Test
    void lance_une_recherche_par_entree_de_mots_cles() {
        when(parametresRechercheRepository.recuperer())
                .thenReturn(new ParametresRecherche(List.of("Java,développeur", "lead dev, lead tech"), List.of(NANTES), "CDI"));
        when(offreEmploiApiPort.rechercherOffres(any(CritereRecherche.class))).thenReturn(List.of());

        useCase.executer();

        verify(offreEmploiApiPort).rechercherOffres(new CritereRecherche("Java,développeur", "CDI", List.of(NANTES)));
        verify(offreEmploiApiPort).rechercherOffres(new CritereRecherche("lead dev, lead tech", "CDI", List.of(NANTES)));
    }

    @Test
    void deduplique_les_offres_trouvees_par_plusieurs_recherches() {
        when(parametresRechercheRepository.recuperer())
                .thenReturn(new ParametresRecherche(List.of("Java,développeur", "lead dev, lead tech"), List.of(NANTES), "CDI"));
        Offre offre = Offre.builder().idExterne("123").intitule("Développeur Java").build();
        when(offreEmploiApiPort.rechercherOffres(new CritereRecherche("Java,développeur", "CDI", List.of(NANTES))))
                .thenReturn(List.of(offre));
        when(offreEmploiApiPort.rechercherOffres(new CritereRecherche("lead dev, lead tech", "CDI", List.of(NANTES))))
                .thenReturn(List.of(offre));

        int nombre = useCase.executer();

        assertThat(nombre).isEqualTo(1);
        verify(offreStorageRepository).sauvegarderTout(List.of(offre));
    }

    @Test
    void leve_une_exception_quand_aucun_mot_cle_n_est_configure() {
        when(parametresRechercheRepository.recuperer())
                .thenReturn(new ParametresRecherche(List.of(), List.of(NANTES), "CDI"));

        assertThatThrownBy(() -> useCase.executer())
                .isInstanceOf(ParametresRechercheNonConfiguresException.class);
        verifyNoInteractions(offreEmploiApiPort, offreStorageRepository);
    }

    @Test
    void leve_une_exception_quand_aucune_commune_n_est_configuree() {
        when(parametresRechercheRepository.recuperer())
                .thenReturn(new ParametresRecherche(List.of("Java,développeur"), List.of(), "CDI"));

        assertThatThrownBy(() -> useCase.executer())
                .isInstanceOf(ParametresRechercheNonConfiguresException.class);
        verifyNoInteractions(offreEmploiApiPort, offreStorageRepository);
    }

    @Test
    void leve_une_exception_quand_le_type_de_contrat_n_est_pas_configure() {
        when(parametresRechercheRepository.recuperer())
                .thenReturn(new ParametresRecherche(List.of("Java,développeur"), List.of(NANTES), null));

        assertThatThrownBy(() -> useCase.executer())
                .isInstanceOf(ParametresRechercheNonConfiguresException.class);
        verifyNoInteractions(offreEmploiApiPort, offreStorageRepository);
    }
}
