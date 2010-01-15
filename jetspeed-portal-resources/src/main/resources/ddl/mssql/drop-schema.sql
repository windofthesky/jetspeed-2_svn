/*
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
DROP TRIGGER trig_folder;
DROP TRIGGER trig_folder_menu;
DROP TRIGGER trig_fragment;
DROP TRIGGER trig_page_menu;

ALTER TABLE SSO_SITE
    DROP CONSTRAINT FK_SECURITY_DOMAIN_2;

ALTER TABLE SECURITY_CREDENTIAL
    DROP CONSTRAINT FK_SECURITY_CREDENTIAL_1;

ALTER TABLE PRINCIPAL_PERMISSION
    DROP CONSTRAINT FK_PRINCIPAL_PERMISSION_1;

ALTER TABLE PRINCIPAL_PERMISSION
    DROP CONSTRAINT FK_PRINCIPAL_PERMISSION_2;

ALTER TABLE SECURITY_PRINCIPAL_ASSOC
    DROP CONSTRAINT FK_FROM_PRINCIPAL_ASSOC;

ALTER TABLE SECURITY_PRINCIPAL_ASSOC
    DROP CONSTRAINT FK_TO_PRINCIPAL_ASSOC;

ALTER TABLE SECURITY_ATTRIBUTE
    DROP CONSTRAINT FK_PRINCIPAL_ATTR;

ALTER TABLE SECURITY_PRINCIPAL
    DROP CONSTRAINT FK_SECURITY_DOMAIN_1;

ALTER TABLE EVENT_DEFINITION
    DROP CONSTRAINT FK_EVENT_DEFINITION_1;

ALTER TABLE CUSTOM_WINDOW_STATE
    DROP CONSTRAINT FK_CUSTOM_WINDOW_STATE_1;

ALTER TABLE CUSTOM_PORTLET_MODE
    DROP CONSTRAINT FK_CUSTOM_PORTLET_MODE_1;

ALTER TABLE USER_ATTRIBUTE
    DROP CONSTRAINT FK_USER_ATTRIBUTE_1;

ALTER TABLE USER_ATTRIBUTE_REF
    DROP CONSTRAINT FK_USER_ATTRIBUTE_REF_1;

ALTER TABLE SECURITY_ROLE
    DROP CONSTRAINT FK_SECURITY_ROLE_REF_1;

ALTER TABLE PORTLET_PREFERENCE_VALUE
    DROP CONSTRAINT FK_PORTLET_PREFERENCE;

ALTER TABLE PD_METADATA_FIELDS
    DROP CONSTRAINT FK_PD_METADATA_FIELDS_1;

ALTER TABLE PA_METADATA_FIELDS
    DROP CONSTRAINT FK_PA_METADATA_FIELDS_1;

ALTER TABLE RULE_CRITERION
    DROP CONSTRAINT FK_RULE_CRITERION_1;

ALTER TABLE PAGE_SEC_CONSTRAINTS_REF
    DROP CONSTRAINT FK_PAGE_SEC_CONSTRAINTS_REF_1;

ALTER TABLE PAGE_SEC_CONSTRAINT_DEF
    DROP CONSTRAINT FK_PAGE_SEC_CONSTRAINT_DEF_1;

ALTER TABLE PAGE_SEC_CONSTRAINTS_DEF
    DROP CONSTRAINT FK_PAGE_SEC_CONSTRAINTS_DEF_1;

ALTER TABLE PAGE_SECURITY
    DROP CONSTRAINT FK_PAGE_SECURITY_1;

ALTER TABLE LINK_CONSTRAINTS_REF
    DROP CONSTRAINT FK_LINK_CONSTRAINTS_REF_1;

ALTER TABLE LINK_CONSTRAINT
    DROP CONSTRAINT FK_LINK_CONSTRAINT_1;

ALTER TABLE LINK_METADATA
    DROP CONSTRAINT FK_LINK_METADATA_1;

ALTER TABLE LINK
    DROP CONSTRAINT FK_LINK_1;

ALTER TABLE FRAGMENT_PROP
    DROP CONSTRAINT FK_FRAGMENT_PROP_1;

ALTER TABLE FRAGMENT_PREF_VALUE
    DROP CONSTRAINT FK_FRAGMENT_PREF_VALUE_1;

ALTER TABLE FRAGMENT_PREF
    DROP CONSTRAINT FK_FRAGMENT_PREF_1;

ALTER TABLE FRAGMENT_CONSTRAINTS_REF
    DROP CONSTRAINT FK_FRAGMENT_CONSTRAINTS_REF_1;

ALTER TABLE FRAGMENT_CONSTRAINT
    DROP CONSTRAINT FK_FRAGMENT_CONSTRAINT_1;

ALTER TABLE FRAGMENT
    DROP CONSTRAINT FK_FRAGMENT_1;

ALTER TABLE FRAGMENT
    DROP CONSTRAINT FK_FRAGMENT_2;

ALTER TABLE PAGE_MENU_METADATA
    DROP CONSTRAINT FK_PAGE_MENU_METADATA_1;

ALTER TABLE PAGE_MENU
    DROP CONSTRAINT FK_PAGE_MENU_1;

ALTER TABLE PAGE_MENU
    DROP CONSTRAINT PM_M_FK_PAGE_ID_PAGE;

ALTER TABLE PAGE_CONSTRAINTS_REF
    DROP CONSTRAINT FK_PAGE_CONSTRAINTS_REF_1;

ALTER TABLE PAGE_CONSTRAINT
    DROP CONSTRAINT FK_PAGE_CONSTRAINT_1;

ALTER TABLE PAGE_METADATA
    DROP CONSTRAINT FK_PAGE_METADATA_1;

ALTER TABLE PAGE
    DROP CONSTRAINT FK_PAGE_1;

ALTER TABLE FOLDER_MENU_METADATA
    DROP CONSTRAINT FK_FOLDER_MENU_METADATA_1;

ALTER TABLE FOLDER_MENU
    DROP CONSTRAINT FK_FOLDER_MENU_1;

ALTER TABLE FOLDER_MENU
    DROP CONSTRAINT FK_FOLDER_MENU_2;

ALTER TABLE FOLDER_ORDER
    DROP CONSTRAINT FK_FOLDER_ORDER_1;

ALTER TABLE FOLDER_CONSTRAINTS_REF
    DROP CONSTRAINT FK_FOLDER_CONSTRAINTS_REF_1;

ALTER TABLE FOLDER_CONSTRAINT
    DROP CONSTRAINT FK_FOLDER_CONSTRAINT_1;

ALTER TABLE FOLDER_METADATA
    DROP CONSTRAINT FK_FOLDER_METADATA_1;

ALTER TABLE FOLDER
    DROP CONSTRAINT FK_FOLDER_1;

-- ----------------------------------------------------------------------- 
-- SECURITY_DOMAIN 
-- ----------------------------------------------------------------------- 

DROP TABLE SECURITY_DOMAIN;

-- ----------------------------------------------------------------------- 
-- SSO_SITE 
-- ----------------------------------------------------------------------- 

DROP TABLE SSO_SITE;

-- ----------------------------------------------------------------------- 
-- SECURITY_CREDENTIAL 
-- ----------------------------------------------------------------------- 

DROP TABLE SECURITY_CREDENTIAL;

-- ----------------------------------------------------------------------- 
-- PRINCIPAL_PERMISSION 
-- ----------------------------------------------------------------------- 

DROP TABLE PRINCIPAL_PERMISSION;

-- ----------------------------------------------------------------------- 
-- SECURITY_PERMISSION 
-- ----------------------------------------------------------------------- 

DROP TABLE SECURITY_PERMISSION;

-- ----------------------------------------------------------------------- 
-- SECURITY_PRINCIPAL_ASSOC 
-- ----------------------------------------------------------------------- 

DROP TABLE SECURITY_PRINCIPAL_ASSOC;

-- ----------------------------------------------------------------------- 
-- SECURITY_ATTRIBUTE 
-- ----------------------------------------------------------------------- 

DROP TABLE SECURITY_ATTRIBUTE;

-- ----------------------------------------------------------------------- 
-- SECURITY_PRINCIPAL 
-- ----------------------------------------------------------------------- 

DROP TABLE SECURITY_PRINCIPAL;

-- ----------------------------------------------------------------------- 
-- LOCALE_ENCODING_MAPPING 
-- ----------------------------------------------------------------------- 

DROP TABLE LOCALE_ENCODING_MAPPING;

-- ----------------------------------------------------------------------- 
-- SECURED_PORTLET 
-- ----------------------------------------------------------------------- 

DROP TABLE SECURED_PORTLET;

-- ----------------------------------------------------------------------- 
-- PA_SECURITY_CONSTRAINT 
-- ----------------------------------------------------------------------- 

DROP TABLE PA_SECURITY_CONSTRAINT;

-- ----------------------------------------------------------------------- 
-- PORTLET_LISTENER 
-- ----------------------------------------------------------------------- 

DROP TABLE PORTLET_LISTENER;

-- ----------------------------------------------------------------------- 
-- FILTERED_PORTLET 
-- ----------------------------------------------------------------------- 

DROP TABLE FILTERED_PORTLET;

-- ----------------------------------------------------------------------- 
-- FILTER_MAPPING 
-- ----------------------------------------------------------------------- 

DROP TABLE FILTER_MAPPING;

-- ----------------------------------------------------------------------- 
-- FILTER_LIFECYCLE 
-- ----------------------------------------------------------------------- 

DROP TABLE FILTER_LIFECYCLE;

-- ----------------------------------------------------------------------- 
-- PORTLET_FILTER 
-- ----------------------------------------------------------------------- 

DROP TABLE PORTLET_FILTER;

-- ----------------------------------------------------------------------- 
-- PUBLIC_PARAMETER 
-- ----------------------------------------------------------------------- 

DROP TABLE PUBLIC_PARAMETER;

-- ----------------------------------------------------------------------- 
-- RUNTIME_VALUE 
-- ----------------------------------------------------------------------- 

DROP TABLE RUNTIME_VALUE;

-- ----------------------------------------------------------------------- 
-- RUNTIME_OPTION 
-- ----------------------------------------------------------------------- 

DROP TABLE RUNTIME_OPTION;

-- ----------------------------------------------------------------------- 
-- NAMED_PARAMETER 
-- ----------------------------------------------------------------------- 

DROP TABLE NAMED_PARAMETER;

-- ----------------------------------------------------------------------- 
-- PROCESSING_EVENT 
-- ----------------------------------------------------------------------- 

DROP TABLE PROCESSING_EVENT;

-- ----------------------------------------------------------------------- 
-- PUBLISHING_EVENT 
-- ----------------------------------------------------------------------- 

DROP TABLE PUBLISHING_EVENT;

-- ----------------------------------------------------------------------- 
-- PARAMETER_ALIAS 
-- ----------------------------------------------------------------------- 

DROP TABLE PARAMETER_ALIAS;

-- ----------------------------------------------------------------------- 
-- EVENT_ALIAS 
-- ----------------------------------------------------------------------- 

DROP TABLE EVENT_ALIAS;

-- ----------------------------------------------------------------------- 
-- EVENT_DEFINITION 
-- ----------------------------------------------------------------------- 

DROP TABLE EVENT_DEFINITION;

-- ----------------------------------------------------------------------- 
-- CUSTOM_WINDOW_STATE 
-- ----------------------------------------------------------------------- 

DROP TABLE CUSTOM_WINDOW_STATE;

-- ----------------------------------------------------------------------- 
-- CUSTOM_PORTLET_MODE 
-- ----------------------------------------------------------------------- 

DROP TABLE CUSTOM_PORTLET_MODE;

-- ----------------------------------------------------------------------- 
-- LOCALIZED_DISPLAY_NAME 
-- ----------------------------------------------------------------------- 

DROP TABLE LOCALIZED_DISPLAY_NAME;

-- ----------------------------------------------------------------------- 
-- LOCALIZED_DESCRIPTION 
-- ----------------------------------------------------------------------- 

DROP TABLE LOCALIZED_DESCRIPTION;

-- ----------------------------------------------------------------------- 
-- JETSPEED_SERVICE 
-- ----------------------------------------------------------------------- 

DROP TABLE JETSPEED_SERVICE;

-- ----------------------------------------------------------------------- 
-- USER_ATTRIBUTE 
-- ----------------------------------------------------------------------- 

DROP TABLE USER_ATTRIBUTE;

-- ----------------------------------------------------------------------- 
-- USER_ATTRIBUTE_REF 
-- ----------------------------------------------------------------------- 

DROP TABLE USER_ATTRIBUTE_REF;

-- ----------------------------------------------------------------------- 
-- SECURITY_ROLE 
-- ----------------------------------------------------------------------- 

DROP TABLE SECURITY_ROLE;

-- ----------------------------------------------------------------------- 
-- SECURITY_ROLE_REFERENCE 
-- ----------------------------------------------------------------------- 

DROP TABLE SECURITY_ROLE_REFERENCE;

-- ----------------------------------------------------------------------- 
-- PORTLET_PREFERENCE_VALUE 
-- ----------------------------------------------------------------------- 

DROP TABLE PORTLET_PREFERENCE_VALUE;

-- ----------------------------------------------------------------------- 
-- PORTLET_PREFERENCE 
-- ----------------------------------------------------------------------- 

DROP TABLE PORTLET_PREFERENCE;

-- ----------------------------------------------------------------------- 
-- PARAMETER 
-- ----------------------------------------------------------------------- 

DROP TABLE PARAMETER;

-- ----------------------------------------------------------------------- 
-- PORTLET_SUPPORTS 
-- ----------------------------------------------------------------------- 

DROP TABLE PORTLET_SUPPORTS;

-- ----------------------------------------------------------------------- 
-- LANGUAGE 
-- ----------------------------------------------------------------------- 

DROP TABLE LANGUAGE;

-- ----------------------------------------------------------------------- 
-- PD_METADATA_FIELDS 
-- ----------------------------------------------------------------------- 

DROP TABLE PD_METADATA_FIELDS;

-- ----------------------------------------------------------------------- 
-- PA_METADATA_FIELDS 
-- ----------------------------------------------------------------------- 

DROP TABLE PA_METADATA_FIELDS;

-- ----------------------------------------------------------------------- 
-- PORTLET_APPLICATION 
-- ----------------------------------------------------------------------- 

DROP TABLE PORTLET_APPLICATION;

-- ----------------------------------------------------------------------- 
-- PORTLET_DEFINITION 
-- ----------------------------------------------------------------------- 

DROP TABLE PORTLET_DEFINITION;

-- ----------------------------------------------------------------------- 
-- OJB_DMAP 
-- ----------------------------------------------------------------------- 

DROP TABLE OJB_DMAP;

-- ----------------------------------------------------------------------- 
-- OJB_DSET_ENTRIES 
-- ----------------------------------------------------------------------- 

DROP TABLE OJB_DSET_ENTRIES;

-- ----------------------------------------------------------------------- 
-- OJB_DSET 
-- ----------------------------------------------------------------------- 

DROP TABLE OJB_DSET;

-- ----------------------------------------------------------------------- 
-- OJB_DLIST_ENTRIES 
-- ----------------------------------------------------------------------- 

DROP TABLE OJB_DLIST_ENTRIES;

-- ----------------------------------------------------------------------- 
-- OJB_DLIST 
-- ----------------------------------------------------------------------- 

DROP TABLE OJB_DLIST;

-- ----------------------------------------------------------------------- 
-- OJB_NRM 
-- ----------------------------------------------------------------------- 

DROP TABLE OJB_NRM;

-- ----------------------------------------------------------------------- 
-- OJB_LOCKENTRY 
-- ----------------------------------------------------------------------- 

DROP TABLE OJB_LOCKENTRY;

-- ----------------------------------------------------------------------- 
-- OJB_HL_SEQ 
-- ----------------------------------------------------------------------- 

DROP TABLE OJB_HL_SEQ;

-- ----------------------------------------------------------------------- 
-- CLUBS 
-- ----------------------------------------------------------------------- 

DROP TABLE CLUBS;

-- ----------------------------------------------------------------------- 
-- PROFILE_PAGE_ASSOC 
-- ----------------------------------------------------------------------- 

DROP TABLE PROFILE_PAGE_ASSOC;

-- ----------------------------------------------------------------------- 
-- PRINCIPAL_RULE_ASSOC 
-- ----------------------------------------------------------------------- 

DROP TABLE PRINCIPAL_RULE_ASSOC;

-- ----------------------------------------------------------------------- 
-- RULE_CRITERION 
-- ----------------------------------------------------------------------- 

DROP TABLE RULE_CRITERION;

-- ----------------------------------------------------------------------- 
-- PROFILING_RULE 
-- ----------------------------------------------------------------------- 

DROP TABLE PROFILING_RULE;

-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

DROP TABLE PAGE_SEC_CONSTRAINTS_REF;

-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINT_DEF 
-- ----------------------------------------------------------------------- 

DROP TABLE PAGE_SEC_CONSTRAINT_DEF;

-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINTS_DEF 
-- ----------------------------------------------------------------------- 

DROP TABLE PAGE_SEC_CONSTRAINTS_DEF;

-- ----------------------------------------------------------------------- 
-- PAGE_SECURITY 
-- ----------------------------------------------------------------------- 

DROP TABLE PAGE_SECURITY;

-- ----------------------------------------------------------------------- 
-- LINK_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

DROP TABLE LINK_CONSTRAINTS_REF;

-- ----------------------------------------------------------------------- 
-- LINK_CONSTRAINT 
-- ----------------------------------------------------------------------- 

DROP TABLE LINK_CONSTRAINT;

-- ----------------------------------------------------------------------- 
-- LINK_METADATA 
-- ----------------------------------------------------------------------- 

DROP TABLE LINK_METADATA;

-- ----------------------------------------------------------------------- 
-- LINK 
-- ----------------------------------------------------------------------- 

DROP TABLE LINK;

-- ----------------------------------------------------------------------- 
-- FRAGMENT_PROP
-- ----------------------------------------------------------------------- 

DROP TABLE FRAGMENT_PROP;

-- ----------------------------------------------------------------------- 
-- FRAGMENT_PREF_VALUE 
-- ----------------------------------------------------------------------- 

DROP TABLE FRAGMENT_PREF_VALUE;

-- ----------------------------------------------------------------------- 
-- FRAGMENT_PREF 
-- ----------------------------------------------------------------------- 

DROP TABLE FRAGMENT_PREF;

-- ----------------------------------------------------------------------- 
-- FRAGMENT_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

DROP TABLE FRAGMENT_CONSTRAINTS_REF;

-- ----------------------------------------------------------------------- 
-- FRAGMENT_CONSTRAINT 
-- ----------------------------------------------------------------------- 

DROP TABLE FRAGMENT_CONSTRAINT;

-- ----------------------------------------------------------------------- 
-- FRAGMENT 
-- ----------------------------------------------------------------------- 

DROP TABLE FRAGMENT;

-- ----------------------------------------------------------------------- 
-- PAGE_MENU_METADATA 
-- ----------------------------------------------------------------------- 

DROP TABLE PAGE_MENU_METADATA;

-- ----------------------------------------------------------------------- 
-- PAGE_MENU 
-- ----------------------------------------------------------------------- 

DROP TABLE PAGE_MENU;

-- ----------------------------------------------------------------------- 
-- PAGE_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

DROP TABLE PAGE_CONSTRAINTS_REF;

-- ----------------------------------------------------------------------- 
-- PAGE_CONSTRAINT 
-- ----------------------------------------------------------------------- 

DROP TABLE PAGE_CONSTRAINT;

-- ----------------------------------------------------------------------- 
-- PAGE_METADATA 
-- ----------------------------------------------------------------------- 

DROP TABLE PAGE_METADATA;

-- ----------------------------------------------------------------------- 
-- PAGE 
-- ----------------------------------------------------------------------- 

DROP TABLE PAGE;

-- ----------------------------------------------------------------------- 
-- FOLDER_MENU_METADATA 
-- ----------------------------------------------------------------------- 

DROP TABLE FOLDER_MENU_METADATA;

-- ----------------------------------------------------------------------- 
-- FOLDER_MENU 
-- ----------------------------------------------------------------------- 

DROP TABLE FOLDER_MENU;

-- ----------------------------------------------------------------------- 
-- FOLDER_ORDER 
-- ----------------------------------------------------------------------- 

DROP TABLE FOLDER_ORDER;

-- ----------------------------------------------------------------------- 
-- FOLDER_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

DROP TABLE FOLDER_CONSTRAINTS_REF;

-- ----------------------------------------------------------------------- 
-- FOLDER_CONSTRAINT 
-- ----------------------------------------------------------------------- 

DROP TABLE FOLDER_CONSTRAINT;

-- ----------------------------------------------------------------------- 
-- FOLDER_METADATA 
-- ----------------------------------------------------------------------- 

DROP TABLE FOLDER_METADATA;

-- ----------------------------------------------------------------------- 
-- FOLDER 
-- ----------------------------------------------------------------------- 

DROP TABLE FOLDER;

-- ----------------------------------------------------------------------- 
-- USER_ACTIVITY 
-- ----------------------------------------------------------------------- 

DROP TABLE USER_ACTIVITY;

-- ----------------------------------------------------------------------- 
-- ADMIN_ACTIVITY 
-- ----------------------------------------------------------------------- 

DROP TABLE ADMIN_ACTIVITY;

-- ----------------------------------------------------------------------- 
-- USER_STATISTICS 
-- ----------------------------------------------------------------------- 

DROP TABLE USER_STATISTICS;

-- ----------------------------------------------------------------------- 
-- PAGE_STATISTICS 
-- ----------------------------------------------------------------------- 

DROP TABLE PAGE_STATISTICS;

-- ----------------------------------------------------------------------- 
-- PORTLET_STATISTICS 
-- ----------------------------------------------------------------------- 

DROP TABLE PORTLET_STATISTICS;

-- ----------------------------------------------------------------------- 
-- MEDIATYPE_TO_MIMETYPE 
-- ----------------------------------------------------------------------- 

DROP TABLE MEDIATYPE_TO_MIMETYPE;

-- ----------------------------------------------------------------------- 
-- MEDIATYPE_TO_CAPABILITY 
-- ----------------------------------------------------------------------- 

DROP TABLE MEDIATYPE_TO_CAPABILITY;

-- ----------------------------------------------------------------------- 
-- CLIENT_TO_MIMETYPE 
-- ----------------------------------------------------------------------- 

DROP TABLE CLIENT_TO_MIMETYPE;

-- ----------------------------------------------------------------------- 
-- CLIENT_TO_CAPABILITY 
-- ----------------------------------------------------------------------- 

DROP TABLE CLIENT_TO_CAPABILITY;

-- ----------------------------------------------------------------------- 
-- CAPABILITY 
-- ----------------------------------------------------------------------- 

DROP TABLE CAPABILITY;

-- ----------------------------------------------------------------------- 
-- MIMETYPE 
-- ----------------------------------------------------------------------- 

DROP TABLE MIMETYPE;

-- ----------------------------------------------------------------------- 
-- CLIENT 
-- ----------------------------------------------------------------------- 

DROP TABLE CLIENT;

-- ----------------------------------------------------------------------- 
-- MEDIA_TYPE 
-- ----------------------------------------------------------------------- 

DROP TABLE MEDIA_TYPE;

