--Table: require

--DROP TABLE IF EXISTS require CASCADE;
CREATE TABLE require(
	competences     			 BIGINT REFERENCES competences(id),
	maintenance_procedures  	 BIGINT REFERENCES maintenance_procedures(id),
	PRIMARY KEY (competences,maintenance_procedures)
);