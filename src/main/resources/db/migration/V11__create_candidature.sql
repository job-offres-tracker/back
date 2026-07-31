CREATE TABLE candidature (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    offre_id BIGINT NOT NULL UNIQUE REFERENCES offre(id),
    date_candidature TIMESTAMP NOT NULL
);

CREATE TABLE evenement_candidature (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    candidature_id BIGINT NOT NULL REFERENCES candidature(id) ON DELETE CASCADE,
    date_evenement DATE NOT NULL,
    type VARCHAR(20) NOT NULL,
    description TEXT
);

CREATE TABLE document_candidature (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    candidature_id BIGINT NOT NULL REFERENCES candidature(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL,
    libelle VARCHAR(255) NOT NULL,
    cv_id BIGINT REFERENCES cv(id),
    nom_stocke VARCHAR(255),
    taille_octets BIGINT,
    content_type VARCHAR(100),
    contenu_texte TEXT,
    date_ajout TIMESTAMP NOT NULL
);

CREATE INDEX idx_evenement_candidature_candidature_id ON evenement_candidature(candidature_id);
CREATE INDEX idx_document_candidature_candidature_id ON document_candidature(candidature_id);
