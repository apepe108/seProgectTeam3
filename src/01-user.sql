--Table: user

--DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users(
	internal_id				BIGINT PRIMARY KEY,
	email					VARCHAR(100),
	password				VARCHAR(1000)
);

--DROP SEQUENCE IF EXISTS user_id CASCADE;
CREATE SEQUENCE user_id AS BIGINT;

--DROP TABLE IF EXISTS planner CASCADE;
CREATE TABLE planner (
	internal_id 	BIGINT PRIMARY KEY,
	email			VARCHAR (100) NOT NULL,
	name 			VARCHAR (30) NOT NULL
);

--DROP TABLE IF EXISTS maintainer CASCADE;
CREATE TABLE maintainer (
	internal_id 	BIGINT PRIMARY KEY,
	email 			VARCHAR (100) NOT NULL,
	name 			VARCHAR (30) NOT NULL
);

--DROP TABLE IF EXISTS maintainer CASCADE;
CREATE TABLE sysadmin (
	internal_id 	BIGINT PRIMARY KEY,
	email 			VARCHAR (100) NOT NULL,
	name 			VARCHAR (30) NOT NULL
);
