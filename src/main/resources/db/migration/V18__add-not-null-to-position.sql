-- project_rate

ALTER TABLE project_rate
ALTER COLUMN team_member_position SET NOT NULL;

ALTER TABLE project_rate
ADD CONSTRAINT unique_project_id_team_member_position1 UNIQUE (project_id, team_member_position);

-- team_member

ALTER TABLE team_member
ALTER COLUMN team_member_position SET NOT NULL;

ALTER TABLE team_member
ADD CONSTRAINT unique_project_id_team_member_position2 UNIQUE (project_id, team_member_position);
