----------------------------------------------------------------------------
-- PSML Roots
----------------------------------------------------------------------------
CREATE TABLE PSML
(
    PSML_ID INTEGER NOT NULL PRIMARY KEY,
    NAME VARCHAR (32),
    PSML_TYPE SMALLINT NOT NULL,
    MEDIA_TYPE VARCHAR (128),
    LANGUAGE VARCHAR (2),
    COUNTRY VARCHAR (2),
    PAGE VARCHAR (128)
);

ALTER TABLE PSML ADD CONSTRAINT UK_PSML UNIQUE (PSML_TYPE, NAME, MEDIA_TYPE, LANGUAGE, COUNTRY, PAGE);
-- CREATE INDEX IX_PSML_1 ON PSML (PSML_TYPE, NAME, MEDIA_TYPE, LANGUAGE, COUNTRY, PAGE);

----------------------------------------------------------------------------
-- PSML Entries
----------------------------------------------------------------------------
CREATE TABLE PSML_ENTRY
( 
    PSML_ENTRY_ID INTEGER NOT NULL PRIMARY KEY,
    PSML_ID INTEGER NOT NULL,
    ENTRY_TYPE SMALLINT NOT NULL,
    PARENT_ID INTEGER,
    PORTLET_ID INTEGER,
    LAYOUT_ID INTEGER,
    CONTROL_ID INTEGER,
    SKIN_ID INTEGER,
    SECURITY_ID INTEGER
);

----------------------------------------------------------------------------
-- Layout (Controller) Registry
----------------------------------------------------------------------------
CREATE TABLE LAYOUT
( 
    LAYOUT_ID INTEGER NOT NULL PRIMARY KEY,
    CLASSNAME VARCHAR (128),
    TEMPLATE VARCHAR (128),
    ACTION VARCHAR (128),
    TITLE VARCHAR (128),
    DESCRIPTION VARCHAR (256)
    -- TODO PARAMETERS
    -- TODO MEDIA TYPES
 
);

----------------------------------------------------------------------------
-- Control (Window) Registry
----------------------------------------------------------------------------
CREATE TABLE CONTROL
( 
    CONTROL_ID INTEGER NOT NULL PRIMARY KEY,
    CLASSNAME VARCHAR (128),
    TEMPLATE VARCHAR (128),
    ACTION VARCHAR (128),
    TITLE VARCHAR (128),
    DESCRIPTION VARCHAR (256)
    -- TODO PARAMETERS
    -- TODO MEDIA TYPES
 
);

----------------------------------------------------------------------------
-- Skin Registry
----------------------------------------------------------------------------
CREATE TABLE SKIN
( 
    SKIN_ID INTEGER NOT NULL PRIMARY KEY
    -- TODO  
);

----------------------------------------------------------------------------
-- Security Registry
----------------------------------------------------------------------------
CREATE TABLE SECURITY
( 
    SECURITY_ID INTEGER NOT NULL PRIMARY KEY,
    NAME VARCHAR (128) NOT NULL,
    TITLE VARCHAR (128),
    DESCRIPTION VARCHAR (256)
    -- TODO ACCESS ACTIONS
    -- TODO ALLOW IF CONSTRAINTS (ROLE, USER, GROUP, OWNER)
);

CREATE INDEX IX_PSML_ENTRY_1 ON PSML_ENTRY (PSML_ID, PARENT_ID);
ALTER TABLE PSML_ENTRY ADD CONSTRAINT FK_PSML_ENTRY_1 FOREIGN KEY (PSML_ID) REFERENCES PSML (PSML_ID) ON DELETE CASCADE;
ALTER TABLE PSML_ENTRY ADD CONSTRAINT FK_PSML_ENTRY_2 FOREIGN KEY (PARENT_ID) REFERENCES PSML_ENTRY (PSML_ENTRY_ID) ON DELETE CASCADE;
-- ALTER TABLE PSML_ENTRY ADD CONSTRAINT FK_PSML_ENTRY_3 FOREIGN KEY (LAYOUT_ID) REFERENCES LAYOUT (LAYOUT_ID) ON DELETE SET NULL;
-- ALTER TABLE PSML_ENTRY ADD CONSTRAINT FK_PSML_ENTRY_4 FOREIGN KEY (CONTROL_ID) REFERENCES CONTROL (CONTROL_ID) ON DELETE SET NULL;
-- ALTER TABLE PSML_ENTRY ADD CONSTRAINT FK_PSML_ENTRY_5 FOREIGN KEY (SKIN_ID) REFERENCES SKIN (SKIN_ID) ON DELETE SET NULL;
-- ALTER TABLE PSML_ENTRY ADD CONSTRAINT FK_PSML_ENTRY_6 FOREIGN KEY (SECURITY_ID) REFERENCES SKIN (SECURITY_ID) ON DELETE SET NULL;

