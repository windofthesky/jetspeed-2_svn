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
    VERSION VARCHAR(40),
    PREFERRED_MIMETYPE_ID INTEGER NOT NULL
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


