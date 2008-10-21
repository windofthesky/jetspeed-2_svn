/*
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
-- ----------------------------------------------------------------------- 
-- MEDIA_TYPE 
-- ----------------------------------------------------------------------- 

CREATE TABLE MEDIA_TYPE
(
    MEDIATYPE_ID INT NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    CHARACTER_SET VARCHAR(40),
    TITLE VARCHAR(80),
    DESCRIPTION TEXT,
    PRIMARY KEY (MEDIATYPE_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- CLIENT 
-- ----------------------------------------------------------------------- 

CREATE TABLE CLIENT
(
    CLIENT_ID INT NOT NULL,
    EVAL_ORDER INT NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    USER_AGENT_PATTERN VARCHAR(128),
    MANUFACTURER VARCHAR(80),
    MODEL VARCHAR(80),
    VERSION VARCHAR(40),
    PREFERRED_MIMETYPE_ID INT NOT NULL,
    PRIMARY KEY (CLIENT_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- MIMETYPE 
-- ----------------------------------------------------------------------- 

CREATE TABLE MIMETYPE
(
    MIMETYPE_ID INT NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    PRIMARY KEY (MIMETYPE_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- CAPABILITY 
-- ----------------------------------------------------------------------- 

CREATE TABLE CAPABILITY
(
    CAPABILITY_ID INT NOT NULL,
    CAPABILITY VARCHAR(80) NOT NULL,
    PRIMARY KEY (CAPABILITY_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- CLIENT_TO_CAPABILITY 
-- ----------------------------------------------------------------------- 

CREATE TABLE CLIENT_TO_CAPABILITY
(
    CLIENT_ID INT NOT NULL,
    CAPABILITY_ID INT NOT NULL
);
GO
-- ----------------------------------------------------------------------- 
-- CLIENT_TO_MIMETYPE 
-- ----------------------------------------------------------------------- 

CREATE TABLE CLIENT_TO_MIMETYPE
(
    CLIENT_ID INT NOT NULL,
    MIMETYPE_ID INT NOT NULL
);
GO
-- ----------------------------------------------------------------------- 
-- MEDIATYPE_TO_CAPABILITY 
-- ----------------------------------------------------------------------- 

CREATE TABLE MEDIATYPE_TO_CAPABILITY
(
    MEDIATYPE_ID INT NOT NULL,
    CAPABILITY_ID INT NOT NULL
);
GO
-- ----------------------------------------------------------------------- 
-- MEDIATYPE_TO_MIMETYPE 
-- ----------------------------------------------------------------------- 

CREATE TABLE MEDIATYPE_TO_MIMETYPE
(
    MEDIATYPE_ID INT NOT NULL,
    MIMETYPE_ID INT NOT NULL
);
GO
-- ----------------------------------------------------------------------- 
-- PORTLET_STATISTICS 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_STATISTICS
(
    IPADDRESS VARCHAR(80),
    USER_NAME VARCHAR(80),
    TIME_STAMP DATETIME,
    PAGE VARCHAR(80),
    PORTLET VARCHAR(255),
    STATUS INT,
    ELAPSED_TIME DECIMAL(19,0)
);
GO
-- ----------------------------------------------------------------------- 
-- PAGE_STATISTICS 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_STATISTICS
(
    IPADDRESS VARCHAR(80),
    USER_NAME VARCHAR(80),
    TIME_STAMP DATETIME,
    PAGE VARCHAR(80),
    STATUS INT,
    ELAPSED_TIME DECIMAL(19,0)
);
GO
-- ----------------------------------------------------------------------- 
-- USER_STATISTICS 
-- ----------------------------------------------------------------------- 

CREATE TABLE USER_STATISTICS
(
    IPADDRESS VARCHAR(80),
    USER_NAME VARCHAR(80),
    TIME_STAMP DATETIME,
    STATUS INT,
    ELAPSED_TIME DECIMAL(19,0)
);
GO
-- ----------------------------------------------------------------------- 
-- ADMIN_ACTIVITY 
-- ----------------------------------------------------------------------- 

CREATE TABLE ADMIN_ACTIVITY
(
    ACTIVITY VARCHAR(40),
    CATEGORY VARCHAR(40),
    ADMIN VARCHAR(80),
    USER_NAME VARCHAR(80),
    TIME_STAMP DATETIME,
    IPADDRESS VARCHAR(80),
    ATTR_NAME VARCHAR(40),
    ATTR_VALUE_BEFORE VARCHAR(80),
    ATTR_VALUE_AFTER VARCHAR(80),
    DESCRIPTION VARCHAR(128)
);
GO
-- ----------------------------------------------------------------------- 
-- USER_ACTIVITY 
-- ----------------------------------------------------------------------- 

CREATE TABLE USER_ACTIVITY
(
    ACTIVITY VARCHAR(40),
    CATEGORY VARCHAR(40),
    USER_NAME VARCHAR(80),
    TIME_STAMP DATETIME,
    IPADDRESS VARCHAR(80),
    ATTR_NAME VARCHAR(40),
    ATTR_VALUE_BEFORE VARCHAR(80),
    ATTR_VALUE_AFTER VARCHAR(80),
    DESCRIPTION VARCHAR(128)
);
GO
-- ----------------------------------------------------------------------- 
-- FOLDER 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER
(
    FOLDER_ID INT NOT NULL,
    PARENT_ID INT,
    PATH VARCHAR(240) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(40),
    IS_HIDDEN SMALLINT NOT NULL,
    SKIN VARCHAR(80),
    DEFAULT_LAYOUT_DECORATOR VARCHAR(80),
    DEFAULT_PORTLET_DECORATOR VARCHAR(80),
    DEFAULT_PAGE_NAME VARCHAR(80),
    SUBSITE VARCHAR(40),
    USER_PRINCIPAL VARCHAR(40),
    ROLE_PRINCIPAL VARCHAR(40),
    GROUP_PRINCIPAL VARCHAR(40),
    MEDIATYPE VARCHAR(15),
    LOCALE VARCHAR(20),
    EXT_ATTR_NAME VARCHAR(15),
    EXT_ATTR_VALUE VARCHAR(40),
    OWNER_PRINCIPAL VARCHAR(40),
    PRIMARY KEY (FOLDER_ID)
);
GO
CREATE INDEX IX_FOLDER_1 ON FOLDER (PARENT_ID);
GO
CREATE UNIQUE INDEX UN_FOLDER_1 ON FOLDER (PATH);
GO
-- ----------------------------------------------------------------------- 
-- FOLDER_METADATA 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_METADATA
(
    METADATA_ID INT NOT NULL,
    FOLDER_ID INT NOT NULL,
    NAME VARCHAR(15) NOT NULL,
    LOCALE VARCHAR(20) NOT NULL,
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (METADATA_ID)
);
GO
CREATE INDEX IX_FOLDER_METADATA_1 ON FOLDER_METADATA (FOLDER_ID);
GO
CREATE UNIQUE INDEX UN_FOLDER_METADATA_1 ON FOLDER_METADATA (FOLDER_ID, NAME, LOCALE, VALUE);
GO
-- ----------------------------------------------------------------------- 
-- FOLDER_CONSTRAINT 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_CONSTRAINT
(
    CONSTRAINT_ID INT NOT NULL,
    FOLDER_ID INT NOT NULL,
    APPLY_ORDER INT NOT NULL,
    USER_PRINCIPALS_ACL VARCHAR(120),
    ROLE_PRINCIPALS_ACL VARCHAR(120),
    GROUP_PRINCIPALS_ACL VARCHAR(120),
    PERMISSIONS_ACL VARCHAR(120),
    PRIMARY KEY (CONSTRAINT_ID)
);
GO
CREATE INDEX IX_FOLDER_CONSTRAINT_1 ON FOLDER_CONSTRAINT (FOLDER_ID);
GO
-- ----------------------------------------------------------------------- 
-- FOLDER_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID INT NOT NULL,
    FOLDER_ID INT NOT NULL,
    APPLY_ORDER INT NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_REF_ID)
);
GO
CREATE INDEX IX_FOLDER_CONSTRAINTS_REF_1 ON FOLDER_CONSTRAINTS_REF (FOLDER_ID);
GO
CREATE UNIQUE INDEX UN_FOLDER_CONSTRAINTS_REF_1 ON FOLDER_CONSTRAINTS_REF (FOLDER_ID, NAME);
GO
-- ----------------------------------------------------------------------- 
-- FOLDER_ORDER 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_ORDER
(
    ORDER_ID INT NOT NULL,
    FOLDER_ID INT NOT NULL,
    SORT_ORDER INT NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    PRIMARY KEY (ORDER_ID)
);
GO
CREATE INDEX IX_FOLDER_ORDER_1 ON FOLDER_ORDER (FOLDER_ID);
GO
CREATE UNIQUE INDEX UN_FOLDER_ORDER_1 ON FOLDER_ORDER (FOLDER_ID, NAME);
GO
-- ----------------------------------------------------------------------- 
-- FOLDER_MENU 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_MENU
(
    MENU_ID INT NOT NULL,
    CLASS_NAME VARCHAR(100) NOT NULL,
    PARENT_ID INT,
    FOLDER_ID INT,
    ELEMENT_ORDER INT,
    NAME VARCHAR(100),
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(40),
    TEXT VARCHAR(100),
    OPTIONS VARCHAR(255),
    DEPTH INT,
    IS_PATHS SMALLINT,
    IS_REGEXP SMALLINT,
    PROFILE VARCHAR(80),
    OPTIONS_ORDER VARCHAR(255),
    SKIN VARCHAR(80),
    IS_NEST SMALLINT,
    PRIMARY KEY (MENU_ID)
);
GO
CREATE INDEX IX_FOLDER_MENU_1 ON FOLDER_MENU (PARENT_ID);
GO
CREATE INDEX UN_FOLDER_MENU_1 ON FOLDER_MENU (FOLDER_ID, NAME);
GO
-- ----------------------------------------------------------------------- 
-- FOLDER_MENU_METADATA 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_MENU_METADATA
(
    METADATA_ID INT NOT NULL,
    MENU_ID INT NOT NULL,
    NAME VARCHAR(15) NOT NULL,
    LOCALE VARCHAR(20) NOT NULL,
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (METADATA_ID)
);
GO
CREATE INDEX IX_FOLDER_MENU_METADATA_1 ON FOLDER_MENU_METADATA (MENU_ID);
GO
CREATE UNIQUE INDEX UN_FOLDER_MENU_METADATA_1 ON FOLDER_MENU_METADATA (MENU_ID, NAME, LOCALE, VALUE);
GO
-- ----------------------------------------------------------------------- 
-- PAGE 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE
(
    PAGE_ID INT NOT NULL,
    PARENT_ID INT NOT NULL,
    PATH VARCHAR(240) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    VERSION VARCHAR(40),
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(40),
    IS_HIDDEN SMALLINT NOT NULL,
    SKIN VARCHAR(80),
    DEFAULT_LAYOUT_DECORATOR VARCHAR(80),
    DEFAULT_PORTLET_DECORATOR VARCHAR(80),
    SUBSITE VARCHAR(40),
    USER_PRINCIPAL VARCHAR(40),
    ROLE_PRINCIPAL VARCHAR(40),
    GROUP_PRINCIPAL VARCHAR(40),
    MEDIATYPE VARCHAR(15),
    LOCALE VARCHAR(20),
    EXT_ATTR_NAME VARCHAR(15),
    EXT_ATTR_VALUE VARCHAR(40),
    OWNER_PRINCIPAL VARCHAR(40),
    PRIMARY KEY (PAGE_ID)
);
GO
CREATE INDEX IX_PAGE_1 ON PAGE (PARENT_ID);
GO
CREATE UNIQUE INDEX UN_PAGE_1 ON PAGE (PATH);
GO
-- ----------------------------------------------------------------------- 
-- PAGE_METADATA 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_METADATA
(
    METADATA_ID INT NOT NULL,
    PAGE_ID INT NOT NULL,
    NAME VARCHAR(15) NOT NULL,
    LOCALE VARCHAR(20) NOT NULL,
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (METADATA_ID)
);
GO
CREATE INDEX IX_PAGE_METADATA_1 ON PAGE_METADATA (PAGE_ID);
GO
CREATE UNIQUE INDEX UN_PAGE_METADATA_1 ON PAGE_METADATA (PAGE_ID, NAME, LOCALE, VALUE);
GO
-- ----------------------------------------------------------------------- 
-- PAGE_CONSTRAINT 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_CONSTRAINT
(
    CONSTRAINT_ID INT NOT NULL,
    PAGE_ID INT NOT NULL,
    APPLY_ORDER INT NOT NULL,
    USER_PRINCIPALS_ACL VARCHAR(120),
    ROLE_PRINCIPALS_ACL VARCHAR(120),
    GROUP_PRINCIPALS_ACL VARCHAR(120),
    PERMISSIONS_ACL VARCHAR(120),
    PRIMARY KEY (CONSTRAINT_ID)
);
GO
CREATE INDEX IX_PAGE_CONSTRAINT_1 ON PAGE_CONSTRAINT (PAGE_ID);
GO
-- ----------------------------------------------------------------------- 
-- PAGE_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID INT NOT NULL,
    PAGE_ID INT NOT NULL,
    APPLY_ORDER INT NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_REF_ID)
);
GO
CREATE INDEX IX_PAGE_CONSTRAINTS_REF_1 ON PAGE_CONSTRAINTS_REF (PAGE_ID);
GO
CREATE UNIQUE INDEX UN_PAGE_CONSTRAINTS_REF_1 ON PAGE_CONSTRAINTS_REF (PAGE_ID, NAME);
GO
-- ----------------------------------------------------------------------- 
-- PAGE_MENU 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_MENU
(
    MENU_ID INT NOT NULL,
    CLASS_NAME VARCHAR(100) NOT NULL,
    PARENT_ID INT,
    PAGE_ID INT,
    ELEMENT_ORDER INT,
    NAME VARCHAR(100),
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(40),
    TEXT VARCHAR(100),
    OPTIONS VARCHAR(255),
    DEPTH INT,
    IS_PATHS SMALLINT,
    IS_REGEXP SMALLINT,
    PROFILE VARCHAR(80),
    OPTIONS_ORDER VARCHAR(255),
    SKIN VARCHAR(80),
    IS_NEST SMALLINT,
    PRIMARY KEY (MENU_ID)
);
GO
CREATE INDEX IX_PAGE_MENU_1 ON PAGE_MENU (PARENT_ID);
GO
CREATE INDEX UN_PAGE_MENU_1 ON PAGE_MENU (PAGE_ID, NAME);
GO
-- ----------------------------------------------------------------------- 
-- PAGE_MENU_METADATA 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_MENU_METADATA
(
    METADATA_ID INT NOT NULL,
    MENU_ID INT NOT NULL,
    NAME VARCHAR(15) NOT NULL,
    LOCALE VARCHAR(20) NOT NULL,
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (METADATA_ID)
);
GO
CREATE INDEX IX_PAGE_MENU_METADATA_1 ON PAGE_MENU_METADATA (MENU_ID);
GO
CREATE UNIQUE INDEX UN_PAGE_MENU_METADATA_1 ON PAGE_MENU_METADATA (MENU_ID, NAME, LOCALE, VALUE);
GO
-- ----------------------------------------------------------------------- 
-- FRAGMENT 
-- ----------------------------------------------------------------------- 

CREATE TABLE FRAGMENT
(
    FRAGMENT_ID INT NOT NULL,
    PARENT_ID INT,
    PAGE_ID INT,
    NAME VARCHAR(100),
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(40),
    TYPE VARCHAR(40),
    SKIN VARCHAR(80),
    DECORATOR VARCHAR(80),
    STATE VARCHAR(10),
    PMODE VARCHAR(10),
    LAYOUT_ROW INT,
    LAYOUT_COLUMN INT,
    LAYOUT_SIZES VARCHAR(20),
    LAYOUT_X REAL,
    LAYOUT_Y REAL,
    LAYOUT_Z REAL,
    LAYOUT_WIDTH REAL,
    LAYOUT_HEIGHT REAL,
    EXT_PROP_NAME_1 VARCHAR(40),
    EXT_PROP_VALUE_1 VARCHAR(80),
    EXT_PROP_NAME_2 VARCHAR(40),
    EXT_PROP_VALUE_2 VARCHAR(80),
    OWNER_PRINCIPAL VARCHAR(40),
    PRIMARY KEY (FRAGMENT_ID)
);
GO
CREATE INDEX IX_FRAGMENT_1 ON FRAGMENT (PARENT_ID);
GO
CREATE INDEX UN_FRAGMENT_1 ON FRAGMENT (PAGE_ID);
GO
-- ----------------------------------------------------------------------- 
-- FRAGMENT_CONSTRAINT 
-- ----------------------------------------------------------------------- 

CREATE TABLE FRAGMENT_CONSTRAINT
(
    CONSTRAINT_ID INT NOT NULL,
    FRAGMENT_ID INT NOT NULL,
    APPLY_ORDER INT NOT NULL,
    USER_PRINCIPALS_ACL VARCHAR(120),
    ROLE_PRINCIPALS_ACL VARCHAR(120),
    GROUP_PRINCIPALS_ACL VARCHAR(120),
    PERMISSIONS_ACL VARCHAR(120),
    PRIMARY KEY (CONSTRAINT_ID)
);
GO
CREATE INDEX IX_FRAGMENT_CONSTRAINT_1 ON FRAGMENT_CONSTRAINT (FRAGMENT_ID);
GO
-- ----------------------------------------------------------------------- 
-- FRAGMENT_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE FRAGMENT_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID INT NOT NULL,
    FRAGMENT_ID INT NOT NULL,
    APPLY_ORDER INT NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_REF_ID)
);
GO
CREATE INDEX IX_FRAGMENT_CONSTRAINTS_REF_1 ON FRAGMENT_CONSTRAINTS_REF (FRAGMENT_ID);
GO
CREATE UNIQUE INDEX UN_FRAGMENT_CONSTRAINTS_REF_1 ON FRAGMENT_CONSTRAINTS_REF (FRAGMENT_ID, NAME);
GO
-- ----------------------------------------------------------------------- 
-- FRAGMENT_PREF 
-- ----------------------------------------------------------------------- 

CREATE TABLE FRAGMENT_PREF
(
    PREF_ID INT NOT NULL,
    FRAGMENT_ID INT NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    IS_READ_ONLY SMALLINT NOT NULL,
    PRIMARY KEY (PREF_ID)
);
GO
CREATE INDEX IX_FRAGMENT_PREF_1 ON FRAGMENT_PREF (FRAGMENT_ID);
GO
CREATE UNIQUE INDEX UN_FRAGMENT_PREF_1 ON FRAGMENT_PREF (FRAGMENT_ID, NAME);
GO
-- ----------------------------------------------------------------------- 
-- FRAGMENT_PREF_VALUE 
-- ----------------------------------------------------------------------- 

CREATE TABLE FRAGMENT_PREF_VALUE
(
    PREF_VALUE_ID INT NOT NULL,
    PREF_ID INT NOT NULL,
    VALUE_ORDER INT NOT NULL,
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (PREF_VALUE_ID)
);
GO
CREATE INDEX IX_FRAGMENT_PREF_VALUE_1 ON FRAGMENT_PREF_VALUE (PREF_ID);
GO
-- ----------------------------------------------------------------------- 
-- LINK 
-- ----------------------------------------------------------------------- 

CREATE TABLE LINK
(
    LINK_ID INT NOT NULL,
    PARENT_ID INT NOT NULL,
    PATH VARCHAR(240) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    VERSION VARCHAR(40),
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(40),
    IS_HIDDEN SMALLINT NOT NULL,
    SKIN VARCHAR(80),
    TARGET VARCHAR(80),
    URL VARCHAR(255),
    SUBSITE VARCHAR(40),
    USER_PRINCIPAL VARCHAR(40),
    ROLE_PRINCIPAL VARCHAR(40),
    GROUP_PRINCIPAL VARCHAR(40),
    MEDIATYPE VARCHAR(15),
    LOCALE VARCHAR(20),
    EXT_ATTR_NAME VARCHAR(15),
    EXT_ATTR_VALUE VARCHAR(40),
    OWNER_PRINCIPAL VARCHAR(40),
    PRIMARY KEY (LINK_ID)
);
GO
CREATE INDEX IX_LINK_1 ON LINK (PARENT_ID);
GO
CREATE UNIQUE INDEX UN_LINK_1 ON LINK (PATH);
GO
-- ----------------------------------------------------------------------- 
-- LINK_METADATA 
-- ----------------------------------------------------------------------- 

CREATE TABLE LINK_METADATA
(
    METADATA_ID INT NOT NULL,
    LINK_ID INT NOT NULL,
    NAME VARCHAR(15) NOT NULL,
    LOCALE VARCHAR(20) NOT NULL,
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (METADATA_ID)
);
GO
CREATE INDEX IX_LINK_METADATA_1 ON LINK_METADATA (LINK_ID);
GO
CREATE UNIQUE INDEX UN_LINK_METADATA_1 ON LINK_METADATA (LINK_ID, NAME, LOCALE, VALUE);
GO
-- ----------------------------------------------------------------------- 
-- LINK_CONSTRAINT 
-- ----------------------------------------------------------------------- 

CREATE TABLE LINK_CONSTRAINT
(
    CONSTRAINT_ID INT NOT NULL,
    LINK_ID INT NOT NULL,
    APPLY_ORDER INT NOT NULL,
    USER_PRINCIPALS_ACL VARCHAR(120),
    ROLE_PRINCIPALS_ACL VARCHAR(120),
    GROUP_PRINCIPALS_ACL VARCHAR(120),
    PERMISSIONS_ACL VARCHAR(120),
    PRIMARY KEY (CONSTRAINT_ID)
);
GO
CREATE INDEX IX_LINK_CONSTRAINT_1 ON LINK_CONSTRAINT (LINK_ID);
GO
-- ----------------------------------------------------------------------- 
-- LINK_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE LINK_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID INT NOT NULL,
    LINK_ID INT NOT NULL,
    APPLY_ORDER INT NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_REF_ID)
);
GO
CREATE INDEX IX_LINK_CONSTRAINTS_REF_1 ON LINK_CONSTRAINTS_REF (LINK_ID);
GO
CREATE UNIQUE INDEX UN_LINK_CONSTRAINTS_REF_1 ON LINK_CONSTRAINTS_REF (LINK_ID, NAME);
GO
-- ----------------------------------------------------------------------- 
-- PAGE_SECURITY 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_SECURITY
(
    PAGE_SECURITY_ID INT NOT NULL,
    PARENT_ID INT NOT NULL,
    PATH VARCHAR(240) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    VERSION VARCHAR(40),
    SUBSITE VARCHAR(40),
    USER_PRINCIPAL VARCHAR(40),
    ROLE_PRINCIPAL VARCHAR(40),
    GROUP_PRINCIPAL VARCHAR(40),
    MEDIATYPE VARCHAR(15),
    LOCALE VARCHAR(20),
    EXT_ATTR_NAME VARCHAR(15),
    EXT_ATTR_VALUE VARCHAR(40),
    PRIMARY KEY (PAGE_SECURITY_ID)
);
GO
CREATE UNIQUE INDEX UN_PAGE_SECURITY_1 ON PAGE_SECURITY (PARENT_ID);
GO
CREATE UNIQUE INDEX UN_PAGE_SECURITY_2 ON PAGE_SECURITY (PATH);
GO
-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINTS_DEF 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_SEC_CONSTRAINTS_DEF
(
    CONSTRAINTS_DEF_ID INT NOT NULL,
    PAGE_SECURITY_ID INT NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_DEF_ID)
);
GO
CREATE INDEX IX_PAGE_SEC_CONSTRAINTS_DEF_1 ON PAGE_SEC_CONSTRAINTS_DEF (PAGE_SECURITY_ID);
GO
CREATE UNIQUE INDEX UN_PAGE_SEC_CONSTRAINTS_DEF_1 ON PAGE_SEC_CONSTRAINTS_DEF (PAGE_SECURITY_ID, NAME);
GO
-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINT_DEF 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_SEC_CONSTRAINT_DEF
(
    CONSTRAINT_DEF_ID INT NOT NULL,
    CONSTRAINTS_DEF_ID INT NOT NULL,
    APPLY_ORDER INT NOT NULL,
    USER_PRINCIPALS_ACL VARCHAR(120),
    ROLE_PRINCIPALS_ACL VARCHAR(120),
    GROUP_PRINCIPALS_ACL VARCHAR(120),
    PERMISSIONS_ACL VARCHAR(120),
    PRIMARY KEY (CONSTRAINT_DEF_ID)
);
GO
CREATE INDEX IX_PAGE_SEC_CONSTRAINT_DEF_1 ON PAGE_SEC_CONSTRAINT_DEF (CONSTRAINTS_DEF_ID);
GO
-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_SEC_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID INT NOT NULL,
    PAGE_SECURITY_ID INT NOT NULL,
    APPLY_ORDER INT NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_REF_ID)
);
GO
CREATE INDEX IX_PAGE_SEC_CONSTRAINTS_REF_1 ON PAGE_SEC_CONSTRAINTS_REF (PAGE_SECURITY_ID);
GO
CREATE UNIQUE INDEX UN_PAGE_SEC_CONSTRAINTS_REF_1 ON PAGE_SEC_CONSTRAINTS_REF (PAGE_SECURITY_ID, NAME);
GO
-- ----------------------------------------------------------------------- 
-- PROFILING_RULE 
-- ----------------------------------------------------------------------- 

CREATE TABLE PROFILING_RULE
(
    RULE_ID VARCHAR(80) NOT NULL,
    CLASS_NAME VARCHAR(100) NOT NULL,
    TITLE VARCHAR(100),
    PRIMARY KEY (RULE_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- RULE_CRITERION 
-- ----------------------------------------------------------------------- 

CREATE TABLE RULE_CRITERION
(
    CRITERION_ID VARCHAR(80) NOT NULL,
    RULE_ID VARCHAR(80) NOT NULL,
    FALLBACK_ORDER INT NOT NULL,
    REQUEST_TYPE VARCHAR(40) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    COLUMN_VALUE VARCHAR(128),
    FALLBACK_TYPE INT DEFAULT 1,
    PRIMARY KEY (CRITERION_ID)
);
GO
CREATE INDEX IX_RULE_CRITERION_1 ON RULE_CRITERION (RULE_ID, FALLBACK_ORDER);
GO
-- ----------------------------------------------------------------------- 
-- PRINCIPAL_RULE_ASSOC 
-- ----------------------------------------------------------------------- 

CREATE TABLE PRINCIPAL_RULE_ASSOC
(
    PRINCIPAL_NAME VARCHAR(80) NOT NULL,
    LOCATOR_NAME VARCHAR(80) NOT NULL,
    RULE_ID VARCHAR(80) NOT NULL,
    PRIMARY KEY (PRINCIPAL_NAME, LOCATOR_NAME)
);
GO
-- ----------------------------------------------------------------------- 
-- PROFILE_PAGE_ASSOC 
-- ----------------------------------------------------------------------- 

CREATE TABLE PROFILE_PAGE_ASSOC
(
    LOCATOR_HASH VARCHAR(40) NOT NULL,
    PAGE_ID VARCHAR(80) NOT NULL
);
GO
CREATE UNIQUE INDEX UN_PROFILE_PAGE_1 ON PROFILE_PAGE_ASSOC (LOCATOR_HASH, PAGE_ID);
GO
-- ----------------------------------------------------------------------- 
-- CLUBS 
-- ----------------------------------------------------------------------- 

CREATE TABLE CLUBS
(
    NAME VARCHAR(80) NOT NULL,
    COUNTRY VARCHAR(40) NOT NULL,
    CITY VARCHAR(40) NOT NULL,
    STADIUM VARCHAR(80) NOT NULL,
    CAPACITY INT,
    FOUNDED INT,
    PITCH VARCHAR(40),
    NICKNAME VARCHAR(40),
    PRIMARY KEY (NAME)
);
GO
-- ----------------------------------------------------------------------- 
-- OJB_HL_SEQ 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_HL_SEQ
(
    TABLENAME VARCHAR(175) NOT NULL,
    FIELDNAME VARCHAR(70) NOT NULL,
    MAX_KEY INT,
    GRAB_SIZE INT,
    VERSION INT,
    PRIMARY KEY (TABLENAME, FIELDNAME)
);
GO
-- ----------------------------------------------------------------------- 
-- OJB_LOCKENTRY 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_LOCKENTRY
(
    OID_ VARCHAR(250) NOT NULL,
    TX_ID VARCHAR(50) NOT NULL,
    TIMESTAMP_ DATETIME,
    ISOLATIONLEVEL INT,
    LOCKTYPE INT,
    PRIMARY KEY (OID_, TX_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- OJB_NRM 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_NRM
(
    NAME VARCHAR(250) NOT NULL,
    OID_ IMAGE,
    PRIMARY KEY (NAME)
);
GO
-- ----------------------------------------------------------------------- 
-- OJB_DLIST 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_DLIST
(
    ID INT NOT NULL,
    SIZE_ INT,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- OJB_DLIST_ENTRIES 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_DLIST_ENTRIES
(
    ID INT NOT NULL,
    DLIST_ID INT,
    POSITION_ INT,
    OID_ IMAGE,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- OJB_DSET 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_DSET
(
    ID INT NOT NULL,
    SIZE_ INT,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- OJB_DSET_ENTRIES 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_DSET_ENTRIES
(
    ID INT NOT NULL,
    DLIST_ID INT,
    POSITION_ INT,
    OID_ IMAGE,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- OJB_DMAP 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_DMAP
(
    ID INT NOT NULL,
    SIZE_ INT,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- PORTLET_DEFINITION 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_DEFINITION
(
    ID INT NOT NULL,
    NAME VARCHAR(80),
    CLASS_NAME VARCHAR(255),
    APPLICATION_ID INT NOT NULL,
    PORTLET_IDENTIFIER VARCHAR(80),
    EXPIRATION_CACHE VARCHAR(30),
    RESOURCE_BUNDLE VARCHAR(255),
    PREFERENCE_VALIDATOR VARCHAR(255),
    SECURITY_REF VARCHAR(40),
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- PORTLET_APPLICATION 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_APPLICATION
(
    APPLICATION_ID INT NOT NULL,
    APP_NAME VARCHAR(80) NOT NULL,
    APP_IDENTIFIER VARCHAR(80),
    VERSION VARCHAR(80),
    APP_TYPE INT,
    CHECKSUM VARCHAR(80),
    DESCRIPTION VARCHAR(80),
    WEB_APP_ID INT NOT NULL,
    SECURITY_REF VARCHAR(40),
    PRIMARY KEY (APPLICATION_ID)
);
GO
CREATE UNIQUE INDEX UK_APPLICATION ON PORTLET_APPLICATION (APP_NAME);
GO
-- ----------------------------------------------------------------------- 
-- WEB_APPLICATION 
-- ----------------------------------------------------------------------- 

CREATE TABLE WEB_APPLICATION
(
    ID INT NOT NULL,
    CONTEXT_ROOT VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- PA_METADATA_FIELDS 
-- ----------------------------------------------------------------------- 

CREATE TABLE PA_METADATA_FIELDS
(
    ID INT NOT NULL,
    OBJECT_ID INT NOT NULL,
    COLUMN_VALUE TEXT NOT NULL,
    NAME VARCHAR(100) NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- PD_METADATA_FIELDS 
-- ----------------------------------------------------------------------- 

CREATE TABLE PD_METADATA_FIELDS
(
    ID INT NOT NULL,
    OBJECT_ID INT NOT NULL,
    COLUMN_VALUE TEXT NOT NULL,
    NAME VARCHAR(100) NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- LANGUAGE 
-- ----------------------------------------------------------------------- 

CREATE TABLE LANGUAGE
(
    ID INT NOT NULL,
    PORTLET_ID INT NOT NULL,
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(100),
    LOCALE_STRING VARCHAR(50) NOT NULL,
    KEYWORDS TEXT,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- PORTLET_CONTENT_TYPE 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_CONTENT_TYPE
(
    CONTENT_TYPE_ID INT NOT NULL,
    PORTLET_ID INT NOT NULL,
    CONTENT_TYPE VARCHAR(30) NOT NULL,
    MODES TEXT,
    PRIMARY KEY (CONTENT_TYPE_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- PARAMETER 
-- ----------------------------------------------------------------------- 

CREATE TABLE PARAMETER
(
    PARAMETER_ID INT NOT NULL,
    PARENT_ID INT NOT NULL,
    CLASS_NAME VARCHAR(255) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    PARAMETER_VALUE TEXT,
    PRIMARY KEY (PARAMETER_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- PORTLET_ENTITY 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_ENTITY
(
    PEID INT NOT NULL,
    ID VARCHAR(80) NOT NULL,
    APP_NAME VARCHAR(80) NOT NULL,
    PORTLET_NAME VARCHAR(80) NOT NULL,
    PRIMARY KEY (PEID)
);
GO
CREATE UNIQUE INDEX UK_ENTITY_ID ON PORTLET_ENTITY (ID);
GO
-- ----------------------------------------------------------------------- 
-- PORTLET_PREFERENCE 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_PREFERENCE
(
    ID INT NOT NULL,
    APPLICATION_NAME VARCHAR(80) NOT NULL,
    PORTLET_NAME VARCHAR(80) NOT NULL,
    NAME VARCHAR(254) NOT NULL,
    PRIMARY KEY (ID)
);
GO
CREATE UNIQUE INDEX UIX_PORTLET_PREFERENCE ON PORTLET_PREFERENCE (APPLICATION_NAME, PORTLET_NAME, NAME);
GO
-- ----------------------------------------------------------------------- 
-- PORTLET_PREFERENCE_VALUE 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_PREFERENCE_VALUE
(
    PREF_ID INT NOT NULL,
    IDX SMALLINT NOT NULL,
    ENTITY_OID INT NOT NULL,
    USER_NAME VARCHAR(80) NOT NULL,
    ENTITY_ID VARCHAR(80),
    READONLY SMALLINT NOT NULL,
    NULL_VALUE SMALLINT NOT NULL,
    PREF_VALUE VARCHAR(4000),
    PRIMARY KEY (PREF_ID, IDX, ENTITY_OID, USER_NAME)
);
GO
CREATE INDEX IX_PREFS_PREF_ID ON PORTLET_PREFERENCE_VALUE (PREF_ID);
GO
-- ----------------------------------------------------------------------- 
-- SECURITY_ROLE_REFERENCE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_ROLE_REFERENCE
(
    ID INT NOT NULL,
    PORTLET_DEFINITION_ID INT NOT NULL,
    ROLE_NAME VARCHAR(150) NOT NULL,
    ROLE_LINK VARCHAR(150),
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- SECURITY_ROLE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_ROLE
(
    ID INT NOT NULL,
    WEB_APPLICATION_ID INT NOT NULL,
    ROLE_NAME VARCHAR(150) NOT NULL,
    DESCRIPTION VARCHAR(150),
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- USER_ATTRIBUTE_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE USER_ATTRIBUTE_REF
(
    ID INT NOT NULL,
    APPLICATION_ID INT NOT NULL,
    NAME VARCHAR(150),
    NAME_LINK VARCHAR(150),
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- USER_ATTRIBUTE 
-- ----------------------------------------------------------------------- 

CREATE TABLE USER_ATTRIBUTE
(
    ID INT NOT NULL,
    APPLICATION_ID INT NOT NULL,
    NAME VARCHAR(150),
    DESCRIPTION VARCHAR(150),
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- JETSPEED_SERVICE 
-- ----------------------------------------------------------------------- 

CREATE TABLE JETSPEED_SERVICE
(
    ID INT NOT NULL,
    APPLICATION_ID INT NOT NULL,
    NAME VARCHAR(150),
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- LOCALIZED_DESCRIPTION 
-- ----------------------------------------------------------------------- 

CREATE TABLE LOCALIZED_DESCRIPTION
(
    ID INT NOT NULL,
    OBJECT_ID INT NOT NULL,
    CLASS_NAME VARCHAR(255) NOT NULL,
    DESCRIPTION TEXT NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- LOCALIZED_DISPLAY_NAME 
-- ----------------------------------------------------------------------- 

CREATE TABLE LOCALIZED_DISPLAY_NAME
(
    ID INT NOT NULL,
    OBJECT_ID INT NOT NULL,
    CLASS_NAME VARCHAR(255),
    DISPLAY_NAME TEXT NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- CUSTOM_PORTLET_MODE 
-- ----------------------------------------------------------------------- 

CREATE TABLE CUSTOM_PORTLET_MODE
(
    ID INT NOT NULL,
    APPLICATION_ID INT NOT NULL,
    CUSTOM_NAME VARCHAR(150) NOT NULL,
    MAPPED_NAME VARCHAR(150),
    DESCRIPTION TEXT,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- CUSTOM_WINDOW_STATE 
-- ----------------------------------------------------------------------- 

CREATE TABLE CUSTOM_WINDOW_STATE
(
    ID INT NOT NULL,
    APPLICATION_ID INT NOT NULL,
    CUSTOM_NAME VARCHAR(150) NOT NULL,
    MAPPED_NAME VARCHAR(150),
    DESCRIPTION TEXT,
    PRIMARY KEY (ID)
);
GO
-- ----------------------------------------------------------------------- 
-- SECURITY_PRINCIPAL 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_PRINCIPAL
(
    PRINCIPAL_ID INT NOT NULL,
    PRINCIPAL_TYPE VARCHAR(20) NOT NULL,
    PRINCIPAL_NAME VARCHAR(200) NOT NULL,
    IS_MAPPED SMALLINT NOT NULL,
    IS_ENABLED SMALLINT NOT NULL,
    IS_READONLY SMALLINT NOT NULL,
    IS_REMOVABLE SMALLINT NOT NULL,
    CREATION_DATE DATETIME NOT NULL,
    MODIFIED_DATE DATETIME NOT NULL,
    PRIMARY KEY (PRINCIPAL_ID)
);
GO
CREATE UNIQUE INDEX UIX_SECURITY_PRINCIPAL ON SECURITY_PRINCIPAL (PRINCIPAL_TYPE, PRINCIPAL_NAME);
GO
-- ----------------------------------------------------------------------- 
-- SECURITY_ATTRIBUTE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_ATTRIBUTE
(
    ATTR_ID INT NOT NULL,
    PRINCIPAL_ID INT NOT NULL,
    ATTR_NAME VARCHAR(200) NOT NULL,
    ATTR_VALUE VARCHAR(1000),
    PRIMARY KEY (ATTR_ID, PRINCIPAL_ID, ATTR_NAME)
);
GO
CREATE INDEX IX_NAME_LOOKUP ON SECURITY_ATTRIBUTE (ATTR_NAME);
GO
-- ----------------------------------------------------------------------- 
-- SECURITY_PRINCIPAL_ASSOC 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_PRINCIPAL_ASSOC
(
    ASSOC_NAME VARCHAR(30) NOT NULL,
    FROM_PRINCIPAL_ID INT NOT NULL,
    TO_PRINCIPAL_ID INT NOT NULL,
    PRIMARY KEY (ASSOC_NAME, FROM_PRINCIPAL_ID, TO_PRINCIPAL_ID)
);
GO
CREATE INDEX IX_TO_PRINCIPAL_ASSOC_LOOKUP ON SECURITY_PRINCIPAL_ASSOC (ASSOC_NAME, TO_PRINCIPAL_ID);
GO
-- ----------------------------------------------------------------------- 
-- SECURITY_PERMISSION 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_PERMISSION
(
    PERMISSION_ID INT NOT NULL,
    PERMISSION_TYPE VARCHAR(30) NOT NULL,
    NAME VARCHAR(254) NOT NULL,
    ACTIONS VARCHAR(254) NOT NULL,
    PRIMARY KEY (PERMISSION_ID)
);
GO
CREATE UNIQUE INDEX UIX_SECURITY_PERMISSION ON SECURITY_PERMISSION (PERMISSION_TYPE, NAME);
GO
-- ----------------------------------------------------------------------- 
-- PRINCIPAL_PERMISSION 
-- ----------------------------------------------------------------------- 

CREATE TABLE PRINCIPAL_PERMISSION
(
    PRINCIPAL_ID INT NOT NULL,
    PERMISSION_ID INT NOT NULL,
    PRIMARY KEY (PRINCIPAL_ID, PERMISSION_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- SECURITY_CREDENTIAL 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_CREDENTIAL
(
    CREDENTIAL_ID INT NOT NULL,
    PRINCIPAL_ID INT NOT NULL,
    CREDENTIAL_VALUE VARCHAR(254),
    TYPE SMALLINT NOT NULL,
    UPDATE_ALLOWED SMALLINT NOT NULL,
    IS_STATE_READONLY SMALLINT NOT NULL,
    UPDATE_REQUIRED SMALLINT NOT NULL,
    IS_ENCODED SMALLINT NOT NULL,
    IS_ENABLED SMALLINT NOT NULL,
    AUTH_FAILURES SMALLINT NOT NULL,
    IS_EXPIRED SMALLINT NOT NULL,
    CREATION_DATE DATETIME NOT NULL,
    MODIFIED_DATE DATETIME NOT NULL,
    PREV_AUTH_DATE DATETIME,
    LAST_AUTH_DATE DATETIME,
    EXPIRATION_DATE DATETIME,
    PRIMARY KEY (CREDENTIAL_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- SSO_SITE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SSO_SITE
(
    SITE_ID INT NOT NULL,
    NAME VARCHAR(254) NOT NULL,
    URL VARCHAR(254) NOT NULL,
    ALLOW_USER_SET SMALLINT DEFAULT 0,
    REQUIRES_CERTIFICATE SMALLINT DEFAULT 0,
    CHALLENGE_RESPONSE_AUTH SMALLINT DEFAULT 0,
    FORM_AUTH SMALLINT DEFAULT 0,
    FORM_USER_FIELD VARCHAR(128),
    FORM_PWD_FIELD VARCHAR(128),
    REALM VARCHAR(128),
    PRIMARY KEY (SITE_ID)
);
GO
CREATE UNIQUE INDEX UIX_SITE_URL ON SSO_SITE (URL);
GO
-- ----------------------------------------------------------------------- 
-- SSO_COOKIE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SSO_COOKIE
(
    COOKIE_ID INT NOT NULL,
    COOKIE VARCHAR(1024) NOT NULL,
    CREATE_DATE DATETIME NOT NULL,
    PRIMARY KEY (COOKIE_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- SSO_SITE_TO_PRINCIPALS 
-- ----------------------------------------------------------------------- 

CREATE TABLE SSO_SITE_TO_PRINCIPALS
(
    SITE_ID INT NOT NULL,
    PRINCIPAL_ID INT NOT NULL,
    PRIMARY KEY (SITE_ID, PRINCIPAL_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- SSO_PRINCIPAL_TO_REMOTE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SSO_PRINCIPAL_TO_REMOTE
(
    PRINCIPAL_ID INT NOT NULL,
    REMOTE_PRINCIPAL_ID INT NOT NULL,
    PRIMARY KEY (PRINCIPAL_ID, REMOTE_PRINCIPAL_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- SSO_SITE_TO_REMOTE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SSO_SITE_TO_REMOTE
(
    SITE_ID INT NOT NULL,
    PRINCIPAL_ID INT NOT NULL,
    PRIMARY KEY (SITE_ID, PRINCIPAL_ID)
);
GO
-- ----------------------------------------------------------------------- 
-- SSO_COOKIE_TO_REMOTE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SSO_COOKIE_TO_REMOTE
(
    COOKIE_ID INT NOT NULL,
    REMOTE_PRINCIPAL_ID INT NOT NULL,
    PRIMARY KEY (COOKIE_ID, REMOTE_PRINCIPAL_ID)
);
GO

/*CREATE TRIGGER trig_PREFS_NODE
  ON PREFS_NODE 
  INSTEAD OF DELETE 
  AS 
  SET NOCOUNT ON;

  WITH cte AS 
  ( SELECT NODE_ID, PARENT_NODE_ID 
    FROM DELETED 
    UNION ALL 
    SELECT c.NODE_ID, c.PARENT_NODE_ID
    FROM PREFS_NODE AS c 
    INNER JOIN cte AS p 
    ON c.PARENT_NODE_ID = p.NODE_ID 
  ) 
  SELECT * 
  into #tmp
  FROM cte
  OPTION (MAXRECURSION 32767)
  
  DELETE FROM PREFS_PROPERTY_VALUE
    WHERE NODE_ID IN (SELECT NODE_ID FROM #tmp);   

  DELETE FROM PREFS_NODE 
    WHERE NODE_ID IN(
      SELECT NODE_ID FROM #TMP)
  drop table #tmp;
*/
GO
CREATE TRIGGER trig_SECURITY_PRINCIPAL
  ON SECURITY_PRINCIPAL
  INSTEAD OF DELETE 
  AS 
  
  SET NOCOUNT ON;

  DELETE FROM SSO_PRINCIPAL_TO_REMOTE
    WHERE REMOTE_PRINCIPAL_ID IN (SELECT PRINCIPAL_ID FROM DELETED)
