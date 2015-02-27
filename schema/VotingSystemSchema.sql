--SQL Schema for Voting System
-- ====================================

DROP TABLE IF EXISTS voters;
DROP TABLE IF EXISTS candidates;
DROP TABLE IF EXISTS districts;
DROP TABLE IF EXISTS parties;
DROP TABLE IF EXISTS addresses;

--CREATE DATABASE TABLES
--=======================

CREATE TABLE if not exists voters(
  id integer PRIMARY KEY NOT NULL,
  firstName text NOT NULL,
  lastName text NOT NULL,
  hasVoted boolean NOT NULL,
  votedFor integer id,
  FOREIGN KEY(district_id) REFERENCES districts(id),
);

CREATE TABLE if not exists candidates(
  id integer PRIMARY KEY NOT NULL,
  firstName text NOT NULL,
  lastName text NOT NULL,
  FOREIGN KEY(district_id) REFERENCE districts(id) 
);

CREATE TABLE if not exists districts(
  id integer PRIMARY KEY NOT NULL,
  name text NOT NULL,
);

CREATE TABLE if not exists parties(
  id integer PRIMARY KEY AUTOINCREMENT NOT NULL,
  name text NOT NULL,
  leader text NOT NULL,
);

CREATE TABLE if not exists addresses(
  streetNumber text NOT NULL,
  street text NOT NULL,
  city text NOT NULL,
  province text NOT NULL,
  postalCode text NOT NULL,
);

