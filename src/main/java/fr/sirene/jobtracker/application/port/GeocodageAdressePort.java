package fr.sirene.jobtracker.application.port;

import java.util.Optional;

public interface GeocodageAdressePort {

    Optional<String> resoudreAdresse(double latitude, double longitude);
}
