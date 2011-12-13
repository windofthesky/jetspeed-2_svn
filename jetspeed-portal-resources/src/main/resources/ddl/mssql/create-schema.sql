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
    MEDIATYPE_ID INTEGER NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    CHARACTER_SET VARCHAR(40),
    TITLE VARCHAR(80),
    DESCRIPTION TEXT,
    PRIMARY KEY (MEDIATYPE_ID)
);

-- ----------------------------------------------------------------------- 
-- CLIENT 
-- ----------------------------------------------------------------------- 

CREATE TABLE CLIENT
(
    CLIENT_ID INTEGER NOT NULL,
    EVAL_ORDER INTEGER NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    USER_AGENT_PATTERN VARCHAR(128),
    MANUFACTURER VARCHAR(80),
    MODEL VARCHAR(80),
    VERSION VARCHAR(40),
    PREFERRED_MIMETYPE_ID INTEGER NOT NULL,
    PRIMARY KEY (CLIENT_ID)
);

-- ----------------------------------------------------------------------- 
-- MIMETYPE 
-- ----------------------------------------------------------------------- 

CREATE TABLE MIMETYPE
(
    MIMETYPE_ID INTEGER NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    PRIMARY KEY (MIMETYPE_ID)
);

-- ----------------------------------------------------------------------- 
-- CAPABILITY 
-- ----------------------------------------------------------------------- 

CREATE TABLE CAPABILITY
(
    CAPABILITY_ID INTEGER NOT NULL,
    CAPABILITY VARCHAR(80) NOT NULL,
    PRIMARY KEY (CAPABILITY_ID)
);

-- ----------------------------------------------------------------------- 
-- CLIENT_TO_CAPABILITY 
-- ----------------------------------------------------------------------- 

CREATE TABLE CLIENT_TO_CAPABILITY
(
    CLIENT_ID INTEGER NOT NULL,
    CAPABILITY_ID INTEGER NOT NULL
);

-- ----------------------------------------------------------------------- 
-- CLIENT_TO_MIMETYPE 
-- ----------------------------------------------------------------------- 

CREATE TABLE CLIENT_TO_MIMETYPE
(
    CLIENT_ID INTEGER NOT NULL,
    MIMETYPE_ID INTEGER NOT NULL
);

-- ----------------------------------------------------------------------- 
-- MEDIATYPE_TO_CAPABILITY 
-- ----------------------------------------------------------------------- 

CREATE TABLE MEDIATYPE_TO_CAPABILITY
(
    MEDIATYPE_ID INTEGER NOT NULL,
    CAPABILITY_ID INTEGER NOT NULL
);

-- ----------------------------------------------------------------------- 
-- MEDIATYPE_TO_MIMETYPE 
-- ----------------------------------------------------------------------- 

CREATE TABLE MEDIATYPE_TO_MIMETYPE
(
    MEDIATYPE_ID INTEGER NOT NULL,
    MIMETYPE_ID INTEGER NOT NULL
);

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
    STATUS INTEGER,
    ELAPSED_TIME BIGINT
);

-- ----------------------------------------------------------------------- 
-- PAGE_STATISTICS 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_STATISTICS
(
    IPADDRESS VARCHAR(80),
    USER_NAME VARCHAR(80),
    TIME_STAMP DATETIME,
    PAGE VARCHAR(80),
    STATUS INTEGER,
    ELAPSED_TIME BIGINT
);

-- ----------------------------------------------------------------------- 
-- USER_STATISTICS 
-- ----------------------------------------------------------------------- 

CREATE TABLE USER_STATISTICS
(
    IPADDRESS VARCHAR(80),
    USER_NAME VARCHAR(80),
    TIME_STAMP DATETIME,
    STATUS INTEGER,
    ELAPSED_TIME BIGINT
);

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
    ATTR_NAME VARCHAR(200),
    ATTR_VALUE_BEFORE VARCHAR(1000),
    ATTR_VALUE_AFTER VARCHAR(1000),
    DESCRIPTION VARCHAR(128)
);

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
    ATTR_NAME VARCHAR(200),
    ATTR_VALUE_BEFORE VARCHAR(1000),
    ATTR_VALUE_AFTER VARCHAR(1000),
    DESCRIPTION VARCHAR(128)
);

-- ----------------------------------------------------------------------- 
-- FOLDER 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER
(
    FOLDER_ID INTEGER NOT NULL,
    PARENT_ID INTEGER,
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

CREATE INDEX IX_FOLDER_1 ON FOLDER (PARENT_ID);

CREATE UNIQUE INDEX UN_FOLDER_1 ON FOLDER (PATH);

-- ----------------------------------------------------------------------- 
-- FOLDER_METADATA 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_METADATA
(
    METADATA_ID INTEGER NOT NULL,
    FOLDER_ID INTEGER NOT NULL,
    NAME VARCHAR(15) NOT NULL,
    LOCALE VARCHAR(20) NULL,
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (METADATA_ID)
);

CREATE INDEX IX_FOLDER_METADATA_1 ON FOLDER_METADATA (FOLDER_ID);

CREATE UNIQUE INDEX UN_FOLDER_METADATA_1 ON FOLDER_METADATA (FOLDER_ID, NAME, LOCALE, VALUE);

-- ----------------------------------------------------------------------- 
-- FOLDER_CONSTRAINT 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_CONSTRAINT
(
    CONSTRAINT_ID INTEGER NOT NULL,
    FOLDER_ID INTEGER NOT NULL,
    APPLY_ORDER INTEGER NOT NULL,
    USER_PRINCIPALS_ACL VARCHAR(120),
    ROLE_PRINCIPALS_ACL VARCHAR(120),
    GROUP_PRINCIPALS_ACL VARCHAR(120),
    PERMISSIONS_ACL VARCHAR(120),
    PRIMARY KEY (CONSTRAINT_ID)
);

CREATE INDEX IX_FOLDER_CONSTRAINT_1 ON FOLDER_CONSTRAINT (FOLDER_ID);

-- ----------------------------------------------------------------------- 
-- FOLDER_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID INTEGER NOT NULL,
    FOLDER_ID INTEGER NOT NULL,
    APPLY_ORDER INTEGER NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_REF_ID)
);

CREATE INDEX IX_FOLDER_CONSTRAINTS_REF_1 ON FOLDER_CONSTRAINTS_REF (FOLDER_ID);

CREATE UNIQUE INDEX UN_FOLDER_CONSTRAINTS_REF_1 ON FOLDER_CONSTRAINTS_REF (FOLDER_ID, NAME);

-- ----------------------------------------------------------------------- 
-- FOLDER_ORDER 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_ORDER
(
    ORDER_ID INTEGER NOT NULL,
    FOLDER_ID INTEGER NOT NULL,
    SORT_ORDER INTEGER NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    PRIMARY KEY (ORDER_ID)
);

CREATE INDEX IX_FOLDER_ORDER_1 ON FOLDER_ORDER (FOLDER_ID);

