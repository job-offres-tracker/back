CREATE TABLE offre (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_externe VARCHAR(64) NOT NULL UNIQUE,
    intitule VARCHAR(500),
    description TEXT,
    entreprise VARCHAR(255),
    lieu_travail VARCHAR(255),
    type_contrat VARCHAR(50),
    salaire VARCHAR(255),
    url_origine VARCHAR(1000),
    date_creation TIMESTAMP,
    date_import TIMESTAMP NOT NULL
);

CREATE INDEX idx_offre_date_creation ON offre (date_creation DESC);
