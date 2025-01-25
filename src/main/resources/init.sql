CREATE EXTENSION IF NOT EXISTS pgcrypto;
SELECT EXISTS (SELECT 1 FROM pg_catalog.pg_tables WHERE schemaname = 'public'  AND tablename = 'cinemas');
SELECT EXISTS (SELECT 1 FROM pg_catalog.pg_tables WHERE schemaname = 'public' AND tablename = 'sessions');
CREATE TABLE IF NOT EXISTS directors(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL);
CREATE TABLE IF NOT EXISTS movies(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    director_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    hall INT NOT NULL,
    FOREIGN KEY (director_id) REFERENCES directors(id) ON DELETE CASCADE);
