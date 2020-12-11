--Table: materials

--DROP SEQUENCE IF EXISTS materials_id CASCADE;
CREATE SEQUENCE materials_id AS BIGINT;

--DROP TABLE IF EXISTS materials CASCADE;
CREATE TABLE materials (
	name 				VARCHAR (30) NOT NULL,
	description			VARCHAR (1024),
	id 					BIGINT PRIMARY KEY
);
