-- Table: user

DROP SEQUENCE IF EXISTS user_id CASCADE;
CREATE SEQUENCE user_id AS BIGINT;

DROP TABLE IF EXISTS planner CASCADE;
CREATE TABLE planner (
	internal_id 	BIGINT PRIMARY KEY,
	email			VARCHAR (100) NOT NULL,
	name 			VARCHAR (30) NOT NULL
);

DROP TABLE IF EXISTS maintainer CASCADE;
CREATE TABLE maintainer (
	internal_id 	BIGINT PRIMARY KEY,
	email 			VARCHAR (100) NOT NULL,
	name 			VARCHAR (30) NOT NULL
);

DROP TABLE IF EXISTS sysadmin CASCADE;
CREATE TABLE sysadmin (
	internal_id 	BIGINT PRIMARY KEY,
	email 			VARCHAR (100) NOT NULL,
	name 			VARCHAR (30) NOT NULL
);

--Table: user

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users(
	internal_id				BIGINT PRIMARY KEY,
	email					VARCHAR(100),
	password				VARCHAR(1000)
);

--Table: competences

DROP SEQUENCE IF EXISTS competences_id CASCADE;
CREATE SEQUENCE competences_id AS BIGINT;

DROP TABLE IF EXISTS competences CASCADE;
CREATE TABLE competences (
	name				VARCHAR (30) NOT NULL,
	description			VARCHAR (1024),
	id 					BIGINT PRIMARY KEY
);

--Table: maintainer

DROP SEQUENCE IF EXISTS maintainer_role_id  CASCADE;
CREATE SEQUENCE maintainer_role_id AS BIGINT;

DROP TABLE IF EXISTS maintainer_role CASCADE;
CREATE TABLE maintainer_role (
	name 				VARCHAR (30) NOT NULL,
	description			VARCHAR (1024),
	id 					BIGINT PRIMARY KEY
);


--Table: smp

DROP SEQUENCE IF EXISTS smp_id CASCADE;
CREATE SEQUENCE smp_id AS BIGINT;

DROP TABLE IF EXISTS smp CASCADE;
CREATE TABLE smp(
	pdf_file 		BYTEA NOT NULL,
	id        		BIGINT PRIMARY KEY
);

--Table: maintenance_procedures

DROP SEQUENCE IF EXISTS maintenance_procedures_id CASCADE;
CREATE SEQUENCE maintenance_procedures_id AS BIGINT;

DROP TABLE IF EXISTS maintenance_procedures CASCADE;
CREATE TABLE maintenance_procedures (
	name 		VARCHAR (100) NOT NULL,
	id 			BIGINT PRIMARY KEY,
	smp 		BIGINT REFERENCES smp(id)
);

--Table: materials

DROP SEQUENCE IF EXISTS materials_id CASCADE;
CREATE SEQUENCE materials_id AS BIGINT;

DROP TABLE IF EXISTS materials CASCADE;
CREATE TABLE materials (
	name 				VARCHAR (30) NOT NULL,
	description			VARCHAR (1024),
	id 					BIGINT PRIMARY KEY
);


--Table: maintenance_typologies

DROP SEQUENCE IF EXISTS maintenance_typologies_id CASCADE;
CREATE SEQUENCE maintenance_typologies_id AS BIGINT;

DROP TABLE IF EXISTS maintenance_typologies CASCADE;
CREATE TABLE maintenance_typologies(
	name				VARCHAR (30) NOT NULL,
	description			VARCHAR (1024),
	id 					BIGINT PRIMARY KEY
);


--Table: workspace_notes

DROP SEQUENCE IF EXISTS workspace_notes_id CASCADE;
CREATE SEQUENCE workspace_notes_id AS BIGINT;

DROP TABLE IF EXISTS workspace_notes CASCADE;
CREATE TABLE workspace_notes(
	description 			VARCHAR (1024) NOT NULL,
	id 						BIGINT PRIMARY KEY
);


--Table: site
--Table: area
--Table: factory_site

DROP SEQUENCE IF EXISTS factory_site_id CASCADE;
CREATE SEQUENCE factory_site_id AS BIGINT;

DROP TABLE IF EXISTS factory_site CASCADE;
CREATE TABLE factory_site(
	name 		VARCHAR (100) NOT NULL,
	id          BIGINT PRIMARY KEY
);

DROP SEQUENCE IF EXISTS area_id CASCADE;
CREATE SEQUENCE area_id AS BIGINT;

DROP TABLE IF EXISTS area CASCADE;
CREATE TABLE area(
	name      			VARCHAR (30) NOT NULL,
	id					BIGINT PRIMARY KEY,
	factory_site		BIGINT REFERENCES factory_site(id) NOT NULL,
	workspace_notes     BIGINT REFERENCES workspace_notes(id)
);

DROP SEQUENCE IF EXISTS site_id CASCADE;
CREATE SEQUENCE site_id AS BIGINT;

