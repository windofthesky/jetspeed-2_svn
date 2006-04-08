
-----------------------------------------------------------------------------
-- FOLDER
-----------------------------------------------------------------------------

CREATE TABLE FOLDER
(
    FOLDER_ID int4 NOT NULL,
    PARENT_ID int4,
    PATH varchar(240) NOT NULL,
    NAME varchar(80) NOT NULL,
    TITLE varchar(100),
    SHORT_TITLE varchar(40),
    IS_HIDDEN int2 NOT NULL,
    DEFAULT_PAGE_NAME varchar(80),
    SUBSITE varchar(40),
    USER_PRINCIPAL varchar(40),
    ROLE_PRINCIPAL varchar(40),
    GROUP_PRINCIPAL varchar(40),
    MEDIATYPE varchar(15),
    LOCALE varchar(20),
    EXT_ATTR_NAME varchar(15),
    EXT_ATTR_VALUE varchar(40),
    OWNER_PRINCIPAL varchar(40),
    CONSTRAINT UN_FOLDER_1 UNIQUE (PATH)
);

ALTER TABLE FOLDER
    ADD CONSTRAINT FOLDER_PK
PRIMARY KEY (FOLDER_ID);

CREATE INDEX IX_FOLDER_1 ON FOLDER (PARENT_ID);





-----------------------------------------------------------------------------
-- FOLDER_METADATA
-----------------------------------------------------------------------------

CREATE TABLE FOLDER_METADATA
(
    METADATA_ID int4 NOT NULL,
    FOLDER_ID int4 NOT NULL,
    NAME varchar(15) NOT NULL,
    LOCALE varchar(20) NOT NULL,
    VALUE varchar(100) NOT NULL,
    CONSTRAINT UN_FOLDER_METADATA_1 UNIQUE (FOLDER_ID, NAME, LOCALE, VALUE)
);

ALTER TABLE FOLDER_METADATA
    ADD CONSTRAINT FOLDER_METADATA_PK
PRIMARY KEY (METADATA_ID);

CREATE INDEX IX_FOLDER_METADATA_1 ON FOLDER_METADATA (FOLDER_ID);





-----------------------------------------------------------------------------
-- FOLDER_CONSTRAINT
-----------------------------------------------------------------------------

CREATE TABLE FOLDER_CONSTRAINT
(
    CONSTRAINT_ID int4 NOT NULL,
    FOLDER_ID int4 NOT NULL,
    APPLY_ORDER int4 NOT NULL,
    USER_PRINCIPALS_ACL varchar(120),
    ROLE_PRINCIPALS_ACL varchar(120),
    GROUP_PRINCIPALS_ACL varchar(120),
    PERMISSIONS_ACL varchar(120)
);

ALTER TABLE FOLDER_CONSTRAINT
    ADD CONSTRAINT FOLDER_CONSTRAINT_PK
PRIMARY KEY (CONSTRAINT_ID);

CREATE INDEX IX_FOLDER_CONSTRAINT_1 ON FOLDER_CONSTRAINT (FOLDER_ID);





-----------------------------------------------------------------------------
-- FOLDER_CONSTRAINTS_REF
-----------------------------------------------------------------------------

CREATE TABLE FOLDER_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID int4 NOT NULL,
    FOLDER_ID int4 NOT NULL,
    APPLY_ORDER int4 NOT NULL,
    NAME varchar(40) NOT NULL,
    CONSTRAINT UN_FOLDER_CONSTRAINTS_REF_1 UNIQUE (FOLDER_ID, NAME)
);

ALTER TABLE FOLDER_CONSTRAINTS_REF
    ADD CONSTRAINT FOLDER_CONSTRAINTS_REF_PK
PRIMARY KEY (CONSTRAINTS_REF_ID);

CREATE INDEX IX_FOLDER_CONSTRAINTS_REF_1 ON FOLDER_CONSTRAINTS_REF (FOLDER_ID);





-----------------------------------------------------------------------------
-- FOLDER_ORDER
-----------------------------------------------------------------------------

CREATE TABLE FOLDER_ORDER
(
    ORDER_ID int4 NOT NULL,
    FOLDER_ID int4 NOT NULL,
    SORT_ORDER int4 NOT NULL,
    NAME varchar(80) NOT NULL,
    CONSTRAINT UN_FOLDER_ORDER_1 UNIQUE (FOLDER_ID, NAME)
);

ALTER TABLE FOLDER_ORDER
    ADD CONSTRAINT FOLDER_ORDER_PK
PRIMARY KEY (ORDER_ID);

