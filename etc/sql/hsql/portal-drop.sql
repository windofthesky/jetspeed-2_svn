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

DROP TABLE SECURITY IF EXISTS; 
DROP TABLE SKIN IF EXISTS; 
DROP TABLE CONTROL IF EXISTS; 
DROP TABLE LAYOUT IF EXISTS; 
DROP TABLE PSML_ENTRY IF EXISTS; 
DROP TABLE PSML IF EXISTS; 

DROP TABLE OJB_HL_SEQ IF EXISTS; 
DROP TABLE OJB_LOCKENTRY IF EXISTS; 
DROP TABLE OJB_NRM IF EXISTS; 
DROP TABLE OJB_DLIST IF EXISTS; 
DROP TABLE OJB_DLIST_ENTRIES IF EXISTS; 
DROP TABLE OJB_DSET IF EXISTS; 
DROP TABLE OJB_DSET_ENTRIES IF EXISTS; 
DROP TABLE OJB_DMAP IF EXISTS; 
DROP TABLE OJB_DMAP_ENTRIES IF EXISTS;

DROP TABLE CAPABILITY IF EXISTS;
DROP TABLE MIMETYPE IF EXISTS;
DROP TABLE CLIENT IF EXISTS;
DROP TABLE MEDIA_TYPE IF EXISTS;

DROP TABLE CLIENT_TO_CAPABILITY IF EXISTS;
DROP TABLE CLIENT_TO_MIMETYPE IF EXISTS;
DROP TABLE MEDIATYPE_TO_CAPABILITY IF EXISTS;
DROP TABLE MEDIATYPE_TO_MIMETYPE IF EXISTS;

DROP TABLE SUB_PAGES IF EXISTS;
DROP TABLE PAGE IF EXISTS;
DROP TABLE FRAGMENT IF EXISTS;
DROP TABLE PAGE_FRAGMENTS IF EXISTS;
DROP TABLE SUB_FRAGMENTS IF EXISTS;
DROP TABLE FRAGMENT_PARAMETER IF EXISTS;
DROP TABLE FRAGMENT_REF IF EXISTS;
DROP TABLE FRAGMENT_REFS IF EXISTS;
DROP TABLE PROFILING_RULE IF EXISTS;
DROP TABLE RULE_CRITERION IF EXISTS;
DROP TABLE PRINCIPAL_RULE_ASSOC IF EXISTS;
DROP TABLE PROFILE_PAGE_ASSOC IF EXISTS;

-----------------------------------------------------------------------------
-- drop preferences schema
-----------------------------------------------------------------------------
DROP TABLE pref_property_value IF EXISTS;
DROP TABLE pref_property_key IF EXISTS;
DROP TABLE pref_node IF EXISTS;
DROP TABLE pref_property_set_def IF EXISTS;

