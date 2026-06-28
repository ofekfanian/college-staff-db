-- ============================================================
-- 10 Meaningful SQL Queries - College Staff Management System
-- Course: Database Systems 10127 | Project 2026
-- ============================================================

-- Query 1: Average salary of all lecturers in the college
SELECT ROUND(AVG(salary), 2) AS average_salary
FROM lecturer
WHERE college_id = 1;

-- ---------------------------------------------------------------
-- Query 2: Average salary and lecturer count per department
-- (sorted highest salary first)
-- ---------------------------------------------------------------
SELECT d.name                        AS department,
       COUNT(l.lecturer_id)          AS num_lecturers,
       ROUND(AVG(l.salary), 2)       AS avg_salary
FROM department d
JOIN lecturer l ON d.dept_id = l.dept_id
WHERE d.college_id = 1
GROUP BY d.dept_id, d.name
ORDER BY avg_salary DESC;

-- ---------------------------------------------------------------
-- Query 3: Count of lecturers per degree type
-- ---------------------------------------------------------------
SELECT degree,
       COUNT(*) AS count
FROM lecturer
WHERE college_id = 1
GROUP BY degree
ORDER BY CASE degree
             WHEN 'FIRST'  THEN 1
             WHEN 'SECOND' THEN 2
             WHEN 'DR'     THEN 3
             WHEN 'PROF'   THEN 4
         END;

-- ---------------------------------------------------------------
-- Query 4: All lecturers with their department (including unassigned)
-- ---------------------------------------------------------------
SELECT l.name                          AS lecturer,
       l.degree,
       l.salary,
       COALESCE(d.name, 'Unassigned')  AS department
FROM lecturer l
LEFT JOIN department d ON l.dept_id = d.dept_id
WHERE l.college_id = 1
ORDER BY d.name NULLS LAST, l.name;

-- ---------------------------------------------------------------
-- Query 5: Number of articles per researcher (DR and PROF only)
-- ---------------------------------------------------------------
SELECT l.name,
       l.degree,
       COUNT(a.article_id) AS num_articles
FROM lecturer l
LEFT JOIN article a ON l.lecturer_id = a.lecturer_id
WHERE l.degree IN ('DR', 'PROF')
  AND l.college_id = 1
GROUP BY l.lecturer_id, l.name, l.degree
ORDER BY num_articles DESC;

-- ---------------------------------------------------------------
-- Query 6: Committees with chairman and number of members
-- ---------------------------------------------------------------
SELECT c.name                  AS committee,
       ch.name                 AS chairman,
       ch.degree               AS chairman_degree,
       COUNT(cm.lecturer_id)   AS num_members
FROM committee c
JOIN lecturer ch ON c.chairman_id = ch.lecturer_id
LEFT JOIN committee_member cm ON c.committee_id = cm.committee_id
WHERE c.college_id = 1
GROUP BY c.committee_id, c.name, ch.name, ch.degree
ORDER BY num_members DESC;

-- ---------------------------------------------------------------
-- Query 7: Total articles written by members of each committee
-- ---------------------------------------------------------------
SELECT c.name                       AS committee,
       COUNT(DISTINCT cm.lecturer_id) AS num_members,
       COUNT(a.article_id)          AS total_member_articles
FROM committee c
JOIN committee_member cm ON c.committee_id = cm.committee_id
LEFT JOIN article a ON cm.lecturer_id = a.lecturer_id
WHERE c.college_id = 1
GROUP BY c.committee_id, c.name
ORDER BY total_member_articles DESC;

-- ---------------------------------------------------------------
-- Query 8: Lecturers not assigned to any department
-- ---------------------------------------------------------------
SELECT l.name, l.degree, l.salary
FROM lecturer l
WHERE l.dept_id IS NULL
  AND l.college_id = 1;

-- ---------------------------------------------------------------
-- Query 9: Lecturers who belong to more than one committee
-- ---------------------------------------------------------------
SELECT l.name,
       l.degree,
       COUNT(cm.committee_id) AS committee_count
FROM lecturer l
JOIN committee_member cm ON l.lecturer_id = cm.lecturer_id
WHERE l.college_id = 1
GROUP BY l.lecturer_id, l.name, l.degree
HAVING COUNT(cm.committee_id) > 1
ORDER BY committee_count DESC;

-- ---------------------------------------------------------------
-- Query 10: Department with the highest average salary
-- ---------------------------------------------------------------
SELECT d.name                  AS top_department,
       ROUND(AVG(l.salary), 2) AS avg_salary
FROM department d
JOIN lecturer l ON d.dept_id = l.dept_id
WHERE d.college_id = 1
GROUP BY d.dept_id, d.name
ORDER BY avg_salary DESC
LIMIT 1;

-- ---------------------------------------------------------------
-- Query 11: Full committee membership report
-- ---------------------------------------------------------------
SELECT c.name   AS committee,
       ch.name  AS chairman,
       l.name   AS member,
       l.degree AS member_degree
FROM committee c
JOIN lecturer ch             ON c.chairman_id  = ch.lecturer_id
JOIN committee_member cm     ON c.committee_id = cm.committee_id
JOIN lecturer l              ON cm.lecturer_id = l.lecturer_id
WHERE c.college_id = 1
ORDER BY c.name, l.name;

-- ---------------------------------------------------------------
-- Query 12: Lecturers not in any committee
-- ---------------------------------------------------------------
SELECT l.name,
       l.degree,
       COALESCE(d.name, 'No Department') AS department
FROM lecturer l
LEFT JOIN committee_member cm ON l.lecturer_id = cm.lecturer_id
LEFT JOIN department d        ON l.dept_id     = d.dept_id
WHERE cm.committee_id IS NULL
  AND l.college_id = 1
ORDER BY l.degree, l.name;
