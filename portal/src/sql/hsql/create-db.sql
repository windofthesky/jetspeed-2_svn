----------------------------------------------------------------------------
-- Create Portlet Application Table
----------------------------------------------------------------------------
CREATE TABLE PORTLET_APPLICATION
( 
    APPLICATION_ID INTEGER NOT NULL PRIMARY KEY,
    APP_NAME VARCHAR(80) NOT NULL,
    APP_IDENTIFIER VARCHAR(80), 
    VERSION VARCHAR(80),
    DESCRIPTION VARCHAR(80),
    WEB_APP_ID INTEGER NOT NULL
);

ALTER TABLE PORTLET_APPLICATION ADD CONSTRAINT UK_APPLICATION UNIQUE (APP_NAME);

----------------------------------------------------------------------------
-- Create Web Application Table
----------------------------------------------------------------------------

CREATE TABLE WEB_APPLICATION(
    ID INTEGER NOT NULL PRIMARY KEY,
    CONTEXT_ROOT VARCHAR(255)  NOT NULL
);


----------------------------------------------------------------------------
-- Create Portlet Definition Table
----------------------------------------------------------------------------

CREATE TABLE PORTLET_DEFINITION(
    ID INTEGER NOT NULL PRIMARY KEY,
    NAME VARCHAR(80),
    CLASS_NAME VARCHAR(100),
    APPLICATION_ID INTEGER NOT NULL,
    PORTLET_IDENTIFIER VARCHAR(80),
    EXPIRATION_CACHE VARCHAR(30)
);

----------------------------------------------------------------------------
-- Create Language  Table
----------------------------------------------------------------------------

CREATE TABLE LANGUAGE(
    ID INTEGER NOT NULL PRIMARY KEY,
    PORTLET_ID INTEGER NOT NULL,
    TITLE VARCHAR(100),
    SHORT_TITLE VARCHAR(50),
    LOCALE_STRING VARCHAR(50) NOT NULL,
    KEYWORDS LONGVARCHAR
);

----------------------------------------------------------------------------
-- Create Content Type Table
----------------------------------------------------------------------------

CREATE TABLE PORTLET_CONTENT_TYPE(
    CONTENT_TYPE_ID INTEGER PRIMARY KEY,
    PORTLET_ID INTEGER NOT NULL,
    CONTENT_TYPE VARCHAR(30) NOT NULL,
    MODES LONGVARCHAR
);

----------------------------------------------------------------------------
-- Create Parameter Table
-- NOTE: This table supports both Servlet and Portlet parameters
-- Class name is required to decide if this is a portlet or servlet parameter
----------------------------------------------------------------------------

CREATE TABLE PARAMETER(
    PARAMETER_ID INTEGER PRIMARY KEY,
    PARENT_ID INTEGER NOT NULL,
    CLASS_NAME VARCHAR(30) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    VALUE LONGVARCHAR NOT NULL
);



------------------------------------------------------------------------------
-- Preference and Preference Values
------------------------------------------------------------------------------
CREATE TABLE PORTLET_PREFERENCE(
    ID INTEGER PRIMARY KEY,
    PORTLET_ID INTEGER NOT NULL,
    NAME VARCHAR(80) NOT NULL

);

CREATE TABLE PREFERENCE_VALUE(
    PREFERENCE_ID INTEGER,
	ID INTEGER,
    VALUE LONGVARCHAR,
    
    CONSTRAINT PK
 	PRIMARY KEY (PREFERENCE_ID, ID)

);


-------------------------------------------------------------------------------
-- Portlet Entity
-- Represents the managed state of individual portlet
-- intance within a "page"
-------------------------------------------------------------------------------
CREATE TABLE PORTLET_ENTITY(
    ID INTEGER PRIMARY KEY,
	PORTLET_DEFINITION_ID INTEGER NOT NULL	
);


------------------------------------------------------------------------------
-- User Preference and User Preference Values
------------------------------------------------------------------------------
CREATE TABLE USER_PORTLET_PREFERENCE(
    ID INTEGER PRIMARY KEY,
    PORTLET_ENTITY_ID INTEGER NOT NULL,
    USER_NAME VARCHAR(50),
    NAME VARCHAR(80) NOT NULL

);

CREATE TABLE USER_PREFERENCE_VALUE(
    PREFERENCE_ID INTEGER,
	ID INTEGER,
    VALUE LONGVARCHAR,
    
    CONSTRAINT PK
 	PRIMARY KEY (PREFERENCE_ID, ID)

);

------------------------------------------------------------------------------
-- Localized Descriptions
------------------------------------------------------------------------------
CREATE TABLE LOCALIZED_DESCRITPION(
    ID INTEGER PRIMARY KEY,
    OBJECT_ID INTEGER NOT NULL,
    TYPE VARCHAR(25) NOT NULL,
    DESCRIPTION LONGVARCHAR NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL
);

------------------------------------------------------------------------------
-- Localized Display Names
------------------------------------------------------------------------------
CREATE TABLE LOCALIZED_DISPLAY_NAME(
    ID INTEGER PRIMARY KEY,
    OBJECT_ID INTEGER NOT NULL,
    TYPE VARCHAR(25) NOT NULL,
    DISPLAY_NAME LONGVARCHAR NOT NULL,
    LOCALE_STRING VARCHAR(50) NOT NULL
);


