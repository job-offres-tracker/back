package fr.sirene.jobtracker.infrastructure.francetravail.mapper;

import fr.sirene.jobtracker.domain.model.EtatOffre;
import fr.sirene.jobtracker.domain.model.Offre;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.EntrepriseFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.LieuTravailFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.OffreFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.OrigineOffreFranceTravail;
import fr.sirene.jobtracker.infrastructure.francetravail.dto.SalaireFranceTravail;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OffreMapperTest {

    private final OffreMapper mapper = new OffreMapper();

    @Test
    void mappe_une_offre_complete() {
        OffreFranceTravail dto = new OffreFranceTravail(
                "123ABC",
                "Développeur Java",
                "Description du poste",
                "2026-06-01T10:00:00.000Z",
                new EntrepriseFranceTravail("ACME Corp"),
                new LieuTravailFranceTravail("Nantes - 44", "44109", 47.2184, -1.5536),
                "CDI",
                new SalaireFranceTravail("Selon profil"),
                new OrigineOffreFranceTravail("https://candidat.francetravail.fr/offres/123ABC"));

        Offre offre = mapper.toDomain(dto);

        assertThat(offre.getIdExterne()).isEqualTo("123ABC");
        assertThat(offre.getIntitule()).isEqualTo("Développeur Java");
        assertThat(offre.getDescription()).isEqualTo("Description du poste");
        assertThat(offre.getEntreprise()).isEqualTo("ACME Corp");
        assertThat(offre.getLieu().libelle()).isEqualTo("Nantes - 44");
        assertThat(offre.getLieu().codeCommune()).isEqualTo("44109");
        assertThat(offre.getLieu().latitude()).isEqualTo(47.2184);
        assertThat(offre.getLieu().longitude()).isEqualTo(-1.5536);
        assertThat(offre.getTypeContrat()).isEqualTo("CDI");
        assertThat(offre.getSalaire()).isEqualTo("Selon profil");
        assertThat(offre.getUrlOrigine()).isEqualTo("https://candidat.francetravail.fr/offres/123ABC");
        assertThat(offre.getDateCreation()).isEqualTo(LocalDateTime.of(2026, 6, 1, 10, 0, 0));
        assertThat(offre.getEtat()).isEqualTo(EtatOffre.NON_LU);
        assertThat(offre.getProvenance()).isEqualTo("FRANCE_TRAVAIL");
    }

    @Test
    void gere_les_sous_objets_nuls() {
        OffreFranceTravail dto = new OffreFranceTravail(
                "123ABC", "Développeur Java", "Description", "2026-06-01T10:00:00.000Z",
                null, null, "CDI", null, null);

        Offre offre = mapper.toDomain(dto);

        assertThat(offre.getEntreprise()).isNull();
        assertThat(offre.getLieu()).isNull();
        assertThat(offre.getSalaire()).isNull();
        assertThat(offre.getUrlOrigine()).isNull();
    }

    @Test
    void renvoie_une_date_de_creation_nulle_si_le_format_est_invalide() {
        OffreFranceTravail dto = new OffreFranceTravail(
                "123ABC", "Développeur Java", "Description", "pas-une-date",
                null, null, "CDI", null, null);

        Offre offre = mapper.toDomain(dto);

        assertThat(offre.getDateCreation()).isNull();
    }

    @Test
    void renvoie_une_date_de_creation_nulle_si_absente() {
        OffreFranceTravail dto = new OffreFranceTravail(
                "123ABC", "Développeur Java", "Description", null,
                null, null, "CDI", null, null);

        Offre offre = mapper.toDomain(dto);

        assertThat(offre.getDateCreation()).isNull();
    }

    @Test
    void toDomainList_mappe_toutes_les_offres() {
        OffreFranceTravail dto1 = new OffreFranceTravail(
                "1", "Poste 1", null, null, null, null, "CDI", null, null);
        OffreFranceTravail dto2 = new OffreFranceTravail(
                "2", "Poste 2", null, null, null, null, "CDD", null, null);

        List<Offre> offres = mapper.toDomainList(Arrays.asList(dto1, dto2));

        assertThat(offres).hasSize(2);
        assertThat(offres.get(0).getIdExterne()).isEqualTo("1");
        assertThat(offres.get(1).getIdExterne()).isEqualTo("2");
    }
}