CREATE INDEX IX_FOLDER_ORDER_1 ON FOLDER_ORDER (FOLDER_ID);





-----------------------------------------------------------------------------
-- FOLDER_MENU
-----------------------------------------------------------------------------

CREATE TABLE FOLDER_MENU
(
    MENU_ID int4 NOT NULL,
    CLASS_NAME varchar(100) NOT NULL,
    PARENT_ID int4,
    FOLDER_ID int4,
    ELEMENT_ORDER int4,
    NAME varchar(100),
    TITLE varchar(100),
    SHORT_TITLE varchar(40),
    TEXT varchar(100),
    OPTIONS varchar(255),
    DEPTH int4,
    IS_PATHS int2,
    IS_REGEXP int2,
    PROFILE varchar(80),
    OPTIONS_ORDER varchar(255),
    SKIN varchar(80),
    IS_NEST int2,
    CONSTRAINT UN_FOLDER_MENU_1 UNIQUE (FOLDER_ID, NAME)
);

ALTER TABLE FOLDER_MENU
    ADD CONSTRAINT FOLDER_MENU_PK
PRIMARY KEY (MENU_ID);

CREATE INDEX IX_FOLDER_MENU_1 ON FOLDER_MENU (PARENT_ID);





-----------------------------------------------------------------------------
-- FOLDER_MENU_METADATA
-----------------------------------------------------------------------------

CREATE TABLE FOLDER_MENU_METADATA
(
    METADATA_ID int4 NOT NULL,
    MENU_ID int4 NOT NULL,
    NAME varchar(15) NOT NULL,
    LOCALE varchar(20) NOT NULL,
    VALUE varchar(100) NOT NULL,
    CONSTRAINT UN_FOLDER_MENU_METADATA_1 UNIQUE (MENU_ID, NAME, LOCALE, VALUE)
);

ALTER TABLE FOLDER_MENU_METADATA
    ADD CONSTRAINT FOLDER_MENU_METADATA_PK
PRIMARY KEY (METADATA_ID);

CREATE INDEX IX_FOLDER_MENU_METADATA_1 ON FOLDER_MENU_METADATA (MENU_ID);





-----------------------------------------------------------------------------
-- PAGE
-----------------------------------------------------------------------------

CREATE TABLE PAGE
(
    PAGE_ID int4 NOT NULL,
    PARENT_ID int4 NOT NULL,
    PATH varchar(240) NOT NULL,
    NAME varchar(80) NOT NULL,
    VERSION varchar(40),
    TITLE varchar(100),
    SHORT_TITLE varchar(40),
    IS_HIDDEN int2 NOT NULL,
    SKIN varchar(80),
    DEFAULT_LAYOUT_DECORATOR varchar(80),
    DEFAULT_PORTLET_DECORATOR varchar(80),
    SUBSITE varchar(40),
    USER_PRINCIPAL varchar(40),
    ROLE_PRINCIPAL varchar(40),
    GROUP_PRINCIPAL varchar(40),
    MEDIATYPE varchar(15),
    LOCALE varchar(20),
    EXT_ATTR_NAME varchar(15),
    EXT_ATTR_VALUE varchar(40),
    OWNER_PRINCIPAL varchar(40),
    CONSTRAINT UN_PAGE_1 UNIQUE (PATH)
);

ALTER TABLE PAGE
    ADD CONSTRAINT PAGE_PK
PRIMARY KEY (PAGE_ID);

CREATE INDEX IX_PAGE_1 ON PAGE (PARENT_ID);





-----------------------------------------------------------------------------
-- PAGE_METADATA
-----------------------------------------------------------------------------

CREATE TABLE PAGE_METADATA
(
    METADATA_ID int4 NOT NULL,
    PAGE_ID int4 NOT NULL,
    NAME varchar(15) NOT NULL,
    LOCALE varchar(20) NOT NULL,
    VALUE varchar(100) NOT NULL,
    CONSTRAINT UN_PAGE_METADATA_1 UNIQUE (PAGE_ID, NAME, LOCALE, VALUE)
);

ALTER TABLE PAGE_METADATA
    ADD CONSTRAINT PAGE_METADATA_PK
PRIMARY KEY (METADATA_ID);

CREATE INDEX IX_PAGE_METADATA_1 ON PAGE_METADATA (PAGE_ID);





-----------------------------------------------------------------------------
-- PAGE_CONSTRAINT
-----------------------------------------------------------------------------

