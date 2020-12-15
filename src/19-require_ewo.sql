-- Table: Require ewo

--DROP TABLE IF EXISTS require_ewo;
CREATE TABLE require_ewo (
	activity 			BIGINT NOT NULL REFERENCES activity(id),
	competences			BIGINT NOT NULL REFERENCES competences(id),
	PRIMARY KEY (activity, competences)
);