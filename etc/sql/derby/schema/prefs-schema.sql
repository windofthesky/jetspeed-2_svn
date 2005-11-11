-----------------------------------------------------------------------------
-- PREFS_PROPERTY_VALUE
-----------------------------------------------------------------------------

CREATE TABLE PREFS_PROPERTY_VALUE
(
    PROPERTY_VALUE_ID INTEGER NOT NULL,
    NODE_ID INTEGER,
    PROPERTY_KEY_ID INTEGER,
    BOOLEAN_VALUE INTEGER,
    DATETIME_VALUE TIMESTAMP,
    LONG_VALUE INTEGER,
    DOUBLE_VALUE DOUBLE,
    TEXT_VALUE VARCHAR(254),
    CREATION_DATE TIMESTAMP,
    MODIFIED_DATE TIMESTAMP,
    PRIMARY KEY(PROPERTY_VALUE_ID));

-----------------------------------------------------------------------------
-- PREFS_NODE_PROPERTY_KEY
-----------------------------------------------------------------------------

CREATE TABLE PREFS_NODE_PROPERTY_KEY
(
    NODE_ID INTEGER NOT NULL,
    PROPERTY_KEY_ID INTEGER NOT NULL,
    PRIMARY KEY(NODE_ID,PROPERTY_KEY_ID));

-----------------------------------------------------------------------------
-- PREFS_PROPERTY_KEY
-----------------------------------------------------------------------------

CREATE TABLE PREFS_PROPERTY_KEY
(
    PROPERTY_KEY_ID INTEGER NOT NULL,
    PROPERTY_NAME VARCHAR(100),
    PROPERTY_TYPE SMALLINT,
    CREATION_DATE TIMESTAMP,
    MODIFIED_DATE TIMESTAMP,
    PRIMARY KEY(PROPERTY_KEY_ID));

-----------------------------------------------------------------------------
-- PREFS_NODE
-----------------------------------------------------------------------------

CREATE TABLE PREFS_NODE
(
    NODE_ID INTEGER NOT NULL,
    PARENT_NODE_ID INTEGER,
    NODE_NAME VARCHAR(100),
    NODE_TYPE SMALLINT,
    FULL_PATH VARCHAR(254),
    CREATION_DATE TIMESTAMP,
    MODIFIED_DATE TIMESTAMP,
    PRIMARY KEY(NODE_ID),
    FOREIGN KEY (PARENT_NODE_ID) REFERENCES PREFS_NODE (NODE_ID)
    );

