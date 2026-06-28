# College Staff Management System

Database Systems 10127 - Project 2026  
Ofek Fanian, Idan Gazit, Ori Schnieder

## About

This project takes the College Staff Management System from the OOP course and replaces file storage with a PostgreSQL database.

## Structure

```
src/          Java source code (JDBC)
original/     Original OOP project
sql/          Database scripts
  create_db.sql   Create tables and insert sample data
  queries.sql     12 SQL queries
  triggers.sql    2 bonus triggers
lib/          PostgreSQL JDBC driver
```

## How to run

1. Make sure PostgreSQL is running
2. Run `create_db.sql` to set up the database
3. Compile and run:

```bash
javac -d out src/college/Main.java src/college/db/CollegeDAO.java src/college/db/DBConnection.java
java -cp "out:lib/postgresql-42.7.4.jar" college.Main
```
