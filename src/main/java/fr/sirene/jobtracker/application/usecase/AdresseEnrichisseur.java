package fr.sirene.jobtracker.application.usecase;

import fr.sirene.jobtracker.application.port.GeocodageAdressePort;
import fr.sirene.jobtracker.application.port.LieuRepository;
import fr.sirene.jobtracker.domain.model.Lieu;
import fr.sirene.jobtracker.domain.model.Offre;

import org.springframework.stereotype.Component;

@Component
public class AdresseEnrichisseur {

    private final GeocodageAdressePort geocodageAdressePort;
    private final LieuRepository lieuRepository;

    public AdresseEnrichisseur(GeocodageAdressePort geocodageAdressePort, LieuRepository lieuRepository) {
        this.geocodageAdressePort = geocodageAdressePort;
        this.lieuRepository = lieuRepository;
    }

    public Offre enrichir(Offre offre) {
        Lieu lieu = offre.getLieu();
        if (lieu == null || lieu.latitude() == null || lieu.longitude() == null) {
            return offre;
        }

        String adresse = resoudreAdresse(lieu.latitude(), lieu.longitude());
        if (adresse == null) {
            return offre;
        }

        Lieu lieuEnrichi = new Lieu(lieu.libelle(), lieu.codeCommune(), lieu.latitude(), lieu.longitude(), adresse);
        return offre.toBuilder().lieu(lieuEnrichi).build();
    }

    private String resoudreAdresse(double latitude, double longitude) {
        return lieuRepository.rechercherAdresse(latitude, longitude)
                .orElseGet(() -> geocodageAdressePort.resoudreAdresse(latitude, longitude)
                        .map(adresse -> {
                            lieuRepository.enregistrerAdresse(latitude, longitude, adresse);
                            return adresse;
                        })
                        .orElse(null));
    }
}
