CREATE TABLE IF NOT EXISTS "milestone" (
    id BIGINT PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES project (id)
                               ON DELETE RESTRICT
                               ON UPDATE RESTRICT,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    start_timestamp TIMESTAMP NOT NULL,
    end_timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_updated_at TIMESTAMP NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS milestone_id_sequence AS BIGINT;

CREATE INDEX IF NOT EXISTS project_id_index
ON milestone (project_id);