;
  DELETE FROM SECURITY_USER_ROLE
    WHERE USER_ID IN (SELECT PRINCIPAL_ID FROM DELETED)
;
  DELETE FROM SECURITY_USER_GROUP
    WHERE USER_ID IN (SELECT PRINCIPAL_ID FROM DELETED)
;
  DELETE FROM SECURITY_GROUP_ROLE
    WHERE ROLE_ID IN (SELECT PRINCIPAL_ID FROM DELETED)
;
  DELETE FROM SECURITY_PRINCIPAL
    WHERE PRINCIPAL_ID IN (SELECT PRINCIPAL_ID FROM DELETED)
;

GO

ALTER TABLE FOLDER
    ADD CONSTRAINT FK_FOLDER_1 FOREIGN KEY (PARENT_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE NO ACTION;
GO
ALTER TABLE FOLDER_METADATA
    ADD CONSTRAINT FK_FOLDER_METADATA_1 FOREIGN KEY (FOLDER_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;
GO
ALTER TABLE FOLDER_CONSTRAINT
    ADD CONSTRAINT FK_FOLDER_CONSTRAINT_1 FOREIGN KEY (FOLDER_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;
GO
ALTER TABLE FOLDER_CONSTRAINTS_REF
    ADD CONSTRAINT FK_FOLDER_CONSTRAINTS_REF_1 FOREIGN KEY (FOLDER_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;
GO
ALTER TABLE FOLDER_ORDER
    ADD CONSTRAINT FK_FOLDER_ORDER_1 FOREIGN KEY (FOLDER_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;
GO
ALTER TABLE FOLDER_MENU
    ADD CONSTRAINT FK_FOLDER_MENU_1 FOREIGN KEY (PARENT_ID) REFERENCES FOLDER_MENU (MENU_ID) ON DELETE NO ACTION;
GO
ALTER TABLE FOLDER_MENU
    ADD CONSTRAINT FK_FOLDER_MENU_2 FOREIGN KEY (FOLDER_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;
GO
ALTER TABLE FOLDER_MENU_METADATA
    ADD CONSTRAINT FK_FOLDER_MENU_METADATA_1 FOREIGN KEY (MENU_ID) REFERENCES FOLDER_MENU (MENU_ID) ON DELETE CASCADE;
GO
ALTER TABLE PAGE
    ADD CONSTRAINT FK_PAGE_1 FOREIGN KEY (PARENT_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;
GO
ALTER TABLE PAGE_METADATA
    ADD CONSTRAINT FK_PAGE_METADATA_1 FOREIGN KEY (PAGE_ID) REFERENCES PAGE (PAGE_ID) ON DELETE CASCADE;
GO
ALTER TABLE PAGE_CONSTRAINT
    ADD CONSTRAINT FK_PAGE_CONSTRAINT_1 FOREIGN KEY (PAGE_ID) REFERENCES PAGE (PAGE_ID) ON DELETE CASCADE;
GO
ALTER TABLE PAGE_CONSTRAINTS_REF
    ADD CONSTRAINT FK_PAGE_CONSTRAINTS_REF_1 FOREIGN KEY (PAGE_ID) REFERENCES PAGE (PAGE_ID) ON DELETE CASCADE;
GO
ALTER TABLE PAGE_MENU
    ADD CONSTRAINT FK_PAGE_MENU_1 FOREIGN KEY (PARENT_ID) REFERENCES PAGE_MENU (MENU_ID) ON DELETE NO ACTION;;
GO
ALTER TABLE PAGE_MENU
    ADD CONSTRAINT PM_M_FK_PAGE_ID_PAGE FOREIGN KEY (PAGE_ID) REFERENCES PAGE (PAGE_ID) ON DELETE CASCADE;
GO
ALTER TABLE PAGE_MENU_METADATA
    ADD CONSTRAINT FK_PAGE_MENU_METADATA_1 FOREIGN KEY (MENU_ID) REFERENCES PAGE_MENU (MENU_ID) ON DELETE CASCADE;
GO
ALTER TABLE FRAGMENT
    ADD CONSTRAINT FK_FRAGMENT_1 FOREIGN KEY (PARENT_ID) REFERENCES FRAGMENT (FRAGMENT_ID) ON DELETE NO ACTION;
GO
ALTER TABLE FRAGMENT
    ADD CONSTRAINT FK_FRAGMENT_2 FOREIGN KEY (PAGE_ID) REFERENCES PAGE (PAGE_ID) ON DELETE CASCADE;
GO
ALTER TABLE FRAGMENT_CONSTRAINT
    ADD CONSTRAINT FK_FRAGMENT_CONSTRAINT_1 FOREIGN KEY (FRAGMENT_ID) REFERENCES FRAGMENT (FRAGMENT_ID) ON DELETE CASCADE;
GO
ALTER TABLE FRAGMENT_CONSTRAINTS_REF
    ADD CONSTRAINT FK_FRAGMENT_CONSTRAINTS_REF_1 FOREIGN KEY (FRAGMENT_ID) REFERENCES FRAGMENT (FRAGMENT_ID) ON DELETE CASCADE;
GO
ALTER TABLE FRAGMENT_PREF
    ADD CONSTRAINT FK_FRAGMENT_PREF_1 FOREIGN KEY (FRAGMENT_ID) REFERENCES FRAGMENT (FRAGMENT_ID) ON DELETE CASCADE;
GO
ALTER TABLE FRAGMENT_PREF_VALUE
    ADD CONSTRAINT FK_FRAGMENT_PREF_VALUE_1 FOREIGN KEY (PREF_ID) REFERENCES FRAGMENT_PREF (PREF_ID) ON DELETE CASCADE;
GO
ALTER TABLE LINK
    ADD CONSTRAINT FK_LINK_1 FOREIGN KEY (PARENT_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;
GO
ALTER TABLE LINK_METADATA
    ADD CONSTRAINT FK_LINK_METADATA_1 FOREIGN KEY (LINK_ID) REFERENCES LINK (LINK_ID) ON DELETE CASCADE;
GO
ALTER TABLE LINK_CONSTRAINT
    ADD CONSTRAINT FK_LINK_CONSTRAINT_1 FOREIGN KEY (LINK_ID) REFERENCES LINK (LINK_ID) ON DELETE CASCADE;
GO
ALTER TABLE LINK_CONSTRAINTS_REF
    ADD CONSTRAINT FK_LINK_CONSTRAINTS_REF_1 FOREIGN KEY (LINK_ID) REFERENCES LINK (LINK_ID) ON DELETE CASCADE;
GO
ALTER TABLE PAGE_SECURITY
    ADD CONSTRAINT FK_PAGE_SECURITY_1 FOREIGN KEY (PARENT_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;
GO
ALTER TABLE PAGE_SEC_CONSTRAINTS_DEF
    ADD CONSTRAINT FK_PAGE_SEC_CONSTRAINTS_DEF_1 FOREIGN KEY (PAGE_SECURITY_ID) REFERENCES PAGE_SECURITY (PAGE_SECURITY_ID) ON DELETE CASCADE;
GO
ALTER TABLE PAGE_SEC_CONSTRAINT_DEF
    ADD CONSTRAINT FK_PAGE_SEC_CONSTRAINT_DEF_1 FOREIGN KEY (CONSTRAINTS_DEF_ID) REFERENCES PAGE_SEC_CONSTRAINTS_DEF (CONSTRAINTS_DEF_ID) ON DELETE CASCADE;
GO
ALTER TABLE PAGE_SEC_CONSTRAINTS_REF
    ADD CONSTRAINT FK_PAGE_SEC_CONSTRAINTS_REF_1 FOREIGN KEY (PAGE_SECURITY_ID) REFERENCES PAGE_SECURITY (PAGE_SECURITY_ID) ON DELETE CASCADE;
GO
ALTER TABLE RULE_CRITERION
    ADD CONSTRAINT FK_RULE_CRITERION_1 FOREIGN KEY (RULE_ID) REFERENCES PROFILING_RULE (RULE_ID) ON DELETE CASCADE;
GO
ALTER TABLE PA_METADATA_FIELDS
    ADD CONSTRAINT FK_PA_METADATA_FIELDS_1 FOREIGN KEY (OBJECT_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;
GO
ALTER TABLE PD_METADATA_FIELDS
    ADD CONSTRAINT FK_PD_METADATA_FIELDS_1 FOREIGN KEY (OBJECT_ID) REFERENCES PORTLET_DEFINITION (ID) ON DELETE CASCADE;
GO
ALTER TABLE PORTLET_PREFERENCE_VALUE
    ADD CONSTRAINT FK_PORTLET_PREFERENCE FOREIGN KEY (PREF_ID) REFERENCES PORTLET_PREFERENCE (ID) ON DELETE CASCADE;
GO
ALTER TABLE USER_ATTRIBUTE_REF
    ADD CONSTRAINT FK_USER_ATTRIBUTE_REF_1 FOREIGN KEY (APPLICATION_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;
GO
ALTER TABLE USER_ATTRIBUTE
    ADD CONSTRAINT FK_USER_ATTRIBUTE_1 FOREIGN KEY (APPLICATION_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;
GO
ALTER TABLE CUSTOM_PORTLET_MODE
    ADD CONSTRAINT FK_CUSTOM_PORTLET_MODE_1 FOREIGN KEY (APPLICATION_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;
GO
ALTER TABLE CUSTOM_WINDOW_STATE
    ADD CONSTRAINT FK_CUSTOM_WINDOW_STATE_1 FOREIGN KEY (APPLICATION_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;
GO
ALTER TABLE SECURITY_ATTRIBUTE
    ADD CONSTRAINT FK_PRINCIPAL_ATTR FOREIGN KEY (PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;
GO
ALTER TABLE SECURITY_PRINCIPAL_ASSOC
    ADD CONSTRAINT FK_FROM_PRINCIPAL_ASSOC FOREIGN KEY (FROM_PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;
GO
ALTER TABLE SECURITY_PRINCIPAL_ASSOC
    ADD CONSTRAINT FK_TO_PRINCIPAL_ASSOC FOREIGN KEY (TO_PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE NO ACTION;
GO
ALTER TABLE PRINCIPAL_PERMISSION
    ADD CONSTRAINT FK_PRINCIPAL_PERMISSION_1 FOREIGN KEY (PERMISSION_ID) REFERENCES SECURITY_PERMISSION (PERMISSION_ID) ON DELETE CASCADE;
GO
ALTER TABLE PRINCIPAL_PERMISSION
    ADD CONSTRAINT FK_PRINCIPAL_PERMISSION_2 FOREIGN KEY (PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;
GO
ALTER TABLE SECURITY_CREDENTIAL
    ADD CONSTRAINT FK_SECURITY_CREDENTIAL_1 FOREIGN KEY (PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;
GO
ALTER TABLE SSO_SITE_TO_PRINCIPALS
    ADD CONSTRAINT SSO_SITE_TO_PRINC_FK1 FOREIGN KEY (SITE_ID) REFERENCES SSO_SITE (SITE_ID) ON DELETE CASCADE;
GO
ALTER TABLE SSO_SITE_TO_PRINCIPALS
    ADD CONSTRAINT SSO_SITE_TO_PRINC_FK2 FOREIGN KEY (PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;
GO
ALTER TABLE SSO_PRINCIPAL_TO_REMOTE
    ADD CONSTRAINT FK_SSO_PRINCIPAL_TO_REMOTE_1 FOREIGN KEY (PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;
GO
ALTER TABLE SSO_PRINCIPAL_TO_REMOTE
    ADD CONSTRAINT FK_SSO_PRINCIPAL_TO_REMOTE_2 FOREIGN KEY (REMOTE_PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE NO ACTION;
GO
ALTER TABLE SSO_SITE_TO_REMOTE
    ADD CONSTRAINT FK_SSO_SITE_TO_REMOTE_1 FOREIGN KEY (SITE_ID) REFERENCES SSO_SITE (SITE_ID) ON DELETE CASCADE;
GO
ALTER TABLE SSO_SITE_TO_REMOTE
    ADD CONSTRAINT FK_SSO_SITE_TO_REMOTE_2 FOREIGN KEY (PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;
GO
ALTER TABLE SSO_COOKIE_TO_REMOTE
    ADD CONSTRAINT FK_SSO_COOKIE_TO_REMOTE_1 FOREIGN KEY (COOKIE_ID) REFERENCES SSO_COOKIE (COOKIE_ID) ON DELETE CASCADE;
GO
ALTER TABLE SSO_COOKIE_TO_REMOTE
    ADD CONSTRAINT FK_SSO_COOKIE_TO_REMOTE_2 FOREIGN KEY (REMOTE_PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;
GO
