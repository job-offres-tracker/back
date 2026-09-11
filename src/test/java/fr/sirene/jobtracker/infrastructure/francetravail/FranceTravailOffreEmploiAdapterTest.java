package fr.sirene.jobtracker.infrastructure.francetravail;

import fr.sirene.jobtracker.domain.model.CritereRecherche;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.infrastructure.francetravail.client.FranceTravailApiClient;
import fr.sirene.jobtracker.infrastructure.francetravail.client.FranceTravailAuthClient;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.OffreFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.ReponseRechercheFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.mapper.OffreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fr.sirene.jobtracker.domain.model.CommuneRechercheFixtures.NANTES;
import static fr.sirene.jobtracker.domain.model.CommuneRechercheFixtures.SAINT_HERBLAIN;
import static fr.sirene.jobtracker.domain.model.CommuneRechercheFixtures.septCommunesNantesMetropole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranceTravailOffreEmploiAdapterTest {

    @Mock
    private FranceTravailAuthClient authClient;

    @Mock
    private FranceTravailApiClient apiClient;

    @Mock
    private OffreMapper mapper;

    private FranceTravailOffreEmploiAdapter adapter;

    private final CritereRecherche critere = new CritereRecherche("Java", "CDI", List.of(NANTES));

    @BeforeEach
    void setUp() {
        adapter = new FranceTravailOffreEmploiAdapter(authClient, apiClient, mapper);
        lenient().when(authClient.obtenirToken()).thenReturn("jeton-abc");
    }

    private OffreFranceTravail offreDto(String id) {
        return new OffreFranceTravail(id, "Développeur Java", null, null, null, null, "CDI", null, null);
    }

    private List<OffreFranceTravail> pageDe(int taille, int offsetId) {
        List<OffreFranceTravail> page = new ArrayList<>();
        for (int i = 0; i < taille; i++) {
            page.add(offreDto("id-" + (offsetId + i)));
        }
        return page;
    }

    private List<Offre> mapperOffres(List<OffreFranceTravail> dtos) {
        return dtos.stream().map(dto -> Offre.builder().idExterne(dto.id()).build()).toList();
    }

    @Test
    void s_arrete_apres_une_seule_page_quand_le_total_est_atteint() {
        List<OffreFranceTravail> page = pageDe(2, 0);
        when(apiClient.rechercherOffres("Java", "CDI", "44109", 0, 49, "jeton-abc"))
                .thenReturn(ResponseEntity.ok()
                        .header("Content-Range", "offres 0-1/2")
                        .body(new ReponseRechercheFranceTravail(page)));
        when(mapper.toDomainList(page)).thenAnswer(inv -> mapperOffres(page));

        List<Offre> resultat = adapter.rechercherOffres(critere);

        assertThat(resultat).hasSize(2);
        verify(apiClient).rechercherOffres("Java", "CDI", "44109", 0, 49, "jeton-abc");
    }

    @Test
    void enchaine_les_pages_jusqu_a_couvrir_le_total_annonce() {
        List<OffreFranceTravail> page1 = pageDe(50, 0);
        List<OffreFranceTravail> page2 = pageDe(20, 50);
        when(apiClient.rechercherOffres("Java", "CDI", "44109", 0, 49, "jeton-abc"))
                .thenReturn(ResponseEntity.ok()
                        .header("Content-Range", "offres 0-49/70")
                        .body(new ReponseRechercheFranceTravail(page1)));
        when(apiClient.rechercherOffres("Java", "CDI", "44109", 50, 99, "jeton-abc"))
                .thenReturn(ResponseEntity.ok()
                        .header("Content-Range", "offres 50-69/70")
                        .body(new ReponseRechercheFranceTravail(page2)));
        when(mapper.toDomainList(page1)).thenAnswer(inv -> mapperOffres(page1));
        when(mapper.toDomainList(page2)).thenAnswer(inv -> mapperOffres(page2));

        List<Offre> resultat = adapter.rechercherOffres(critere);

        assertThat(resultat).hasSize(70);
        verify(apiClient).rechercherOffres("Java", "CDI", "44109", 0, 49, "jeton-abc");
        verify(apiClient).rechercherOffres("Java", "CDI", "44109", 50, 99, "jeton-abc");
    }

    @Test
    void s_arrete_si_une_page_est_vide_meme_si_le_total_annonce_en_suggere_davantage() {
        List<OffreFranceTravail> page1 = pageDe(50, 0);
        when(apiClient.rechercherOffres("Java", "CDI", "44109", 0, 49, "jeton-abc"))
                .thenReturn(ResponseEntity.ok()
                        .header("Content-Range", "offres 0-49/200")
                        .body(new ReponseRechercheFranceTravail(page1)));
        when(apiClient.rechercherOffres("Java", "CDI", "44109", 50, 99, "jeton-abc"))
                .thenReturn(ResponseEntity.ok().body(new ReponseRechercheFranceTravail(Collections.emptyList())));
        when(mapper.toDomainList(page1)).thenAnswer(inv -> mapperOffres(page1));

        List<Offre> resultat = adapter.rechercherOffres(critere);

        assertThat(resultat).hasSize(50);
        verify(apiClient).rechercherOffres("Java", "CDI", "44109", 50, 99, "jeton-abc");
    }

    @Test
    void s_arrete_apres_la_premiere_page_quand_l_en_tete_content_range_est_absent() {
        List<OffreFranceTravail> page = pageDe(50, 0);
        when(apiClient.rechercherOffres("Java", "CDI", "44109", 0, 49, "jeton-abc"))
                .thenReturn(ResponseEntity.ok().body(new ReponseRechercheFranceTravail(page)));
        when(mapper.toDomainList(page)).thenAnswer(inv -> mapperOffres(page));

        List<Offre> resultat = adapter.rechercherOffres(critere);

        assertThat(resultat).hasSize(50);
        verify(apiClient, times(1))
                .rechercherOffres(anyString(), anyString(), anyString(), anyInt(), anyInt(), anyString());
    }

    @Test
    void joint_les_codes_commune_d_un_meme_groupe_en_une_chaine_separee_par_des_virgules() {
        CritereRecherche critereDeuxCommunes = new CritereRecherche("Java", "CDI", List.of(NANTES, SAINT_HERBLAIN));
        when(apiClient.rechercherOffres("Java", "CDI", "44109,44020", 0, 49, "jeton-abc"))
                .thenReturn(ResponseEntity.ok().body(new ReponseRechercheFranceTravail(Collections.emptyList())));

        adapter.rechercherOffres(critereDeuxCommunes);

        verify(apiClient).rechercherOffres("Java", "CDI", "44109,44020", 0, 49, "jeton-abc");
    }

    @Test
    void decoupe_les_communes_en_groupes_de_5_maximum_pour_respecter_la_contrainte_de_l_api() {
        CritereRecherche critereSeptCommunes = new CritereRecherche("Java", "CDI", septCommunesNantesMetropole());
        when(apiClient.rechercherOffres(anyString(), anyString(), anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(ResponseEntity.ok().body(new ReponseRechercheFranceTravail(Collections.emptyList())));

        adapter.rechercherOffres(critereSeptCommunes);

        verify(apiClient).rechercherOffres("Java", "CDI", "44109,44020,44143,44114,44047", 0, 49, "jeton-abc");
        verify(apiClient).rechercherOffres("Java", "CDI", "44215,44018", 0, 49, "jeton-abc");
    }

    @Test
    void agrege_les_offres_de_tous_les_groupes_de_communes() {
        CritereRecherche critereSeptCommunes = new CritereRecherche("Java", "CDI", septCommunesNantesMetropole());
        List<OffreFranceTravail> pageGroupe1 = pageDe(2, 0);
        List<OffreFranceTravail> pageGroupe2 = pageDe(1, 2);
        when(apiClient.rechercherOffres("Java", "CDI", "44109,44020,44143,44114,44047", 0, 49, "jeton-abc"))
                .thenReturn(ResponseEntity.ok().body(new ReponseRechercheFranceTravail(pageGroupe1)));
        when(apiClient.rechercherOffres("Java", "CDI", "44215,44018", 0, 49, "jeton-abc"))
                .thenReturn(ResponseEntity.ok().body(new ReponseRechercheFranceTravail(pageGroupe2)));
        when(mapper.toDomainList(pageGroupe1)).thenAnswer(inv -> mapperOffres(pageGroupe1));
        when(mapper.toDomainList(pageGroupe2)).thenAnswer(inv -> mapperOffres(pageGroupe2));

        List<Offre> resultat = adapter.rechercherOffres(critereSeptCommunes);

        assertThat(resultat).hasSize(3);
    }
}
