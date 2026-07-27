package fr.sirene.jobtracker.application.port;

import java.util.Optional;

public interface LieuRepository {

    Optional<String> rechercherAdresse(double latitude, double longitude);

    void enregistrerAdresse(double latitude, double longitude, String adresse);
}
