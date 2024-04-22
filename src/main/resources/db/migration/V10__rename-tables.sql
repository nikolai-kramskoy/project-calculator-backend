-- projects_rate table

ALTER TABLE IF EXISTS projects_rate
RENAME TO project_rate;

-- team_members

ALTER TABLE IF EXISTS team_members
RENAME TO team_member;