CREATE TABLE PAGE_CONSTRAINT
(
    CONSTRAINT_ID int4 NOT NULL,
    PAGE_ID int4 NOT NULL,
    APPLY_ORDER int4 NOT NULL,
    USER_PRINCIPALS_ACL varchar(120),
    ROLE_PRINCIPALS_ACL varchar(120),
    GROUP_PRINCIPALS_ACL varchar(120),
    PERMISSIONS_ACL varchar(120)
);

ALTER TABLE PAGE_CONSTRAINT
    ADD CONSTRAINT PAGE_CONSTRAINT_PK
PRIMARY KEY (CONSTRAINT_ID);

CREATE INDEX IX_PAGE_CONSTRAINT_1 ON PAGE_CONSTRAINT (PAGE_ID);





-----------------------------------------------------------------------------
-- PAGE_CONSTRAINTS_REF
-----------------------------------------------------------------------------

CREATE TABLE PAGE_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID int4 NOT NULL,
    PAGE_ID int4 NOT NULL,
    APPLY_ORDER int4 NOT NULL,
    NAME varchar(40) NOT NULL,
    CONSTRAINT UN_PAGE_CONSTRAINTS_REF_1 UNIQUE (PAGE_ID, NAME)
);

ALTER TABLE PAGE_CONSTRAINTS_REF
    ADD CONSTRAINT PAGE_CONSTRAINTS_REF_PK
PRIMARY KEY (CONSTRAINTS_REF_ID);

CREATE INDEX IX_PAGE_CONSTRAINTS_REF_1 ON PAGE_CONSTRAINTS_REF (PAGE_ID);





-----------------------------------------------------------------------------
-- PAGE_MENU
-----------------------------------------------------------------------------

CREATE TABLE PAGE_MENU
(
    MENU_ID int4 NOT NULL,
    CLASS_NAME varchar(100) NOT NULL,
    PARENT_ID int4,
    PAGE_ID int4,
    ELEMENT_ORDER int4,
    NAME varchar(100),
    TITLE varchar(100),
    SHORT_TITLE varchar(40),
    TEXT varchar(100),
    OPTIONS varchar(255),
    DEPTH int4,
    IS_PATHS int2,
    IS_REGEXP int2,
    PROFILE varchar(80),
    OPTIONS_ORDER varchar(255),
    SKIN varchar(80),
    IS_NEST int2,
    CONSTRAINT UN_PAGE_MENU_1 UNIQUE (PAGE_ID, NAME)
);

ALTER TABLE PAGE_MENU
    ADD CONSTRAINT PAGE_MENU_PK
PRIMARY KEY (MENU_ID);

CREATE INDEX IX_PAGE_MENU_1 ON PAGE_MENU (PARENT_ID);





-----------------------------------------------------------------------------
-- PAGE_MENU_METADATA
-----------------------------------------------------------------------------

CREATE TABLE PAGE_MENU_METADATA
(
    METADATA_ID int4 NOT NULL,
    MENU_ID int4 NOT NULL,
    NAME varchar(15) NOT NULL,
    LOCALE varchar(20) NOT NULL,
    VALUE varchar(100) NOT NULL,
    CONSTRAINT UN_PAGE_MENU_METADATA_1 UNIQUE (MENU_ID, NAME, LOCALE, VALUE)
);

ALTER TABLE PAGE_MENU_METADATA
    ADD CONSTRAINT PAGE_MENU_METADATA_PK
PRIMARY KEY (METADATA_ID);

CREATE INDEX IX_PAGE_MENU_METADATA_1 ON PAGE_MENU_METADATA (MENU_ID);





-----------------------------------------------------------------------------
-- FRAGMENT
-----------------------------------------------------------------------------

CREATE TABLE FRAGMENT
(
    FRAGMENT_ID int4 NOT NULL,
    PARENT_ID int4,
    PAGE_ID int4,
    NAME varchar(100),
    TITLE varchar(100),
    SHORT_TITLE varchar(40),
    TYPE varchar(40),
    SKIN varchar(80),
    DECORATOR varchar(80),
    STATE varchar(10),
    PMODE varchar(10),
    LAYOUT_ROW int4,
    LAYOUT_COLUMN int4,
    LAYOUT_SIZES varchar(20),
    LAYOUT_X real,
    LAYOUT_Y real,
    LAYOUT_Z real,
    LAYOUT_WIDTH real,
    LAYOUT_HEIGHT real,
    EXT_PROP_NAME_1 varchar(40),
    EXT_PROP_VALUE_1 varchar(80),
    EXT_PROP_NAME_2 varchar(40),
    EXT_PROP_VALUE_2 varchar(80),
    OWNER_PRINCIPAL varchar(40),
    CONSTRAINT UN_FRAGMENT_1 UNIQUE (PAGE_ID)
);

