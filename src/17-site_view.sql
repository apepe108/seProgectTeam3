CREATE OR REPLACE VIEW site_view AS 
(
SELECT s.id AS site_id, f.name AS factory_name, a.name AS area_name, wn.id AS workspace_id, wn.description AS workspace_description
FROM(site AS s JOIN factory_site AS f ON s.factory_site = f.id
JOIN area AS a ON s.area = a.id)
LEFT JOIN workspace_notes AS wn ON a.workspace_notes = wn.id
);