DROP TABLE IF EXISTS site CASCADE;
CREATE TABLE site(
	id  			BIGINT PRIMARY KEY,
	factory_site 	BIGINT NOT NULL REFERENCES factory_site(id),
	area 			BIGINT NOT NULL REFERENCES area(id)
);

--Table: is_a

DROP TABLE IF EXISTS is_a CASCADE;
CREATE TABLE is_a(
	maintainer  		BIGINT,
	maintainer_role 	BIGINT,
	PRIMARY KEY (maintainer, maintainer_role),
	FOREIGN KEY (maintainer) REFERENCES maintainer(internal_id),
	FOREIGN KEY (maintainer_role) REFERENCES maintainer_role (id)
);

--Table: has_skill

DROP TABLE IF EXISTS has_skill CASCADE;
CREATE TABLE has_skill(
	competences    		BIGINT REFERENCES competences (id),
	maintainer_role 	BIGINT REFERENCES maintainer_role (id),
	PRIMARY KEY (competences, maintainer_role )
);

--Table: require

DROP TABLE IF EXISTS require CASCADE;
CREATE TABLE require(
	competences     			 BIGINT REFERENCES competences(id),
	maintenance_procedures  	 BIGINT REFERENCES maintenance_procedures(id),
	PRIMARY KEY (competences,maintenance_procedures)
);

-- Table: activity
-- Table: assigned_activity

DROP SEQUENCE IF EXISTS activity_id CASCADE;
CREATE SEQUENCE activity_id AS BIGINT;

DROP TABLE IF EXISTS activity CASCADE;
CREATE TABLE activity(
	type 							CHAR NOT NULL,
	year 							INT NOT NULL,
	week 							SMALLINT NOT NULL,
	day 							SMALLINT,
	interruptibility				BOOLEAN NOT NULL,
	estimated_intervention_time 	SMALLINT,
	description						VARCHAR (1024),
	id 								BIGINT PRIMARY KEY,
	maintenance_typologies 			BIGINT NOT NULL REFERENCES maintenance_typologies (id),
	maintenance_procedures			BIGINT REFERENCES maintenance_procedures (id),
	site 							BIGINT NOT NULL REFERENCES site (id)
);

DROP TABLE IF EXISTS assigned_activity CASCADE;
CREATE TABLE assigned_activity(
	maintainer 			BIGINT NOT NULL REFERENCES maintainer (internal_id),
	activity			BIGINT PRIMARY KEY REFERENCES activity (id)
);

--Table: daily_time_slot
--Table: assigned_slot

DROP SEQUENCE IF EXISTS daily_time_slot_id CASCADE;
CREATE SEQUENCE daily_time_slot_id AS BIGINT;

DROP TABLE IF EXISTS daily_time_slot CASCADE;
CREATE TABLE daily_time_slot (
	duration 				SMALLINT NOT NULL,
	hour_start				TIME NOT NULL,
	id 						BIGINT PRIMARY KEY
);

DROP TABLE IF EXISTS assigned_slot CASCADE;
CREATE TABLE assigned_slot (
	daily_time_slot 		BIGINT NOT NULL REFERENCES daily_time_slot (id),
	assigned_activity		BIGINT NOT NULL REFERENCES assigned_activity (activity),
	minutes					SMALLINT NOT NULL,
	PRIMARY KEY(daily_time_slot, assigned_activity)
);
--Table: need

DROP TABLE IF EXISTS need CASCADE;
CREATE TABLE need (
	activity		BIGINT REFERENCES activity(id),
	materials		BIGINT REFERENCES materials(id),
	PRIMARY KEY (activity, materials)
);


CREATE OR REPLACE VIEW site_view AS 
(
SELECT s.id AS site_id, f.name AS factory_name, a.name AS area_name, wn.id AS workspace_id, wn.description AS workspace_description
FROM(site AS s JOIN factory_site AS f ON s.factory_site = f.id
JOIN area AS a ON s.area = a.id)
LEFT JOIN workspace_notes AS wn ON a.workspace_notes = wn.id
);

DROP SEQUENCE IF EXISTS access_record_id;
CREATE SEQUENCE access_record_id AS BIGINT;

DROP TABLE IF EXISTS access_record;
CREATE TABLE access_record (
	id 				BIGINT PRIMARY KEY,
	email 			VARCHAR(100) NOT NULL,
	name			VARCHAR(30) NOT NULL,
	role			VARCHAR(30) NOT NULL,
	login_date 		TIMESTAMP NOT NULL,
	logout_date 	TIMESTAMP
);

-- Table: Require ewo

DROP TABLE IF EXISTS require_ewo;
CREATE TABLE require_ewo (
	activity 			BIGINT NOT NULL REFERENCES activity(id),
	competences			BIGINT NOT NULL REFERENCES competences(id),
	PRIMARY KEY (activity, competences)
);