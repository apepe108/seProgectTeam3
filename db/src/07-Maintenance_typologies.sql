--Table: maintenance_typologies

--DROP SEQUENCE IF EXISTS maintenance_typologies_id CASCADE;
CREATE SEQUENCE maintenance_typologies_id AS BIGINT;

--DROP TABLE IF EXISTS maintenance_typologies CASCADE;
CREATE TABLE maintenance_typologies(
	name				VARCHAR (30) NOT NULL,
	description			VARCHAR (1024),
	id 					BIGINT PRIMARY KEY
);
