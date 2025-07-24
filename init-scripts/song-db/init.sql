--\connect song-db;

CREATE TABLE songs_metadata (
                       id SERIAL PRIMARY KEY,
                       resource_id INTEGER NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       artist VARCHAR(255)  NOT NULL,
                       album VARCHAR(255) NOT NULL,
                       duration VARCHAR(255) NOT NULL,
                       year VARCHAR(255) NOT NULL
);

CREATE INDEX idx_songs_metadata_resource_id ON songs_metadata (resource_id);