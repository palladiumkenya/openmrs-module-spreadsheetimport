-- MySQL dump 10.13  Distrib 5.6.51, for Linux (x86_64)
--
-- Host: localhost    Database: openmrs
-- ------------------------------------------------------
-- Server version	5.6.51

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `spreadsheetimport_template`
--

DROP TABLE IF EXISTS `spreadsheetimport_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spreadsheetimport_template` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `encounter` tinyint(1) DEFAULT '0',
  `target_form` varchar(255) DEFAULT NULL,
  `creator` int(11) NOT NULL DEFAULT '0',
  `date_created` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `changed_by` int(11) DEFAULT NULL,
  `date_changed` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `User who wrote this template` (`creator`),
  KEY `User who changed this template` (`changed_by`),
  CONSTRAINT `User who changed this template` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `User who wrote this template` FOREIGN KEY (`creator`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spreadsheetimport_template`
--

LOCK TABLES `spreadsheetimport_template` WRITE;
/*!40000 ALTER TABLE `spreadsheetimport_template` DISABLE KEYS */;
INSERT INTO `spreadsheetimport_template` VALUES (1,'Covid Screening Form','Covid Screening Form',1,'146',1,'2022-12-02 18:28:19',1,'2022-12-02 18:28:19'),(2,'CCA COVID Testing','CCA COVID Testing',1,'150',1,'2022-12-07 23:35:57',1,'2022-12-07 23:35:57'),(3,'CCA COVID Clinical Review','CCA COVID Clinical Review',1,'151',1,'2022-12-07 23:47:55',1,'2022-12-07 23:47:55');
/*!40000 ALTER TABLE `spreadsheetimport_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spreadsheetimport_template_column`
--

DROP TABLE IF EXISTS `spreadsheetimport_template_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spreadsheetimport_template_column` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `template_id` int(32) NOT NULL,
  `name` varchar(100) NOT NULL,
  `database_table_dot_column` varchar(1000) NOT NULL,
  `database_table_dataset_index` int(11) DEFAULT NULL,
  `column_import_index` int(32) NOT NULL,
  `disallow_duplicate_value` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `Template to which this column belongs` (`template_id`),
  CONSTRAINT `Template to which this column belongs` FOREIGN KEY (`template_id`) REFERENCES `spreadsheetimport_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spreadsheetimport_template_column`
--

LOCK TABLES `spreadsheetimport_template_column` WRITE;
/*!40000 ALTER TABLE `spreadsheetimport_template_column` DISABLE KEYS */;
INSERT INTO `spreadsheetimport_template_column` VALUES (1,1,'Person_Id','patient_identifier.identifier',NULL,33,0),(2,1,'Encounter_ID','encounter.encounter_id',NULL,0,0),(3,1,'screening_department','obs.value_coded',0,1,0),(4,1,'hiv_status','obs.value_coded',1,2,0),(5,1,'in_tb_program','obs.value_coded',2,3,0),(6,1,'pregnant','obs.value_coded',3,4,0),(7,1,'vaccinated_for_covid','obs.value_coded',4,5,0),(8,1,'covid_vaccination_status','obs.value_coded',5,6,0),(9,1,'ever_tested_for_covid','obs.value_coded',6,7,0),(10,1,'covid_test_date','obs.value_datetime',7,8,0),(11,1,'fever','obs.value_coded',8,9,0),(12,1,'general_weakness','obs.value_coded',9,10,0),(13,1,'cough','obs.value_coded',10,11,0),(14,1,'sore_throat','obs.value_coded',11,12,0),(15,1,'runny_nose','obs.value_coded',12,13,0),(16,1,'breathing_difficulty','obs.value_coded',13,14,0),(17,1,'diarrhoea','obs.value_coded',14,15,0),(18,1,'nausea_vomiting','obs.value_coded',15,16,0),(19,1,'headache','obs.value_coded',16,17,0),(20,1,'altered_mental_status','obs.value_coded',17,18,0),(21,1,'muscular_pain','obs.value_coded',18,19,0),(22,1,'chest_pain','obs.value_coded',19,20,0),(23,1,'abdominal_pain','obs.value_coded',20,21,0),(24,1,'joint_pain','obs.value_coded',21,22,0),(25,1,'loss_of_taste_smell','obs.value_coded',22,23,0),(26,1,'other_symptom','obs.value_coded',23,24,0),(27,1,'specify_symptoms','obs.value_text',24,25,0),(28,1,'onset_symptoms_date','obs.value_datetime',25,26,0),(29,1,'recent_travel','obs.value_coded',26,27,0),(30,1,'contact_with_suspected_or_confirmed_case','obs.value_coded',27,28,0),(31,1,'attended_large_gathering','obs.value_coded',28,29,0),(32,1,'eligible_for_covid_test','obs.value_coded',29,30,0),(33,1,'consented_for_covid_test','obs.value_coded',30,31,0),(34,1,'decline_reason','obs.value_text',31,32,0),(35,2,'Person_Id','patient_identifier.identifier',NULL,9,0),(36,2,'Encounter_ID','encounter.encounter_id',NULL,0,0),(37,2,'consented_for_covid_test','obs.value_coded',0,1,0),(38,2,'decline_reason','obs.value_text',1,2,0),(39,2,'nationality','obs.value_coded',2,3,0),(40,2,'passport_id_number','obs.value_text',3,4,0),(41,2,'sample_type','obs.value_coded',4,5,0),(42,2,'test_reason','obs.value_coded',5,6,0),(43,2,'test_reason_other','obs.value_text',6,7,0),(44,2,'ag_rdt_test_done','obs.value_coded',7,8,0),(45,3,'Person_Id','patient_identifier.identifier',NULL,6,0),(46,3,'Encounter_ID','encounter.encounter_id',NULL,0,0),(47,3,'ag_rdt_test_result','obs.value_coded',0,1,0),(48,3,'case_classification','obs.value_coded',1,2,0),(49,3,'action_taken','obs.value_coded',2,3,0),(50,3,'hospital_referred_to','obs.value_text',3,4,0),(51,3,'notes','obs.value_text',4,5,0);
/*!40000 ALTER TABLE `spreadsheetimport_template_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spreadsheetimport_template_column_column`
--

DROP TABLE IF EXISTS `spreadsheetimport_template_column_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spreadsheetimport_template_column_column` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `template_column_id_import_first` int(32) NOT NULL,
  `template_column_id_import_next` int(32) NOT NULL,
  `foreign_key_column_name` varchar(1000) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Template column which must be imported first` (`template_column_id_import_first`),
  KEY `Template column which must be imported next` (`template_column_id_import_next`),
  CONSTRAINT `Template column which must be imported first` FOREIGN KEY (`template_column_id_import_first`) REFERENCES `spreadsheetimport_template_column` (`id`),
  CONSTRAINT `Template column which must be imported next` FOREIGN KEY (`template_column_id_import_next`) REFERENCES `spreadsheetimport_template_column` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spreadsheetimport_template_column_column`
--

LOCK TABLES `spreadsheetimport_template_column_column` WRITE;
/*!40000 ALTER TABLE `spreadsheetimport_template_column_column` DISABLE KEYS */;
INSERT INTO `spreadsheetimport_template_column_column` VALUES (1,2,3,'encounter_id'),(2,2,4,'encounter_id'),(3,2,5,'encounter_id'),(4,2,6,'encounter_id'),(5,2,7,'encounter_id'),(6,2,8,'encounter_id'),(7,2,9,'encounter_id'),(8,2,10,'encounter_id'),(9,2,11,'encounter_id'),(10,2,12,'encounter_id'),(11,2,13,'encounter_id'),(12,2,14,'encounter_id'),(13,2,15,'encounter_id'),(14,2,16,'encounter_id'),(15,2,17,'encounter_id'),(16,2,18,'encounter_id'),(17,2,19,'encounter_id'),(18,2,20,'encounter_id'),(19,2,21,'encounter_id'),(20,2,22,'encounter_id'),(21,2,23,'encounter_id'),(22,2,24,'encounter_id'),(23,2,25,'encounter_id'),(24,2,26,'encounter_id'),(25,2,27,'encounter_id'),(26,2,28,'encounter_id'),(27,2,29,'encounter_id'),(28,2,30,'encounter_id'),(29,2,31,'encounter_id'),(30,2,32,'encounter_id'),(31,2,33,'encounter_id'),(32,2,34,'encounter_id'),(33,36,37,'encounter_id'),(34,36,38,'encounter_id'),(35,36,39,'encounter_id'),(36,36,40,'encounter_id'),(37,36,41,'encounter_id'),(38,36,42,'encounter_id'),(39,36,43,'encounter_id'),(40,36,44,'encounter_id'),(41,46,47,'encounter_id'),(42,46,48,'encounter_id'),(43,46,49,'encounter_id'),(44,46,50,'encounter_id'),(45,46,51,'encounter_id');
/*!40000 ALTER TABLE `spreadsheetimport_template_column_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spreadsheetimport_template_column_prespecified_value`
--

DROP TABLE IF EXISTS `spreadsheetimport_template_column_prespecified_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spreadsheetimport_template_column_prespecified_value` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `template_column_id` int(32) NOT NULL,
  `template_prespecified_value_id` int(32) NOT NULL,
  `foreign_key_column_name` varchar(1000) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Template column which is being mapped to a pre-specified value` (`template_column_id`),
  KEY `Pre-specified value which is being mapped to a template column` (`template_prespecified_value_id`),
  CONSTRAINT `Pre-specifived value which is being mapped to a template column` FOREIGN KEY (`template_prespecified_value_id`) REFERENCES `spreadsheetimport_template_prespecified_value` (`id`),
  CONSTRAINT `Template column which is being mapped to a pre-specified value` FOREIGN KEY (`template_column_id`) REFERENCES `spreadsheetimport_template_column` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spreadsheetimport_template_column_prespecified_value`
--

LOCK TABLES `spreadsheetimport_template_column_prespecified_value` WRITE;
/*!40000 ALTER TABLE `spreadsheetimport_template_column_prespecified_value` DISABLE KEYS */;
INSERT INTO `spreadsheetimport_template_column_prespecified_value` VALUES (1,2,1,'form_id'),(2,2,2,'patient_id'),(3,2,3,'location_id'),(4,2,4,'encounter_type'),(5,3,5,'person_id'),(6,3,6,'concept_id'),(7,4,7,'person_id'),(8,4,8,'concept_id'),(9,5,9,'person_id'),(10,5,10,'concept_id'),(11,6,11,'person_id'),(12,6,12,'concept_id'),(13,7,13,'person_id'),(14,7,14,'concept_id'),(15,8,15,'person_id'),(16,8,16,'concept_id'),(17,9,17,'person_id'),(18,9,18,'concept_id'),(19,10,19,'person_id'),(20,10,20,'concept_id'),(21,11,21,'person_id'),(22,11,22,'concept_id'),(23,12,23,'person_id'),(24,12,24,'concept_id'),(25,13,25,'person_id'),(26,13,26,'concept_id'),(27,14,27,'person_id'),(28,14,28,'concept_id'),(29,15,29,'person_id'),(30,15,30,'concept_id'),(31,16,31,'person_id'),(32,16,32,'concept_id'),(33,17,33,'person_id'),(34,17,34,'concept_id'),(35,18,35,'person_id'),(36,18,36,'concept_id'),(37,19,37,'person_id'),(38,19,38,'concept_id'),(39,20,39,'person_id'),(40,20,40,'concept_id'),(41,21,41,'person_id'),(42,21,42,'concept_id'),(43,22,43,'person_id'),(44,22,44,'concept_id'),(45,23,45,'person_id'),(46,23,46,'concept_id'),(47,24,47,'person_id'),(48,24,48,'concept_id'),(49,25,49,'person_id'),(50,25,50,'concept_id'),(51,26,51,'person_id'),(52,26,52,'concept_id'),(53,27,53,'person_id'),(54,27,54,'concept_id'),(55,28,55,'person_id'),(56,28,56,'concept_id'),(57,29,57,'person_id'),(58,29,58,'concept_id'),(59,30,59,'person_id'),(60,30,60,'concept_id'),(61,31,61,'person_id'),(62,31,62,'concept_id'),(63,32,63,'person_id'),(64,32,64,'concept_id'),(65,33,65,'person_id'),(66,33,66,'concept_id'),(67,34,67,'person_id'),(68,34,68,'concept_id'),(69,1,69,'identifier_type'),(70,1,70,'patient_id'),(71,36,71,'form_id'),(72,36,72,'patient_id'),(73,36,73,'location_id'),(74,36,74,'encounter_type'),(75,37,75,'person_id'),(76,37,76,'concept_id'),(77,38,77,'person_id'),(78,38,78,'concept_id'),(79,39,79,'person_id'),(80,39,80,'concept_id'),(81,40,81,'person_id'),(82,40,82,'concept_id'),(83,41,83,'person_id'),(84,41,84,'concept_id'),(85,42,85,'person_id'),(86,42,86,'concept_id'),(87,43,87,'person_id'),(88,43,88,'concept_id'),(89,44,89,'person_id'),(90,44,90,'concept_id'),(91,35,91,'identifier_type'),(92,35,92,'patient_id'),(93,46,93,'form_id'),(94,46,94,'patient_id'),(95,46,95,'location_id'),(96,46,96,'encounter_type'),(97,47,97,'person_id'),(98,47,98,'concept_id'),(99,48,99,'person_id'),(100,48,100,'concept_id'),(101,49,101,'person_id'),(102,49,102,'concept_id'),(103,50,103,'person_id'),(104,50,104,'concept_id'),(105,51,105,'person_id'),(106,51,106,'concept_id'),(107,45,107,'identifier_type'),(108,45,108,'patient_id');
/*!40000 ALTER TABLE `spreadsheetimport_template_column_prespecified_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spreadsheetimport_template_prespecified_value`
--

DROP TABLE IF EXISTS `spreadsheetimport_template_prespecified_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spreadsheetimport_template_prespecified_value` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `template_id` int(32) NOT NULL,
  `database_table_dot_column` varchar(1000) NOT NULL,
  `value` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Template to which this pre-specified value belongs` (`template_id`),
  CONSTRAINT `Template to which this pre-specified value belongs` FOREIGN KEY (`template_id`) REFERENCES `spreadsheetimport_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spreadsheetimport_template_prespecified_value`
--

LOCK TABLES `spreadsheetimport_template_prespecified_value` WRITE;
/*!40000 ALTER TABLE `spreadsheetimport_template_prespecified_value` DISABLE KEYS */;
INSERT INTO `spreadsheetimport_template_prespecified_value` VALUES (1,1,'form.form_id','146'),(2,1,'patient.patient_id',''),(3,1,'location.location_id',''),(4,1,'encounter_type.encounter_type_id','129'),(5,1,'person.person_id',''),(6,1,'concept.concept_id','164918'),(7,1,'person.person_id',''),(8,1,'concept.concept_id','1169'),(9,1,'person.person_id',''),(10,1,'concept.concept_id','162309'),(11,1,'person.person_id',''),(12,1,'concept.concept_id','5272'),(13,1,'person.person_id',''),(14,1,'concept.concept_id','163100'),(15,1,'person.person_id',''),(16,1,'concept.concept_id','164134'),(17,1,'person.person_id',''),(18,1,'concept.concept_id','165852'),(19,1,'person.person_id',''),(20,1,'concept.concept_id','159948'),(21,1,'person.person_id',''),(22,1,'concept.concept_id','140238'),(23,1,'person.person_id',''),(24,1,'concept.concept_id','122943'),(25,1,'person.person_id',''),(26,1,'concept.concept_id','143264'),(27,1,'person.person_id',''),(28,1,'concept.concept_id','162737'),(29,1,'person.person_id',''),(30,1,'concept.concept_id','163336'),(31,1,'person.person_id',''),(32,1,'concept.concept_id','164441'),(33,1,'person.person_id',''),(34,1,'concept.concept_id','142412'),(35,1,'person.person_id',''),(36,1,'concept.concept_id','122983'),(37,1,'person.person_id',''),(38,1,'concept.concept_id','5219'),(39,1,'person.person_id',''),(40,1,'concept.concept_id','6023'),(41,1,'person.person_id',''),(42,1,'concept.concept_id','160388'),(43,1,'person.person_id',''),(44,1,'concept.concept_id','1123'),(45,1,'person.person_id',''),(46,1,'concept.concept_id','1125'),(47,1,'person.person_id',''),(48,1,'concept.concept_id','160687'),(49,1,'person.person_id',''),(50,1,'concept.concept_id','1729'),(51,1,'person.person_id',''),(52,1,'concept.concept_id','1838'),(53,1,'person.person_id',''),(54,1,'concept.concept_id','160632'),(55,1,'person.person_id',''),(56,1,'concept.concept_id','1730'),(57,1,'person.person_id',''),(58,1,'concept.concept_id','162619'),(59,1,'person.person_id',''),(60,1,'concept.concept_id','162633'),(61,1,'person.person_id',''),(62,1,'concept.concept_id','165163'),(63,1,'person.person_id',''),(64,1,'concept.concept_id','165087'),(65,1,'person.person_id',''),(66,1,'concept.concept_id','1710'),(67,1,'person.person_id',''),(68,1,'concept.concept_id','161011'),(69,1,'patient_identifier_type.patient_identifier_type_id','31'),(70,1,'patient.patient_id',''),(71,2,'form.form_id','150'),(72,2,'patient.patient_id',''),(73,2,'location.location_id',''),(74,2,'encounter_type.encounter_type_id','133'),(75,2,'person.person_id',''),(76,2,'concept.concept_id','1710'),(77,2,'person.person_id',''),(78,2,'concept.concept_id','161011'),(79,2,'person.person_id',''),(80,2,'concept.concept_id','165847'),(81,2,'person.person_id',''),(82,2,'concept.concept_id','163084'),(83,2,'person.person_id',''),(84,2,'concept.concept_id','159959'),(85,2,'person.person_id',''),(86,2,'concept.concept_id','164126'),(87,2,'person.person_id',''),(88,2,'concept.concept_id','160632'),(89,2,'person.person_id',''),(90,2,'concept.concept_id','165852'),(91,2,'patient_identifier_type.patient_identifier_type_id','20'),(92,2,'patient.patient_id',''),(93,3,'form.form_id','151'),(94,3,'patient.patient_id',''),(95,3,'location.location_id',''),(96,3,'encounter_type.encounter_type_id','134'),(97,3,'person.person_id',''),(98,3,'concept.concept_id','165852'),(99,3,'person.person_id',''),(100,3,'concept.concept_id','159640'),(101,3,'person.person_id',''),(102,3,'concept.concept_id','1272'),(103,3,'person.person_id',''),(104,3,'concept.concept_id','5090'),(105,3,'person.person_id',''),(106,3,'concept.concept_id','161011'),(107,3,'patient_identifier_type.patient_identifier_type_id','20'),(108,3,'patient.patient_id','');
/*!40000 ALTER TABLE `spreadsheetimport_template_prespecified_value` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-08  0:44:33
