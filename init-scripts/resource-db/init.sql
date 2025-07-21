--\connect resource-db;

CREATE TABLE songs (
                           id SERIAL PRIMARY KEY,
--                           data BYTEA,
                           data OID,
                           checksum VARCHAR(255) UNIQUE NOT NULL
);

