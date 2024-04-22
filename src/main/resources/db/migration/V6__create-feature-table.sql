CREATE TABLE IF NOT EXISTS feature (
    id BIGINT PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES project (id),
    milestone_id BIGINT REFERENCES milestone (id),
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    -- i guess DECIMAL(10, 2) will be enough for time estimation in days
    best_case_estimate_in_days DECIMAL(10, 2) NOT NULL,
    most_likely_estimate_in_days DECIMAL(10, 2) NOT NULL,
    worst_case_estimate_in_days DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_updated_at TIMESTAMP NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS feature_id_sequence AS BIGINT;

CREATE INDEX IF NOT EXISTS project_id_index
ON feature (project_id);

CREATE INDEX IF NOT EXISTS milestone_id_index
ON feature (milestone_id);
