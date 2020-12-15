INSERT INTO competences (id, name, description)
VALUES (nextval('competences_id'), 'PAV Certification', 'Certification of acknowledgment about risks of electric works'),
(nextval('competences_id'), 'Electrical Maintenance', 'Ability to test, monitor, fix and replace elements of an electrical system'), 
(nextval('competences_id'), 'Knowledge of cable types', 'Ability to recognize cable types'),
(nextval('competences_id'), 'Xyz-type robot knowledge', 'Knowledge of the robot type xyz'), 
(nextval('competences_id'), 'Knowledge of robot workstation 23', 'Ability to perform maintenance activities on robot at workstation 23');

INSERT INTO maintenance_procedures (id, name, smp) 
VALUES (nextval('maintenance_procedures_id'), 'Check of utility lines', null), 
(nextval('maintenance_procedures_id'), 'Plumbing maintenance', null), 
(nextval('maintenance_procedures_id'), 'Infrastructure check for damage or leaks', null),
(nextval('maintenance_procedures_id'), 'Check for electrical damage', null),
(nextval('maintenance_procedures_id'), 'Leather cleaning robot maintenance', null), 
(nextval('maintenance_procedures_id'), 'Machine oiling', null), 
(nextval('maintenance_procedures_id'), 'ECU replacement', null),
(nextval('maintenance_procedures_id'), 'Gasket replacement', null),
(nextval('maintenance_procedures_id'), 'Filter cleaning', null);

INSERT INTO require(maintenance_procedures, competences)
VALUES (1, 2), (1, 3), (2, 1), (3, 1), (3, 2), (4, 3), (5, 4), (5, 5), (6, 1), (6, 4), 
(7, 1), (7, 2), (7, 3), (7, 5), (9, 1);

INSERT INTO factory_site (id, name) 
VALUES (nextval('factory_site_id'), 'Fisciano'),
(nextval('factory_site_id'), 'Nusco'),
(nextval('factory_site_id'), 'Morra');
INSERT INTO area (id, name, factory_site)
VALUES (nextval('area_id'), 'Molding', 1), 
(nextval('area_id'), 'Carpentry', 1),
(nextval('area_id'), 'Molding', 2), 
(nextval('area_id'), 'Carpentry', 2),
(nextval('area_id'), 'Painting', 2),
(nextval('area_id'), 'Molding', 3), 
(nextval('area_id'), 'Carpentry', 3); 
INSERT INTO site (id, factory_site, area) 
VALUES (nextval('site_id'), 1, 1), 
(nextval('site_id'), 1, 2),
(nextval('site_id'), 2, 3), 
(nextval('site_id'), 2, 4),
(nextval('site_id'), 2, 5),
(nextval('site_id'), 3, 6), 
(nextval('site_id'), 3, 7);

INSERT INTO materials (id, name, description) 
VALUES (nextval('materials_id'), 'Wires', 'Set of Wires for electrical maintenance'),
(nextval('materials_id'), 'Fuel', 'Fuel to till machines engine'),
(nextval('materials_id'), 'Lithium Lubricant', 'Lubricant higly resisting lower and higher temperatures, oxidation, corruption and rust'),
(nextval('materials_id'), 'Tubes', 'Set of polyprophilen tubes for hydraulic maintenances');

INSERT INTO maintenance_typologies (id, name, description)
VALUES (nextval('maintenance_typologies_id'), 'Mechanical', 'Activity involving Mechanical Operations'),
(nextval('maintenance_typologies_id'), 'Electric', 'Activity involving Electrical Operations'),
(nextval('maintenance_typologies_id'), 'Hydraulic', 'Activity involving Hydraulical Operations'),
(nextval('maintenance_typologies_id'), 'Electronics', 'Activity involving Electronical Operations');

INSERT INTO users VALUES (nextval('user_id'), 'sysadmin@pma.com', 'password');
INSERT INTO sysadmin VALUES (currval('user_id'), 'sysadmin@pma.com', 'root');

INSERT INTO daily_time_slot (id, hour_start, duration)
VALUES (1, '06:00:00', 60), (2, '07:00:00', 60), (3, '08:00:00', 60),
(4, '09:00:00', 60), (5, '10:00:00', 60), (6, '11:00:00', 60),
(7, '12:00:00', 60), (8, '13:00:00', 60), (9, '14:00:00', 60),
(10, '15:00:00', 60), (11, '16:00:00', 60), (12, '17:00:00', 60),
(13, '18:00:00', 60), (14, '19:00:00', 60), (15, '20:00:00', 60);

