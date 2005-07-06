-----------------------------------------------------------------------------
-- Copyright 2004 The Apache Software Foundation
-- 
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
-- http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-----------------------------------------------------------------------------

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