CREATE UNIQUE INDEX UN_FOLDER_ORDER_1 ON FOLDER_ORDER (FOLDER_ID, NAME);

-- ----------------------------------------------------------------------- 
-- FOLDER_MENU 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_MENU
(
    MENU_ID INTEGER NOT NULL,
    CLASS_NAME VARCHAR(100) NOT NULL,
    PARENT_ID INTEGER,
    FOLDER_ID INTEGER,
    ELEMENT_ORDER INTEGER,
    NAME VARCHAR(100),
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(40),
    TEXT VARCHAR(100),
    OPTIONS VARCHAR(255),
    DEPTH INTEGER,
    IS_PATHS SMALLINT,
    IS_REGEXP SMALLINT,
    PROFILE VARCHAR(80),
    OPTIONS_ORDER VARCHAR(255),
    SKIN VARCHAR(80),
    IS_NEST SMALLINT,
    PRIMARY KEY (MENU_ID)
);

CREATE INDEX IX_FOLDER_MENU_1 ON FOLDER_MENU (PARENT_ID);

CREATE INDEX UN_FOLDER_MENU_1 ON FOLDER_MENU (FOLDER_ID, NAME);

-- ----------------------------------------------------------------------- 
-- FOLDER_MENU_METADATA 
-- ----------------------------------------------------------------------- 

CREATE TABLE FOLDER_MENU_METADATA
(
    METADATA_ID INTEGER NOT NULL,
    MENU_ID INTEGER NOT NULL,
    NAME VARCHAR(15) NOT NULL,
    LOCALE VARCHAR(20) NULL,
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (METADATA_ID)
);

CREATE INDEX IX_FOLDER_MENU_METADATA_1 ON FOLDER_MENU_METADATA (MENU_ID);

CREATE UNIQUE INDEX UN_FOLDER_MENU_METADATA_1 ON FOLDER_MENU_METADATA (MENU_ID, NAME, LOCALE, VALUE);

-- ----------------------------------------------------------------------- 
-- PAGE 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE
(
    PAGE_ID INTEGER NOT NULL,
    CLASS_NAME VARCHAR(100) NOT NULL,
    PARENT_ID INTEGER NOT NULL,
    PATH VARCHAR(240) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    CONTENT_TYPE VARCHAR(4),
    VERSION VARCHAR(40),
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(40),
    IS_HIDDEN SMALLINT,
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

CREATE INDEX IX_PAGE_1 ON PAGE (PARENT_ID);

CREATE UNIQUE INDEX UN_PAGE_1 ON PAGE (PATH);

-- ----------------------------------------------------------------------- 
-- PAGE_METADATA 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_METADATA
(
    METADATA_ID INTEGER NOT NULL,
    PAGE_ID INTEGER NOT NULL,
    NAME VARCHAR(15) NOT NULL,
    LOCALE VARCHAR(20),
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (METADATA_ID)
);

CREATE INDEX IX_PAGE_METADATA_1 ON PAGE_METADATA (PAGE_ID);

CREATE UNIQUE INDEX UN_PAGE_METADATA_1 ON PAGE_METADATA (PAGE_ID, NAME, LOCALE, VALUE);

-- ----------------------------------------------------------------------- 
-- PAGE_CONSTRAINT 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_CONSTRAINT
(
    CONSTRAINT_ID INTEGER NOT NULL,
    PAGE_ID INTEGER NOT NULL,
    APPLY_ORDER INTEGER NOT NULL,
    USER_PRINCIPALS_ACL VARCHAR(120),
    ROLE_PRINCIPALS_ACL VARCHAR(120),
    GROUP_PRINCIPALS_ACL VARCHAR(120),
    PERMISSIONS_ACL VARCHAR(120),
    PRIMARY KEY (CONSTRAINT_ID)
);

CREATE INDEX IX_PAGE_CONSTRAINT_1 ON PAGE_CONSTRAINT (PAGE_ID);

-- ----------------------------------------------------------------------- 
-- PAGE_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID INTEGER NOT NULL,
    PAGE_ID INTEGER NOT NULL,
    APPLY_ORDER INTEGER NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_REF_ID)
);

CREATE INDEX IX_PAGE_CONSTRAINTS_REF_1 ON PAGE_CONSTRAINTS_REF (PAGE_ID);

CREATE UNIQUE INDEX UN_PAGE_CONSTRAINTS_REF_1 ON PAGE_CONSTRAINTS_REF (PAGE_ID, NAME);

-- ----------------------------------------------------------------------- 
-- PAGE_MENU 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_MENU
(
    MENU_ID INTEGER NOT NULL,
    CLASS_NAME VARCHAR(100) NOT NULL,
    PARENT_ID INTEGER,
    PAGE_ID INTEGER,
    ELEMENT_ORDER INTEGER,
    NAME VARCHAR(100),
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(40),
    TEXT VARCHAR(100),
    OPTIONS VARCHAR(255),
    DEPTH INTEGER,
    IS_PATHS SMALLINT,
    IS_REGEXP SMALLINT,
    PROFILE VARCHAR(80),
    OPTIONS_ORDER VARCHAR(255),
    SKIN VARCHAR(80),
    IS_NEST SMALLINT,
    PRIMARY KEY (MENU_ID)
);

CREATE INDEX IX_PAGE_MENU_1 ON PAGE_MENU (PARENT_ID);

CREATE INDEX UN_PAGE_MENU_1 ON PAGE_MENU (PAGE_ID, NAME);

-- ----------------------------------------------------------------------- 
-- PAGE_MENU_METADATA 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_MENU_METADATA
(
    METADATA_ID INTEGER NOT NULL,
    MENU_ID INTEGER NOT NULL,
    NAME VARCHAR(15) NOT NULL,
    LOCALE VARCHAR(20),
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (METADATA_ID)
);

CREATE INDEX IX_PAGE_MENU_METADATA_1 ON PAGE_MENU_METADATA (MENU_ID);

CREATE UNIQUE INDEX UN_PAGE_MENU_METADATA_1 ON PAGE_MENU_METADATA (MENU_ID, NAME, LOCALE, VALUE);

-- ----------------------------------------------------------------------- 
-- FRAGMENT 
-- ----------------------------------------------------------------------- 

CREATE TABLE FRAGMENT
(
    FRAGMENT_ID INTEGER NOT NULL,
    CLASS_NAME VARCHAR(100) NOT NULL,
    PARENT_ID INTEGER,
    PAGE_ID INTEGER,
    FRAGMENT_STRING_ID VARCHAR(80),
    FRAGMENT_STRING_REFID VARCHAR(80),
    NAME VARCHAR(100),
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(40),
    TYPE VARCHAR(40),
    SKIN VARCHAR(80),
    DECORATOR VARCHAR(80),
    STATE VARCHAR(10),
    PMODE VARCHAR(10),
    LAYOUT_ROW INTEGER,
    LAYOUT_COLUMN INTEGER,
    LAYOUT_SIZES VARCHAR(20),
    LAYOUT_X REAL,
    LAYOUT_Y REAL,
    LAYOUT_Z REAL,
    LAYOUT_WIDTH REAL,
    LAYOUT_HEIGHT REAL,
    OWNER_PRINCIPAL VARCHAR(40),
    PRIMARY KEY (FRAGMENT_ID)
);

