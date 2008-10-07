-----------------------------------------------------------------------------
-- Copyright 2004 The Apache Software Foundation
-- 
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
-- http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-----------------------------------------------------------------------------

----------------------------------------------------------------------------
-- Desktop 
----------------------------------------------------------------------------
CREATE TABLE DESKTOP
(
    DESKTOP_ID VARCHAR(80) NOT NULL PRIMARY KEY,
    TITLE VARCHAR(100),
    COMPONENT_NAME VARCHAR(100),
    ACL VARCHAR(80),
    DEFAULT_PAGE_ID VARCHAR(80),
    DEFAULT_THEME VARCHAR(100),
    DEFAULT_DECORATOR VARCHAR(100),
    DEFAULT_PORTLET_DECORATOR VARCHAR(100),
    UNIQUE(DESKTOP_ID)
);

-- associates a desktop with its top level pages
CREATE TABLE DESKTOP_PAGES
(
    DESKTOP_ID VARCHAR(80) NOT NULL,
    PAGE_ID VARCHAR(80) NOT NULL
);

CREATE INDEX IX_DESKTOP_PAGES_1 ON DESKTOP_PAGES (DESKTOP_ID, PAGE_ID);

-- associates a page with its sub group pages
CREATE TABLE SUB_PAGES
(
    PAGE_ID VARCHAR(80) NOT NULL,
    SUB_PAGE_ID VARCHAR(80) NOT NULL
);

CREATE INDEX IX_SUB_PAGES_1 ON SUB_PAGES (PAGE_ID, SUB_PAGE_ID);

----------------------------------------------------------------------------
-- Page Definitions
----------------------------------------------------------------------------
CREATE TABLE PAGE
(
    PAGE_ID VARCHAR(80) NOT NULL PRIMARY KEY,
    TITLE VARCHAR(100),
    DEFAULT_SKIN VARCHAR(100),
    DEFAULT_DECORATOR VARCHAR(100),
    DEFAULT_PORTLET_DECORATOR VARCHAR(100),
    ACL VARCHAR(80),
    UNIQUE(PAGE_ID)
);

----------------------------------------------------------------------------
-- Fragments (portlets, layouts)
----------------------------------------------------------------------------
CREATE TABLE FRAGMENT
( 
    FRAGMENT_ID VARCHAR(80) NOT NULL PRIMARY KEY,
    COMPONENT_NAME VARCHAR(100),
    ACL VARCHAR(80),
    TITLE VARCHAR(100),
    TYPE VARCHAR(40),
    SKIN VARCHAR(80),
    DECORATOR VARCHAR(80),
    STATE VARCHAR(40)
);
                                
-- associates a page with its top level fragments
CREATE TABLE PAGE_FRAGMENTS
(
    PAGE_ID VARCHAR(80) NOT NULL,
    FRAGMENT_ID VARCHAR(80) NOT NULL
);

CREATE INDEX IX_PAGE_FRAGMENTS_1 ON PAGE_FRAGMENTS (PAGE_ID, FRAGMENT_ID);

-- associates a fragment with its top level fragments
CREATE TABLE SUB_FRAGMENTS
(
    FRAGMENT_ID VARCHAR(80) NOT NULL,
    SUB_FRAGMENT_ID VARCHAR(80) NOT NULL
);

CREATE INDEX IX_SUB_FRAGMENTS_1 ON SUB_FRAGMENTS (FRAGMENT_ID, SUB_FRAGMENT_ID);


CREATE TABLE FRAGMENT_PARAMETER
(
    PARAMETER_ID INTEGER PRIMARY KEY,
    FRAGMENT_ID INTEGER NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    VALUE LONGVARCHAR NOT NULL
);

CREATE UNIQUE INDEX IX_FRAGMENT_PARAMETER_1 ON FRAGMENT_PARAMETER (FRAGMENT_ID, PARAMETER_ID);

-- names a reference to a fragment subtree
CREATE TABLE FRAGMENT_REF
(
    FRAGMENT_REF_ID VARCHAR(80) NOT NULL PRIMARY KEY,
    FRAGMENT_ID VARCHAR(80) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    TITLE VARCHAR(100)
);

CREATE TABLE FRAGMENT_REFS
(
    FRAGMENT_REF_ID VARCHAR(80) NOT NULL,
    FRAGMENT_ID VARCHAR(80) NOT NULL
);

----------------------------------------------------------------------------
-- Profiler
----------------------------------------------------------------------------

CREATE TABLE PROFILING_RULE
(
    RULE_ID VARCHAR(80) PRIMARY KEY,
    CLASS_NAME VARCHAR(100) NOT NULL,
    TITLE VARCHAR(100)
);

CREATE TABLE RULE_CRITERION
(
    CRITERION_ID VARCHAR(80) PRIMARY KEY,
    RULE_ID VARCHAR(80) NOT NULL,        
    FALLBACK_ORDER INTEGER NOT NULL,
    REQUEST_TYPE VARCHAR(40) NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    VALUE VARCHAR(128),
    FALLBACK_TYPE  INTEGER default 1
);

CREATE INDEX IX_RULE_CRITERION_1 ON RULE_CRITERION(RULE_ID, FALLBACK_ORDER);

CREATE TABLE PRINCIPAL_RULE_ASSOC
(
    PRINCIPAL_NAME VARCHAR(80) NOT NULL,
    RULE_ID VARCHAR(80) NOT NULL,
    UNIQUE(PRINCIPAL_NAME)
);

CREATE TABLE PROFILE_DESKTOP_ASSOC
(
    LOCATOR_HASH VARCHAR(40) NOT NULL,
    DESKTOP_ID VARCHAR(80) NOT NULL
);

CREATE UNIQUE INDEX IX_PROFILE_DESKTOP_1 ON PROFILE_DESKTOP_ASSOC(LOCATOR_HASH, DESKTOP_ID);

CREATE TABLE PROFILE_PAGE_ASSOC
(
    LOCATOR_HASH VARCHAR(40) NOT NULL,
    PAGE_ID VARCHAR(80) NOT NULL
);

CREATE UNIQUE INDEX IX_PROFILE_PAGE_1 ON PROFILE_PAGE_ASSOC(LOCATOR_HASH, PAGE_ID);

