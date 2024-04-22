CREATE TABLE IF NOT EXISTS projects_rate
(
    id               BIGINT PRIMARY KEY,
    member_position  VARCHAR(50) NOT NULL,
    dollars_per_hour DECIMAL     NOT NULL,
    project_id       BIGINT      NOT NULL,
    UNIQUE (member_position, project_id),
    FOREIGN KEY (project_id) REFERENCES project (id)
);