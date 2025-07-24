--\connect song-db;

CREATE TABLE songs_metadata (
                       id SERIAL PRIMARY KEY,
                       resource_id INTEGER UNIQUE NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       artist VARCHAR(255)  NOT NULL,
                       album VARCHAR(255) NOT NULL,
                       duration VARCHAR(255) NOT NULL,
                       year VARCHAR(255) NOT NULL
);