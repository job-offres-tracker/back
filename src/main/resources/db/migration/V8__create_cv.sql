CREATE TABLE cv (
    id BIGSERIAL PRIMARY KEY,
    nom_unique VARCHAR(255) NOT NULL UNIQUE,
    nom_original VARCHAR(255) NOT NULL,
    taille_octets BIGINT NOT NULL,
    date_upload TIMESTAMP NOT NULL
);
