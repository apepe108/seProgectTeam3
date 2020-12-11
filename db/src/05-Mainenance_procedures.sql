--Table: maintenance_procedures

--DROP SEQUENCE IF EXISTS maintenance_procedures_id CASCADE;
CREATE SEQUENCE maintenance_procedures_id AS BIGINT;

--DROP TABLE IF EXISTS maintenance_procedures CASCADE;
CREATE TABLE maintenance_procedures (
	name 		VARCHAR (100) NOT NULL,
	id 			BIGINT PRIMARY KEY,
	smp 		BIGINT REFERENCES smp(id)
);