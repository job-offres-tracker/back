package fr.sirene.jobtracker.application.port.offre;

import java.util.Optional;

public interface GeocodageAdressePort {

    Optional<String> resoudreAdresse(double latitude, double longitude);
}
