-- MySQL dump 9.11
--
-- Host: localhost    Database: hosts
-- ------------------------------------------------------
-- Server version	4.0.21-standard

--
-- Table structure for table `host_interface`
--

CREATE TABLE host_interface (
  id int(11) NOT NULL auto_increment,
  host_id int(11) NOT NULL default '0',
  interface_id int(11) NOT NULL default '0',
  PRIMARY KEY  (id)
) TYPE=MyISAM;

--
-- Dumping data for table `host_interface`
--


--
-- Table structure for table `host_service`
--

CREATE TABLE host_service (
  id int(11) NOT NULL auto_increment,
  host_id int(11) NOT NULL default '0',
  service_id int(11) NOT NULL default '0',
  PRIMARY KEY  (id)
) TYPE=MyISAM;

--
-- Dumping data for table `host_service`
--


--
-- Table structure for table `hosts`
--

CREATE TABLE hosts (
  id int(11) NOT NULL auto_increment,
  name varchar(60) default NULL,
  address varchar(60) default NULL,
  os varchar(60) default NULL,
  serial varchar(60) default NULL,
  type varchar(60) default NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

--
-- Dumping data for table `hosts`
--

INSERT INTO hosts VALUES (1,'mercury','192.168.3.53','Cisco 2621','ABC9342HG','cisco');
INSERT INTO hosts VALUES (2,'venus-firewall','192.168.2.3','pix','ABC9342HL','cisco');
INSERT INTO hosts VALUES (3,'venus-loadbalancer','192.168.2.2','os400','ABC9342HM','as400');
INSERT INTO hosts VALUES (4,'earth-switch','192.168.2.16','cisco','ABC9342HO','intel');
INSERT INTO hosts VALUES (5,'mars-switch2','192.168.2.17','redhat','ABC9342HP','intel');
INSERT INTO hosts VALUES (6,'mars-host','192.168.2.15','slackware','ABC9342HQ','intel');
INSERT INTO hosts VALUES (7,'jupiter-firewall','192.168.2.13','Linksys Firewall','ABC9342HS','cisco');
INSERT INTO hosts VALUES (8,'jupiter-switch1','192.168.2.12','Netgear','ABC9342HT','intel');
INSERT INTO hosts VALUES (9,'jupiter-remote','192.168.2.21','Solaris','ABC9342AA','sun');
INSERT INTO hosts VALUES (10,'uranus-remote','192.168.2.22','Exchange Server','ABC9342BB','intel');
INSERT INTO hosts VALUES (11,'uranus-host','192.168.2.23','os400','ABC9342CC','as400');
INSERT INTO hosts VALUES (12,'neptun','192.168.2.30','Solaris','ABC9342DD','sparc');
INSERT INTO hosts VALUES (13,'neptun_reviews','192.168.2.31','debian','ABC9342EE','intel');
INSERT INTO hosts VALUES (14,'neptun_news','192.168.2.32','SuSE','ABC9342FF','intel');
INSERT INTO hosts VALUES (15,'pluto90','192.168.3.54','Cisco 2621','ABC9342HH','cisco');
INSERT INTO hosts VALUES (16,'pluto91','192.168.3.55','Cisco 2690','ABC9342HI','cisco');
INSERT INTO hosts VALUES (17,'pluto92','192.168.3.52','Cisco 2690','ABC9342HK','cisco');

--
-- Table structure for table `interfaces`
--

CREATE TABLE interfaces (
  id int(11) NOT NULL auto_increment,
  name varchar(60) default NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

--
-- Dumping data for table `interfaces`
--


--
-- Table structure for table `services`
--

CREATE TABLE services (
  id int(11) NOT NULL auto_increment,
  name varchar(60) default NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

--
-- Dumping data for table `services`
--

INSERT INTO services VALUES (1,'cpu');
INSERT INTO services VALUES (2,'disk');
INSERT INTO services VALUES (3,'memory');
INSERT INTO services VALUES (4,'process');
INSERT INTO services VALUES (5,'service');
INSERT INTO services VALUES (6,'ssh');

