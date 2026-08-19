package fr.sirene.jobtracker.application.port.cv;

public interface CvStockagePort {

    void ecrire(String nomUnique, byte[] contenu);

    byte[] lire(String nomUnique);
}
