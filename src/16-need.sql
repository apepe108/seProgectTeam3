--Table: need

--DROP TABLE IF EXISTS need CASCADE
CREATE TABLE need (
	activity		BIGINT REFERENCES activity(id),
	materials		BIGINT REFERENCES materials(id),
	PRIMARY KEY (activity, materials)
)