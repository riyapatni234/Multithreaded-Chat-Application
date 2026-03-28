ChatApp JavaFX + PostgreSQL

1) Create PostgreSQL database:
   CREATE DATABASE chatapp;

2) Inside chatapp DB, run:
   CREATE TABLE users (
       id SERIAL PRIMARY KEY,
       username VARCHAR(50) UNIQUE NOT NULL,
       password VARCHAR(100) NOT NULL
   );

   CREATE TABLE messages (
       id SERIAL PRIMARY KEY,
       sender VARCHAR(50) NOT NULL,
       message TEXT NOT NULL,
       timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );

3) Open DatabaseConnection.java
   Change:
   private static final String PASSWORD = "your_password_here";
   to your actual PostgreSQL password.

4) Download PostgreSQL JDBC Driver:
   postgresql-42.x.x.jar

5) Compile:
   Windows:
   javac --module-path "PATH_TO_FX/lib" --add-modules javafx.controls,javafx.graphics -cp ".;postgresql-42.x.x.jar" *.java

   Linux/Mac:
   javac --module-path "PATH_TO_FX/lib" --add-modules javafx.controls,javafx.graphics -cp ".:postgresql-42.x.x.jar" *.java

6) Run:
   Windows:
   java --module-path "PATH_TO_FX/lib" --add-modules javafx.controls,javafx.graphics -cp ".;postgresql-42.x.x.jar" Main

   Linux/Mac:
   java --module-path "PATH_TO_FX/lib" --add-modules javafx.controls,javafx.graphics -cp ".:postgresql-42.x.x.jar" Main
