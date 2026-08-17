package fr.sirene.jobtracker.infrastructure.cv;

import fr.sirene.jobtracker.application.port.cv.CvStockagePort;
import fr.sirene.jobtracker.domain.exception.StockageFichierException;
import fr.sirene.jobtracker.infrastructure.cv.config.CvStockageProperties;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class FichierCvStockageAdapter implements CvStockagePort {

    private final Path repertoire;

    public FichierCvStockageAdapter(CvStockageProperties properties) {
        this.repertoire = Path.of(properties.repertoire());
        try {
            Files.createDirectories(repertoire);
        } catch (IOException e) {
            throw new StockageFichierException(
                    "Impossible de créer le répertoire de stockage des CV : " + repertoire, e);
        }
    }

    @Override
    public void ecrire(String nomUnique, byte[] contenu) {
        try {
            Files.write(repertoire.resolve(nomUnique), contenu);
        } catch (IOException e) {
            throw new StockageFichierException("Impossible d'écrire le fichier " + nomUnique, e);
        }
    }

    @Override
    public byte[] lire(String nomUnique) {
        try {
            return Files.readAllBytes(repertoire.resolve(nomUnique));
        } catch (IOException e) {
            throw new StockageFichierException("Impossible de lire le fichier " + nomUnique, e);
        }
    }
}
