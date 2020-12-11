--Table: maintainer

--DROP SEQUENCE IF EXISTS maintainer_role_id  CASCADE;
CREATE SEQUENCE maintainer_role_id AS BIGINT;

--DROP TABLE IF EXISTS maintainer_role CASCADE;
CREATE TABLE maintainer_role (
	name 				VARCHAR (30) NOT NULL,
	description			VARCHAR (1024),
	id 					BIGINT PRIMARY KEY
);