-- rate table

ALTER TABLE IF EXISTS project_rate
RENAME COLUMN dollars_per_hour TO rubles_per_hour;
