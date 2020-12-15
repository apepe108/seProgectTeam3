--Table: competences

--DROP SEQUENCE IF EXISTS competences_id CASCADE;
CREATE SEQUENCE competences_id AS BIGINT;

--DROP TABLE IF EXISTS competences CASCADE;
CREATE TABLE competences (
	name				VARCHAR (30) NOT NULL,
	description			VARCHAR (1024),
	id 					BIGINT PRIMARY KEY
);