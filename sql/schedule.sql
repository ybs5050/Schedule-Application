CREATE DATABASE IF NOT EXISTS schedule;
USE schedule;

/*classes*/
DROP TABLE IF EXISTS `classes`;
CREATE TABLE `classes` (
  `course id` int(64) NOT NULL,
  `course name` text NOT NULL,
  `course abbreviation` text NOT NULL,
  PRIMARY KEY (`course id`)
);

/*meetings*/
DROP TABLE IF EXISTS `meetings`;
CREATE TABLE `meetings` (
  `meeting id` int(64) NOT NULL,
  `start time` text NOT NULL,
  `end time` text NOT NULL,
  `M` text null,
  `T` text null,
  `W` text null,
  `TR` text null,
  `F` text null,
  PRIMARY KEY (`meeting id`)
);

/*instructors*/
DROP TABLE IF EXISTS `instructors`;
CREATE TABLE `instructors` (
  `user id` int(64) NOT NULL,
  `name` text NOT NULL,
  `discipline` text NOT NULL,
  PRIMARY KEY (`user id`)
);