ALTER TABLE FRAGMENT
    ADD CONSTRAINT FRAGMENT_PK
PRIMARY KEY (FRAGMENT_ID);

CREATE INDEX IX_FRAGMENT_1 ON FRAGMENT (PARENT_ID);





-----------------------------------------------------------------------------
-- FRAGMENT_CONSTRAINT
-----------------------------------------------------------------------------

CREATE TABLE FRAGMENT_CONSTRAINT
(
    CONSTRAINT_ID int4 NOT NULL,
    FRAGMENT_ID int4 NOT NULL,
    APPLY_ORDER int4 NOT NULL,
    USER_PRINCIPALS_ACL varchar(120),
    ROLE_PRINCIPALS_ACL varchar(120),
    GROUP_PRINCIPALS_ACL varchar(120),
    PERMISSIONS_ACL varchar(120)
);

ALTER TABLE FRAGMENT_CONSTRAINT
    ADD CONSTRAINT FRAGMENT_CONSTRAINT_PK
PRIMARY KEY (CONSTRAINT_ID);

CREATE INDEX IX_FRAGMENT_CONSTRAINT_1 ON FRAGMENT_CONSTRAINT (FRAGMENT_ID);





-----------------------------------------------------------------------------
-- FRAGMENT_CONSTRAINTS_REF
-----------------------------------------------------------------------------

CREATE TABLE FRAGMENT_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID int4 NOT NULL,
    FRAGMENT_ID int4 NOT NULL,
    APPLY_ORDER int4 NOT NULL,
    NAME varchar(40) NOT NULL,
    CONSTRAINT UN_FRAGMENT_CONSTRAINTS_REF_1 UNIQUE (FRAGMENT_ID, NAME)
);

ALTER TABLE FRAGMENT_CONSTRAINTS_REF
    ADD CONSTRAINT FRAGMENT_CONSTRAINTS_REF_PK
PRIMARY KEY (CONSTRAINTS_REF_ID);

CREATE INDEX IX_FRAGMENT_CONSTRAINTS_REF_1 ON FRAGMENT_CONSTRAINTS_REF (FRAGMENT_ID);





-----------------------------------------------------------------------------
-- FRAGMENT_PREF
-----------------------------------------------------------------------------

CREATE TABLE FRAGMENT_PREF
(
    PREF_ID int4 NOT NULL,
    FRAGMENT_ID int4 NOT NULL,
    NAME varchar(40) NOT NULL,
    IS_READ_ONLY int2 NOT NULL,
    CONSTRAINT UN_FRAGMENT_PREF_1 UNIQUE (FRAGMENT_ID, NAME)
);

ALTER TABLE FRAGMENT_PREF
    ADD CONSTRAINT FRAGMENT_PREF_PK
PRIMARY KEY (PREF_ID);

CREATE INDEX IX_FRAGMENT_PREF_1 ON FRAGMENT_PREF (FRAGMENT_ID);





-----------------------------------------------------------------------------
-- FRAGMENT_PREF_VALUE
-----------------------------------------------------------------------------

CREATE TABLE FRAGMENT_PREF_VALUE
(
    PREF_VALUE_ID int4 NOT NULL,
    PREF_ID int4 NOT NULL,
    VALUE_ORDER int4 NOT NULL,
    VALUE varchar(100) NOT NULL
);

ALTER TABLE FRAGMENT_PREF_VALUE
    ADD CONSTRAINT FRAGMENT_PREF_VALUE_PK
PRIMARY KEY (PREF_VALUE_ID);

CREATE INDEX IX_FRAGMENT_PREF_VALUE_1 ON FRAGMENT_PREF_VALUE (PREF_ID);





-----------------------------------------------------------------------------
-- LINK
-----------------------------------------------------------------------------

CREATE TABLE LINK
(
    LINK_ID int4 NOT NULL,
    PARENT_ID int4 NOT NULL,
    PATH varchar(240) NOT NULL,
    NAME varchar(80) NOT NULL,
    VERSION varchar(40),
    TITLE varchar(100),
    SHORT_TITLE varchar(40),
    IS_HIDDEN int2 NOT NULL,
    TARGET varchar(80),
    URL varchar(255),
    SUBSITE varchar(40),
    USER_PRINCIPAL varchar(40),
    ROLE_PRINCIPAL varchar(40),
    GROUP_PRINCIPAL varchar(40),
    MEDIATYPE varchar(15),
    LOCALE varchar(20),
    EXT_ATTR_NAME varchar(15),
    EXT_ATTR_VALUE varchar(40),
    OWNER_PRINCIPAL varchar(40),
    CONSTRAINT UN_LINK_1 UNIQUE (PATH)
);

