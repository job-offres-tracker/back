package fr.sirene.jobtracker.application.port;

public interface CvStockagePort {

    void ecrire(String nomUnique, byte[] contenu);

    byte[] lire(String nomUnique);
}
