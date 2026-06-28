# College Staff Management System

A Java CLI application for managing college staff, departments, committees, and research articles — backed by a PostgreSQL relational database.

## Features

- Add and manage lecturers with different academic degrees (First, Second, Doctor, Professor)
- Organize lecturers into departments
- Create and manage committees with a designated chairman
- Track research articles written by doctors and professors
- Compare researchers by article count and committees by size or research output
- Clone committees
- 12 SQL queries for statistics and reports
- 2 database-level triggers enforcing business rules

## Tech Stack

- Java 17
- PostgreSQL 17
- JDBC (postgresql-42.7.4)

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL running locally

### Setup

1. Run the SQL script to create and populate the database:

```bash
psql -U your_user -d your_db -f sql/create_db.sql
```

2. Compile:

```bash
javac -d out src/college/Main.java src/college/db/CollegeDAO.java src/college/db/DBConnection.java
```

3. Run:

```bash
java -cp "out:lib/postgresql-42.7.4.jar" college.Main
```

> On Windows replace `:` with `;` in the classpath.

## Project Structure

```
src/
  college/
    Main.java           Entry point and menu
    db/
      DBConnection.java Singleton JDBC connection
      CollegeDAO.java   All database operations
sql/
  create_db.sql         Schema + sample data
  queries.sql           12 standalone SQL queries
  triggers.sql          2 BEFORE INSERT/UPDATE triggers
original/               Original version using binary file storage
lib/                    PostgreSQL JDBC driver
```

## Database Schema

6 tables normalized to 3NF:

| Table | Description |
|---|---|
| `college` | The institution |
| `department` | Departments within the college |
| `lecturer` | All staff (single-table inheritance for degree subtypes) |
| `article` | Research papers linked to DR/PROF lecturers |
| `committee` | Committees with a chairman |
| `committee_member` | Many-to-many junction between lecturers and committees |

## Triggers

- **trg_article_author_degree** — prevents inserting an article for a lecturer who is not DR or PROF
- **trg_chairman_degree** — prevents setting a committee chairman who is not DR or PROF
