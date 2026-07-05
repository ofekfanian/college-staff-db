DROP TABLE IF EXISTS committee_member CASCADE;
DROP TABLE IF EXISTS article         CASCADE;
DROP TABLE IF EXISTS committee       CASCADE;
DROP TABLE IF EXISTS lecturer        CASCADE;
DROP TABLE IF EXISTS department      CASCADE;
DROP TABLE IF EXISTS college         CASCADE;

CREATE TABLE college (
    college_id  SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE department (
    dept_id         SERIAL PRIMARY KEY,
    college_id      INT NOT NULL REFERENCES college(college_id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    num_students    INT NOT NULL DEFAULT 0 CHECK (num_students >= 0),
    UNIQUE (college_id, name)
);

CREATE TABLE lecturer (
    lecturer_id  VARCHAR(20)    PRIMARY KEY,
    college_id   INT            NOT NULL REFERENCES college(college_id)    ON DELETE CASCADE,
    dept_id      INT                     REFERENCES department(dept_id)    ON DELETE SET NULL,
    name         VARCHAR(100)   NOT NULL,
    degree       VARCHAR(10)    NOT NULL CHECK (degree IN ('FIRST','SECOND','DR','PROF')),
    degree_name  VARCHAR(100)   NOT NULL,
    salary       DECIMAL(12,2)  NOT NULL CHECK (salary >= 0),
    institution  VARCHAR(150)
);

CREATE TABLE article (
    article_id   SERIAL PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    lecturer_id  VARCHAR(20)  NOT NULL REFERENCES lecturer(lecturer_id) ON DELETE CASCADE
);

CREATE TABLE committee (
    committee_id   SERIAL PRIMARY KEY,
    college_id     INT          NOT NULL REFERENCES college(college_id) ON DELETE CASCADE,
    name           VARCHAR(100) NOT NULL,
    chairman_id    VARCHAR(20)  NOT NULL REFERENCES lecturer(lecturer_id),
    member_degree  VARCHAR(10)           CHECK (member_degree IN ('FIRST','SECOND','DR','PROF')),
    UNIQUE (college_id, name)
);

CREATE TABLE committee_member (
    committee_id  INT         NOT NULL REFERENCES committee(committee_id) ON DELETE CASCADE,
    lecturer_id   VARCHAR(20) NOT NULL REFERENCES lecturer(lecturer_id)   ON DELETE CASCADE,
    PRIMARY KEY (committee_id, lecturer_id)
);

INSERT INTO college (name) VALUES ('Afeka College of Engineering');

INSERT INTO department (college_id, name, num_students) VALUES
    (1, 'Computer Science',        520),
    (1, 'Software Engineering',    410),
    (1, 'Electrical Engineering',  310),
    (1, 'Mathematics',             180);

INSERT INTO lecturer (lecturer_id, college_id, dept_id, name, degree, degree_name, salary, institution) VALUES
    ('L001', 1, 1, 'Alice Cohen',     'PROF',   'Professor of CS',    25000.00, 'MIT'),
    ('L002', 1, 1, 'Bob Levy',        'DR',     'Doctor of CS',       18000.00,  NULL),
    ('L003', 1, 2, 'Carol Mizrahi',   'PROF',   'Professor of SE',    24000.00, 'Technion'),
    ('L004', 1, 2, 'David Ben-David', 'DR',     'Doctor of SE',       17000.00,  NULL),
    ('L005', 1, 3, 'Eve Shapiro',     'DR',     'Doctor of EE',       16000.00,  NULL),
    ('L006', 1, 3, 'Frank Katz',      'SECOND', 'M.Sc.',              12000.00,  NULL),
    ('L007', 1, 4, 'Grace Levi',      'FIRST',  'B.Sc.',              10000.00,  NULL),
    ('L008', 1,  NULL, 'Henry Gross', 'DR',     'Doctor of Math',     15000.00,  NULL);

INSERT INTO article (title, lecturer_id) VALUES
    ('Machine Learning in Practice',        'L001'),
    ('Deep Learning Architectures',         'L001'),
    ('Quantum Computing Basics',            'L001'),
    ('Database Optimization Strategies',    'L002'),
    ('NoSQL vs SQL: A Comparison',          'L002'),
    ('Agile Development in Industry',       'L003'),
    ('Microservices Design Patterns',       'L003'),
    ('Cloud Architecture Principles',       'L003'),
    ('Test Driven Development',             'L004'),
    ('Neural Networks Fundamentals',        'L005'),
    ('Advanced Signal Processing',          'L005'),
    ('Graph Theory Applications',           'L008');

INSERT INTO committee (college_id, name, chairman_id, member_degree) VALUES
    (1, 'Research Committee',   'L001', 'DR'),
    (1, 'Curriculum Committee', 'L003', 'DR'),
    (1, 'Ethics Committee',     'L002',  NULL);

INSERT INTO committee_member (committee_id, lecturer_id) VALUES
    (1, 'L002'),
    (1, 'L004'),
    (1, 'L005'),
    (2, 'L004'),
    (2, 'L008'),
    (3, 'L005'),
    (3, 'L008');
