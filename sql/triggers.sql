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
