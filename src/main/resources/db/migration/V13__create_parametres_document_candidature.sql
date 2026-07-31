CREATE TABLE parametres_document_candidature (
    id BIGSERIAL PRIMARY KEY,
    taille_max_octets BIGINT NOT NULL
);

INSERT INTO parametres_document_candidature (taille_max_octets) VALUES (10485760);