ALTER TABLE LINK
    ADD CONSTRAINT LINK_PK
PRIMARY KEY (LINK_ID);

CREATE INDEX IX_LINK_1 ON LINK (PARENT_ID);





-----------------------------------------------------------------------------
-- LINK_METADATA
-----------------------------------------------------------------------------

CREATE TABLE LINK_METADATA
(
    METADATA_ID int4 NOT NULL,
    LINK_ID int4 NOT NULL,
    NAME varchar(15) NOT NULL,
    LOCALE varchar(20) NOT NULL,
    VALUE varchar(100) NOT NULL,
    CONSTRAINT UN_LINK_METADATA_1 UNIQUE (LINK_ID, NAME, LOCALE, VALUE)
);

ALTER TABLE LINK_METADATA
    ADD CONSTRAINT LINK_METADATA_PK
PRIMARY KEY (METADATA_ID);

CREATE INDEX IX_LINK_METADATA_1 ON LINK_METADATA (LINK_ID);





-----------------------------------------------------------------------------
-- LINK_CONSTRAINT
-----------------------------------------------------------------------------

CREATE TABLE LINK_CONSTRAINT
(
    CONSTRAINT_ID int4 NOT NULL,
    LINK_ID int4 NOT NULL,
    APPLY_ORDER int4 NOT NULL,
    USER_PRINCIPALS_ACL varchar(120),
    ROLE_PRINCIPALS_ACL varchar(120),
    GROUP_PRINCIPALS_ACL varchar(120),
    PERMISSIONS_ACL varchar(120)
);

ALTER TABLE LINK_CONSTRAINT
    ADD CONSTRAINT LINK_CONSTRAINT_PK
PRIMARY KEY (CONSTRAINT_ID);

CREATE INDEX IX_LINK_CONSTRAINT_1 ON LINK_CONSTRAINT (LINK_ID);





-----------------------------------------------------------------------------
-- LINK_CONSTRAINTS_REF
-----------------------------------------------------------------------------

CREATE TABLE LINK_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID int4 NOT NULL,
    LINK_ID int4 NOT NULL,
    APPLY_ORDER int4 NOT NULL,
    NAME varchar(40) NOT NULL,
    CONSTRAINT UN_LINK_CONSTRAINTS_REF_1 UNIQUE (LINK_ID, NAME)
);

ALTER TABLE LINK_CONSTRAINTS_REF
    ADD CONSTRAINT LINK_CONSTRAINTS_REF_PK
PRIMARY KEY (CONSTRAINTS_REF_ID);

CREATE INDEX IX_LINK_CONSTRAINTS_REF_1 ON LINK_CONSTRAINTS_REF (LINK_ID);





-----------------------------------------------------------------------------
-- PAGE_SECURITY
-----------------------------------------------------------------------------

CREATE TABLE PAGE_SECURITY
(
    PAGE_SECURITY_ID int4 NOT NULL,
    PARENT_ID int4 NOT NULL,
    PATH varchar(240) NOT NULL,
    NAME varchar(80) NOT NULL,
    VERSION varchar(40),
    SUBSITE varchar(40),
    USER_PRINCIPAL varchar(40),
    ROLE_PRINCIPAL varchar(40),
    GROUP_PRINCIPAL varchar(40),
    MEDIATYPE varchar(15),
    LOCALE varchar(20),
    EXT_ATTR_NAME varchar(15),
    EXT_ATTR_VALUE varchar(40),
    CONSTRAINT UN_PAGE_SECURITY_1 UNIQUE (PARENT_ID),
    CONSTRAINT UN_PAGE_SECURITY_2 UNIQUE (PATH)
);

ALTER TABLE PAGE_SECURITY
    ADD CONSTRAINT PAGE_SECURITY_PK
PRIMARY KEY (PAGE_SECURITY_ID);






-----------------------------------------------------------------------------
-- PAGE_SEC_CONSTRAINTS_DEF
-----------------------------------------------------------------------------

CREATE TABLE PAGE_SEC_CONSTRAINTS_DEF
(
    CONSTRAINTS_DEF_ID int4 NOT NULL,
    PAGE_SECURITY_ID int4 NOT NULL,
    NAME varchar(40) NOT NULL,
    CONSTRAINT UN_PAGE_SEC_CONSTRAINTS_DEF_1 UNIQUE (PAGE_SECURITY_ID, NAME)
);

