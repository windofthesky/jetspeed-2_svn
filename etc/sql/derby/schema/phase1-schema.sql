-----------------------------------------------------------------------------
-- MEDIA_TYPE
-----------------------------------------------------------------------------

CREATE TABLE MEDIA_TYPE
(
    MEDIATYPE_ID INTEGER NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    CHARACTER_SET VARCHAR(40),
    TITLE VARCHAR(80),
    DESCRIPTION LONG VARCHAR,
    PRIMARY KEY(MEDIATYPE_ID));

-----------------------------------------------------------------------------
-- CLIENT
-----------------------------------------------------------------------------

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
    PRIMARY KEY(CLIENT_ID));

-----------------------------------------------------------------------------
-- MIMETYPE
-----------------------------------------------------------------------------

CREATE TABLE MIMETYPE
(
    MIMETYPE_ID INTEGER NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    PRIMARY KEY(MIMETYPE_ID));

-----------------------------------------------------------------------------
-- CAPABILITY
-----------------------------------------------------------------------------

CREATE TABLE CAPABILITY
(
    CAPABILITY_ID INTEGER NOT NULL,
    CAPABILITY VARCHAR(80) NOT NULL,
    PRIMARY KEY(CAPABILITY_ID));

-----------------------------------------------------------------------------
-- CLIENT_TO_CAPABILITY
-----------------------------------------------------------------------------

CREATE TABLE CLIENT_TO_CAPABILITY
(
    CLIENT_ID INTEGER NOT NULL,
    CAPABILITY_ID INTEGER NOT NULL);

-----------------------------------------------------------------------------
-- CLIENT_TO_MIMETYPE
-----------------------------------------------------------------------------

CREATE TABLE CLIENT_TO_MIMETYPE
(
    CLIENT_ID INTEGER NOT NULL,
    MIMETYPE_ID INTEGER NOT NULL);

-----------------------------------------------------------------------------
-- MEDIATYPE_TO_CAPABILITY
-----------------------------------------------------------------------------

CREATE TABLE MEDIATYPE_TO_CAPABILITY
(
    MEDIATYPE_ID INTEGER NOT NULL,
    CAPABILITY_ID INTEGER NOT NULL);

-----------------------------------------------------------------------------
-- MEDIATYPE_TO_MIMETYPE
-----------------------------------------------------------------------------

CREATE TABLE MEDIATYPE_TO_MIMETYPE
(
    MEDIATYPE_ID INTEGER NOT NULL,
    MIMETYPE_ID INTEGER NOT NULL);

-----------------------------------------------------------------------------
-- PORTLET_STATISTICS
-----------------------------------------------------------------------------

CREATE TABLE PORTLET_STATISTICS
(
    IPADDRESS VARCHAR(80),
    USER_NAME VARCHAR(80),
    TIME_STAMP TIMESTAMP,
    PAGE VARCHAR(80),
    PORTLET VARCHAR(80),
    STATUS INTEGER,
    ELAPSED_TIME INTEGER);

-----------------------------------------------------------------------------
-- PAGE_STATISTICS
-----------------------------------------------------------------------------

CREATE TABLE PAGE_STATISTICS
(
    IPADDRESS VARCHAR(80),
    USER_NAME VARCHAR(80),
    TIME_STAMP TIMESTAMP,
    PAGE VARCHAR(80),
    STATUS INTEGER,
    ELAPSED_TIME INTEGER);

-----------------------------------------------------------------------------
-- USER_STATISTICS
-----------------------------------------------------------------------------

CREATE TABLE USER_STATISTICS
(
    IPADDRESS VARCHAR(80),
    USER_NAME VARCHAR(80),
    TIME_STAMP TIMESTAMP,
    STATUS INTEGER,
    ELAPSED_TIME INTEGER);

