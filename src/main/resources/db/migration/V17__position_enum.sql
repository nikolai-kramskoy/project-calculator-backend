CREATE TYPE position_type
AS ENUM ('REGULAR_DEVELOPER', 'SENIOR_DEVELOPER', 'PROJECT_MANAGER', 'QA_ENGINEER', 'ARCHITECT', 'DEVOPS_ENGINEER');

-- project_rate

ALTER TABLE IF EXISTS project_rate
ADD COLUMN team_member_position position_type;

UPDATE project_rate SET team_member_position = CASE
  WHEN member_position = 'REGULAR_DEVELOPER' THEN 'REGULAR_DEVELOPER'::position_type
  WHEN member_position = 'SENIOR_DEVELOPER' THEN 'SENIOR_DEVELOPER'::position_type
  WHEN member_position = 'PROJECT_MANAGER' THEN 'PROJECT_MANAGER'::position_type
  WHEN member_position = 'QA_ENGINEER' THEN 'QA_ENGINEER'::position_type
  WHEN member_position = 'ARCHITECT' THEN 'ARCHITECT'::position_type
  WHEN member_position = 'DEVOPS_ENGINEER' THEN 'DEVOPS_ENGINEER'::position_type
  ELSE NULL
END;

ALTER TABLE IF EXISTS project_rate
DROP COLUMN member_position;

-- team_member

ALTER TABLE IF EXISTS team_member
ADD COLUMN team_member_position position_type;

UPDATE team_member SET team_member_position = CASE
  WHEN member_position = 'REGULAR_DEVELOPER' THEN 'REGULAR_DEVELOPER'::position_type
  WHEN member_position = 'SENIOR_DEVELOPER' THEN 'SENIOR_DEVELOPER'::position_type
  WHEN member_position = 'PROJECT_MANAGER' THEN 'PROJECT_MANAGER'::position_type
  WHEN member_position = 'QA_ENGINEER' THEN 'QA_ENGINEER'::position_type
  WHEN member_position = 'ARCHITECT' THEN 'ARCHITECT'::position_type
  WHEN member_position = 'DEVOPS_ENGINEER' THEN 'DEVOPS_ENGINEER'::position_type
  ELSE NULL
END;

ALTER TABLE IF EXISTS team_member
DROP COLUMN member_position;