CREATE INDEX IX_FRAGMENT_1 ON FRAGMENT (PARENT_ID);

CREATE INDEX UN_FRAGMENT_1 ON FRAGMENT (PAGE_ID);

CREATE INDEX IX_FRAGMENT_2 ON FRAGMENT (FRAGMENT_STRING_REFID);

CREATE INDEX IX_FRAGMENT_3 ON FRAGMENT (FRAGMENT_STRING_ID);

-- ----------------------------------------------------------------------- 
-- FRAGMENT_CONSTRAINT 
-- ----------------------------------------------------------------------- 

CREATE TABLE FRAGMENT_CONSTRAINT
(
    CONSTRAINT_ID INTEGER NOT NULL,
    FRAGMENT_ID INTEGER NOT NULL,
    APPLY_ORDER INTEGER NOT NULL,
    USER_PRINCIPALS_ACL VARCHAR(120),
    ROLE_PRINCIPALS_ACL VARCHAR(120),
    GROUP_PRINCIPALS_ACL VARCHAR(120),
    PERMISSIONS_ACL VARCHAR(120),
    PRIMARY KEY (CONSTRAINT_ID)
);

CREATE INDEX IX_FRAGMENT_CONSTRAINT_1 ON FRAGMENT_CONSTRAINT (FRAGMENT_ID);

-- ----------------------------------------------------------------------- 
-- FRAGMENT_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE FRAGMENT_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID INTEGER NOT NULL,
    FRAGMENT_ID INTEGER NOT NULL,
    APPLY_ORDER INTEGER NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_REF_ID)
);

CREATE INDEX IX_FRAGMENT_CONSTRAINTS_REF_1 ON FRAGMENT_CONSTRAINTS_REF (FRAGMENT_ID);

CREATE UNIQUE INDEX UN_FRAGMENT_CONSTRAINTS_REF_1 ON FRAGMENT_CONSTRAINTS_REF (FRAGMENT_ID, NAME);

-- ----------------------------------------------------------------------- 
-- FRAGMENT_PREF 
-- ----------------------------------------------------------------------- 

CREATE TABLE FRAGMENT_PREF
(
    PREF_ID INTEGER NOT NULL,
    FRAGMENT_ID INTEGER NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    IS_READ_ONLY SMALLINT NOT NULL,
    PRIMARY KEY (PREF_ID)
);

CREATE INDEX IX_FRAGMENT_PREF_1 ON FRAGMENT_PREF (FRAGMENT_ID);

CREATE UNIQUE INDEX UN_FRAGMENT_PREF_1 ON FRAGMENT_PREF (FRAGMENT_ID, NAME);

-- ----------------------------------------------------------------------- 
-- FRAGMENT_PREF_VALUE 
-- ----------------------------------------------------------------------- 

CREATE TABLE FRAGMENT_PREF_VALUE
(
    PREF_VALUE_ID INTEGER NOT NULL,
    PREF_ID INTEGER NOT NULL,
    VALUE_ORDER INTEGER NOT NULL,
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (PREF_VALUE_ID)
);

CREATE INDEX IX_FRAGMENT_PREF_VALUE_1 ON FRAGMENT_PREF_VALUE (PREF_ID);

-- ----------------------------------------------------------------------- 
-- FRAGMENT_PROP 
-- ----------------------------------------------------------------------- 

CREATE TABLE FRAGMENT_PROP
(
    PROP_ID INTEGER NOT NULL,
    FRAGMENT_ID INTEGER NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    SCOPE VARCHAR(10),
    SCOPE_VALUE VARCHAR(40),
    VALUE VARCHAR(100) NOT NULL,        
    PRIMARY KEY (PROP_ID)
);

CREATE INDEX IX_FRAGMENT_PROP_1 ON FRAGMENT_PROP (FRAGMENT_ID);

CREATE UNIQUE INDEX UN_FRAGMENT_PROP ON FRAGMENT_PROP (FRAGMENT_ID, NAME,SCOPE,SCOPE_VALUE);

-- ----------------------------------------------------------------------- 
-- LINK 
-- ----------------------------------------------------------------------- 

CREATE TABLE LINK
(
    LINK_ID INTEGER NOT NULL,
    PARENT_ID INTEGER NOT NULL,
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

CREATE INDEX IX_LINK_1 ON LINK (PARENT_ID);

CREATE UNIQUE INDEX UN_LINK_1 ON LINK (PATH);

-- ----------------------------------------------------------------------- 
-- LINK_METADATA 
-- ----------------------------------------------------------------------- 

CREATE TABLE LINK_METADATA
(
    METADATA_ID INTEGER NOT NULL,
    LINK_ID INTEGER NOT NULL,
    NAME VARCHAR(15) NOT NULL,
    LOCALE VARCHAR(20),
    VALUE VARCHAR(100) NOT NULL,
    PRIMARY KEY (METADATA_ID)
);

CREATE INDEX IX_LINK_METADATA_1 ON LINK_METADATA (LINK_ID);

CREATE UNIQUE INDEX UN_LINK_METADATA_1 ON LINK_METADATA (LINK_ID, NAME, LOCALE, VALUE);

-- ----------------------------------------------------------------------- 
-- LINK_CONSTRAINT 
-- ----------------------------------------------------------------------- 

CREATE TABLE LINK_CONSTRAINT
(
    CONSTRAINT_ID INTEGER NOT NULL,
    LINK_ID INTEGER NOT NULL,
    APPLY_ORDER INTEGER NOT NULL,
    USER_PRINCIPALS_ACL VARCHAR(120),
    ROLE_PRINCIPALS_ACL VARCHAR(120),
    GROUP_PRINCIPALS_ACL VARCHAR(120),
    PERMISSIONS_ACL VARCHAR(120),
    PRIMARY KEY (CONSTRAINT_ID)
);

CREATE INDEX IX_LINK_CONSTRAINT_1 ON LINK_CONSTRAINT (LINK_ID);

-- ----------------------------------------------------------------------- 
-- LINK_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE LINK_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID INTEGER NOT NULL,
    LINK_ID INTEGER NOT NULL,
    APPLY_ORDER INTEGER NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_REF_ID)
);

CREATE INDEX IX_LINK_CONSTRAINTS_REF_1 ON LINK_CONSTRAINTS_REF (LINK_ID);

CREATE UNIQUE INDEX UN_LINK_CONSTRAINTS_REF_1 ON LINK_CONSTRAINTS_REF (LINK_ID, NAME);

