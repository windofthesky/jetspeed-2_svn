
-----------------------------------------------------------------------------
-- PORTLET_DEFINITION
-----------------------------------------------------------------------------

CREATE TABLE PORTLET_DEFINITION
(
    ID int4 NOT NULL,
    NAME varchar(80),
    CLASS_NAME varchar(255),
    APPLICATION_ID int4 NOT NULL,
    PORTLET_IDENTIFIER varchar(80),
    EXPIRATION_CACHE varchar(30),
    RESOURCE_BUNDLE varchar(255),
    PREFERENCE_VALIDATOR varchar(255)
);

ALTER TABLE PORTLET_DEFINITION
    ADD CONSTRAINT PORTLET_DEFINITION_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- PORTLET_APPLICATION
-----------------------------------------------------------------------------

CREATE TABLE PORTLET_APPLICATION
(
    APPLICATION_ID int4 NOT NULL,
    APP_NAME varchar(80) NOT NULL,
    APP_IDENTIFIER varchar(80),
    VERSION varchar(80),
    APP_TYPE int4,
    CHECKSUM varchar(80),
    DESCRIPTION varchar(80),
    WEB_APP_ID int4 NOT NULL,
    CONSTRAINT UK_APPLICATION UNIQUE (APP_NAME)
);

ALTER TABLE PORTLET_APPLICATION
    ADD CONSTRAINT PORTLET_APPLICATION_PK
PRIMARY KEY (APPLICATION_ID);






-----------------------------------------------------------------------------
-- WEB_APPLICATION
-----------------------------------------------------------------------------

CREATE TABLE WEB_APPLICATION
(
    ID int4 NOT NULL,
    CONTEXT_ROOT varchar(255) NOT NULL
);

ALTER TABLE WEB_APPLICATION
    ADD CONSTRAINT WEB_APPLICATION_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- PA_METADATA_FIELDS
-----------------------------------------------------------------------------

CREATE TABLE PA_METADATA_FIELDS
(
    ID int4 NOT NULL,
    OBJECT_ID int4 NOT NULL,
    COLUMN_VALUE varchar(2000) NOT NULL,
    NAME varchar(100) NOT NULL,
    LOCALE_STRING varchar(50) NOT NULL
);

ALTER TABLE PA_METADATA_FIELDS
    ADD CONSTRAINT PA_METADATA_FIELDS_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- PD_METADATA_FIELDS
-----------------------------------------------------------------------------

CREATE TABLE PD_METADATA_FIELDS
(
    ID int4 NOT NULL,
    OBJECT_ID int4 NOT NULL,
    COLUMN_VALUE varchar(2000) NOT NULL,
    NAME varchar(100) NOT NULL,
    LOCALE_STRING varchar(50) NOT NULL
);

ALTER TABLE PD_METADATA_FIELDS
    ADD CONSTRAINT PD_METADATA_FIELDS_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- LANGUAGE
-----------------------------------------------------------------------------

CREATE TABLE LANGUAGE
(
    ID int4 NOT NULL,
    PORTLET_ID int4 NOT NULL,
    TITLE varchar(100),
    SHORT_TITLE varchar(100),
    LOCALE_STRING varchar(50) NOT NULL,
    KEYWORDS varchar(2000)
);

ALTER TABLE LANGUAGE
    ADD CONSTRAINT LANGUAGE_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- PORTLET_CONTENT_TYPE
-----------------------------------------------------------------------------

CREATE TABLE PORTLET_CONTENT_TYPE
(
    CONTENT_TYPE_ID int4 NOT NULL,
    PORTLET_ID int4 NOT NULL,
    CONTENT_TYPE varchar(30) NOT NULL,
    MODES varchar(2000)
);

ALTER TABLE PORTLET_CONTENT_TYPE
    ADD CONSTRAINT PORTLET_CONTENT_TYPE_PK
PRIMARY KEY (CONTENT_TYPE_ID);






-----------------------------------------------------------------------------
-- PARAMETER
-----------------------------------------------------------------------------

CREATE TABLE PARAMETER
(
    PARAMETER_ID int4 NOT NULL,
    PARENT_ID int4 NOT NULL,
    CLASS_NAME varchar(255) NOT NULL,
    NAME varchar(80) NOT NULL,
    PARAMETER_VALUE varchar(2000) NOT NULL
);

ALTER TABLE PARAMETER
    ADD CONSTRAINT PARAMETER_PK
PRIMARY KEY (PARAMETER_ID);






-----------------------------------------------------------------------------
-- PORTLET_ENTITY
-----------------------------------------------------------------------------

CREATE TABLE PORTLET_ENTITY
(
    PEID int4 NOT NULL,
    ID varchar(255) NOT NULL,
    APP_NAME varchar(255) NOT NULL,
    PORTLET_NAME varchar(255) NOT NULL,
    CONSTRAINT UK_ENTITY_ID UNIQUE (ID)
);

