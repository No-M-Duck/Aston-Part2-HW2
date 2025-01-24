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

INSERT INTO directors (id, name, last_name, country) VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'Steven', 'Spielberg', 'USA'),
    ('550e8400-e29b-41d4-a716-446655440001', 'Christopher', 'Nolan', 'UK');

INSERT INTO movies (id, director_id, title, release_date, duration, hall) VALUES
    ('660e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', 'Jurassic Park', '1993-06-11', 127, 1),
    ('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Inception', '2010-07-16', 148, 2);