CREATE TABLE parametres_recherche (
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE parametres_recherche_mot_cle (
    parametres_recherche_id BIGINT NOT NULL REFERENCES parametres_recherche(id) ON DELETE CASCADE,
    valeur VARCHAR(255) NOT NULL
);

CREATE TABLE parametres_recherche_commune (
    parametres_recherche_id BIGINT NOT NULL REFERENCES parametres_recherche(id) ON DELETE CASCADE,
    code_insee VARCHAR(5) NOT NULL
);
