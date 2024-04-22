-- project creator_id

ALTER TABLE IF EXISTS project
DROP CONSTRAINT project_creator_id_fkey,
ADD CONSTRAINT project_creator_id_fkey
FOREIGN KEY (creator_id) REFERENCES "user" (id)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- milestone project_id

ALTER TABLE IF EXISTS milestone
DROP CONSTRAINT milestone_project_id_fkey,
ADD CONSTRAINT milestone_project_id_fkey
FOREIGN KEY (project_id) REFERENCES project (id)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- rate project_id

ALTER TABLE IF EXISTS project_rate
DROP CONSTRAINT projects_rate_project_id_fkey,
ADD CONSTRAINT project_rate_project_id_fkey
FOREIGN KEY (project_id) REFERENCES project (id)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- team member project_id

ALTER TABLE IF EXISTS team_member
DROP CONSTRAINT team_members_project_id_fkey,
ADD CONSTRAINT team_member_project_id_fkey
FOREIGN KEY (project_id) REFERENCES project (id)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- feature project_id

ALTER TABLE IF EXISTS feature
DROP CONSTRAINT feature_project_id_fkey,
ADD CONSTRAINT feature_project_id_fkey
FOREIGN KEY (project_id) REFERENCES project (id)
ON DELETE CASCADE
ON UPDATE CASCADE;

-- feature milestone_id

ALTER TABLE IF EXISTS feature
DROP CONSTRAINT feature_milestone_id_fkey,
ADD CONSTRAINT feature_milestone_id_fkey
FOREIGN KEY (milestone_id) REFERENCES milestone (id)
ON DELETE SET NULL
ON UPDATE CASCADE;
