--Table: has_skill

--DROP TABLE IF EXISTS has_skill CASCADE;
CREATE TABLE has_skill(
	competences    		BIGINT REFERENCES competences (id),
	maintainer_role 	BIGINT REFERENCES maintainer_role (id),
	PRIMARY KEY (competences, maintainer_role )
);
