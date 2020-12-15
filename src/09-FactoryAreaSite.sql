--Table: site
--Table: area
--Table: factory_site

--DROP SEQUENCE IF EXISTS factory_site_id CASCADE;
CREATE SEQUENCE factory_site_id AS BIGINT;

--DROP TABLE IF EXISTS factory_site CASCADE;
CREATE TABLE factory_site(
	name 		VARCHAR (100) NOT NULL,
	id          BIGINT PRIMARY KEY
);

--DROP SEQUENCE IF EXISTS area_id CASCADE;
CREATE SEQUENCE area_id AS BIGINT;

--DROP TABLE IF EXISTS area CASCADE;
CREATE TABLE area(
	name      			VARCHAR (30) NOT NULL,
	id					BIGINT PRIMARY KEY,
	factory_site		BIGINT REFERENCES factory_site(id) NOT NULL,
	workspace_notes     BIGINT REFERENCES workspace_notes(id)
);

--DROP SEQUENCE IF EXISTS site_id CASCADE;
CREATE SEQUENCE site_id AS BIGINT;

--DROP TABLE IF EXISTS site CASCADE;
CREATE TABLE site(
	id  			BIGINT PRIMARY KEY,
	factory_site 	BIGINT NOT NULL REFERENCES factory_site(id),
	area 			BIGINT NOT NULL REFERENCES area(id)
);