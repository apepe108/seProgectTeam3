--Table: workspace_notes

--DROP SEQUENCE IF EXISTS workspace_notes_id CASCADE;
CREATE SEQUENCE workspace_notes_id AS BIGINT;

--DROP TABLE IF EXISTS workspace_notes CASCADE;
CREATE TABLE workspace_notes(
	description 			VARCHAR (1024) NOT NULL,
	id 						BIGINT PRIMARY KEY
);