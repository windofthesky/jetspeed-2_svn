
/* ---------------------------------------------------------------------- */
/* MEDIA_TYPE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'MEDIA_TYPE')
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
         and tables.name = 'MEDIA_TYPE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_1, @constraintname_1
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_1+' drop constraint '+@constraintname_1)
       FETCH NEXT from refcursor into @reftable_1, @constraintname_1
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE MEDIA_TYPE
END
;

CREATE TABLE MEDIA_TYPE
(
            MEDIATYPE_ID INT NOT NULL,
            NAME VARCHAR (80) NOT NULL,
            CHARACTER_SET VARCHAR (40) NULL,
            TITLE VARCHAR (80) NULL,
            DESCRIPTION TEXT NULL,

    CONSTRAINT MEDIA_TYPE_PK PRIMARY KEY(MEDIATYPE_ID));





/* ---------------------------------------------------------------------- */
/* CLIENT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CLIENT')
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
         and tables.name = 'CLIENT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_2, @constraintname_2
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_2+' drop constraint '+@constraintname_2)
       FETCH NEXT from refcursor into @reftable_2, @constraintname_2
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE CLIENT
END
;

CREATE TABLE CLIENT
(
            CLIENT_ID INT NOT NULL,
            EVAL_ORDER INT NOT NULL,
            NAME VARCHAR (80) NOT NULL,
            USER_AGENT_PATTERN VARCHAR (128) NULL,
            MANUFACTURER VARCHAR (80) NULL,
            MODEL VARCHAR (80) NULL,
            VERSION VARCHAR (40) NULL,
            PREFERRED_MIMETYPE_ID INT NOT NULL,

    CONSTRAINT CLIENT_PK PRIMARY KEY(CLIENT_ID));





/* ---------------------------------------------------------------------- */
/* MIMETYPE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'MIMETYPE')
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
         and tables.name = 'MIMETYPE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_3, @constraintname_3
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_3+' drop constraint '+@constraintname_3)
       FETCH NEXT from refcursor into @reftable_3, @constraintname_3
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE MIMETYPE
END
;

CREATE TABLE MIMETYPE
(
            MIMETYPE_ID INT NOT NULL,
            NAME VARCHAR (80) NOT NULL,

    CONSTRAINT MIMETYPE_PK PRIMARY KEY(MIMETYPE_ID));





/* ---------------------------------------------------------------------- */
/* CAPABILITY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CAPABILITY')
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
         and tables.name = 'CAPABILITY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_4, @constraintname_4
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_4+' drop constraint '+@constraintname_4)
       FETCH NEXT from refcursor into @reftable_4, @constraintname_4
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE CAPABILITY
END
;

CREATE TABLE CAPABILITY
(
            CAPABILITY_ID INT NOT NULL,
            CAPABILITY VARCHAR (80) NOT NULL,

    CONSTRAINT CAPABILITY_PK PRIMARY KEY(CAPABILITY_ID));





/* ---------------------------------------------------------------------- */
/* CLIENT_TO_CAPABILITY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CLIENT_TO_CAPABILITY')
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
         and tables.name = 'CLIENT_TO_CAPABILITY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_5+' drop constraint '+@constraintname_5)
       FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE CLIENT_TO_CAPABILITY
END
;

CREATE TABLE CLIENT_TO_CAPABILITY
(
            CLIENT_ID INT NOT NULL,
            CAPABILITY_ID INT NOT NULL,
);





/* ---------------------------------------------------------------------- */
/* CLIENT_TO_MIMETYPE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CLIENT_TO_MIMETYPE')
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
         and tables.name = 'CLIENT_TO_MIMETYPE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_6, @constraintname_6
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_6+' drop constraint '+@constraintname_6)
       FETCH NEXT from refcursor into @reftable_6, @constraintname_6
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE CLIENT_TO_MIMETYPE
END
;

CREATE TABLE CLIENT_TO_MIMETYPE
(
            CLIENT_ID INT NOT NULL,
            MIMETYPE_ID INT NOT NULL,
);





/* ---------------------------------------------------------------------- */
/* MEDIATYPE_TO_CAPABILITY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'MEDIATYPE_TO_CAPABILITY')
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
         and tables.name = 'MEDIATYPE_TO_CAPABILITY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_7, @constraintname_7
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_7+' drop constraint '+@constraintname_7)
       FETCH NEXT from refcursor into @reftable_7, @constraintname_7
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE MEDIATYPE_TO_CAPABILITY
END
;

CREATE TABLE MEDIATYPE_TO_CAPABILITY
(
            MEDIATYPE_ID INT NOT NULL,
            CAPABILITY_ID INT NOT NULL,
);





/* ---------------------------------------------------------------------- */
/* MEDIATYPE_TO_MIMETYPE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'MEDIATYPE_TO_MIMETYPE')
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
         and tables.name = 'MEDIATYPE_TO_MIMETYPE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_8, @constraintname_8
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_8+' drop constraint '+@constraintname_8)
       FETCH NEXT from refcursor into @reftable_8, @constraintname_8
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE MEDIATYPE_TO_MIMETYPE
END
;

CREATE TABLE MEDIATYPE_TO_MIMETYPE
(
            MEDIATYPE_ID INT NOT NULL,
            MIMETYPE_ID INT NOT NULL,
);





/* ---------------------------------------------------------------------- */
/* PORTLET_STATISTICS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_STATISTICS')
BEGIN
     DECLARE @reftable_9 nvarchar(60), @constraintname_9 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'PORTLET_STATISTICS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_9, @constraintname_9
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_9+' drop constraint '+@constraintname_9)
       FETCH NEXT from refcursor into @reftable_9, @constraintname_9
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE PORTLET_STATISTICS
END
;

CREATE TABLE PORTLET_STATISTICS
(
            IPADDRESS VARCHAR (80) NULL,
            USER_NAME VARCHAR (80) NULL,
            TIME_STAMP DATETIME NULL,
            PAGE VARCHAR (80) NULL,
            PORTLET VARCHAR (255) NULL,
            STATUS INT NULL,
            ELAPSED_TIME BIGINT NULL,
);





/* ---------------------------------------------------------------------- */
/* PAGE_STATISTICS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE_STATISTICS')
BEGIN
     DECLARE @reftable_10 nvarchar(60), @constraintname_10 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'PAGE_STATISTICS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_10, @constraintname_10
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_10+' drop constraint '+@constraintname_10)
       FETCH NEXT from refcursor into @reftable_10, @constraintname_10
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE PAGE_STATISTICS
END
;

CREATE TABLE PAGE_STATISTICS
(
            IPADDRESS VARCHAR (80) NULL,
            USER_NAME VARCHAR (80) NULL,
            TIME_STAMP DATETIME NULL,
            PAGE VARCHAR (80) NULL,
            STATUS INT NULL,
            ELAPSED_TIME BIGINT NULL,
);





/* ---------------------------------------------------------------------- */
/* USER_STATISTICS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'USER_STATISTICS')
BEGIN
     DECLARE @reftable_11 nvarchar(60), @constraintname_11 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'USER_STATISTICS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_11, @constraintname_11
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_11+' drop constraint '+@constraintname_11)
       FETCH NEXT from refcursor into @reftable_11, @constraintname_11
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE USER_STATISTICS
END
;

CREATE TABLE USER_STATISTICS
(
            IPADDRESS VARCHAR (80) NULL,
            USER_NAME VARCHAR (80) NULL,
            TIME_STAMP DATETIME NULL,
            STATUS INT NULL,
            ELAPSED_TIME BIGINT NULL,
);





/* ---------------------------------------------------------------------- */
/* ADMIN_ACTIVITY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'ADMIN_ACTIVITY')
BEGIN
     DECLARE @reftable_12 nvarchar(60), @constraintname_12 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'ADMIN_ACTIVITY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_12, @constraintname_12
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_12+' drop constraint '+@constraintname_12)
       FETCH NEXT from refcursor into @reftable_12, @constraintname_12
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE ADMIN_ACTIVITY
END
;

CREATE TABLE ADMIN_ACTIVITY
(
            ACTIVITY VARCHAR (40) NULL,
            CATEGORY VARCHAR (40) NULL,
            ADMIN VARCHAR (80) NULL,
            USER_NAME VARCHAR (80) NULL,
            TIME_STAMP DATETIME NULL,
            IPADDRESS VARCHAR (80) NULL,
            ATTR_NAME VARCHAR (40) NULL,
            ATTR_VALUE_BEFORE VARCHAR (80) NULL,
            ATTR_VALUE_AFTER VARCHAR (80) NULL,
            DESCRIPTION VARCHAR (128) NULL,
);





/* ---------------------------------------------------------------------- */
/* USER_ACTIVITY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'USER_ACTIVITY')
BEGIN
     DECLARE @reftable_13 nvarchar(60), @constraintname_13 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'USER_ACTIVITY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_13, @constraintname_13
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_13+' drop constraint '+@constraintname_13)
       FETCH NEXT from refcursor into @reftable_13, @constraintname_13
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE USER_ACTIVITY
END
;

CREATE TABLE USER_ACTIVITY
(
            ACTIVITY VARCHAR (40) NULL,
            CATEGORY VARCHAR (40) NULL,
            USER_NAME VARCHAR (80) NULL,
            TIME_STAMP DATETIME NULL,
            IPADDRESS VARCHAR (80) NULL,
            ATTR_NAME VARCHAR (40) NULL,
            ATTR_VALUE_BEFORE VARCHAR (80) NULL,
            ATTR_VALUE_AFTER VARCHAR (80) NULL,
            DESCRIPTION VARCHAR (128) NULL,
);





/* ---------------------------------------------------------------------- */
/* USER_ACTIVITY                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* MEDIA_TYPE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* CLIENT                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* MIMETYPE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* CAPABILITY                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* CLIENT_TO_CAPABILITY                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* CLIENT_TO_MIMETYPE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* MEDIATYPE_TO_CAPABILITY                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* MEDIATYPE_TO_MIMETYPE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* PORTLET_STATISTICS                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* PAGE_STATISTICS                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* USER_STATISTICS                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* ADMIN_ACTIVITY                                                      */
/* ---------------------------------------------------------------------- */



