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
    EXPIRATION_CACHE VARCHAR(30),
    PREFERENCE_VALIDATOR VARCHAR(255),
    UNIQUE(APPLICATION_ID, NAME)
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
    PARENT_ID INTEGER NOT NULL,
    NAME VARCHAR(80) NOT NULL,
	CLASS_NAME VARCHAR(50) NOT NULL,
    READ_ONLY CHAR(1) DEFAULT '1'

);

CREATE TABLE PREFERENCE_VALUE(
   	ID INTEGER,
    PREFERENCE_ID INTEGER,    
    VALUE LONGVARCHAR,
    CONSTRAINT PK
 	PRIMARY KEY (ID)

);


-------------------------------------------------------------------------------
-- Portlet Entity
-- Represents the managed state of individual portlet
-- intance within a "page"
-------------------------------------------------------------------------------
CREATE TABLE PORTLET_ENTITY(
    ID INTEGER PRIMARY KEY,
	PORTLET_DEFINITION_ID INTEGER NOT NULL,
	GUID VARCHAR(255) NOT NULL
);


------------------------------------------------------------------------------
-- Security Role Reference
------------------------------------------------------------------------------
CREATE TABLE SECURITY_ROLE_REFERENCE(
    ID INTEGER PRIMARY KEY,
	PORTLET_DEFINITION_ID INTEGER NOT NULL,
	ROLE_NAME VARCHAR(150),
	ROLE_LINK VARCHAR(150)
);


------------------------------------------------------------------------------
-- Localized Descriptions
------------------------------------------------------------------------------
CREATE TABLE LOCALIZED_DESCRIPTION(
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

------------------------------------------------------------------------------
-- Tables for Capability mapping
------------------------------------------------------------------------------

-----------------------------------------------------------------------------
-- Media Type
------------------------------------------------------------------------------

CREATE TABLE MEDIA_TYPE(
	MEDIATYPE_ID INTEGER NOT NULL PRIMARY KEY,
	NAME VARCHAR(80) NOT NULL,
	CHARACTER_SET VARCHAR(40),
	TITLE VARCHAR(80),
	DESCRIPTION LONGVARCHAR
);

------------------------------------------------------------------------------
-- Client
------------------------------------------------------------------------------

CREATE TABLE CLIENT(
	CLIENT_ID INTEGER NOT NULL PRIMARY KEY,
	NAME VARCHAR(80) NOT NULL,
	USER_AGENT_PATTERN VARCHAR (128),
	MANUFACTURER VARCHAR (80),
	MODEL VARCHAR(80),
	VERSION VARCHAR(40)
);

------------------------------------------------------------------------------
-- Mimetype
------------------------------------------------------------------------------

CREATE TABLE MIMETYPE(
	MIMETYPE_ID INTEGER NOT NULL PRIMARY KEY,
	NAME VARCHAR(80) NOT NULL
);

------------------------------------------------------------------------------
-- Capability
------------------------------------------------------------------------------

CREATE TABLE  CAPABILITY(
	CAPABILITY_ID INTEGER NOT NULL PRIMARY KEY,
	CAPABILITY VARCHAR(80) NOT NULL
);

------------------------------------------------------
-- Client association
------------------------------------------------------
CREATE TABLE  CLIENT_TO_CAPABILITY(

	CLIENT_ID INTEGER NOT NULL,
	CAPABILITY_ID INTEGER NOT NULL
);


CREATE TABLE  CLIENT_TO_MIMETYPE(
	CLIENT_ID INTEGER NOT NULL,
	MIMETYPE_ID INTEGER NOT NULL
);

----------------------------------------------------
-- Media Type association
----------------------------------------------------
CREATE TABLE  MEDIATYPE_TO_CAPABILITY(

	MEDIATYPE_ID INTEGER NOT NULL,
	CAPABILITY_ID INTEGER NOT NULL
);


CREATE TABLE  MEDIATYPE_TO_MIMETYPE(
	MEDIATYPE_ID INTEGER NOT NULL,
	MIMETYPE_ID INTEGER NOT NULL
);


