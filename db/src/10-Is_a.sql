--Table: is_a

--DROP TABLE IF EXISTS is_a CASCADE;
CREATE TABLE is_a(
	maintainer  		BIGINT,
	maintainer_role 	BIGINT,
	PRIMARY KEY (maintainer, maintainer_role),
	FOREIGN KEY (maintainer) REFERENCES maintainer(internal_id),
	FOREIGN KEY (maintainer_role) REFERENCES maintainer_role (id)
);
