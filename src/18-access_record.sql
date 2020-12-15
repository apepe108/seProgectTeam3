--DROP SEQUENCE IF EXISTS access_record_id;
CREATE SEQUENCE access_record_id AS BIGINT;

--DROP TABLE IF EXISTS access_record;
CREATE TABLE access_record (
	id 				BIGINT PRIMARY KEY,
	email 			VARCHAR(100) NOT NULL,
	name			VARCHAR(30) NOT NULL,
	role			VARCHAR(30) NOT NULL,
	login_date 		TIMESTAMP NOT NULL,
	logout_date 	TIMESTAMP
);