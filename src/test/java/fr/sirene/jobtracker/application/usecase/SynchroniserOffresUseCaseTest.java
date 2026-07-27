package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.OffreEmploiApiPort;
import fr.sirene.jobtracker.application.port.OffreStorageRepository;
import fr.sirene.jobtracker.domain.model.CritereRecherche;
import fr.sirene.jobtracker.domain.model.Offre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SynchroniserOffresUseCaseTest {

    @Mock
    private OffreEmploiApiPort offreEmploiApiPort;

    @Mock
    private OffreStorageRepository offreStorageRepository;

    @Mock
    private AdresseEnrichisseur adresseEnrichisseur;

    private SynchroniserOffresUseCase useCase;

    @BeforeEach
    void setUp() {
        lenient().when(adresseEnrichisseur.enrichir(any())).thenAnswer(invocation -> invocation.getArgument(0));
        RechercheOffresProperties proprietes =
                new RechercheOffresProperties(List.of("Java,développeur"), "CDI", "44109");
        useCase = new SynchroniserOffresUseCase(offreEmploiApiPort, offreStorageRepository, proprietes, adresseEnrichisseur);
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
    void construit_le_critere_de_recherche_a_partir_de_la_configuration() {
        when(offreEmploiApiPort.rechercherOffres(any(CritereRecherche.class))).thenReturn(List.of());

        useCase.executer();

        verify(offreEmploiApiPort).rechercherOffres(new CritereRecherche("Java,développeur", "CDI", "44109"));
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
        RechercheOffresProperties proprietes = new RechercheOffresProperties(
                List.of("Java,développeur", "lead dev, lead tech"), "CDI", "44109");
        useCase = new SynchroniserOffresUseCase(offreEmploiApiPort, offreStorageRepository, proprietes, adresseEnrichisseur);
        when(offreEmploiApiPort.rechercherOffres(any(CritereRecherche.class))).thenReturn(List.of());

        useCase.executer();

        verify(offreEmploiApiPort).rechercherOffres(new CritereRecherche("Java,développeur", "CDI", "44109"));
        verify(offreEmploiApiPort).rechercherOffres(new CritereRecherche("lead dev, lead tech", "CDI", "44109"));
    }

    @Test
    void deduplique_les_offres_trouvees_par_plusieurs_recherches() {
        RechercheOffresProperties proprietes = new RechercheOffresProperties(
                List.of("Java,développeur", "lead dev, lead tech"), "CDI", "44109");
        useCase = new SynchroniserOffresUseCase(offreEmploiApiPort, offreStorageRepository, proprietes, adresseEnrichisseur);
        Offre offre = Offre.builder().idExterne("123").intitule("Développeur Java").build();
        when(offreEmploiApiPort.rechercherOffres(new CritereRecherche("Java,développeur", "CDI", "44109")))
                .thenReturn(List.of(offre));
        when(offreEmploiApiPort.rechercherOffres(new CritereRecherche("lead dev, lead tech", "CDI", "44109")))
                .thenReturn(List.of(offre));

        int nombre = useCase.executer();

        assertThat(nombre).isEqualTo(1);
        verify(offreStorageRepository).sauvegarderTout(List.of(offre));
    }
}
