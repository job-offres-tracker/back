CREATE TABLE parametres_cv (
    id BIGSERIAL PRIMARY KEY,
    taille_max_octets BIGINT NOT NULL
);

INSERT INTO parametres_cv (taille_max_octets) VALUES (5242880);
