ALTER TABLE IF EXISTS project
ADD COLUMN communication_coefficient DECIMAL NOT NULL;

ALTER TABLE IF EXISTS project
ADD COLUMN risk_coefficient DECIMAL NOT NULL;