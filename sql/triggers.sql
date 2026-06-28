-- ============================================================
-- Triggers (BONUS) - College Staff Management System
-- Course: Database Systems 10127 | Project 2026
--
-- These triggers enforce, at the DATABASE level, two business
-- rules that the original OOP program enforced only in Java.
-- The point: the rules now hold no matter which client writes
-- to the database (Java app, DataGrip, psql, ...).
-- ============================================================

-- ------------------------------------------------------------
-- TRIGGER 1: only DR / PROF lecturers may own articles
-- Mirrors the OOP rule that only Doctor/Professor have articles
-- ------------------------------------------------------------
CREATE OR REPLACE FUNCTION check_article_author_degree()
RETURNS TRIGGER AS $$
DECLARE
    author_degree VARCHAR(10);
BEGIN
    SELECT degree INTO author_degree
    FROM lecturer
    WHERE lecturer_id = NEW.lecturer_id;

    IF author_degree NOT IN ('DR', 'PROF') THEN
        RAISE EXCEPTION 'Only DR or PROF lecturers can have articles (lecturer % has degree %)',
            NEW.lecturer_id, author_degree;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_article_author_degree ON article;
CREATE TRIGGER trg_article_author_degree
    BEFORE INSERT OR UPDATE ON article
    FOR EACH ROW
    EXECUTE FUNCTION check_article_author_degree();

-- ------------------------------------------------------------
-- TRIGGER 2: a committee chairman must hold degree DR or PROF
-- Mirrors the OOP rule in Committee.setChairman()
-- ------------------------------------------------------------
CREATE OR REPLACE FUNCTION check_chairman_degree()
RETURNS TRIGGER AS $$
DECLARE
    chairman_degree VARCHAR(10);
BEGIN
    SELECT degree INTO chairman_degree
    FROM lecturer
    WHERE lecturer_id = NEW.chairman_id;

    IF chairman_degree NOT IN ('DR', 'PROF') THEN
        RAISE EXCEPTION 'Committee chairman must have degree DR or higher (lecturer % has degree %)',
            NEW.chairman_id, chairman_degree;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_chairman_degree ON committee;
CREATE TRIGGER trg_chairman_degree
    BEFORE INSERT OR UPDATE ON committee
    FOR EACH ROW
    EXECUTE FUNCTION check_chairman_degree();
