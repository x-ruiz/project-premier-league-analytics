SELECT c.name AS competition_name,
       a.name AS area_name
FROM `premier-league-analytics.pla_landing_us.t_competitions_raw` AS c
         INNER JOIN
     `premier-league-analytics.pla_landing_us.t_areas_raw` AS a ON c.areaId = a.id
WHERE c.dt = '2025-03-30'
  AND a.dt = '2025-03-30'
  AND c.type = 'LEAGUE';