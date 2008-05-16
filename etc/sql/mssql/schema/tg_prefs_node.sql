CREATE TRIGGER trig_prefs_node
ON prefs_node
INSTEAD OF DELETE
AS
WITH cte AS
( SELECT     node_id, parent_node_id
  FROM       DELETED
  UNION ALL
  SELECT     c.node_id, c.parent_node_id
  FROM       prefs_node AS c
  INNER JOIN cte AS p
  ON         c.parent_node_id = p.node_id
)
DELETE     a
FROM       prefs_node AS a
INNER JOIN cte AS b
ON         a.node_id = b.node_id
OPTION     (MAXRECURSION 0)
;
