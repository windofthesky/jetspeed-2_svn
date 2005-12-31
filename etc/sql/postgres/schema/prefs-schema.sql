
-----------------------------------------------------------------------------
-- PREFS_PROPERTY_VALUE
-----------------------------------------------------------------------------

CREATE TABLE PREFS_PROPERTY_VALUE
(
    PROPERTY_VALUE_ID int4 NOT NULL,
    NODE_ID int4,
    PROPERTY_NAME varchar(100),
    PROPERTY_VALUE varchar(254),
    CREATION_DATE TIMESTAMP,
    MODIFIED_DATE TIMESTAMP
);

ALTER TABLE PREFS_PROPERTY_VALUE
    ADD CONSTRAINT PREFS_PROPERTY_VALUE_PK
PRIMARY KEY (PROPERTY_VALUE_ID);






-----------------------------------------------------------------------------
-- PREFS_NODE
-----------------------------------------------------------------------------

CREATE TABLE PREFS_NODE
(
    NODE_ID int4 NOT NULL,
    PARENT_NODE_ID int4,
    NODE_NAME varchar(100),
    NODE_TYPE int4,
    FULL_PATH varchar(254),
    CREATION_DATE TIMESTAMP,
    MODIFIED_DATE TIMESTAMP
);

ALTER TABLE PREFS_NODE
    ADD CONSTRAINT PREFS_NODE_PK
PRIMARY KEY (NODE_ID);







ALTER TABLE PREFS_NODE
    ADD CONSTRAINT PREFS_NODE_FK_1 FOREIGN KEY (PARENT_NODE_ID)
    REFERENCES PREFS_NODE (NODE_ID)
;



