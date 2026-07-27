package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.GeocodageAdressePort;
import fr.sirene.jobtracker.application.port.LieuRepository;
import fr.sirene.jobtracker.domain.model.Lieu;
import fr.sirene.jobtracker.domain.model.Offre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdresseEnrichisseurTest {

    @Mock
    private GeocodageAdressePort geocodageAdressePort;

    @Mock
    private LieuRepository lieuRepository;

    @InjectMocks
    private AdresseEnrichisseur enrichisseur;


    @Test
    void ne_fait_rien_si_le_lieu_est_absent() {
        Offre offre = Offre.builder().idExterne("123").build();

        Offre resultat = enrichisseur.enrichir(offre);

        assertThat(resultat).isSameAs(offre);
        verify(geocodageAdressePort, never()).resoudreAdresse(anyDouble(), anyDouble());
    }

    @Test
    void ne_fait_rien_si_les_coordonnees_sont_absentes() {
        Lieu lieu = new Lieu("Nantes - 44", "44109", null, null, null);
        Offre offre = Offre.builder().idExterne("123").lieu(lieu).build();

        Offre resultat = enrichisseur.enrichir(offre);

        assertThat(resultat).isSameAs(offre);
        verify(geocodageAdressePort, never()).resoudreAdresse(anyDouble(), anyDouble());
    }

    @Test
    void utilise_le_cache_sans_appeler_le_geocodage() {
        Lieu lieu = new Lieu("Nantes - 44", "44109", 47.2184, -1.5536, null);
        Offre offre = Offre.builder().idExterne("123").lieu(lieu).build();
        when(lieuRepository.rechercherAdresse(47.2184, -1.5536)).thenReturn(Optional.of("8 Bd du Port 44000 Nantes"));

        Offre resultat = enrichisseur.enrichir(offre);

        assertThat(resultat.getLieu().adresse()).isEqualTo("8 Bd du Port 44000 Nantes");
        verify(geocodageAdressePort, never()).resoudreAdresse(anyDouble(), anyDouble());
    }

    @Test
    void appelle_le_geocodage_puis_enregistre_en_cache_si_absent() {
        Lieu lieu = new Lieu("Nantes - 44", "44109", 47.2184, -1.5536, null);
        Offre offre = Offre.builder().idExterne("123").lieu(lieu).build();
        when(lieuRepository.rechercherAdresse(47.2184, -1.5536)).thenReturn(Optional.empty());
        when(geocodageAdressePort.resoudreAdresse(47.2184, -1.5536)).thenReturn(Optional.of("8 Bd du Port 44000 Nantes"));

        Offre resultat = enrichisseur.enrichir(offre);

        assertThat(resultat.getLieu().adresse()).isEqualTo("8 Bd du Port 44000 Nantes");
        verify(lieuRepository).enregistrerAdresse(47.2184, -1.5536, "8 Bd du Port 44000 Nantes");
    }

    @Test
    void renvoie_l_offre_inchangee_si_le_geocodage_ne_trouve_rien() {
        Lieu lieu = new Lieu("Nantes - 44", "44109", 47.2184, -1.5536, null);
        Offre offre = Offre.builder().idExterne("123").lieu(lieu).build();
        when(lieuRepository.rechercherAdresse(47.2184, -1.5536)).thenReturn(Optional.empty());
        when(geocodageAdressePort.resoudreAdresse(47.2184, -1.5536)).thenReturn(Optional.empty());

        Offre resultat = enrichisseur.enrichir(offre);

        assertThat(resultat).isSameAs(offre);
        verify(lieuRepository, never()).enregistrerAdresse(anyDouble(), anyDouble(), any());
    }
}
