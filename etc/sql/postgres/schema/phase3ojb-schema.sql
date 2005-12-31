
-----------------------------------------------------------------------------
-- OJB_HL_SEQ
-----------------------------------------------------------------------------

CREATE TABLE OJB_HL_SEQ
(
    TABLENAME varchar(175) NOT NULL,
    FIELDNAME varchar(70) NOT NULL,
    MAX_KEY int4,
    GRAB_SIZE int4,
    VERSION int4
);

ALTER TABLE OJB_HL_SEQ
    ADD CONSTRAINT OJB_HL_SEQ_PK
PRIMARY KEY (TABLENAME,FIELDNAME);






-----------------------------------------------------------------------------
-- OJB_LOCKENTRY
-----------------------------------------------------------------------------

CREATE TABLE OJB_LOCKENTRY
(
    OID_ varchar(250) NOT NULL,
    TX_ID varchar(50) NOT NULL,
    TIMESTAMP_ TIMESTAMP,
    ISOLATIONLEVEL int4,
    LOCKTYPE int4
);

ALTER TABLE OJB_LOCKENTRY
    ADD CONSTRAINT OJB_LOCKENTRY_PK
PRIMARY KEY (OID_,TX_ID);






-----------------------------------------------------------------------------
-- OJB_NRM
-----------------------------------------------------------------------------

CREATE TABLE OJB_NRM
(
    NAME varchar(250) NOT NULL,
    OID_ bytea
);

ALTER TABLE OJB_NRM
    ADD CONSTRAINT OJB_NRM_PK
PRIMARY KEY (NAME);






-----------------------------------------------------------------------------
-- OJB_DLIST
-----------------------------------------------------------------------------

CREATE TABLE OJB_DLIST
(
    ID int4 NOT NULL,
    SIZE_ int4
);

ALTER TABLE OJB_DLIST
    ADD CONSTRAINT OJB_DLIST_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- OJB_DLIST_ENTRIES
-----------------------------------------------------------------------------

CREATE TABLE OJB_DLIST_ENTRIES
(
    ID int4 NOT NULL,
    DLIST_ID int4,
    POSITION_ int4,
    OID_ bytea
);

ALTER TABLE OJB_DLIST_ENTRIES
    ADD CONSTRAINT OJB_DLIST_ENTRIES_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- OJB_DSET
-----------------------------------------------------------------------------

CREATE TABLE OJB_DSET
(
    ID int4 NOT NULL,
    SIZE_ int4
);

ALTER TABLE OJB_DSET
    ADD CONSTRAINT OJB_DSET_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- OJB_DSET_ENTRIES
-----------------------------------------------------------------------------

CREATE TABLE OJB_DSET_ENTRIES
(
    ID int4 NOT NULL,
    DLIST_ID int4,
    POSITION_ int4,
    OID_ bytea
);

ALTER TABLE OJB_DSET_ENTRIES
    ADD CONSTRAINT OJB_DSET_ENTRIES_PK
PRIMARY KEY (ID);






-----------------------------------------------------------------------------
-- OJB_DMAP
-----------------------------------------------------------------------------

CREATE TABLE OJB_DMAP
(
    ID int4 NOT NULL,
    SIZE_ int4
);

ALTER TABLE OJB_DMAP
    ADD CONSTRAINT OJB_DMAP_PK
PRIMARY KEY (ID);





