ALTER TABLE PAGE_SEC_CONSTRAINTS_DEF
    ADD CONSTRAINT PAGE_SEC_CONSTRAINTS_DEF_PK
PRIMARY KEY (CONSTRAINTS_DEF_ID);

CREATE INDEX IX_PAGE_SEC_CONSTRAINTS_DEF_1 ON PAGE_SEC_CONSTRAINTS_DEF (PAGE_SECURITY_ID);





-----------------------------------------------------------------------------
-- PAGE_SEC_CONSTRAINT_DEF
-----------------------------------------------------------------------------

CREATE TABLE PAGE_SEC_CONSTRAINT_DEF
(
    CONSTRAINT_DEF_ID int4 NOT NULL,
    CONSTRAINTS_DEF_ID int4 NOT NULL,
    APPLY_ORDER int4 NOT NULL,
    USER_PRINCIPALS_ACL varchar(120),
    ROLE_PRINCIPALS_ACL varchar(120),
    GROUP_PRINCIPALS_ACL varchar(120),
    PERMISSIONS_ACL varchar(120)
);

ALTER TABLE PAGE_SEC_CONSTRAINT_DEF
    ADD CONSTRAINT PAGE_SEC_CONSTRAINT_DEF_PK
PRIMARY KEY (CONSTRAINT_DEF_ID);

CREATE INDEX IX_PAGE_SEC_CONSTRAINT_DEF_1 ON PAGE_SEC_CONSTRAINT_DEF (CONSTRAINTS_DEF_ID);





-----------------------------------------------------------------------------
-- PAGE_SEC_CONSTRAINTS_REF
-----------------------------------------------------------------------------

CREATE TABLE PAGE_SEC_CONSTRAINTS_REF
(
    CONSTRAINTS_REF_ID int4 NOT NULL,
    PAGE_SECURITY_ID int4 NOT NULL,
    APPLY_ORDER int4 NOT NULL,
    NAME varchar(40) NOT NULL,
    CONSTRAINT UN_PAGE_SEC_CONSTRAINTS_REF_1 UNIQUE (PAGE_SECURITY_ID, NAME)
);

ALTER TABLE PAGE_SEC_CONSTRAINTS_REF
    ADD CONSTRAINT PAGE_SEC_CONSTRAINTS_REF_PK
PRIMARY KEY (CONSTRAINTS_REF_ID);

CREATE INDEX IX_PAGE_SEC_CONSTRAINTS_REF_1 ON PAGE_SEC_CONSTRAINTS_REF (PAGE_SECURITY_ID);





-----------------------------------------------------------------------------
-- PROFILING_RULE
-----------------------------------------------------------------------------

CREATE TABLE PROFILING_RULE
(
    RULE_ID varchar(80) NOT NULL,
    CLASS_NAME varchar(100) NOT NULL,
    TITLE varchar(100)
);

ALTER TABLE PROFILING_RULE
    ADD CONSTRAINT PROFILING_RULE_PK
PRIMARY KEY (RULE_ID);






-----------------------------------------------------------------------------
-- RULE_CRITERION
-----------------------------------------------------------------------------

CREATE TABLE RULE_CRITERION
(
    CRITERION_ID varchar(80) NOT NULL,
    RULE_ID varchar(80) NOT NULL,
    FALLBACK_ORDER int4 NOT NULL,
    REQUEST_TYPE varchar(40) NOT NULL,
    NAME varchar(80) NOT NULL,
    COLUMN_VALUE varchar(128),
    FALLBACK_TYPE int4 default 1
);

ALTER TABLE RULE_CRITERION
    ADD CONSTRAINT RULE_CRITERION_PK
PRIMARY KEY (CRITERION_ID);

CREATE INDEX IX_RULE_CRITERION_1 ON RULE_CRITERION (RULE_ID, FALLBACK_ORDER);





-----------------------------------------------------------------------------
-- PRINCIPAL_RULE_ASSOC
-----------------------------------------------------------------------------

CREATE TABLE PRINCIPAL_RULE_ASSOC
(
    PRINCIPAL_NAME varchar(80) NOT NULL,
    LOCATOR_NAME varchar(80) NOT NULL,
    RULE_ID varchar(80) NOT NULL
);

ALTER TABLE PRINCIPAL_RULE_ASSOC
    ADD CONSTRAINT PRINCIPAL_RULE_ASSOC_PK
PRIMARY KEY (PRINCIPAL_NAME,LOCATOR_NAME);






-----------------------------------------------------------------------------
-- PROFILE_PAGE_ASSOC
-----------------------------------------------------------------------------

