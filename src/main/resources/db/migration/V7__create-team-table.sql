CREATE TABLE IF NOT EXISTS team_members
(
    id                    BIGINT PRIMARY KEY,
    member_position       VARCHAR(50)      NOT NULL,
    degree_of_involvement DOUBLE PRECISION NOT NULL,
    project_id            BIGINT           NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project (id)
);