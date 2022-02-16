CREATE DATABASE `temperature` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;


CREATE TABLE IF NOT EXISTS `temperature`.`data` (
  `DataId` INT NOT NULL AUTO_INCREMENT,
  `StationId` INT NOT NULL,
  `ParameterId` INT NOT NULL,
  `Temperature` DECIMAL(10,0) NOT NULL,
  `DateValue` INT NULL DEFAULT NULL,
  `TimeValue` INT NULL DEFAULT NULL,
  `DateTimeValue` TEXT NULL DEFAULT NULL,
  `PeriodId` INT NULL DEFAULT NULL,
  PRIMARY KEY (`DataId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci

CREATE TABLE IF NOT EXISTS `temperature`.`periods` (
  `PeriodId` INT NOT NULL,
  `PeriodName` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`PeriodId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci

CREATE TABLE IF NOT EXISTS `temperature`.`run` (
  `RunId` INT NOT NULL AUTO_INCREMENT,
  `StationId` INT NOT NULL,
  `ParameterId` INT NOT NULL,
  `Enabled` INT NULL DEFAULT NULL,
  `PeriodId` INT NULL DEFAULT NULL,
  PRIMARY KEY (`RunId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4

CREATE TABLE IF NOT EXISTS `temperature`.`runconfig` (
  `RunId` INT NOT NULL AUTO_INCREMENT,
  `StationId` INT NOT NULL,
  `ParameterId` INT NOT NULL,
  `Enabled` BIT(1) NOT NULL,
  `PeriodId` INT NOT NULL,
  PRIMARY KEY (`RunId`),
  INDEX `PeriodId` (`PeriodId` ASC) VISIBLE,
  CONSTRAINT `runconfig_ibfk_1`
    FOREIGN KEY (`PeriodId`)
    REFERENCES `temperature`.`periods` (`PeriodId`))
ENGINE = InnoDB
AUTO_INCREMENT = 17
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci

CREATE TABLE IF NOT EXISTS `temperature`.`smhiparameters` (
  `KeyId` INT NOT NULL,
  `Title` TEXT NOT NULL,
  `Summary` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`KeyId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci

CREATE TABLE IF NOT EXISTS `temperature`.`stations` (
  `StationId` INT NOT NULL,
  `StationName` TEXT NOT NULL,
  `Latitud` DECIMAL(10,0) NULL DEFAULT NULL,
  `Longitud` DECIMAL(10,0) NULL DEFAULT NULL,
  `Height` INT NULL DEFAULT NULL,
  `FromDateTime` TEXT NULL DEFAULT NULL,
  `ToDateTime` TEXT NULL DEFAULT NULL,
  `Active` INT NOT NULL,
  PRIMARY KEY (`StationId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci