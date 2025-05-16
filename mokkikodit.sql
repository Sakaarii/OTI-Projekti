-- --------------------------------------------------------
-- Verkkotietokone:              127.0.0.1
-- Palvelinversio:               11.2.2-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Versio:              12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for mokkikodit
CREATE DATABASE IF NOT EXISTS `mokkikodit` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci */;
USE `mokkikodit`;

-- Dumping structure for taulu mokkikodit.asiakas
CREATE TABLE IF NOT EXISTS `asiakas` (
  `nimi` varchar(50) NOT NULL,
  `asiakastunnus` varchar(50) NOT NULL,
  `sahkoposti` varchar(50) NOT NULL,
  `tilinumero` varchar(50) NOT NULL,
  `asiakasID` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`asiakasID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table mokkikodit.asiakas: ~0 rows (suunnilleen)

-- Dumping structure for taulu mokkikodit.lasku
CREATE TABLE IF NOT EXISTS `lasku` (
  `laskun_tunniste` int(11) NOT NULL AUTO_INCREMENT,
  `summa` float NOT NULL,
  `erapaiva` date NOT NULL,
  `maksettu` varchar(10) DEFAULT NULL,
  `varauksen_tunniste` int(11) NOT NULL,
  PRIMARY KEY (`laskun_tunniste`),
  KEY `varauksen_tunniste` (`varauksen_tunniste`),
  CONSTRAINT `lasku_ibfk_1` FOREIGN KEY (`varauksen_tunniste`) REFERENCES `varaus` (`varauksen_tunniste`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table mokkikodit.lasku: ~0 rows (suunnilleen)

-- Dumping structure for taulu mokkikodit.mokki
CREATE TABLE IF NOT EXISTS `mokki` (
  `mokin_tunniste` int(11) NOT NULL AUTO_INCREMENT,
  `osoite` varchar(50) NOT NULL,
  `kapasiteetti` int(11) NOT NULL,
  `varaustilanne` varchar(50) DEFAULT NULL,
  `varaushinta` int(11) NOT NULL,
  PRIMARY KEY (`mokin_tunniste`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table mokkikodit.mokki: ~0 rows (suunnilleen)

-- Dumping structure for taulu mokkikodit.varaus
CREATE TABLE IF NOT EXISTS `varaus` (
  `varauksen_tunniste` int(11) NOT NULL AUTO_INCREMENT,
  `varauksen_alkamispaiva` date NOT NULL,
  `varauksen_paattymispaiva` date NOT NULL,
  `varauksen_kesto` int(11) NOT NULL,
  `mokin_tunniste` int(11) NOT NULL,
  `asiakasID` int(11) NOT NULL,
  PRIMARY KEY (`varauksen_tunniste`),
  KEY `mokin_tunniste` (`mokin_tunniste`),
  KEY `asiakasID` (`asiakasID`),
  CONSTRAINT `varaus_ibfk_1` FOREIGN KEY (`mokin_tunniste`) REFERENCES `mokki` (`mokin_tunniste`),
  CONSTRAINT `varaus_ibfk_2` FOREIGN KEY (`asiakasID`) REFERENCES `asiakas` (`asiakasID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table mokkikodit.varaus: ~0 rows (suunnilleen)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