-- ----------------------------------------------------------------------- 
-- PAGE_SECURITY 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_SECURITY
(
    PAGE_SECURITY_ID INTEGER NOT NULL,
    PARENT_ID INTEGER NOT NULL,
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

CREATE UNIQUE INDEX UN_PAGE_SECURITY_1 ON PAGE_SECURITY (PARENT_ID);

CREATE UNIQUE INDEX UN_PAGE_SECURITY_2 ON PAGE_SECURITY (PATH);

-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINTS_DEF 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_SEC_CONSTRAINTS_DEF
(
    CONSTRAINTS_DEF_ID INTEGER NOT NULL,
    PAGE_SECURITY_ID INTEGER NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_DEF_ID)
);

CREATE INDEX IX_PAGE_SEC_CONSTRAINTS_DEF_1 ON PAGE_SEC_CONSTRAINTS_DEF (PAGE_SECURITY_ID);

CREATE UNIQUE INDEX UN_PAGE_SEC_CONSTRAINTS_DEF_1 ON PAGE_SEC_CONSTRAINTS_DEF (PAGE_SECURITY_ID, NAME);

-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINT_DEF 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_SEC_CONSTRAINT_DEF
(
    CONSTRAINT_DEF_ID INTEGER NOT NULL,
    CONSTRAINTS_DEF_ID INTEGER NOT NULL,
    APPLY_ORDER INTEGER NOT NULL,
    USER_PRINCIPALS_ACL VARCHAR(120),
    ROLE_PRINCIPALS_ACL VARCHAR(120),
    GROUP_PRINCIPALS_ACL VARCHAR(120),
    PERMISSIONS_ACL VARCHAR(120),
    PRIMARY KEY (CONSTRAINT_DEF_ID)
);

CREATE INDEX IX_PAGE_SEC_CONSTRAINT_DEF_1 ON PAGE_SEC_CONSTRAINT_DEF (CONSTRAINTS_DEF_ID);

-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE PAGE_SEC_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID INTEGER NOT NULL,
    PAGE_SECURITY_ID INTEGER NOT NULL,
    APPLY_ORDER INTEGER NOT NULL,
    NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (CONSTRAINTS_REF_ID)
);

CREATE INDEX IX_PAGE_SEC_CONSTRAINTS_REF_1 ON PAGE_SEC_CONSTRAINTS_REF (PAGE_SECURITY_ID);

CREATE UNIQUE INDEX UN_PAGE_SEC_CONSTRAINTS_REF_1 ON PAGE_SEC_CONSTRAINTS_REF (PAGE_SECURITY_ID, NAME);

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

-- ----------------------------------------------------------------------- 
-- RULE_CRITERION 
-- ----------------------------------------------------------------------- 

CREATE TABLE RULE_CRITERION
(
    CRITERION_ID VARCHAR(80) NOT NULL,
    RULE_ID VARCHAR(80) NOT NULL,
    FALLBACK_ORDER INTEGER NOT NULL,
    REQUEST_TYPE VARCHAR(40) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    COLUMN_VALUE VARCHAR(128),
    FALLBACK_TYPE INTEGER DEFAULT 1,
    PRIMARY KEY (CRITERION_ID)
);

CREATE INDEX IX_RULE_CRITERION_1 ON RULE_CRITERION (RULE_ID, FALLBACK_ORDER);

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

-- ----------------------------------------------------------------------- 
-- PROFILE_PAGE_ASSOC 
-- ----------------------------------------------------------------------- 

CREATE TABLE PROFILE_PAGE_ASSOC
(
    LOCATOR_HASH VARCHAR(40) NOT NULL,
    PAGE_ID VARCHAR(80) NOT NULL
);

CREATE UNIQUE INDEX UN_PROFILE_PAGE_1 ON PROFILE_PAGE_ASSOC (LOCATOR_HASH, PAGE_ID);

-- ----------------------------------------------------------------------- 
-- CLUBS 
-- ----------------------------------------------------------------------- 

CREATE TABLE CLUBS
(
    NAME VARCHAR(80) NOT NULL,
    COUNTRY VARCHAR(40) NOT NULL,
    CITY VARCHAR(40) NOT NULL,
    STADIUM VARCHAR(80) NOT NULL,
    CAPACITY INTEGER,
    FOUNDED INTEGER,
    PITCH VARCHAR(40),
    NICKNAME VARCHAR(40),
    PRIMARY KEY (NAME)
);

-- ----------------------------------------------------------------------- 
-- OJB_HL_SEQ 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_HL_SEQ
(
    TABLENAME VARCHAR(175) NOT NULL,
    FIELDNAME VARCHAR(70) NOT NULL,
    MAX_KEY INTEGER,
    GRAB_SIZE INTEGER,
    VERSION INTEGER,
    PRIMARY KEY (TABLENAME, FIELDNAME)
);

-- ----------------------------------------------------------------------- 
-- OJB_LOCKENTRY 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_LOCKENTRY
(
    OID_ VARCHAR(250) NOT NULL,
    TX_ID VARCHAR(50) NOT NULL,
    TIMESTAMP_ DATETIME,
    ISOLATIONLEVEL INTEGER,
    LOCKTYPE INTEGER,
    PRIMARY KEY (OID_, TX_ID)
);

-- ----------------------------------------------------------------------- 
-- OJB_NRM 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_NRM
(
    NAME VARCHAR(250) NOT NULL,
    OID_ IMAGE,
    PRIMARY KEY (NAME)
);

