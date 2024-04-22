-- milestone

ALTER TABLE IF EXISTS milestone
ADD COLUMN estimate_in_days DECIMAL NOT NULL;

-- project

ALTER TABLE IF EXISTS project
ADD COLUMN estimate_in_days DECIMAL NOT NULL;
