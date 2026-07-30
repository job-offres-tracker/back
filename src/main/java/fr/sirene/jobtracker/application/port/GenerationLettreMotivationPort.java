package fr.sirene.jobtracker.application.port;

public interface GenerationLettreMotivationPort {

    String genererLettre(String idExterneOffre, String cvNomUnique);
}
