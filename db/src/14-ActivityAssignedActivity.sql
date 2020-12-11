-- Table: activity
-- Table: assigned_activity

--DROP SEQUENCE IF EXISTS activity_id CASCADE;
CREATE SEQUENCE activity_id AS BIGINT;

--DROP TABLE IF EXISTS activity CASCADE;
CREATE TABLE activity(
	type 							CHAR NOT NULL,
	year 							INT NOT NULL,
	week 							SMALLINT NOT NULL,
	day								SMALLINT,
	interruptibility				BOOLEAN NOT NULL,
	estimated_intervention_time 	SMALLINT,
	description						VARCHAR (1024),
	id 								BIGINT PRIMARY KEY,
	maintenance_typologies 			BIGINT NOT NULL REFERENCES maintenance_typologies (id),
	maintenance_procedures			BIGINT REFERENCES maintenance_procedures (id),
	site 							BIGINT NOT NULL REFERENCES site (id)
);

--DROP TABLE IF EXISTS assigned_activity CASCADE;
CREATE TABLE assigned_activity(
	maintainer 			BIGINT NOT NULL REFERENCES maintainer (internal_id),
	activity			BIGINT PRIMARY KEY REFERENCES activity (id)
);