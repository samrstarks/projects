DROP VIEW IF EXISTS q0, q1i, q1ii, q1iii, q1iv, q2i, q2ii, q2iii, q3i, q3ii, q3iii, q4i, q4ii, q4iii, q4iv;

-- Part 0
CREATE VIEW q0(era) 
AS
  SELECT max(era)
  FROM pitching
;

-- Part 1i
CREATE VIEW q1i(namefirst, namelast, birthyear)
AS
  SELECT namefirst, namelast, birthyear
  FROM master
  WHERE weight > 300;
;

-- Part 1ii
CREATE VIEW q1ii(namefirst, namelast, birthyear)
AS
  SELECT namefirst, namelast, birthyear
  FROM master
  WHERE namefirst LIKE '% %';
;

-- Part 1iii
CREATE VIEW q1iii(birthyear, avgheight, count)
AS
  SELECT birthyear, avg(height), count(*)
  FROM master
  GROUP BY birthyear 
  ORDER BY birthyear asc;
;

-- Part 1iv
CREATE VIEW q1iv(birthyear, avgheight, count)
AS
  SELECT birthyear, avg(height), count(*)
  FROM master
  GROUP BY birthyear
    HAVING avg(height) > 70
  ORDER BY birthyear asc;
;

-- Part 2i
CREATE VIEW q2i(namefirst, namelast, playerid, yearid)
AS
  SELECT namefirst, namelast, m.playerid, yearid
  FROM master as m
  INNER JOIN halloffame on m.playerid = halloffame.playerid and halloffame.inducted = 'Y'
  ORDER BY halloffame.yearid desc
;

-- Subquery for Part 2ii
CREATE VIEW q2ii_help(schoolid, playerid)
AS
  SELECT c.schoolid, c.playerid
  FROM collegeplaying as c
  INNER JOIN schools on c.schoolid = schools.schoolid and schools.schoolstate = 'CA'
;

-- Part 2ii
CREATE VIEW q2ii(namefirst, namelast, playerid, schoolid, yearid)
AS
  SELECT namefirst, namelast, q2.playerid, schoolid, yearid
  FROM q2i as q2
  INNER JOIN q2ii_help on q2.playerid = q2ii_help.playerid
  ORDER BY q2.yearid desc, schoolid, q2.playerid asc;
;

-- Subquery for Part 2ii
CREATE VIEW q2iii_help(schoolid, playerid)
AS
  SELECT c.schoolid, c.playerid
  FROM collegeplaying as c
  INNER JOIN schools on c.schoolid = schools.schoolid
;

-- Part 2iii
CREATE VIEW q2iii(playerid, namefirst, namelast, schoolid)
AS
  SELECT q2.playerid, namefirst, namelast, schoolid
  FROM q2i as q2
  LEFT JOIN q2iii_help on q2.playerid = q2iii_help.playerid
  ORDER BY q2.playerid desc, schoolid asc
;

-- Part 3i
CREATE VIEW q3i(playerid, namefirst, namelast, yearid, slg)
AS
  SELECT m.playerid, m.namefirst, m.namelast, s2.yearid, s2.slg
  FROM master as m
  INNER JOIN
    (SELECT CAST((h-h2b-h3b-hr) + (2*h2b) + (3*h3b) + (4*hr) AS FLOAT) / CAST((ab) AS FLOAT) as slg, playerid, yearid
    FROM 
      (SELECT SUM(h) as h, SUM(h2b) as h2b, SUM(h3b) as h3b, SUM(hr) as hr, SUM(ab) as ab, playerid as playerid, yearid as yearid
        FROM batting
        WHERE ab > 50
        GROUP BY yearid, playerid, stint) as s1) as s2
    ON m.playerid=s2.playerid
    ORDER BY s2.slg desc, yearid, playerid
    LIMIT 10
;

