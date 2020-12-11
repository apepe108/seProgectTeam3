INSERT INTO competences (id, name, description)
VALUES (nextval('competences_id'), 'Skill 1', 'Description skill 1.'),
(nextval('competences_id'), 'Skill 2', 'Description skill 2.'), 
(nextval('competences_id'), 'Skill 3', 'Description skill 3.'),
(nextval('competences_id'), 'Skill 4', 'Description skill 4.'), 
(nextval('competences_id'), 'Skill 5', 'Description skill 5.'),
(nextval('competences_id'), 'Skill 6', 'Description skill 6.'),
(nextval('competences_id'), 'Skill 7', 'Description skill 7.'), 
(nextval('competences_id'), 'Skill 8', 'Description skill 8.');

INSERT INTO factory_site (id, name) 
VALUES (nextval('factory_site_id'), 'Factory Site 1'),
(nextval('factory_site_id'), 'Factory Site 2'),
(nextval('factory_site_id'), 'Factory Site 3');
INSERT INTO area (id, name, factory_site)
VALUES (nextval('area_id'), 'Area 1', 1), 
(nextval('area_id'), 'Area 2', 1),
(nextval('area_id'), 'Area 1', 2), 
(nextval('area_id'), 'Area 2', 2),
(nextval('area_id'), 'Area 3', 2),
(nextval('area_id'), 'Area 1', 3), 
(nextval('area_id'), 'Area 2', 3); 
INSERT INTO site (id, factory_site, area) 
VALUES (nextval('site_id'), 1, 1), 
(nextval('site_id'), 1, 2),
(nextval('site_id'), 2, 3), 
(nextval('site_id'), 2, 4),
(nextval('site_id'), 2, 5),
(nextval('site_id'), 3, 6), 
(nextval('site_id'), 3, 7);

INSERT INTO maintenance_procedures (id, name, smp) 
VALUES (nextval('maintenance_procedures_id'), 'Procedure 1', null), 
(nextval('maintenance_procedures_id'), 'Procedure 2', null), 
(nextval('maintenance_procedures_id'), 'Procedure 3', null),
(nextval('maintenance_procedures_id'), 'Procedure 4', null), 
(nextval('maintenance_procedures_id'), 'Procedure 5', null), 
(nextval('maintenance_procedures_id'), 'Procedure 6', null);

INSERT INTO materials (id, name, description) 
VALUES (nextval('materials_id'), 'Material 1', 'Description material 1'),
(nextval('materials_id'), 'Material 2', 'Description material 2'),
(nextval('materials_id'), 'Material 3', 'Description material 3'),
(nextval('materials_id'), 'Material 4', 'Description material 4'),
(nextval('materials_id'), 'Material 5', 'Description material 5'),
(nextval('materials_id'), 'Material 6', 'Description material 6'),
(nextval('materials_id'), 'Material 7', 'Description material 7'),
(nextval('materials_id'), 'Material 8', 'Description material 8');

INSERT INTO maintenance_typologies (id, name, description)
VALUES (nextval('maintenance_typologies_id'), 'Typologies 1', 'Description Typologies 1'),
(nextval('maintenance_typologies_id'), 'Typologies 2', 'Description Typologies 2'),
(nextval('maintenance_typologies_id'), 'Typologies 3', 'Description Typologies 3'),
(nextval('maintenance_typologies_id'), 'Typologies 4', 'Description Typologies 4');

INSERT INTO users VALUES (nextval('user_id'), 'sysadmin@pma.com', 'password');
INSERT INTO sysadmin VALUES (currval('user_id'), 'sysadmin@pma.com', 'root');