-- ----------------------------------------------------------------------- 
-- OJB_DLIST 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_DLIST
(
    ID INTEGER NOT NULL,
    SIZE_ INTEGER,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- OJB_DLIST_ENTRIES 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_DLIST_ENTRIES
(
    ID INTEGER NOT NULL,
    DLIST_ID INTEGER,
    POSITION_ INTEGER,
    OID_ IMAGE,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- OJB_DSET 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_DSET
(
    ID INTEGER NOT NULL,
    SIZE_ INTEGER,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- OJB_DSET_ENTRIES 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_DSET_ENTRIES
(
    ID INTEGER NOT NULL,
    DLIST_ID INTEGER,
    POSITION_ INTEGER,
    OID_ IMAGE,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- OJB_DMAP 
-- ----------------------------------------------------------------------- 

CREATE TABLE OJB_DMAP
(
    ID INTEGER NOT NULL,
    SIZE_ INTEGER,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- PORTLET_DEFINITION 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_DEFINITION
(
    ID INTEGER NOT NULL,
    NAME VARCHAR(80),
    CLASS_NAME VARCHAR(255),
    APPLICATION_ID INTEGER NOT NULL,
    EXPIRATION_CACHE INTEGER,
    RESOURCE_BUNDLE VARCHAR(255),
    PREFERENCE_VALIDATOR VARCHAR(255),
    SECURITY_REF VARCHAR(40),
    CLONE_PARENT VARCHAR(80),
    CACHE_SCOPE VARCHAR(30),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- PORTLET_APPLICATION 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_APPLICATION
(
    APPLICATION_ID INTEGER NOT NULL,
    APP_NAME VARCHAR(80) NOT NULL,
    CONTEXT_PATH VARCHAR(255) NOT NULL,
    REVISION INTEGER NOT NULL,
    VERSION VARCHAR(80),
    APP_TYPE INTEGER,
    CHECKSUM VARCHAR(80),
    SECURITY_REF VARCHAR(40),
    DEFAULT_NAMESPACE VARCHAR(120),
    RESOURCE_BUNDLE VARCHAR(255),
    PRIMARY KEY (APPLICATION_ID)
);

CREATE UNIQUE INDEX UK_APPLICATION ON PORTLET_APPLICATION (APP_NAME);

-- ----------------------------------------------------------------------- 
-- PA_METADATA_FIELDS 
-- ----------------------------------------------------------------------- 

CREATE TABLE PA_METADATA_FIELDS
(
    ID INTEGER NOT NULL,
    OBJECT_ID INTEGER NOT NULL,
    COLUMN_VALUE TEXT NOT NULL,
    NAME VARCHAR(100) NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- PD_METADATA_FIELDS 
-- ----------------------------------------------------------------------- 

CREATE TABLE PD_METADATA_FIELDS
(
    ID INTEGER NOT NULL,
    OBJECT_ID INTEGER NOT NULL,
    COLUMN_VALUE TEXT NOT NULL,
    NAME VARCHAR(100) NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- LANGUAGE 
-- ----------------------------------------------------------------------- 

CREATE TABLE LANGUAGE
(
    ID INTEGER NOT NULL,
    PORTLET_ID INTEGER NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL,
    SUPPORTED_LOCALE SMALLINT NOT NULL,
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(100),
    KEYWORDS TEXT,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- PORTLET_SUPPORTS 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_SUPPORTS
(
    SUPPORTS_ID INTEGER NOT NULL,
    PORTLET_ID INTEGER NOT NULL,
    MIME_TYPE VARCHAR(30) NOT NULL,
    MODES VARCHAR(255),
    STATES VARCHAR(255),
    PRIMARY KEY (SUPPORTS_ID)
);

CREATE UNIQUE INDEX UK_SUPPORTS ON PORTLET_SUPPORTS (PORTLET_ID, MIME_TYPE);

-- ----------------------------------------------------------------------- 
-- PARAMETER 
-- ----------------------------------------------------------------------- 

CREATE TABLE PARAMETER
(
    PARAMETER_ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    OWNER_CLASS_NAME VARCHAR(255) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    PARAMETER_VALUE TEXT,
    PRIMARY KEY (PARAMETER_ID)
);

-- ----------------------------------------------------------------------- 
-- PORTLET_PREFERENCE 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_PREFERENCE
(
    ID INTEGER NOT NULL,
    DTYPE VARCHAR(10) NOT NULL,
    APPLICATION_NAME VARCHAR(80) NOT NULL,
    PORTLET_NAME VARCHAR(80) NOT NULL,
    ENTITY_ID VARCHAR(80),
    USER_NAME VARCHAR(80),
    NAME VARCHAR(254) NOT NULL,
    READONLY SMALLINT NOT NULL,
    PRIMARY KEY (ID)
);

CREATE UNIQUE INDEX UIX_PORTLET_PREFERENCE ON PORTLET_PREFERENCE (DTYPE, APPLICATION_NAME, PORTLET_NAME, ENTITY_ID, USER_NAME, NAME);

-- ----------------------------------------------------------------------- 
-- PORTLET_PREFERENCE_VALUE 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_PREFERENCE_VALUE
(
    ID INTEGER NOT NULL,
    PREF_ID INTEGER NOT NULL,
    IDX SMALLINT NOT NULL,
    PREF_VALUE VARCHAR(4000),
    PRIMARY KEY (ID, PREF_ID, IDX)
);

CREATE INDEX IX_PREFS_PREF_ID ON PORTLET_PREFERENCE_VALUE (PREF_ID);

-- ----------------------------------------------------------------------- 
-- SECURITY_ROLE_REFERENCE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_ROLE_REFERENCE
(
    ID INTEGER NOT NULL,
    PORTLET_DEFINITION_ID INTEGER NOT NULL,
    ROLE_NAME VARCHAR(150) NOT NULL,
    ROLE_LINK VARCHAR(150),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- SECURITY_ROLE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_ROLE
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    NAME VARCHAR(150) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- USER_ATTRIBUTE_REF 
-- ----------------------------------------------------------------------- 

CREATE TABLE USER_ATTRIBUTE_REF
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    NAME VARCHAR(150),
    NAME_LINK VARCHAR(150),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- USER_ATTRIBUTE 
-- ----------------------------------------------------------------------- 

CREATE TABLE USER_ATTRIBUTE
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    NAME VARCHAR(150),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- JETSPEED_SERVICE 
-- ----------------------------------------------------------------------- 

CREATE TABLE JETSPEED_SERVICE
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    NAME VARCHAR(150),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- LOCALIZED_DESCRIPTION 
-- ----------------------------------------------------------------------- 

CREATE TABLE LOCALIZED_DESCRIPTION
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    OWNER_CLASS_NAME VARCHAR(255) NOT NULL,
    DESCRIPTION TEXT NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- LOCALIZED_DISPLAY_NAME 
-- ----------------------------------------------------------------------- 

CREATE TABLE LOCALIZED_DISPLAY_NAME
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    OWNER_CLASS_NAME VARCHAR(255),
    DISPLAY_NAME TEXT NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- CUSTOM_PORTLET_MODE 
-- ----------------------------------------------------------------------- 

CREATE TABLE CUSTOM_PORTLET_MODE
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    CUSTOM_NAME VARCHAR(150) NOT NULL,
    MAPPED_NAME VARCHAR(150),
    PORTAL_MANAGED SMALLINT NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- CUSTOM_WINDOW_STATE 
-- ----------------------------------------------------------------------- 

CREATE TABLE CUSTOM_WINDOW_STATE
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    CUSTOM_NAME VARCHAR(150) NOT NULL,
    MAPPED_NAME VARCHAR(150),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- EVENT_DEFINITION 
-- ----------------------------------------------------------------------- 

CREATE TABLE EVENT_DEFINITION
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    LOCAL_PART VARCHAR(80) NOT NULL,
    NAMESPACE VARCHAR(80),
    PREFIX VARCHAR(20),
    VALUE_TYPE VARCHAR(255),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- EVENT_ALIAS 
-- ----------------------------------------------------------------------- 

CREATE TABLE EVENT_ALIAS
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    LOCAL_PART VARCHAR(80) NOT NULL,
    NAMESPACE VARCHAR(80),
    PREFIX VARCHAR(20),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- PARAMETER_ALIAS 
-- ----------------------------------------------------------------------- 

CREATE TABLE PARAMETER_ALIAS
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    LOCAL_PART VARCHAR(80) NOT NULL,
    NAMESPACE VARCHAR(80),
    PREFIX VARCHAR(20),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- PUBLISHING_EVENT 
-- ----------------------------------------------------------------------- 

CREATE TABLE PUBLISHING_EVENT
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    LOCAL_PART VARCHAR(80) NOT NULL,
    NAMESPACE VARCHAR(80),
    PREFIX VARCHAR(20),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- PROCESSING_EVENT 
-- ----------------------------------------------------------------------- 

CREATE TABLE PROCESSING_EVENT
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    LOCAL_PART VARCHAR(80) NOT NULL,
    NAMESPACE VARCHAR(80),
    PREFIX VARCHAR(20),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- NAMED_PARAMETER 
-- ----------------------------------------------------------------------- 

CREATE TABLE NAMED_PARAMETER
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    NAME VARCHAR(150) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- RUNTIME_OPTION 
-- ----------------------------------------------------------------------- 

CREATE TABLE RUNTIME_OPTION
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    OWNER_CLASS_NAME VARCHAR(255) NOT NULL,
    NAME VARCHAR(150) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- RUNTIME_VALUE 
-- ----------------------------------------------------------------------- 

CREATE TABLE RUNTIME_VALUE
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    RVALUE VARCHAR(200) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- PUBLIC_PARAMETER 
-- ----------------------------------------------------------------------- 

CREATE TABLE PUBLIC_PARAMETER
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    LOCAL_PART VARCHAR(80) NOT NULL,
    NAMESPACE VARCHAR(80),
    PREFIX VARCHAR(20),
    IDENTIFIER VARCHAR(150) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- PORTLET_FILTER 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_FILTER
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    FILTER_NAME VARCHAR(80) NOT NULL,
    FILTER_CLASS VARCHAR(255),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- FILTER_LIFECYCLE 
-- ----------------------------------------------------------------------- 

CREATE TABLE FILTER_LIFECYCLE
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    NAME VARCHAR(150) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- FILTER_MAPPING 
-- ----------------------------------------------------------------------- 

CREATE TABLE FILTER_MAPPING
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    FILTER_NAME VARCHAR(150) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- FILTERED_PORTLET 
-- ----------------------------------------------------------------------- 

CREATE TABLE FILTERED_PORTLET
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    NAME VARCHAR(150) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- PORTLET_LISTENER 
-- ----------------------------------------------------------------------- 

CREATE TABLE PORTLET_LISTENER
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    LISTENER_CLASS VARCHAR(255),
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- PA_SECURITY_CONSTRAINT 
-- ----------------------------------------------------------------------- 

CREATE TABLE PA_SECURITY_CONSTRAINT
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    TRANSPORT VARCHAR(40) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- SECURED_PORTLET 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURED_PORTLET
(
    ID INTEGER NOT NULL,
    OWNER_ID INTEGER NOT NULL,
    NAME VARCHAR(150) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- LOCALE_ENCODING_MAPPING 
-- ----------------------------------------------------------------------- 

CREATE TABLE LOCALE_ENCODING_MAPPING
(
    ID INTEGER NOT NULL,
    APPLICATION_ID INTEGER NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL,
    ENCODING VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID)
);

-- ----------------------------------------------------------------------- 
-- SECURITY_PRINCIPAL 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_PRINCIPAL
(
    PRINCIPAL_ID INTEGER NOT NULL,
    PRINCIPAL_TYPE VARCHAR(20) NOT NULL,
    PRINCIPAL_NAME VARCHAR(200) NOT NULL,
    IS_MAPPED SMALLINT NOT NULL,
    IS_ENABLED SMALLINT NOT NULL,
    IS_READONLY SMALLINT NOT NULL,
    IS_REMOVABLE SMALLINT NOT NULL,
    CREATION_DATE DATETIME NOT NULL,
    MODIFIED_DATE DATETIME NOT NULL,
    DOMAIN_ID INTEGER NOT NULL,
    PRIMARY KEY (PRINCIPAL_ID)
);

CREATE UNIQUE INDEX UIX_SECURITY_PRINCIPAL ON SECURITY_PRINCIPAL (PRINCIPAL_TYPE, PRINCIPAL_NAME, DOMAIN_ID);

-- ----------------------------------------------------------------------- 
-- SECURITY_ATTRIBUTE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_ATTRIBUTE
(
    ATTR_ID INTEGER NOT NULL,
    PRINCIPAL_ID INTEGER NOT NULL,
    ATTR_NAME VARCHAR(200) NOT NULL,
    ATTR_VALUE VARCHAR(1000),
    PRIMARY KEY (ATTR_ID, PRINCIPAL_ID, ATTR_NAME)
);

CREATE INDEX IX_NAME_LOOKUP ON SECURITY_ATTRIBUTE (ATTR_NAME);

-- ----------------------------------------------------------------------- 
-- SECURITY_PRINCIPAL_ASSOC 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_PRINCIPAL_ASSOC
(
    ASSOC_NAME VARCHAR(30) NOT NULL,
    FROM_PRINCIPAL_ID INTEGER NOT NULL,
    TO_PRINCIPAL_ID INTEGER NOT NULL,
    PRIMARY KEY (ASSOC_NAME, FROM_PRINCIPAL_ID, TO_PRINCIPAL_ID)
);

CREATE INDEX IX_TO_PRINCIPAL_ASSOC_LOOKUP ON SECURITY_PRINCIPAL_ASSOC (ASSOC_NAME, TO_PRINCIPAL_ID);

-- ----------------------------------------------------------------------- 
-- SECURITY_PERMISSION 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_PERMISSION
(
    PERMISSION_ID INTEGER NOT NULL,
    PERMISSION_TYPE VARCHAR(30) NOT NULL,
    NAME VARCHAR(254) NOT NULL,
    ACTIONS VARCHAR(254) NOT NULL,
    PRIMARY KEY (PERMISSION_ID)
);

CREATE UNIQUE INDEX UIX_SECURITY_PERMISSION ON SECURITY_PERMISSION (PERMISSION_TYPE, NAME,ACTIONS);

-- ----------------------------------------------------------------------- 
-- PRINCIPAL_PERMISSION 
-- ----------------------------------------------------------------------- 

CREATE TABLE PRINCIPAL_PERMISSION
(
    PRINCIPAL_ID INTEGER NOT NULL,
    PERMISSION_ID INTEGER NOT NULL,
    PRIMARY KEY (PRINCIPAL_ID, PERMISSION_ID)
);

-- ----------------------------------------------------------------------- 
-- SECURITY_CREDENTIAL 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_CREDENTIAL
(
    CREDENTIAL_ID INTEGER NOT NULL,
    PRINCIPAL_ID INTEGER NOT NULL,
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

-- ----------------------------------------------------------------------- 
-- SSO_SITE 
-- ----------------------------------------------------------------------- 

CREATE TABLE SSO_SITE
(
    SITE_ID INTEGER NOT NULL,
    NAME VARCHAR(254) NOT NULL,
    URL VARCHAR(254) NOT NULL,
    ALLOW_USER_SET SMALLINT DEFAULT 0,
    REQUIRES_CERTIFICATE SMALLINT DEFAULT 0,
    CHALLENGE_RESPONSE_AUTH SMALLINT DEFAULT 0,
    FORM_AUTH SMALLINT DEFAULT 0,
    FORM_USER_FIELD VARCHAR(128),
    FORM_PWD_FIELD VARCHAR(128),
    REALM VARCHAR(128),
    DOMAIN_ID INTEGER NOT NULL,
    PRIMARY KEY (SITE_ID)
);

CREATE UNIQUE INDEX UIX_SITE_NAME ON SSO_SITE (NAME);

CREATE UNIQUE INDEX UIX_SITE_URL ON SSO_SITE (URL);

-- ----------------------------------------------------------------------- 
-- SECURITY_DOMAIN 
-- ----------------------------------------------------------------------- 

CREATE TABLE SECURITY_DOMAIN
(
    DOMAIN_ID INTEGER NOT NULL,
    DOMAIN_NAME VARCHAR(30),
    REMOTE SMALLINT DEFAULT 0,
    ENABLED SMALLINT DEFAULT 1,
    OWNER_DOMAIN_ID INTEGER,
    PRIMARY KEY (DOMAIN_ID)
);

CREATE UNIQUE INDEX UIX_DOMAIN_NAME ON SECURITY_DOMAIN (DOMAIN_NAME);


CREATE TRIGGER trig_folder ON FOLDER FOR DELETE AS DECLARE @FolderID INT SELECT @FolderID = (SELECT FOLDER_ID FROM Deleted) DELETE FROM FOLDER WHERE PARENT_ID = @FolderID; 

CREATE TRIGGER trig_folder_menu ON FOLDER_MENU FOR DELETE AS DECLARE @MenuID INT SELECT @MenuID = (SELECT MENU_ID FROM Deleted) DELETE FROM FOLDER_MENU WHERE PARENT_ID = @MenuID; 

CREATE TRIGGER trig_fragment ON FRAGMENT FOR DELETE AS DECLARE @FragID INT SELECT @FragID = (SELECT FRAGMENT_ID FROM Deleted) DELETE FROM FRAGMENT_ID WHERE PARENT_ID = @FragID; 

CREATE TRIGGER trig_page_menu ON PAGE_MENU FOR DELETE AS DECLARE @PageMenuID INT SELECT @PageMenuID = (SELECT MENU_ID FROM Deleted) DELETE FROM PAGE_MENU WHERE PARENT_ID = @PageMenuID; 


ALTER TABLE FOLDER
    ADD CONSTRAINT FK_FOLDER_1 FOREIGN KEY (PARENT_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE NO ACTION;

ALTER TABLE FOLDER_METADATA
    ADD CONSTRAINT FK_FOLDER_METADATA_1 FOREIGN KEY (FOLDER_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;

ALTER TABLE FOLDER_CONSTRAINT
    ADD CONSTRAINT FK_FOLDER_CONSTRAINT_1 FOREIGN KEY (FOLDER_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;

ALTER TABLE FOLDER_CONSTRAINTS_REF
    ADD CONSTRAINT FK_FOLDER_CONSTRAINTS_REF_1 FOREIGN KEY (FOLDER_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;

ALTER TABLE FOLDER_ORDER
    ADD CONSTRAINT FK_FOLDER_ORDER_1 FOREIGN KEY (FOLDER_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;

ALTER TABLE FOLDER_MENU
    ADD CONSTRAINT FK_FOLDER_MENU_1 FOREIGN KEY (PARENT_ID) REFERENCES FOLDER_MENU (MENU_ID) ON DELETE NO ACTION;

ALTER TABLE FOLDER_MENU
    ADD CONSTRAINT FK_FOLDER_MENU_2 FOREIGN KEY (FOLDER_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;

ALTER TABLE FOLDER_MENU_METADATA
    ADD CONSTRAINT FK_FOLDER_MENU_METADATA_1 FOREIGN KEY (MENU_ID) REFERENCES FOLDER_MENU (MENU_ID) ON DELETE CASCADE;

ALTER TABLE PAGE
    ADD CONSTRAINT FK_PAGE_1 FOREIGN KEY (PARENT_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;

ALTER TABLE PAGE_METADATA
    ADD CONSTRAINT FK_PAGE_METADATA_1 FOREIGN KEY (PAGE_ID) REFERENCES PAGE (PAGE_ID) ON DELETE CASCADE;

ALTER TABLE PAGE_CONSTRAINT
    ADD CONSTRAINT FK_PAGE_CONSTRAINT_1 FOREIGN KEY (PAGE_ID) REFERENCES PAGE (PAGE_ID) ON DELETE CASCADE;

ALTER TABLE PAGE_CONSTRAINTS_REF
    ADD CONSTRAINT FK_PAGE_CONSTRAINTS_REF_1 FOREIGN KEY (PAGE_ID) REFERENCES PAGE (PAGE_ID) ON DELETE CASCADE;

ALTER TABLE PAGE_MENU
    ADD CONSTRAINT FK_PAGE_MENU_1 FOREIGN KEY (PARENT_ID) REFERENCES PAGE_MENU (MENU_ID) ON DELETE NO ACTION;

ALTER TABLE PAGE_MENU
    ADD CONSTRAINT PM_M_FK_PAGE_ID_PAGE FOREIGN KEY (PAGE_ID) REFERENCES PAGE (PAGE_ID) ON DELETE CASCADE;

ALTER TABLE PAGE_MENU_METADATA
    ADD CONSTRAINT FK_PAGE_MENU_METADATA_1 FOREIGN KEY (MENU_ID) REFERENCES PAGE_MENU (MENU_ID) ON DELETE CASCADE;

ALTER TABLE FRAGMENT
    ADD CONSTRAINT FK_FRAGMENT_1 FOREIGN KEY (PARENT_ID) REFERENCES FRAGMENT (FRAGMENT_ID) ON DELETE NO ACTION;

ALTER TABLE FRAGMENT
    ADD CONSTRAINT FK_FRAGMENT_2 FOREIGN KEY (PAGE_ID) REFERENCES PAGE (PAGE_ID) ON DELETE CASCADE;

ALTER TABLE FRAGMENT_CONSTRAINT
    ADD CONSTRAINT FK_FRAGMENT_CONSTRAINT_1 FOREIGN KEY (FRAGMENT_ID) REFERENCES FRAGMENT (FRAGMENT_ID) ON DELETE CASCADE;

ALTER TABLE FRAGMENT_CONSTRAINTS_REF
    ADD CONSTRAINT FK_FRAGMENT_CONSTRAINTS_REF_1 FOREIGN KEY (FRAGMENT_ID) REFERENCES FRAGMENT (FRAGMENT_ID) ON DELETE CASCADE;

ALTER TABLE FRAGMENT_PREF
    ADD CONSTRAINT FK_FRAGMENT_PREF_1 FOREIGN KEY (FRAGMENT_ID) REFERENCES FRAGMENT (FRAGMENT_ID) ON DELETE CASCADE;

ALTER TABLE FRAGMENT_PREF_VALUE
    ADD CONSTRAINT FK_FRAGMENT_PREF_VALUE_1 FOREIGN KEY (PREF_ID) REFERENCES FRAGMENT_PREF (PREF_ID) ON DELETE CASCADE;

ALTER TABLE FRAGMENT_PROP
    ADD CONSTRAINT FK_FRAGMENT_PROP_1 FOREIGN KEY (FRAGMENT_ID) REFERENCES FRAGMENT (FRAGMENT_ID) ON DELETE CASCADE;

ALTER TABLE LINK
    ADD CONSTRAINT FK_LINK_1 FOREIGN KEY (PARENT_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;

ALTER TABLE LINK_METADATA
    ADD CONSTRAINT FK_LINK_METADATA_1 FOREIGN KEY (LINK_ID) REFERENCES LINK (LINK_ID) ON DELETE CASCADE;

ALTER TABLE LINK_CONSTRAINT
    ADD CONSTRAINT FK_LINK_CONSTRAINT_1 FOREIGN KEY (LINK_ID) REFERENCES LINK (LINK_ID) ON DELETE CASCADE;

ALTER TABLE LINK_CONSTRAINTS_REF
    ADD CONSTRAINT FK_LINK_CONSTRAINTS_REF_1 FOREIGN KEY (LINK_ID) REFERENCES LINK (LINK_ID) ON DELETE CASCADE;

ALTER TABLE PAGE_SECURITY
    ADD CONSTRAINT FK_PAGE_SECURITY_1 FOREIGN KEY (PARENT_ID) REFERENCES FOLDER (FOLDER_ID) ON DELETE CASCADE;

ALTER TABLE PAGE_SEC_CONSTRAINTS_DEF
    ADD CONSTRAINT FK_PAGE_SEC_CONSTRAINTS_DEF_1 FOREIGN KEY (PAGE_SECURITY_ID) REFERENCES PAGE_SECURITY (PAGE_SECURITY_ID) ON DELETE CASCADE;

ALTER TABLE PAGE_SEC_CONSTRAINT_DEF
    ADD CONSTRAINT FK_PAGE_SEC_CONSTRAINT_DEF_1 FOREIGN KEY (CONSTRAINTS_DEF_ID) REFERENCES PAGE_SEC_CONSTRAINTS_DEF (CONSTRAINTS_DEF_ID) ON DELETE CASCADE;

ALTER TABLE PAGE_SEC_CONSTRAINTS_REF
    ADD CONSTRAINT FK_PAGE_SEC_CONSTRAINTS_REF_1 FOREIGN KEY (PAGE_SECURITY_ID) REFERENCES PAGE_SECURITY (PAGE_SECURITY_ID) ON DELETE CASCADE;

ALTER TABLE RULE_CRITERION
    ADD CONSTRAINT FK_RULE_CRITERION_1 FOREIGN KEY (RULE_ID) REFERENCES PROFILING_RULE (RULE_ID) ON DELETE CASCADE;

ALTER TABLE PA_METADATA_FIELDS
    ADD CONSTRAINT FK_PA_METADATA_FIELDS_1 FOREIGN KEY (OBJECT_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;

ALTER TABLE PD_METADATA_FIELDS
    ADD CONSTRAINT FK_PD_METADATA_FIELDS_1 FOREIGN KEY (OBJECT_ID) REFERENCES PORTLET_DEFINITION (ID) ON DELETE CASCADE;

ALTER TABLE PORTLET_PREFERENCE_VALUE
    ADD CONSTRAINT FK_PORTLET_PREFERENCE FOREIGN KEY (PREF_ID) REFERENCES PORTLET_PREFERENCE (ID) ON DELETE CASCADE;

ALTER TABLE SECURITY_ROLE
    ADD CONSTRAINT FK_SECURITY_ROLE_REF_1 FOREIGN KEY (APPLICATION_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;

ALTER TABLE USER_ATTRIBUTE_REF
    ADD CONSTRAINT FK_USER_ATTRIBUTE_REF_1 FOREIGN KEY (APPLICATION_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;

ALTER TABLE USER_ATTRIBUTE
    ADD CONSTRAINT FK_USER_ATTRIBUTE_1 FOREIGN KEY (APPLICATION_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;

ALTER TABLE CUSTOM_PORTLET_MODE
    ADD CONSTRAINT FK_CUSTOM_PORTLET_MODE_1 FOREIGN KEY (APPLICATION_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;

ALTER TABLE CUSTOM_WINDOW_STATE
    ADD CONSTRAINT FK_CUSTOM_WINDOW_STATE_1 FOREIGN KEY (APPLICATION_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;

ALTER TABLE EVENT_DEFINITION
    ADD CONSTRAINT FK_EVENT_DEFINITION_1 FOREIGN KEY (APPLICATION_ID) REFERENCES PORTLET_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;

ALTER TABLE SECURITY_PRINCIPAL
    ADD CONSTRAINT FK_SECURITY_DOMAIN_1 FOREIGN KEY (DOMAIN_ID) REFERENCES SECURITY_DOMAIN (DOMAIN_ID) ON DELETE CASCADE;

ALTER TABLE SECURITY_ATTRIBUTE
    ADD CONSTRAINT FK_PRINCIPAL_ATTR FOREIGN KEY (PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;

ALTER TABLE SECURITY_PRINCIPAL_ASSOC
    ADD CONSTRAINT FK_FROM_PRINCIPAL_ASSOC FOREIGN KEY (FROM_PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;

ALTER TABLE SECURITY_PRINCIPAL_ASSOC
    ADD CONSTRAINT FK_TO_PRINCIPAL_ASSOC FOREIGN KEY (TO_PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE NO ACTION;

ALTER TABLE PRINCIPAL_PERMISSION
    ADD CONSTRAINT FK_PRINCIPAL_PERMISSION_1 FOREIGN KEY (PERMISSION_ID) REFERENCES SECURITY_PERMISSION (PERMISSION_ID) ON DELETE CASCADE;

ALTER TABLE PRINCIPAL_PERMISSION
    ADD CONSTRAINT FK_PRINCIPAL_PERMISSION_2 FOREIGN KEY (PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;

ALTER TABLE SECURITY_CREDENTIAL
    ADD CONSTRAINT FK_SECURITY_CREDENTIAL_1 FOREIGN KEY (PRINCIPAL_ID) REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID) ON DELETE CASCADE;

ALTER TABLE SSO_SITE
    ADD CONSTRAINT FK_SECURITY_DOMAIN_2 FOREIGN KEY (DOMAIN_ID) REFERENCES SECURITY_DOMAIN (DOMAIN_ID) ON DELETE CASCADE;

