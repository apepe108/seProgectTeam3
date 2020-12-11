--Table: daily_time_slot
--Table: assigned_slot

--DROP SEQUENCE IF EXISTS daily_time_slot_id CASCADE;
CREATE SEQUENCE daily_time_slot_id AS BIGINT;

--DROP TABLE IF EXISTS daily_time_slot CASCADE;
CREATE TABLE daily_time_slot (
	duration 				SMALLINT NOT NULL,
	hour_start				TIME NOT NULL,
	id 						BIGINT PRIMARY KEY
);

--DROP TABLE IF EXISTS assigned_slot CASCADE;
CREATE TABLE assigned_slot (
	daily_time_slot 		BIGINT NOT NULL REFERENCES daily_time_slot (id),
	assigned_activity		BIGINT NOT NULL REFERENCES assigned_activity (activity),
	minutes					SMALLINT NOT NULL,
	PRIMARY KEY(daily_time_slot, assigned_activity)
);