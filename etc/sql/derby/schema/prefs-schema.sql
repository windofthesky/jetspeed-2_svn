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
    
-----------------------------------------------------------------------------
-- PREFS_PROPERTY_VALUE
-----------------------------------------------------------------------------

CREATE TABLE PREFS_PROPERTY_VALUE
(
    PROPERTY_VALUE_ID INTEGER NOT NULL,
    NODE_ID INTEGER,
    PROPERTY_NAME VARCHAR(100),
    PROPERTY_VALUE VARCHAR(254),
    CREATION_DATE TIMESTAMP,
    MODIFIED_DATE TIMESTAMP,
    PRIMARY KEY(PROPERTY_VALUE_ID)
    -- Still an issue with OJB 1.0.3 when deleting M-N. Foreign Key Violation.
    -- FOREIGN KEY (NODE_ID) REFERENCES PREFS_NODE (NODE_ID)
    );