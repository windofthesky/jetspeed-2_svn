
/* ---------------------------------------------------------------------- */
/* PREFS_NODE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_PREFS_NODE_1')
    ALTER TABLE PREFS_NODE DROP CONSTRAINT FK_PREFS_NODE_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PREFS_NODE')
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
         and tables.name = 'PREFS_NODE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_1, @constraintname_1
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_1+' drop constraint '+@constraintname_1)
       FETCH NEXT from refcursor into @reftable_1, @constraintname_1
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE PREFS_NODE
END
;

CREATE TABLE PREFS_NODE
(
            NODE_ID INT NOT NULL,
            PARENT_NODE_ID INT NULL,
            NODE_NAME VARCHAR (100) NULL,
            NODE_TYPE SMALLINT NULL,
            FULL_PATH VARCHAR (254) NULL,
            CREATION_DATE DATETIME NULL,
            MODIFIED_DATE DATETIME NULL,

    CONSTRAINT PREFS_NODE_PK PRIMARY KEY(NODE_ID));

CREATE  INDEX IX_PREFS_NODE_1 ON PREFS_NODE (PARENT_NODE_ID);
CREATE  INDEX IX_PREFS_NODE_2 ON PREFS_NODE (FULL_PATH);




/* ---------------------------------------------------------------------- */
/* PREFS_PROPERTY_VALUE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='PREFS_PROPERTY_VALUE_FK_1')
    ALTER TABLE PREFS_PROPERTY_VALUE DROP CONSTRAINT PREFS_PROPERTY_VALUE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PREFS_PROPERTY_VALUE')
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
         and tables.name = 'PREFS_PROPERTY_VALUE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_2, @constraintname_2
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_2+' drop constraint '+@constraintname_2)
       FETCH NEXT from refcursor into @reftable_2, @constraintname_2
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE PREFS_PROPERTY_VALUE
END
;

CREATE TABLE PREFS_PROPERTY_VALUE
(
            PROPERTY_VALUE_ID INT NOT NULL,
            NODE_ID INT NULL,
            PROPERTY_NAME VARCHAR (100) NULL,
            PROPERTY_VALUE VARCHAR (254) NULL,
            CREATION_DATE DATETIME NULL,
            MODIFIED_DATE DATETIME NULL,

    CONSTRAINT PREFS_PROPERTY_VALUE_PK PRIMARY KEY(PROPERTY_VALUE_ID));

CREATE  INDEX IX_FKPPV_1 ON PREFS_PROPERTY_VALUE (NODE_ID);




/* ---------------------------------------------------------------------- */
/* DROP TRIGGERS                                                          */
/* ---------------------------------------------------------------------- */



/* ---------------------------------------------------------------------- */
/* PREFS_PROPERTY_VALUE                                                      */
/* ---------------------------------------------------------------------- */




BEGIN
ALTER TABLE PREFS_NODE
    ADD CONSTRAINT FK_PREFS_NODE_1 FOREIGN KEY (PARENT_NODE_ID)
    REFERENCES PREFS_NODE (NODE_ID)
    ON DELETE NO ACTION
END    
;


/* ---------------------------------------------------------------------- */
/* PREFS_NODE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE PREFS_PROPERTY_VALUE
    ADD CONSTRAINT PREFS_PROPERTY_VALUE_FK_1 FOREIGN KEY (NODE_ID)
    REFERENCES PREFS_NODE (NODE_ID)
    ON DELETE NO ACTION
END    
;