CREATE TABLE PROFILE_PAGE_ASSOC
(
    LOCATOR_HASH varchar(40) NOT NULL,
    PAGE_ID varchar(80) NOT NULL,
    CONSTRAINT UN_PROFILE_PAGE_1 UNIQUE (LOCATOR_HASH, PAGE_ID)
);







-----------------------------------------------------------------------------
-- CLUBS
-----------------------------------------------------------------------------

CREATE TABLE CLUBS
(
    NAME varchar(80) NOT NULL,
    COUNTRY varchar(40) NOT NULL,
    CITY varchar(40) NOT NULL,
    STADIUM varchar(80) NOT NULL,
    CAPACITY int4,
    FOUNDED int4,
    PITCH varchar(40),
    NICKNAME varchar(40)
);

ALTER TABLE CLUBS
    ADD CONSTRAINT CLUBS_PK
PRIMARY KEY (NAME);





ALTER TABLE FOLDER
    ADD CONSTRAINT FOLDER_FK_1 FOREIGN KEY (PARENT_ID)
    REFERENCES FOLDER (FOLDER_ID)
ON DELETE CASCADE
;



ALTER TABLE FOLDER_METADATA
    ADD CONSTRAINT FOLDER_METADATA_FK_1 FOREIGN KEY (FOLDER_ID)
    REFERENCES FOLDER (FOLDER_ID)
ON DELETE CASCADE
;



ALTER TABLE FOLDER_CONSTRAINT
    ADD CONSTRAINT FOLDER_CONSTRAINT_FK_1 FOREIGN KEY (FOLDER_ID)
    REFERENCES FOLDER (FOLDER_ID)
ON DELETE CASCADE
;



ALTER TABLE FOLDER_CONSTRAINTS_REF
    ADD CONSTRAINT FOLDER_CONSTRAINTS_REF_FK_1 FOREIGN KEY (FOLDER_ID)
    REFERENCES FOLDER (FOLDER_ID)
ON DELETE CASCADE
;



ALTER TABLE FOLDER_ORDER
    ADD CONSTRAINT FOLDER_ORDER_FK_1 FOREIGN KEY (FOLDER_ID)
    REFERENCES FOLDER (FOLDER_ID)
ON DELETE CASCADE
;



ALTER TABLE FOLDER_MENU
    ADD CONSTRAINT FOLDER_MENU_FK_1 FOREIGN KEY (PARENT_ID)
    REFERENCES FOLDER_MENU (MENU_ID)
ON DELETE CASCADE
;

ALTER TABLE FOLDER_MENU
    ADD CONSTRAINT FOLDER_MENU_FK_2 FOREIGN KEY (FOLDER_ID)
    REFERENCES FOLDER (FOLDER_ID)
ON DELETE CASCADE
;



ALTER TABLE FOLDER_MENU_METADATA
    ADD CONSTRAINT FOLDER_MENU_METADATA_FK_1 FOREIGN KEY (MENU_ID)
    REFERENCES FOLDER_MENU (MENU_ID)
ON DELETE CASCADE
;



ALTER TABLE PAGE
    ADD CONSTRAINT PAGE_FK_1 FOREIGN KEY (PARENT_ID)
    REFERENCES FOLDER (FOLDER_ID)
ON DELETE CASCADE
;



ALTER TABLE PAGE_METADATA
    ADD CONSTRAINT PAGE_METADATA_FK_1 FOREIGN KEY (PAGE_ID)
    REFERENCES PAGE (PAGE_ID)
ON DELETE CASCADE
;



ALTER TABLE PAGE_CONSTRAINT
    ADD CONSTRAINT PAGE_CONSTRAINT_FK_1 FOREIGN KEY (PAGE_ID)
    REFERENCES PAGE (PAGE_ID)
ON DELETE CASCADE
;



ALTER TABLE PAGE_CONSTRAINTS_REF
    ADD CONSTRAINT PAGE_CONSTRAINTS_REF_FK_1 FOREIGN KEY (PAGE_ID)
    REFERENCES PAGE (PAGE_ID)
ON DELETE CASCADE
;



ALTER TABLE PAGE_MENU
    ADD CONSTRAINT PAGE_MENU_FK_1 FOREIGN KEY (PARENT_ID)
    REFERENCES PAGE_MENU (MENU_ID)
ON DELETE CASCADE
;

ALTER TABLE PAGE_MENU
    ADD CONSTRAINT PAGE_MENU_FK_2 FOREIGN KEY (PAGE_ID)
    REFERENCES PAGE (PAGE_ID)
ON DELETE CASCADE
;



