CREATE TABLE lieu (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    libelle VARCHAR(255),
    code_commune VARCHAR(5),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    adresse VARCHAR(500),
    CONSTRAINT uq_lieu_coordonnees UNIQUE (latitude, longitude)
);

ALTER TABLE offre ADD COLUMN lieu_id BIGINT REFERENCES lieu(id);

INSERT INTO lieu (libelle, code_commune, latitude, longitude)
SELECT DISTINCT ON (latitude, longitude) lieu_travail, code_commune, latitude, longitude
FROM offre
WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

UPDATE offre o SET lieu_id = l.id
FROM lieu l
WHERE o.latitude = l.latitude AND o.longitude = l.longitude;

ALTER TABLE offre DROP COLUMN lieu_travail;
ALTER TABLE offre DROP COLUMN code_commune;
ALTER TABLE offre DROP COLUMN latitude;
ALTER TABLE offre DROP COLUMN longitude;
