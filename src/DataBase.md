 # CREATING DATABASE ON MYSQL

 # Create Database
 - create a database with name chessstournamentdb

 SQL SYNTAX: -> create database chesstournamentdb;
             -> use chesstournamentdb;

 # Creating Tables
 - Create table to hold data of players

 SQL SYNTAX: -> create table players
             -> (playerid int primary key,
             -> name varchar(50) not null,
             -> rating int not null,
             -> points double default = 0 );

- Create table to hold data of Matches

SQL SYNTAX: -> create table matches
            -> (matchid int primary key,
            -> player01id int not null,
            -> player02id int not null,
            -> winnerid int not null);

# Add MySQL Password in place of YOUR_PASSWORD in main.java line 11.