ALTER TABLE PORTLET_ENTITY
    ADD CONSTRAINT PORTLET_ENTITY_PK
PRIMARY KEY (PEID);






-----------------------------------------------------------------------------
-- SECURITY_ROLE_REFERENCE
-----------------------------------------------------------------------------

CREATE TABLE SECURITY_ROLE_REFERENCE
(
    ID int4 NOT NULL,
    PORTLET_DEFINITION_ID int4 NOT NULL,
    ROLE_NAME varchar(150) NOT NULL,
    ROLE_LINK varchar(150)
);

ALTER TABLE SECURITY_ROLE_REFERENCE
    ADD CONSTRAINT SECURITY_ROLE_REFERENCE_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- SECURITY_ROLE
-----------------------------------------------------------------------------

CREATE TABLE SECURITY_ROLE
(
    ID int4 NOT NULL,
    WEB_APPLICATION_ID int4 NOT NULL,
    ROLE_NAME varchar(150) NOT NULL,
    DESCRIPTION varchar(150)
);

ALTER TABLE SECURITY_ROLE
    ADD CONSTRAINT SECURITY_ROLE_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- USER_ATTRIBUTE_REF
-----------------------------------------------------------------------------

CREATE TABLE USER_ATTRIBUTE_REF
(
    ID int4 NOT NULL,
    APPLICATION_ID int4 NOT NULL,
    NAME varchar(150),
    NAME_LINK varchar(150)
);

ALTER TABLE USER_ATTRIBUTE_REF
    ADD CONSTRAINT USER_ATTRIBUTE_REF_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- USER_ATTRIBUTE
-----------------------------------------------------------------------------

CREATE TABLE USER_ATTRIBUTE
(
    ID int4 NOT NULL,
    APPLICATION_ID int4 NOT NULL,
    NAME varchar(150),
    DESCRIPTION varchar(150)
);

ALTER TABLE USER_ATTRIBUTE
    ADD CONSTRAINT USER_ATTRIBUTE_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- JETSPEED_SERVICE
-----------------------------------------------------------------------------

CREATE TABLE JETSPEED_SERVICE
(
    ID int4 NOT NULL,
    APPLICATION_ID int4 NOT NULL,
    NAME varchar(150)
);

ALTER TABLE JETSPEED_SERVICE
    ADD CONSTRAINT JETSPEED_SERVICE_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- LOCALIZED_DESCRIPTION
-----------------------------------------------------------------------------

CREATE TABLE LOCALIZED_DESCRIPTION
(
    ID int4 NOT NULL,
    OBJECT_ID int4 NOT NULL,
    CLASS_NAME varchar(255) NOT NULL,
    DESCRIPTION varchar(2000) NOT NULL,
    LOCALE_STRING varchar(50) NOT NULL
);

ALTER TABLE LOCALIZED_DESCRIPTION
    ADD CONSTRAINT LOCALIZED_DESCRIPTION_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- LOCALIZED_DISPLAY_NAME
-----------------------------------------------------------------------------

CREATE TABLE LOCALIZED_DISPLAY_NAME
(
    ID int4 NOT NULL,
    OBJECT_ID int4 NOT NULL,
    CLASS_NAME varchar(255),
    DISPLAY_NAME varchar(2000) NOT NULL,
    LOCALE_STRING varchar(50) NOT NULL
);

ALTER TABLE LOCALIZED_DISPLAY_NAME
    ADD CONSTRAINT LOCALIZED_DISPLAY_NAME_PK
PRIMARY KEY (ID);











ALTER TABLE PA_METADATA_FIELDS
    ADD CONSTRAINT PA_METADATA_FIELDS_FK_1 FOREIGN KEY (OBJECT_ID)
    REFERENCES PORTLET_APPLICATION (APPLICATION_ID)
ON DELETE CASCADE
;



ALTER TABLE PD_METADATA_FIELDS
    ADD CONSTRAINT PD_METADATA_FIELDS_FK_1 FOREIGN KEY (OBJECT_ID)
    REFERENCES PORTLET_DEFINITION (ID)
ON DELETE CASCADE
;















ALTER TABLE USER_ATTRIBUTE_REF
    ADD CONSTRAINT USER_ATTRIBUTE_REF_FK_1 FOREIGN KEY (APPLICATION_ID)
    REFERENCES PORTLET_APPLICATION (APPLICATION_ID)
ON DELETE CASCADE
;



ALTER TABLE USER_ATTRIBUTE
    ADD CONSTRAINT USER_ATTRIBUTE_FK_1 FOREIGN KEY (APPLICATION_ID)
    REFERENCES PORTLET_APPLICATION (APPLICATION_ID)
ON DELETE CASCADE
;









