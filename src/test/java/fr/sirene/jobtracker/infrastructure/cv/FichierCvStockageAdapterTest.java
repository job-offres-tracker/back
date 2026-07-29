package fr.sirene.jobtracker.infrastructure.cv;

import fr.sirene.jobtracker.domain.exception.StockageFichierException;
import fr.sirene.jobtracker.infrastructure.cv.config.CvStockageProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichierCvStockageAdapterTest {

    @TempDir
    private Path repertoireTemporaire;

    @Test
    void ecrit_puis_relit_le_meme_contenu() {
        FichierCvStockageAdapter adapter = new FichierCvStockageAdapter(
                new CvStockageProperties(repertoireTemporaire.toString()));
        byte[] contenu = {1, 2, 3, 4};

        adapter.ecrire("abc.pdf", contenu);

        assertThat(adapter.lire("abc.pdf")).containsExactly(contenu);
    }

    @Test
    void cree_le_repertoire_de_stockage_s_il_n_existe_pas_encore() {
        Path repertoireInexistant = repertoireTemporaire.resolve("sous-dossier/cv");

        new FichierCvStockageAdapter(new CvStockageProperties(repertoireInexistant.toString()));

        assertThat(repertoireInexistant).exists().isDirectory();
    }

    @Test
    void leve_une_exception_de_stockage_si_le_fichier_est_introuvable_a_la_lecture() {
        FichierCvStockageAdapter adapter = new FichierCvStockageAdapter(
                new CvStockageProperties(repertoireTemporaire.toString()));

        assertThatThrownBy(() -> adapter.lire("inconnu.pdf"))
                .isInstanceOf(StockageFichierException.class);
    }
}
