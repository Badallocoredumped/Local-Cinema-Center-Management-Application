-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: cinemacenter
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `sessions`
--

DROP TABLE IF EXISTS `sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sessions` (
  `session_id` int NOT NULL AUTO_INCREMENT,
  `movie_id` int NOT NULL,
  `hall_name` enum('Hall_A','Hall_B') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `session_date` date NOT NULL,
  `start_time` time NOT NULL,
  `vacant_seats` int NOT NULL,
  PRIMARY KEY (`session_id`),
  KEY `movie_id` (`movie_id`),
  KEY `hall_name` (`hall_name`),
  CONSTRAINT `sessions_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`),
  CONSTRAINT `sessions_ibfk_2` FOREIGN KEY (`hall_name`) REFERENCES `halls` (`hall_name`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessions`
--

LOCK TABLES `sessions` WRITE;
/*!40000 ALTER TABLE `sessions` DISABLE KEYS */;
INSERT INTO `sessions` VALUES (1,1,'Hall_A','2025-01-20','14:00:00',14),(2,1,'Hall_A','2025-01-20','16:00:00',16),(3,1,'Hall_A','2025-01-20','18:00:00',16),(4,1,'Hall_A','2025-01-20','20:00:00',16),(5,2,'Hall_A','2025-01-21','14:00:00',16),(6,2,'Hall_B','2025-01-21','16:00:00',46),(7,2,'Hall_B','2025-01-21','18:00:00',48),(8,2,'Hall_B','2025-01-21','20:00:00',48),(9,3,'Hall_A','2025-01-22','14:00:00',16),(10,3,'Hall_A','2025-01-22','16:00:00',16),(11,3,'Hall_B','2025-01-22','18:00:00',48),(12,3,'Hall_B','2025-01-22','20:00:00',48),(13,4,'Hall_A','2025-01-23','14:00:00',12),(14,4,'Hall_A','2025-01-27','16:00:00',16),(15,5,'Hall_B','2025-01-25','14:00:00',48),(16,5,'Hall_B','2025-01-24','16:00:00',48),(17,5,'Hall_B','2025-01-24','18:00:00',48);
/*!40000 ALTER TABLE `sessions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-13 11:37:01
