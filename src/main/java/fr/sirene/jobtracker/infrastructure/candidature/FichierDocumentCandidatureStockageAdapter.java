package fr.sirene.jobtracker.infrastructure.candidature;

import fr.sirene.jobtracker.application.port.candidature.DocumentCandidatureStockagePort;
import fr.sirene.jobtracker.domain.exception.StockageFichierException;
import fr.sirene.jobtracker.infrastructure.candidature.config.CandidatureDocumentStockageProperties;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class FichierDocumentCandidatureStockageAdapter implements DocumentCandidatureStockagePort {

    private final Path repertoire;

    public FichierDocumentCandidatureStockageAdapter(CandidatureDocumentStockageProperties properties) {
        this.repertoire = Path.of(properties.repertoire());
        try {
            Files.createDirectories(repertoire);
        } catch (IOException e) {
            throw new StockageFichierException(
                    "Impossible de créer le répertoire de stockage des documents de candidature : " + repertoire, e);
        }
    }

    @Override
    public void ecrire(Long candidatureId, String nomStocke, byte[] contenu) {
        try {
            Path repertoireCandidature = repertoire.resolve(String.valueOf(candidatureId));
            Files.createDirectories(repertoireCandidature);
            Files.write(repertoireCandidature.resolve(nomStocke), contenu);
        } catch (IOException e) {
            throw new StockageFichierException("Impossible d'écrire le fichier " + nomStocke, e);
        }
    }

    @Override
    public byte[] lire(Long candidatureId, String nomStocke) {
        try {
            return Files.readAllBytes(repertoire.resolve(String.valueOf(candidatureId)).resolve(nomStocke));
        } catch (IOException e) {
            throw new StockageFichierException("Impossible de lire le fichier " + nomStocke, e);
        }
    }
}
