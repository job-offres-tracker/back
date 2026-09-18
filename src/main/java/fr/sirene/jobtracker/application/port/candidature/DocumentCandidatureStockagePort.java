package fr.sirene.jobtracker.application.port.candidature;

public interface DocumentCandidatureStockagePort {

    void ecrire(long candidatureId, String nomStocke, byte[] contenu);

    byte[] lire(long candidatureId, String nomStocke);
}
