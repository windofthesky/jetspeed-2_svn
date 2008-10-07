
/* ---------------------------------------------------------------------- */
/* OJB_HL_SEQ                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_HL_SEQ')
BEGIN
     DECLARE @reftable_1 nvarchar(60), @constraintname_1 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'OJB_HL_SEQ'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_1, @constraintname_1
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_1+' drop constraint '+@constraintname_1)
       FETCH NEXT from refcursor into @reftable_1, @constraintname_1
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE OJB_HL_SEQ
END
;

CREATE TABLE OJB_HL_SEQ
(
            TABLENAME VARCHAR (175) NOT NULL,
            FIELDNAME VARCHAR (70) NOT NULL,
            MAX_KEY INT NULL,
            GRAB_SIZE INT NULL,
            VERSION INT NULL,

    CONSTRAINT OJB_HL_SEQ_PK PRIMARY KEY(TABLENAME,FIELDNAME));





/* ---------------------------------------------------------------------- */
/* OJB_LOCKENTRY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_LOCKENTRY')
BEGIN
     DECLARE @reftable_2 nvarchar(60), @constraintname_2 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'OJB_LOCKENTRY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_2, @constraintname_2
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_2+' drop constraint '+@constraintname_2)
       FETCH NEXT from refcursor into @reftable_2, @constraintname_2
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE OJB_LOCKENTRY
END
;

CREATE TABLE OJB_LOCKENTRY
(
            OID_ VARCHAR (250) NOT NULL,
            TX_ID VARCHAR (50) NOT NULL,
            TIMESTAMP_ DATETIME NULL,
            ISOLATIONLEVEL INT NULL,
            LOCKTYPE INT NULL,

    CONSTRAINT OJB_LOCKENTRY_PK PRIMARY KEY(OID_,TX_ID));





/* ---------------------------------------------------------------------- */
/* OJB_NRM                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_NRM')
BEGIN
     DECLARE @reftable_3 nvarchar(60), @constraintname_3 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'OJB_NRM'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_3, @constraintname_3
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_3+' drop constraint '+@constraintname_3)
       FETCH NEXT from refcursor into @reftable_3, @constraintname_3
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE OJB_NRM
END
;

CREATE TABLE OJB_NRM
(
            NAME VARCHAR (250) NOT NULL,
            OID_ IMAGE NULL,

    CONSTRAINT OJB_NRM_PK PRIMARY KEY(NAME));





/* ---------------------------------------------------------------------- */
/* OJB_DLIST                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_DLIST')
BEGIN
     DECLARE @reftable_4 nvarchar(60), @constraintname_4 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'OJB_DLIST'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_4, @constraintname_4
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_4+' drop constraint '+@constraintname_4)
       FETCH NEXT from refcursor into @reftable_4, @constraintname_4
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE OJB_DLIST
END
;

CREATE TABLE OJB_DLIST
(
            ID INT NOT NULL,
            SIZE_ INT NULL,

    CONSTRAINT OJB_DLIST_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* OJB_DLIST_ENTRIES                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_DLIST_ENTRIES')
BEGIN
     DECLARE @reftable_5 nvarchar(60), @constraintname_5 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'OJB_DLIST_ENTRIES'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_5+' drop constraint '+@constraintname_5)
       FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE OJB_DLIST_ENTRIES
END
;

CREATE TABLE OJB_DLIST_ENTRIES
(
            ID INT NOT NULL,
            DLIST_ID INT NULL,
            POSITION_ INT NULL,
            OID_ IMAGE NULL,

    CONSTRAINT OJB_DLIST_ENTRIES_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* OJB_DSET                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_DSET')
BEGIN
     DECLARE @reftable_6 nvarchar(60), @constraintname_6 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'OJB_DSET'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_6, @constraintname_6
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_6+' drop constraint '+@constraintname_6)
       FETCH NEXT from refcursor into @reftable_6, @constraintname_6
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE OJB_DSET
END
;

CREATE TABLE OJB_DSET
(
            ID INT NOT NULL,
            SIZE_ INT NULL,

    CONSTRAINT OJB_DSET_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* OJB_DSET_ENTRIES                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_DSET_ENTRIES')
BEGIN
     DECLARE @reftable_7 nvarchar(60), @constraintname_7 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'OJB_DSET_ENTRIES'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_7, @constraintname_7
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_7+' drop constraint '+@constraintname_7)
       FETCH NEXT from refcursor into @reftable_7, @constraintname_7
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE OJB_DSET_ENTRIES
END
;

CREATE TABLE OJB_DSET_ENTRIES
(
            ID INT NOT NULL,
            DLIST_ID INT NULL,
            POSITION_ INT NULL,
            OID_ IMAGE NULL,

    CONSTRAINT OJB_DSET_ENTRIES_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* OJB_DMAP                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_DMAP')
BEGIN
     DECLARE @reftable_8 nvarchar(60), @constraintname_8 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'OJB_DMAP'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_8, @constraintname_8
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_8+' drop constraint '+@constraintname_8)
       FETCH NEXT from refcursor into @reftable_8, @constraintname_8
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE OJB_DMAP
END
;

CREATE TABLE OJB_DMAP
(
            ID INT NOT NULL,
            SIZE_ INT NULL,

    CONSTRAINT OJB_DMAP_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* OJB_DMAP                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* OJB_HL_SEQ                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* OJB_LOCKENTRY                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* OJB_NRM                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* OJB_DLIST                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* OJB_DLIST_ENTRIES                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* OJB_DSET                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* OJB_DSET_ENTRIES                                                      */
/* ---------------------------------------------------------------------- */