-- Part 3ii
CREATE VIEW q3ii(playerid, namefirst, namelast, lslg)
AS
  SELECT m.playerid, m.namefirst, m.namelast, s2.lslg
  FROM master as m
  INNER JOIN
    (SELECT CAST((h-h2b-h3b-hr) + (2*h2b) + (3*h3b) + (4*hr) AS FLOAT) / CAST((ab) AS FLOAT) as lslg, playerid, 3, 4
    FROM 
      (SELECT SUM(h) as h, SUM(h2b) as h2b, SUM(h3b) as h3b, SUM(hr) as hr, SUM(ab) as ab, playerid as playerid
      FROM batting
      GROUP BY playerid
      HAVING sum(ab) > 50) as s1
    ORDER BY lslg desc, playerid asc
    LIMIT 10) as s2
  ON m.playerid = s2.playerid
;

CREATE VIEW q3_help(playerid, namefirst, namelast, lslg)
AS
  SELECT m.playerid, m.namefirst, m.namelast, s2.lslg
  FROM master as m
  INNER JOIN
    (SELECT CAST((h-h2b-h3b-hr) + (2*h2b) + (3*h3b) + (4*hr) AS FLOAT) / CAST((ab) AS FLOAT) as lslg, playerid, 3, 4
    FROM 
      (SELECT SUM(h) as h, SUM(h2b) as h2b, SUM(h3b) as h3b, SUM(hr) as hr, SUM(ab) as ab, playerid as playerid
      FROM batting
      GROUP BY playerid
      HAVING sum(ab) > 50) as s1
    ORDER BY lslg desc, playerid asc) as s2
  ON m.playerid = s2.playerid
;

-- Part 3iii
CREATE VIEW q3iii(namefirst, namelast, lslg)
AS
  SELECT namefirst, namelast, lslg
  FROM q3_help q3
  WHERE q3.lslg > ALL
    (SELECT lslg
    FROM q3_help q32
    WHERE q32.playerid = 'mayswi01')
;

-- Part 4i
CREATE VIEW q4i(yearid, min, max, avg, stddev)
AS
	SELECT yearid, min(salary), max(salary), avg(salary), stddev(salary)
	FROM salaries
	GROUP BY yearid
	ORDER BY yearid
;

CREATE VIEW bininfo(min_sal, bin_width)
AS
  SELECT min(salary) as s1, (max(salary)-min(salary))/10 as r1
  FROM salaries
  GROUP BY yearid
  HAVING yearid=2016
;

-- Part 4ii
CREATE VIEW q4ii(binid, low, high, count)
AS
  SELECT binid, min_sal+(bin_width*binid), min_sal+(bin_width*(binid+1)), count(playerid)
  FROM
    (WITH bins(playerid, binid) AS
      (SELECT s1.playerid, 
        CASE WHEN FLOOR((s1.salary-b1.min_sal)/b1.bin_width) > 9 THEN 9
        ELSE FLOOR((s1.salary-b1.min_sal)/b1.bin_width) END
      FROM salaries s1, bininfo b1
      WHERE yearid=2016)
    SELECT 0, playerid, binid, 0
    FROM bins) as idk, bininfo as idk2
  GROUP BY binid, min_sal, bin_width
  ORDER BY binid
;

-- Part 4iii
CREATE VIEW q4iii(yearid, mindiff, maxdiff, avgdiff)
AS
  SELECT s1.yearid, s1.min - s2.min, s1.max - s2.max, s1.avg - s2.avg
  FROM q4i as s1, q4i as s2
  WHERE s2.yearid = (s1.yearid - 1)
  ORDER BY yearid
;

-- Part 4iv
CREATE VIEW q4iv(playerid, namefirst, namelast, salary, yearid)
AS
  SELECT m.playerid, m.namefirst, m.namelast, s1.salary, s1.yearid
  FROM master as m
  RIGHT JOIN
    (SELECT s1.*
    FROM salaries s1
    WHERE s1.salary >= ALL
      (SELECT s2.salary
      FROM salaries s2
      WHERE s2.yearid = 2000)
      AND (s1.yearid = 2000)
    UNION
    SELECT s1.*
    FROM salaries s1
    WHERE s1.salary >= ALL
      (SELECT s2.salary
      FROM salaries s2
      WHERE s2.yearid = 2001)
      AND (s1.yearid = 2001)) as s1
  ON m.playerid=s1.playerid
;