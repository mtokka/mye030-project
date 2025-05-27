CREATE DATABASE IF NOT EXISTS mye030_project;
USE mye030_project;

CREATE TABLE `countries` (
  `ISO` char(2) DEFAULT NULL,
  `ISO3` char(3) DEFAULT NULL,
  `ISO_Code` int DEFAULT NULL,
  `FIPS` varchar(5) DEFAULT NULL,
  `Display_Name` varchar(100) NOT NULL,
  `Official_Name` varchar(255) NOT NULL,
  `Capital` varchar(100) DEFAULT NULL,
  `Continent` varchar(50) DEFAULT NULL,
  `CurrencyCode` char(3) DEFAULT NULL,
  `CurrencyName` varchar(50) DEFAULT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `Region_Code` int DEFAULT NULL,
  `Region_Name` varchar(100) DEFAULT NULL,
  `Sub_region_Code` int DEFAULT NULL,
  `Sub_region_Name` varchar(100) DEFAULT NULL,
  `Intermediate_Region_Code` int DEFAULT NULL,
  `Intermediate_Region_Name` varchar(100) DEFAULT NULL,
  `Status` varchar(50) DEFAULT NULL,
  `Developed_or_Developing` varchar(50) DEFAULT NULL,
  `Area_SqKm` int DEFAULT NULL,
  `Population` bigint DEFAULT NULL,
  `id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;


CREATE TABLE `results` (
  `date` date NOT NULL,
  `Home_team` int NOT NULL,
  `Away_team` int NOT NULL,
  `Home_score` int NOT NULL,
  `Away_score` int NOT NULL,
  `Tournament` varchar(100) DEFAULT NULL,
  `City` varchar(100) DEFAULT NULL,
  `Country` varchar(100) DEFAULT NULL,
  `Neutral` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`date`,`Home_team`,`Away_team`),
  KEY `HOME_FK` (`Home_team`),
  KEY `AWAY_FK` (`Away_team`),
  CONSTRAINT `AWAY_FK` FOREIGN KEY (`Away_team`) REFERENCES `countries` (`id`),
  CONSTRAINT `HOME_FK` FOREIGN KEY (`Home_team`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `scorers` (
  `date` date DEFAULT NULL,
  `home_team` int DEFAULT NULL,
  `away_team` int DEFAULT NULL,
  `team` varchar(100) DEFAULT NULL,
  `scorer` varchar(255) DEFAULT NULL,
  `minute` int DEFAULT NULL,
  `own_goal` int DEFAULT NULL,
  `penalty` int DEFAULT NULL,
  KEY `date` (`date`,`home_team`,`away_team`),
  CONSTRAINT `scorers_ibfk_1` FOREIGN KEY (`date`, `home_team`, `away_team`) REFERENCES `results` (`date`, `Home_team`, `Away_team`) ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE `shootouts` (
  `date` date NOT NULL,
  `home_team` int NOT NULL,
  `away_team` int NOT NULL,
  `winner` int DEFAULT NULL,
  `first_shooter` int DEFAULT NULL,
  PRIMARY KEY (`date`,`home_team`,`away_team`),
  CONSTRAINT `shootouts_ibfk_1` FOREIGN KEY (`date`, `home_team`, `away_team`) REFERENCES `results` (`date`, `Home_team`, `Away_team`) ON DELETE CASCADE
) ENGINE=InnoDB;

