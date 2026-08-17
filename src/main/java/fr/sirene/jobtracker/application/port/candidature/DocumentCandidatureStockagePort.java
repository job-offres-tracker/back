package fr.sirene.jobtracker.application.port.candidature;

public interface DocumentCandidatureStockagePort {

    void ecrire(Long candidatureId, String nomStocke, byte[] contenu);

    byte[] lire(Long candidatureId, String nomStocke);
}
