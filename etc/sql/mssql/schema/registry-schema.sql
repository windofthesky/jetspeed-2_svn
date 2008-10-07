
/* ---------------------------------------------------------------------- */
/* PORTLET_DEFINITION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_DEFINITION')
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
         and tables.name = 'PORTLET_DEFINITION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_1, @constraintname_1
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_1+' drop constraint '+@constraintname_1)
       FETCH NEXT from refcursor into @reftable_1, @constraintname_1
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE PORTLET_DEFINITION
END
;

CREATE TABLE PORTLET_DEFINITION
(
            ID INT NOT NULL,
            NAME VARCHAR (80) NULL,
            CLASS_NAME VARCHAR (255) NULL,
            APPLICATION_ID INT NOT NULL,
            PORTLET_IDENTIFIER VARCHAR (80) NULL,
            EXPIRATION_CACHE VARCHAR (30) NULL,
            RESOURCE_BUNDLE VARCHAR (255) NULL,
            PREFERENCE_VALIDATOR VARCHAR (255) NULL,
            SECURITY_REF VARCHAR (40) NULL,

    CONSTRAINT PORTLET_DEFINITION_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* PORTLET_APPLICATION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_APPLICATION')
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
         and tables.name = 'PORTLET_APPLICATION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_2, @constraintname_2
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_2+' drop constraint '+@constraintname_2)
       FETCH NEXT from refcursor into @reftable_2, @constraintname_2
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE PORTLET_APPLICATION
END
;

CREATE TABLE PORTLET_APPLICATION
(
            APPLICATION_ID INT NOT NULL,
            APP_NAME VARCHAR (80) NOT NULL,
            APP_IDENTIFIER VARCHAR (80) NULL,
            VERSION VARCHAR (80) NULL,
            APP_TYPE INT NULL,
            CHECKSUM VARCHAR (80) NULL,
            DESCRIPTION VARCHAR (80) NULL,
            WEB_APP_ID INT NOT NULL,
            SECURITY_REF VARCHAR (40) NULL,

    CONSTRAINT PORTLET_APPLICATION_PK PRIMARY KEY(APPLICATION_ID),
    UNIQUE (APP_NAME));





/* ---------------------------------------------------------------------- */
/* WEB_APPLICATION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'WEB_APPLICATION')
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
         and tables.name = 'WEB_APPLICATION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_3, @constraintname_3
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_3+' drop constraint '+@constraintname_3)
       FETCH NEXT from refcursor into @reftable_3, @constraintname_3
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE WEB_APPLICATION
END
;

CREATE TABLE WEB_APPLICATION
(
            ID INT NOT NULL,
            CONTEXT_ROOT VARCHAR (255) NOT NULL,

    CONSTRAINT WEB_APPLICATION_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* PA_METADATA_FIELDS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_PA_METADATA_FIELDS_1')
    ALTER TABLE PA_METADATA_FIELDS DROP CONSTRAINT FK_PA_METADATA_FIELDS_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PA_METADATA_FIELDS')
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
         and tables.name = 'PA_METADATA_FIELDS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_4, @constraintname_4
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_4+' drop constraint '+@constraintname_4)
       FETCH NEXT from refcursor into @reftable_4, @constraintname_4
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE PA_METADATA_FIELDS
END
;

CREATE TABLE PA_METADATA_FIELDS
(
            ID INT NOT NULL,
            OBJECT_ID INT NOT NULL,
            COLUMN_VALUE TEXT NOT NULL,
            NAME VARCHAR (100) NOT NULL,
            LOCALE_STRING VARCHAR (50) NOT NULL,

    CONSTRAINT PA_METADATA_FIELDS_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* PD_METADATA_FIELDS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_PD_METADATA_FIELDS_1')
    ALTER TABLE PD_METADATA_FIELDS DROP CONSTRAINT FK_PD_METADATA_FIELDS_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PD_METADATA_FIELDS')
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
         and tables.name = 'PD_METADATA_FIELDS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_5+' drop constraint '+@constraintname_5)
       FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE PD_METADATA_FIELDS
END
;

CREATE TABLE PD_METADATA_FIELDS
(
            ID INT NOT NULL,
            OBJECT_ID INT NOT NULL,
            COLUMN_VALUE TEXT NOT NULL,
            NAME VARCHAR (100) NOT NULL,
            LOCALE_STRING VARCHAR (50) NOT NULL,

    CONSTRAINT PD_METADATA_FIELDS_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* LANGUAGE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'LANGUAGE')
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
         and tables.name = 'LANGUAGE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_6, @constraintname_6
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_6+' drop constraint '+@constraintname_6)
       FETCH NEXT from refcursor into @reftable_6, @constraintname_6
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE LANGUAGE
END
;

CREATE TABLE LANGUAGE
(
            ID INT NOT NULL,
            PORTLET_ID INT NOT NULL,
            TITLE VARCHAR (100) NULL,
            SHORT_TITLE VARCHAR (100) NULL,
            LOCALE_STRING VARCHAR (50) NOT NULL,
            KEYWORDS TEXT NULL,

    CONSTRAINT LANGUAGE_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* PORTLET_CONTENT_TYPE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_CONTENT_TYPE')
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
         and tables.name = 'PORTLET_CONTENT_TYPE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_7, @constraintname_7
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_7+' drop constraint '+@constraintname_7)
       FETCH NEXT from refcursor into @reftable_7, @constraintname_7
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE PORTLET_CONTENT_TYPE
END
;

CREATE TABLE PORTLET_CONTENT_TYPE
(
            CONTENT_TYPE_ID INT NOT NULL,
            PORTLET_ID INT NOT NULL,
            CONTENT_TYPE VARCHAR (30) NOT NULL,
            MODES TEXT NULL,

    CONSTRAINT PORTLET_CONTENT_TYPE_PK PRIMARY KEY(CONTENT_TYPE_ID));





/* ---------------------------------------------------------------------- */
/* PARAMETER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PARAMETER')
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
         and tables.name = 'PARAMETER'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_8, @constraintname_8
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_8+' drop constraint '+@constraintname_8)
       FETCH NEXT from refcursor into @reftable_8, @constraintname_8
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE PARAMETER
END
;

CREATE TABLE PARAMETER
(
            PARAMETER_ID INT NOT NULL,
            PARENT_ID INT NOT NULL,
            CLASS_NAME VARCHAR (255) NOT NULL,
            NAME VARCHAR (80) NOT NULL,
            PARAMETER_VALUE TEXT NOT NULL,

    CONSTRAINT PARAMETER_PK PRIMARY KEY(PARAMETER_ID));





/* ---------------------------------------------------------------------- */
/* PORTLET_ENTITY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_ENTITY')
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
         and tables.name = 'PORTLET_ENTITY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_9, @constraintname_9
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_9+' drop constraint '+@constraintname_9)
       FETCH NEXT from refcursor into @reftable_9, @constraintname_9
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE PORTLET_ENTITY
END
;

CREATE TABLE PORTLET_ENTITY
(
            PEID INT NOT NULL,
            ID VARCHAR (255) NOT NULL,
            APP_NAME VARCHAR (255) NOT NULL,
            PORTLET_NAME VARCHAR (255) NOT NULL,

    CONSTRAINT PORTLET_ENTITY_PK PRIMARY KEY(PEID),
    UNIQUE (ID));





/* ---------------------------------------------------------------------- */
/* SECURITY_ROLE_REFERENCE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SECURITY_ROLE_REFERENCE')
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
         and tables.name = 'SECURITY_ROLE_REFERENCE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_10, @constraintname_10
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_10+' drop constraint '+@constraintname_10)
       FETCH NEXT from refcursor into @reftable_10, @constraintname_10
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE SECURITY_ROLE_REFERENCE
END
;

CREATE TABLE SECURITY_ROLE_REFERENCE
(
            ID INT NOT NULL,
            PORTLET_DEFINITION_ID INT NOT NULL,
            ROLE_NAME VARCHAR (150) NOT NULL,
            ROLE_LINK VARCHAR (150) NULL,

    CONSTRAINT SECURITY_ROLE_REFERENCE_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* SECURITY_ROLE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SECURITY_ROLE')
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
         and tables.name = 'SECURITY_ROLE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_11, @constraintname_11
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_11+' drop constraint '+@constraintname_11)
       FETCH NEXT from refcursor into @reftable_11, @constraintname_11
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE SECURITY_ROLE
END
;

CREATE TABLE SECURITY_ROLE
(
            ID INT NOT NULL,
            WEB_APPLICATION_ID INT NOT NULL,
            ROLE_NAME VARCHAR (150) NOT NULL,
            DESCRIPTION VARCHAR (150) NULL,

    CONSTRAINT SECURITY_ROLE_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* USER_ATTRIBUTE_REF                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_USER_ATTRIBUTE_REF_1')
    ALTER TABLE USER_ATTRIBUTE_REF DROP CONSTRAINT FK_USER_ATTRIBUTE_REF_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'USER_ATTRIBUTE_REF')
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
         and tables.name = 'USER_ATTRIBUTE_REF'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_12, @constraintname_12
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_12+' drop constraint '+@constraintname_12)
       FETCH NEXT from refcursor into @reftable_12, @constraintname_12
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE USER_ATTRIBUTE_REF
END
;

CREATE TABLE USER_ATTRIBUTE_REF
(
            ID INT NOT NULL,
            APPLICATION_ID INT NOT NULL,
            NAME VARCHAR (150) NULL,
            NAME_LINK VARCHAR (150) NULL,

    CONSTRAINT USER_ATTRIBUTE_REF_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* USER_ATTRIBUTE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_USER_ATTRIBUTE_1')
    ALTER TABLE USER_ATTRIBUTE DROP CONSTRAINT FK_USER_ATTRIBUTE_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'USER_ATTRIBUTE')
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
         and tables.name = 'USER_ATTRIBUTE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_13, @constraintname_13
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_13+' drop constraint '+@constraintname_13)
       FETCH NEXT from refcursor into @reftable_13, @constraintname_13
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE USER_ATTRIBUTE
END
;

CREATE TABLE USER_ATTRIBUTE
(
            ID INT NOT NULL,
            APPLICATION_ID INT NOT NULL,
            NAME VARCHAR (150) NULL,
            DESCRIPTION VARCHAR (150) NULL,

    CONSTRAINT USER_ATTRIBUTE_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* JETSPEED_SERVICE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'JETSPEED_SERVICE')
BEGIN
     DECLARE @reftable_14 nvarchar(60), @constraintname_14 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'JETSPEED_SERVICE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_14, @constraintname_14
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_14+' drop constraint '+@constraintname_14)
       FETCH NEXT from refcursor into @reftable_14, @constraintname_14
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE JETSPEED_SERVICE
END
;

CREATE TABLE JETSPEED_SERVICE
(
            ID INT NOT NULL,
            APPLICATION_ID INT NOT NULL,
            NAME VARCHAR (150) NULL,

    CONSTRAINT JETSPEED_SERVICE_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* LOCALIZED_DESCRIPTION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'LOCALIZED_DESCRIPTION')
BEGIN
     DECLARE @reftable_15 nvarchar(60), @constraintname_15 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'LOCALIZED_DESCRIPTION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_15, @constraintname_15
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_15+' drop constraint '+@constraintname_15)
       FETCH NEXT from refcursor into @reftable_15, @constraintname_15
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE LOCALIZED_DESCRIPTION
END
;

CREATE TABLE LOCALIZED_DESCRIPTION
(
            ID INT NOT NULL,
            OBJECT_ID INT NOT NULL,
            CLASS_NAME VARCHAR (255) NOT NULL,
            DESCRIPTION TEXT NOT NULL,
            LOCALE_STRING VARCHAR (50) NOT NULL,

    CONSTRAINT LOCALIZED_DESCRIPTION_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* LOCALIZED_DISPLAY_NAME                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'LOCALIZED_DISPLAY_NAME')
BEGIN
     DECLARE @reftable_16 nvarchar(60), @constraintname_16 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'LOCALIZED_DISPLAY_NAME'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_16, @constraintname_16
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_16+' drop constraint '+@constraintname_16)
       FETCH NEXT from refcursor into @reftable_16, @constraintname_16
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE LOCALIZED_DISPLAY_NAME
END
;

CREATE TABLE LOCALIZED_DISPLAY_NAME
(
            ID INT NOT NULL,
            OBJECT_ID INT NOT NULL,
            CLASS_NAME VARCHAR (255) NULL,
            DISPLAY_NAME TEXT NOT NULL,
            LOCALE_STRING VARCHAR (50) NOT NULL,

    CONSTRAINT LOCALIZED_DISPLAY_NAME_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* CUSTOM_PORTLET_MODE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_CUSTOM_PORTLET_MODE_1')
    ALTER TABLE CUSTOM_PORTLET_MODE DROP CONSTRAINT FK_CUSTOM_PORTLET_MODE_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CUSTOM_PORTLET_MODE')
BEGIN
     DECLARE @reftable_17 nvarchar(60), @constraintname_17 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'CUSTOM_PORTLET_MODE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_17, @constraintname_17
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_17+' drop constraint '+@constraintname_17)
       FETCH NEXT from refcursor into @reftable_17, @constraintname_17
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE CUSTOM_PORTLET_MODE
END
;

CREATE TABLE CUSTOM_PORTLET_MODE
(
            ID INT NOT NULL,
            APPLICATION_ID INT NOT NULL,
            CUSTOM_NAME VARCHAR (150) NOT NULL,
            MAPPED_NAME VARCHAR (150) NULL,
            DESCRIPTION TEXT NULL,

    CONSTRAINT CUSTOM_PORTLET_MODE_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* CUSTOM_WINDOW_STATE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_CUSTOM_WINDOW_STATE_1')
    ALTER TABLE CUSTOM_WINDOW_STATE DROP CONSTRAINT FK_CUSTOM_WINDOW_STATE_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CUSTOM_WINDOW_STATE')
BEGIN
     DECLARE @reftable_18 nvarchar(60), @constraintname_18 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'CUSTOM_WINDOW_STATE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_18, @constraintname_18
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_18+' drop constraint '+@constraintname_18)
       FETCH NEXT from refcursor into @reftable_18, @constraintname_18
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE CUSTOM_WINDOW_STATE
END
;

CREATE TABLE CUSTOM_WINDOW_STATE
(
            ID INT NOT NULL,
            APPLICATION_ID INT NOT NULL,
            CUSTOM_NAME VARCHAR (150) NOT NULL,
            MAPPED_NAME VARCHAR (150) NULL,
            DESCRIPTION TEXT NULL,

    CONSTRAINT CUSTOM_WINDOW_STATE_PK PRIMARY KEY(ID));





/* ---------------------------------------------------------------------- */
/* CUSTOM_WINDOW_STATE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* PORTLET_DEFINITION                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* PORTLET_APPLICATION                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* WEB_APPLICATION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE PA_METADATA_FIELDS
    ADD CONSTRAINT FK_PA_METADATA_FIELDS_1 FOREIGN KEY (OBJECT_ID)
    REFERENCES PORTLET_APPLICATION (APPLICATION_ID)
    ON DELETE CASCADE 
END    
;




/* ---------------------------------------------------------------------- */
/* PA_METADATA_FIELDS                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE PD_METADATA_FIELDS
    ADD CONSTRAINT FK_PD_METADATA_FIELDS_1 FOREIGN KEY (OBJECT_ID)
    REFERENCES PORTLET_DEFINITION (ID)
    ON DELETE CASCADE 
END    
;




/* ---------------------------------------------------------------------- */
/* PD_METADATA_FIELDS                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* LANGUAGE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* PORTLET_CONTENT_TYPE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* PARAMETER                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* PORTLET_ENTITY                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* SECURITY_ROLE_REFERENCE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* SECURITY_ROLE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE USER_ATTRIBUTE_REF
    ADD CONSTRAINT FK_USER_ATTRIBUTE_REF_1 FOREIGN KEY (APPLICATION_ID)
    REFERENCES PORTLET_APPLICATION (APPLICATION_ID)
    ON DELETE CASCADE 
END    
;




/* ---------------------------------------------------------------------- */
/* USER_ATTRIBUTE_REF                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE USER_ATTRIBUTE
    ADD CONSTRAINT FK_USER_ATTRIBUTE_1 FOREIGN KEY (APPLICATION_ID)
    REFERENCES PORTLET_APPLICATION (APPLICATION_ID)
    ON DELETE CASCADE 
END    
;




/* ---------------------------------------------------------------------- */
/* USER_ATTRIBUTE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* JETSPEED_SERVICE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* LOCALIZED_DESCRIPTION                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* LOCALIZED_DISPLAY_NAME                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE CUSTOM_PORTLET_MODE
    ADD CONSTRAINT FK_CUSTOM_PORTLET_MODE_1 FOREIGN KEY (APPLICATION_ID)
    REFERENCES PORTLET_APPLICATION (APPLICATION_ID)
    ON DELETE CASCADE 
END    
;




/* ---------------------------------------------------------------------- */
/* CUSTOM_PORTLET_MODE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE CUSTOM_WINDOW_STATE
    ADD CONSTRAINT FK_CUSTOM_WINDOW_STATE_1 FOREIGN KEY (APPLICATION_ID)
    REFERENCES PORTLET_APPLICATION (APPLICATION_ID)
    ON DELETE CASCADE 
END    
;



