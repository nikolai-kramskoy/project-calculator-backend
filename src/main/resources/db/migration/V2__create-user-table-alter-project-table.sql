CREATE TABLE IF NOT EXISTS "user" (
    id BIGINT PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS user_id_sequence AS BIGINT;

-- alter project table to comply with user table

ALTER TABLE IF EXISTS project
DROP COLUMN created_by;

ALTER TABLE IF EXISTS project
ADD COLUMN creator_id BIGINT NOT NULL
REFERENCES "user" (id)
ON DELETE RESTRICT
ON UPDATE RESTRICT;

CREATE INDEX IF NOT EXISTS creator_id_index
ON project (creator_id);
