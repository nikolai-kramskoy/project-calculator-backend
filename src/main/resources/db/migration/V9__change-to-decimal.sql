-- feature table

ALTER TABLE IF EXISTS feature
ALTER COLUMN best_case_estimate_in_days SET DATA TYPE DECIMAL,
ALTER COLUMN most_likely_estimate_in_days SET DATA TYPE DECIMAL,
ALTER COLUMN worst_case_estimate_in_days SET DATA TYPE DECIMAL;

-- team table

ALTER TABLE IF EXISTS team_members
ALTER COLUMN degree_of_involvement SET DATA TYPE DECIMAL;
