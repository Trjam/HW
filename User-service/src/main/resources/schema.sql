CREATE SCHEMA IF NOT EXISTS "user";

CREATE TABLE IF NOT EXISTS "user".users (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    age         INTEGER,
    created_at  TIMESTAMP NOT NULL
    );