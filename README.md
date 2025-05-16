# OTI-Projekti

Branch organisaatio: <br>
"develop" branchiin voi tallentaa muutoksia vapaasti. <br>
"main" branchiin tallennetaan valmiita toimintoja.

Käytännöllisiä github komentoja:

Github repositioon yhdistäminen: <br>
Intellij: File -> New -> Project from Version Control -> https://github.com/Sakaarii/OTI-Projekti.git <br>
(Kaikki alla olevat komennot löytyvät myös intellij version hallinnasta)

uudituksien vetäminen: <br>
git pull https://github.com/Sakaarii/OTI-Projekti.git

Näyttää kaikki branchit: <br>
git branch -a 

Vaihtaa brachia: <br>
git checkout [branchin nimi]

Muutosten lisääminen paikallisesti: <br>
git add . <br>
git commit -m "whatever"

Muutosten lisääminen onlineen: <br>

Jos ensimmäinen push: <br>
git push -u origin develop

Muuten: <br>
git push

Jos haluat laittaa development branchin muutokset mainiin: <br>
git checkout master <br>
git merge develop <br>
git push


Ohjelmiston Database:

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