ALTER TABLE PAGE_MENU_METADATA
    ADD CONSTRAINT PAGE_MENU_METADATA_FK_1 FOREIGN KEY (MENU_ID)
    REFERENCES PAGE_MENU (MENU_ID)
ON DELETE CASCADE
;



ALTER TABLE FRAGMENT
    ADD CONSTRAINT FRAGMENT_FK_1 FOREIGN KEY (PARENT_ID)
    REFERENCES FRAGMENT (FRAGMENT_ID)
ON DELETE CASCADE
;

ALTER TABLE FRAGMENT
    ADD CONSTRAINT FRAGMENT_FK_2 FOREIGN KEY (PAGE_ID)
    REFERENCES PAGE (PAGE_ID)
ON DELETE CASCADE
;



ALTER TABLE FRAGMENT_CONSTRAINT
    ADD CONSTRAINT FRAGMENT_CONSTRAINT_FK_1 FOREIGN KEY (FRAGMENT_ID)
    REFERENCES FRAGMENT (FRAGMENT_ID)
ON DELETE CASCADE
;



ALTER TABLE FRAGMENT_CONSTRAINTS_REF
    ADD CONSTRAINT FRAGMENT_CONSTRAINTS_REF_FK_1 FOREIGN KEY (FRAGMENT_ID)
    REFERENCES FRAGMENT (FRAGMENT_ID)
ON DELETE CASCADE
;



ALTER TABLE FRAGMENT_PREF
    ADD CONSTRAINT FRAGMENT_PREF_FK_1 FOREIGN KEY (FRAGMENT_ID)
    REFERENCES FRAGMENT (FRAGMENT_ID)
ON DELETE CASCADE
;



ALTER TABLE FRAGMENT_PREF_VALUE
    ADD CONSTRAINT FRAGMENT_PREF_VALUE_FK_1 FOREIGN KEY (PREF_ID)
    REFERENCES FRAGMENT_PREF (PREF_ID)
ON DELETE CASCADE
;



ALTER TABLE LINK
    ADD CONSTRAINT LINK_FK_1 FOREIGN KEY (PARENT_ID)
    REFERENCES FOLDER (FOLDER_ID)
ON DELETE CASCADE
;



ALTER TABLE LINK_METADATA
    ADD CONSTRAINT LINK_METADATA_FK_1 FOREIGN KEY (LINK_ID)
    REFERENCES LINK (LINK_ID)
ON DELETE CASCADE
;



ALTER TABLE LINK_CONSTRAINT
    ADD CONSTRAINT LINK_CONSTRAINT_FK_1 FOREIGN KEY (LINK_ID)
    REFERENCES LINK (LINK_ID)
ON DELETE CASCADE
;



ALTER TABLE LINK_CONSTRAINTS_REF
    ADD CONSTRAINT LINK_CONSTRAINTS_REF_FK_1 FOREIGN KEY (LINK_ID)
    REFERENCES LINK (LINK_ID)
ON DELETE CASCADE
;



ALTER TABLE PAGE_SECURITY
    ADD CONSTRAINT PAGE_SECURITY_FK_1 FOREIGN KEY (PARENT_ID)
    REFERENCES FOLDER (FOLDER_ID)
ON DELETE CASCADE
;



ALTER TABLE PAGE_SEC_CONSTRAINTS_DEF
    ADD CONSTRAINT PAGE_SEC_CONSTRAINTS_DEF_FK_1 FOREIGN KEY (PAGE_SECURITY_ID)
    REFERENCES PAGE_SECURITY (PAGE_SECURITY_ID)
ON DELETE CASCADE
;



ALTER TABLE PAGE_SEC_CONSTRAINT_DEF
    ADD CONSTRAINT PAGE_SEC_CONSTRAINT_DEF_FK_1 FOREIGN KEY (CONSTRAINTS_DEF_ID)
    REFERENCES PAGE_SEC_CONSTRAINTS_DEF (CONSTRAINTS_DEF_ID)
ON DELETE CASCADE
;



ALTER TABLE PAGE_SEC_CONSTRAINTS_REF
    ADD CONSTRAINT PAGE_SEC_CONSTRAINTS_REF_FK_1 FOREIGN KEY (PAGE_SECURITY_ID)
    REFERENCES PAGE_SECURITY (PAGE_SECURITY_ID)
ON DELETE CASCADE
;





ALTER TABLE RULE_CRITERION
    ADD CONSTRAINT RULE_CRITERION_FK_1 FOREIGN KEY (RULE_ID)
    REFERENCES PROFILING_RULE (RULE_ID)
ON DELETE CASCADE
;









