package fr.sirene.jobtracker.infrastructure.candidature;

import fr.sirene.jobtracker.domain.exception.StockageFichierException;
import fr.sirene.jobtracker.infrastructure.candidature.config.CandidatureDocumentStockageProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichierDocumentCandidatureStockageAdapterTest {

    @TempDir
    private Path repertoireTemporaire;

    @Test
    void ecrit_puis_relit_le_meme_contenu() {
        FichierDocumentCandidatureStockageAdapter adapter = new FichierDocumentCandidatureStockageAdapter(
                new CandidatureDocumentStockageProperties(repertoireTemporaire.toString()));
        byte[] contenu = {1, 2, 3, 4};

        adapter.ecrire(1L, "abc.pdf", contenu);

        assertThat(adapter.lire(1L, "abc.pdf")).containsExactly(contenu);
    }

    @Test
    void stocke_le_fichier_dans_un_sous_dossier_nomme_d_apres_l_id_de_la_candidature() {
        FichierDocumentCandidatureStockageAdapter adapter = new FichierDocumentCandidatureStockageAdapter(
                new CandidatureDocumentStockageProperties(repertoireTemporaire.toString()));

        adapter.ecrire(42L, "abc.pdf", new byte[] {1, 2, 3});

        assertThat(repertoireTemporaire.resolve("42").resolve("abc.pdf")).exists();
    }

    @Test
    void isole_les_fichiers_de_deux_candidatures_differentes() {
        FichierDocumentCandidatureStockageAdapter adapter = new FichierDocumentCandidatureStockageAdapter(
                new CandidatureDocumentStockageProperties(repertoireTemporaire.toString()));

        adapter.ecrire(1L, "meme-nom.pdf", new byte[] {1});
        adapter.ecrire(2L, "meme-nom.pdf", new byte[] {2});

        assertThat(adapter.lire(1L, "meme-nom.pdf")).containsExactly(1);
        assertThat(adapter.lire(2L, "meme-nom.pdf")).containsExactly(2);
    }

    @Test
    void cree_le_repertoire_de_stockage_s_il_n_existe_pas_encore() {
        Path repertoireInexistant = repertoireTemporaire.resolve("sous-dossier/candidature-documents");

        new FichierDocumentCandidatureStockageAdapter(new CandidatureDocumentStockageProperties(repertoireInexistant.toString()));

        assertThat(repertoireInexistant).exists().isDirectory();
    }

    @Test
    void leve_une_exception_de_stockage_si_le_fichier_est_introuvable_a_la_lecture() {
        FichierDocumentCandidatureStockageAdapter adapter = new FichierDocumentCandidatureStockageAdapter(
                new CandidatureDocumentStockageProperties(repertoireTemporaire.toString()));

        assertThatThrownBy(() -> adapter.lire(1L, "inconnu.pdf"))
                .isInstanceOf(StockageFichierException.class);
    }
}
