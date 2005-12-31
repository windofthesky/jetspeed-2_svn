
-----------------------------------------------------------------------------
-- MEDIA_TYPE
-----------------------------------------------------------------------------

CREATE TABLE MEDIA_TYPE
(
    MEDIATYPE_ID int4 NOT NULL,
    NAME varchar(80) NOT NULL,
    CHARACTER_SET varchar(40),
    TITLE varchar(80),
    DESCRIPTION varchar(2000)
);

ALTER TABLE MEDIA_TYPE
    ADD CONSTRAINT MEDIA_TYPE_PK
PRIMARY KEY (MEDIATYPE_ID);






-----------------------------------------------------------------------------
-- CLIENT
-----------------------------------------------------------------------------

CREATE TABLE CLIENT
(
    CLIENT_ID int4 NOT NULL,
    EVAL_ORDER int4 NOT NULL,
    NAME varchar(80) NOT NULL,
    USER_AGENT_PATTERN varchar(128),
    MANUFACTURER varchar(80),
    MODEL varchar(80),
    VERSION varchar(40),
    PREFERRED_MIMETYPE_ID int4 NOT NULL
);

ALTER TABLE CLIENT
    ADD CONSTRAINT CLIENT_PK
PRIMARY KEY (CLIENT_ID);






-----------------------------------------------------------------------------
-- MIMETYPE
-----------------------------------------------------------------------------

CREATE TABLE MIMETYPE
(
    MIMETYPE_ID int4 NOT NULL,
    NAME varchar(80) NOT NULL
);

ALTER TABLE MIMETYPE
    ADD CONSTRAINT MIMETYPE_PK
PRIMARY KEY (MIMETYPE_ID);






-----------------------------------------------------------------------------
-- CAPABILITY
-----------------------------------------------------------------------------

CREATE TABLE CAPABILITY
(
    CAPABILITY_ID int4 NOT NULL,
    CAPABILITY varchar(80) NOT NULL
);

ALTER TABLE CAPABILITY
    ADD CONSTRAINT CAPABILITY_PK
PRIMARY KEY (CAPABILITY_ID);






-----------------------------------------------------------------------------
-- CLIENT_TO_CAPABILITY
-----------------------------------------------------------------------------

CREATE TABLE CLIENT_TO_CAPABILITY
(
    CLIENT_ID int4 NOT NULL,
    CAPABILITY_ID int4 NOT NULL
);







-----------------------------------------------------------------------------
-- CLIENT_TO_MIMETYPE
-----------------------------------------------------------------------------

CREATE TABLE CLIENT_TO_MIMETYPE
(
    CLIENT_ID int4 NOT NULL,
    MIMETYPE_ID int4 NOT NULL
);







-----------------------------------------------------------------------------
-- MEDIATYPE_TO_CAPABILITY
-----------------------------------------------------------------------------

CREATE TABLE MEDIATYPE_TO_CAPABILITY
(
    MEDIATYPE_ID int4 NOT NULL,
    CAPABILITY_ID int4 NOT NULL
);







-----------------------------------------------------------------------------
-- MEDIATYPE_TO_MIMETYPE
-----------------------------------------------------------------------------

CREATE TABLE MEDIATYPE_TO_MIMETYPE
(
    MEDIATYPE_ID int4 NOT NULL,
    MIMETYPE_ID int4 NOT NULL
);







-----------------------------------------------------------------------------
-- PORTLET_STATISTICS
-----------------------------------------------------------------------------

CREATE TABLE PORTLET_STATISTICS
(
    IPADDRESS varchar(80),
    USER_NAME varchar(80),
    TIME_STAMP TIMESTAMP,
    PAGE varchar(80),
    PORTLET varchar(255),
    STATUS int4,
    ELAPSED_TIME int4
);







-----------------------------------------------------------------------------
-- PAGE_STATISTICS
-----------------------------------------------------------------------------

CREATE TABLE PAGE_STATISTICS
(
    IPADDRESS varchar(80),
    USER_NAME varchar(80),
    TIME_STAMP TIMESTAMP,
    PAGE varchar(80),
    STATUS int4,
    ELAPSED_TIME int4
);







-----------------------------------------------------------------------------
-- USER_STATISTICS
-----------------------------------------------------------------------------

CREATE TABLE USER_STATISTICS
(
    IPADDRESS varchar(80),
    USER_NAME varchar(80),
    TIME_STAMP TIMESTAMP,
    STATUS int4,
    ELAPSED_TIME int4
);




























