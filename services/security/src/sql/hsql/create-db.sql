-----------------------------------------------------------------------------
-- Create Security Principal Table
-----------------------------------------------------------------------------
CREATE TABLE SECURITY_PRINCIPAL
(
    PRINCIPAL_ID INTEGER NOT NULL PRIMARY KEY,
    CLASSNAME VARCHAR(254) NOT NULL,
    FULL_PATH VARCHAR(254) NOT NULL,
    CREATION_DATE TIMESTAMP NOT NULL,
    MODIFIED_DATE TIMESTAMP NOT NULL
);

-----------------------------------------------------------------------------
-- Create Security Credential Table
-----------------------------------------------------------------------------
CREATE TABLE SECURITY_CREDENTIAL
(
    CREDENTIAL_ID INTEGER NOT NULL PRIMARY KEY,
    PRINCIPAL_ID INTEGER NOT NULL,
    VALUE VARCHAR(254) NOT NULL,
    TYPE SMALLINT NOT NULL,
    CLASSNAME VARCHAR(254),
    CREATION_DATE TIMESTAMP NOT NULL,
    MODIFIED_DATE TIMESTAMP NOT NULL
);

ALTER TABLE SECURITY_CREDENTIAL
    ADD CONSTRAINT SECURITY_CREDENTIAL_FK1 FOREIGN KEY (PRINCIPAL_ID)
    REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID);

-----------------------------------------------------------------------------
-- Create Security User Role Table
-----------------------------------------------------------------------------
CREATE TABLE SECURITY_USER_ROLE
(
    USER_ID INTEGER NOT NULL,
    ROLE_ID INTEGER NOT NULL,
    PRIMARY KEY (USER_ID, ROLE_ID)
);

ALTER TABLE SECURITY_USER_ROLE
    ADD CONSTRAINT SECURITY_USER_ROLE_FK1 FOREIGN KEY (USER_ID)
    REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID);

ALTER TABLE SECURITY_USER_ROLE
    ADD CONSTRAINT SECURITY_USER_ROLE_FK2 FOREIGN KEY (ROLE_ID)
    REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID);


-----------------------------------------------------------------------------
-- Create Security User Group Table
-----------------------------------------------------------------------------
CREATE TABLE SECURITY_USER_GROUP
(
    USER_ID INTEGER NOT NULL,
    GROUP_ID INTEGER NOT NULL,
    PRIMARY KEY (USER_ID, GROUP_ID)
);

ALTER TABLE SECURITY_USER_GROUP
    ADD CONSTRAINT SECURITY_USER_GROUP_FK1 FOREIGN KEY (USER_ID)
    REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID);

ALTER TABLE SECURITY_USER_GROUP
    ADD CONSTRAINT SECURITY_USER_GROUP_FK2 FOREIGN KEY (GROUP_ID)
    REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID);

-----------------------------------------------------------------------------
-- Create Security Group Role Table
-----------------------------------------------------------------------------
CREATE TABLE SECURITY_GROUP_ROLE
(
    GROUP_ID INTEGER NOT NULL,
    ROLE_ID INTEGER NOT NULL,
    PRIMARY KEY (GROUP_ID, ROLE_ID)
);

ALTER TABLE SECURITY_GROUP_ROLE
    ADD CONSTRAINT SECURITY_GROUP_ROLE_FK1 FOREIGN KEY (GROUP_ID)
    REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID);

ALTER TABLE SECURITY_GROUP_ROLE
    ADD CONSTRAINT SECURITY_GROUP_ROLE_FK2 FOREIGN KEY (ROLE_ID)
    REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID);

-----------------------------------------------------------------------------
-- Create Security Permission Table
-----------------------------------------------------------------------------
CREATE TABLE SECURITY_PERMISSION
(
    PERMISSION_ID INTEGER NOT NULL PRIMARY KEY,
    CLASSNAME VARCHAR(254) NOT NULL,
    NAME VARCHAR(254) NOT NULL,
    ACTIONS VARCHAR(254) NOT NULL,
    CREATION_DATE TIMESTAMP NOT NULL,
    MODIFIED_DATE TIMESTAMP NOT NULL
);

-----------------------------------------------------------------------------
-- Create Security Principal Permission Table
-----------------------------------------------------------------------------
CREATE TABLE SECURITY_PRINCIPAL_PERMISSION
(
    PRINCIPAL_ID INTEGER NOT NULL,
    PERMISSION_ID INTEGER NOT NULL,
    PRIMARY KEY (PRINCIPAL_ID, PERMISSION_ID)
);


ALTER TABLE SECURITY_PRINCIPAL_PERMISSION
    ADD CONSTRAINT SECURITY_PRINCIPAL_PERMISSION_FK1 FOREIGN KEY (PRINCIPAL_ID)
    REFERENCES SECURITY_PRINCIPAL (PRINCIPAL_ID);

ALTER TABLE SECURITY_PRINCIPAL_PERMISSION
    ADD CONSTRAINT SECURITY_PRINCIPAL_PERMISSION_FK2 FOREIGN KEY (PERMISSION_ID)
    REFERENCES SECURITY_PERMISSION (PERMISSION_ID);