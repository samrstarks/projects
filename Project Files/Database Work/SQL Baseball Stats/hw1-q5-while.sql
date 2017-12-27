-- Q5 Iteration

DROP TABLE IF EXISTS q5_extended_paths;
CREATE TABLE q5_extended_paths(src, dest, length, path)
AS
    SELECT qpaths.src, qedge.dest, qpaths.length + qedge.length, qpaths.path||qedge.dest
    FROM q5_paths_to_update as qpaths, q5_edges as qedge
   	WHERE qpaths.dest = qedge.src AND qpaths.src != qedge.dest
;

CREATE TABLE q5_new_paths(src, dest, length, path)
AS
   SELECT p1.src, p1.dest, p1.length, p1.path
   FROM q5_extended_paths as p1
   LEFT JOIN q5_paths as q5 on (p1.src=q5.src AND p1.dest=q5.dest)
   WHERE (q5.path is NULL)
;

CREATE TABLE q5_better_paths(src, dest, length, path)
AS
	WITH uni(src, dest, length) AS
		(SELECT src, dest, length, path
		FROM q5_paths
		UNION
		SELECT src, dest, length, path
		FROM q5_new_paths), p1(src, dest, length) AS
		(SELECT src, dest, min(length)
		FROM uni
		GROUP BY src, dest)
	SELECT p1.src, p1.dest, p1.length, uni.path
	FROM p1
	INNER JOIN uni on (p1.src=uni.src AND p1.dest=uni.dest)
	WHERE p1.length=uni.length
;

DROP TABLE q5_paths;
ALTER TABLE q5_better_paths RENAME TO q5_paths;

DROP TABLE q5_paths_to_update;
ALTER TABLE q5_new_paths RENAME TO q5_paths_to_update;
SELECT COUNT(*) AS path_count,
       CASE WHEN 0 = (SELECT COUNT(*) FROM q5_paths_to_update) 
            THEN 'FINISHED'
            ELSE 'RUN AGAIN' END AS status
FROM q5_paths;
