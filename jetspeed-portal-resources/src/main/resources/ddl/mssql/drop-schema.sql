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
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_SSO_COOKIE_TO_REMOTE_1')
    ALTER TABLE SSO_COOKIE_TO_REMOTE DROP CONSTRAINT FK_SSO_COOKIE_TO_REMOTE_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_SSO_COOKIE_TO_REMOTE_2')
    ALTER TABLE SSO_COOKIE_TO_REMOTE DROP CONSTRAINT FK_SSO_COOKIE_TO_REMOTE_2;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_SSO_SITE_TO_REMOTE_1')
    ALTER TABLE SSO_SITE_TO_REMOTE DROP CONSTRAINT FK_SSO_SITE_TO_REMOTE_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_SSO_SITE_TO_REMOTE_2')
    ALTER TABLE SSO_SITE_TO_REMOTE DROP CONSTRAINT FK_SSO_SITE_TO_REMOTE_2;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_SSO_PRINCIPAL_TO_REMOTE_1')
    ALTER TABLE SSO_PRINCIPAL_TO_REMOTE DROP CONSTRAINT FK_SSO_PRINCIPAL_TO_REMOTE_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_SSO_PRINCIPAL_TO_REMOTE_2')
    ALTER TABLE SSO_PRINCIPAL_TO_REMOTE DROP CONSTRAINT FK_SSO_PRINCIPAL_TO_REMOTE_2;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'SSO_SITE_TO_PRINC_FK1')
    ALTER TABLE SSO_SITE_TO_PRINCIPALS DROP CONSTRAINT SSO_SITE_TO_PRINC_FK1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'SSO_SITE_TO_PRINC_FK2')
    ALTER TABLE SSO_SITE_TO_PRINCIPALS DROP CONSTRAINT SSO_SITE_TO_PRINC_FK2;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_SECURITY_CREDENTIAL_1')
    ALTER TABLE SECURITY_CREDENTIAL DROP CONSTRAINT FK_SECURITY_CREDENTIAL_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PRINCIPAL_PERMISSION_1')
    ALTER TABLE PRINCIPAL_PERMISSION DROP CONSTRAINT FK_PRINCIPAL_PERMISSION_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PRINCIPAL_PERMISSION_2')
    ALTER TABLE PRINCIPAL_PERMISSION DROP CONSTRAINT FK_PRINCIPAL_PERMISSION_2;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FROM_PRINCIPAL_ASSOC')
    ALTER TABLE SECURITY_PRINCIPAL_ASSOC DROP CONSTRAINT FK_FROM_PRINCIPAL_ASSOC;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_TO_PRINCIPAL_ASSOC')
    ALTER TABLE SECURITY_PRINCIPAL_ASSOC DROP CONSTRAINT FK_TO_PRINCIPAL_ASSOC;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PRINCIPAL_ATTR')
    ALTER TABLE SECURITY_ATTRIBUTE DROP CONSTRAINT FK_PRINCIPAL_ATTR;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_CUSTOM_WINDOW_STATE_1')
    ALTER TABLE CUSTOM_WINDOW_STATE DROP CONSTRAINT FK_CUSTOM_WINDOW_STATE_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_CUSTOM_PORTLET_MODE_1')
    ALTER TABLE CUSTOM_PORTLET_MODE DROP CONSTRAINT FK_CUSTOM_PORTLET_MODE_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_USER_ATTRIBUTE_1')
    ALTER TABLE USER_ATTRIBUTE DROP CONSTRAINT FK_USER_ATTRIBUTE_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_USER_ATTRIBUTE_REF_1')
    ALTER TABLE USER_ATTRIBUTE_REF DROP CONSTRAINT FK_USER_ATTRIBUTE_REF_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PORTLET_PREFERENCE')
    ALTER TABLE PORTLET_PREFERENCE_VALUE DROP CONSTRAINT FK_PORTLET_PREFERENCE;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PD_METADATA_FIELDS_1')
    ALTER TABLE PD_METADATA_FIELDS DROP CONSTRAINT FK_PD_METADATA_FIELDS_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PA_METADATA_FIELDS_1')
    ALTER TABLE PA_METADATA_FIELDS DROP CONSTRAINT FK_PA_METADATA_FIELDS_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_RULE_CRITERION_1')
    ALTER TABLE RULE_CRITERION DROP CONSTRAINT FK_RULE_CRITERION_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PAGE_SEC_CONSTRAINTS_REF_1')
    ALTER TABLE PAGE_SEC_CONSTRAINTS_REF DROP CONSTRAINT FK_PAGE_SEC_CONSTRAINTS_REF_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PAGE_SEC_CONSTRAINT_DEF_1')
    ALTER TABLE PAGE_SEC_CONSTRAINT_DEF DROP CONSTRAINT FK_PAGE_SEC_CONSTRAINT_DEF_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PAGE_SEC_CONSTRAINTS_DEF_1')
    ALTER TABLE PAGE_SEC_CONSTRAINTS_DEF DROP CONSTRAINT FK_PAGE_SEC_CONSTRAINTS_DEF_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PAGE_SECURITY_1')
    ALTER TABLE PAGE_SECURITY DROP CONSTRAINT FK_PAGE_SECURITY_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_LINK_CONSTRAINTS_REF_1')
    ALTER TABLE LINK_CONSTRAINTS_REF DROP CONSTRAINT FK_LINK_CONSTRAINTS_REF_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_LINK_CONSTRAINT_1')
    ALTER TABLE LINK_CONSTRAINT DROP CONSTRAINT FK_LINK_CONSTRAINT_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_LINK_METADATA_1')
    ALTER TABLE LINK_METADATA DROP CONSTRAINT FK_LINK_METADATA_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_LINK_1')
    ALTER TABLE LINK DROP CONSTRAINT FK_LINK_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FRAGMENT_PREF_VALUE_1')
    ALTER TABLE FRAGMENT_PREF_VALUE DROP CONSTRAINT FK_FRAGMENT_PREF_VALUE_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FRAGMENT_PREF_1')
    ALTER TABLE FRAGMENT_PREF DROP CONSTRAINT FK_FRAGMENT_PREF_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FRAGMENT_CONSTRAINTS_REF_1')
    ALTER TABLE FRAGMENT_CONSTRAINTS_REF DROP CONSTRAINT FK_FRAGMENT_CONSTRAINTS_REF_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FRAGMENT_CONSTRAINT_1')
    ALTER TABLE FRAGMENT_CONSTRAINT DROP CONSTRAINT FK_FRAGMENT_CONSTRAINT_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FRAGMENT_1')
    ALTER TABLE FRAGMENT DROP CONSTRAINT FK_FRAGMENT_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FRAGMENT_2')
    ALTER TABLE FRAGMENT DROP CONSTRAINT FK_FRAGMENT_2;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PAGE_MENU_METADATA_1')
    ALTER TABLE PAGE_MENU_METADATA DROP CONSTRAINT FK_PAGE_MENU_METADATA_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PAGE_MENU_1')
    ALTER TABLE PAGE_MENU DROP CONSTRAINT FK_PAGE_MENU_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'PM_M_FK_PAGE_ID_PAGE')
    ALTER TABLE PAGE_MENU DROP CONSTRAINT PM_M_FK_PAGE_ID_PAGE;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PAGE_CONSTRAINTS_REF_1')
    ALTER TABLE PAGE_CONSTRAINTS_REF DROP CONSTRAINT FK_PAGE_CONSTRAINTS_REF_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PAGE_CONSTRAINT_1')
    ALTER TABLE PAGE_CONSTRAINT DROP CONSTRAINT FK_PAGE_CONSTRAINT_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PAGE_METADATA_1')
    ALTER TABLE PAGE_METADATA DROP CONSTRAINT FK_PAGE_METADATA_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_PAGE_1')
    ALTER TABLE PAGE DROP CONSTRAINT FK_PAGE_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FOLDER_MENU_METADATA_1')
    ALTER TABLE FOLDER_MENU_METADATA DROP CONSTRAINT FK_FOLDER_MENU_METADATA_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FOLDER_MENU_1')
    ALTER TABLE FOLDER_MENU DROP CONSTRAINT FK_FOLDER_MENU_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FOLDER_MENU_2')
    ALTER TABLE FOLDER_MENU DROP CONSTRAINT FK_FOLDER_MENU_2;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FOLDER_ORDER_1')
    ALTER TABLE FOLDER_ORDER DROP CONSTRAINT FK_FOLDER_ORDER_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FOLDER_CONSTRAINTS_REF_1')
    ALTER TABLE FOLDER_CONSTRAINTS_REF DROP CONSTRAINT FK_FOLDER_CONSTRAINTS_REF_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FOLDER_CONSTRAINT_1')
    ALTER TABLE FOLDER_CONSTRAINT DROP CONSTRAINT FK_FOLDER_CONSTRAINT_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FOLDER_METADATA_1')
    ALTER TABLE FOLDER_METADATA DROP CONSTRAINT FK_FOLDER_METADATA_1;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'F' AND name = 'FK_FOLDER_1')
    ALTER TABLE FOLDER DROP CONSTRAINT FK_FOLDER_1;

-- ----------------------------------------------------------------------- 
-- SSO_COOKIE_TO_REMOTE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SSO_COOKIE_TO_REMOTE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__8000 nvarchar(256), @cnefc812f_11d19c07503__7fff nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SSO_COOKIE_TO_REMOTE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__8000, @cnefc812f_11d19c07503__7fff
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__8000+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fff)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__8000, @cnefc812f_11d19c07503__7fff
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SSO_COOKIE_TO_REMOTE
END;

-- ----------------------------------------------------------------------- 
-- SSO_SITE_TO_REMOTE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SSO_SITE_TO_REMOTE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7ffe nvarchar(256), @cnefc812f_11d19c07503__7ffd nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SSO_SITE_TO_REMOTE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ffe, @cnefc812f_11d19c07503__7ffd
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7ffe+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7ffd)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ffe, @cnefc812f_11d19c07503__7ffd
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SSO_SITE_TO_REMOTE
END;

-- ----------------------------------------------------------------------- 
-- SSO_PRINCIPAL_TO_REMOTE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SSO_PRINCIPAL_TO_REMOTE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7ffc nvarchar(256), @cnefc812f_11d19c07503__7ffb nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SSO_PRINCIPAL_TO_REMOTE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ffc, @cnefc812f_11d19c07503__7ffb
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7ffc+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7ffb)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ffc, @cnefc812f_11d19c07503__7ffb
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SSO_PRINCIPAL_TO_REMOTE
END;

-- ----------------------------------------------------------------------- 
-- SSO_SITE_TO_PRINCIPALS 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SSO_SITE_TO_PRINCIPALS')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7ffa nvarchar(256), @cnefc812f_11d19c07503__7ff9 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SSO_SITE_TO_PRINCIPALS'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ffa, @cnefc812f_11d19c07503__7ff9
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7ffa+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7ff9)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ffa, @cnefc812f_11d19c07503__7ff9
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SSO_SITE_TO_PRINCIPALS
END;

-- ----------------------------------------------------------------------- 
-- SSO_COOKIE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SSO_COOKIE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7ff8 nvarchar(256), @cnefc812f_11d19c07503__7ff7 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SSO_COOKIE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ff8, @cnefc812f_11d19c07503__7ff7
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7ff8+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7ff7)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ff8, @cnefc812f_11d19c07503__7ff7
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SSO_COOKIE
END;

-- ----------------------------------------------------------------------- 
-- SSO_SITE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SSO_SITE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7ff6 nvarchar(256), @cnefc812f_11d19c07503__7ff5 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SSO_SITE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ff6, @cnefc812f_11d19c07503__7ff5
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7ff6+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7ff5)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ff6, @cnefc812f_11d19c07503__7ff5
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SSO_SITE
END;

-- ----------------------------------------------------------------------- 
-- SECURITY_CREDENTIAL 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SECURITY_CREDENTIAL')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7ff4 nvarchar(256), @cnefc812f_11d19c07503__7ff3 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SECURITY_CREDENTIAL'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ff4, @cnefc812f_11d19c07503__7ff3
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7ff4+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7ff3)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ff4, @cnefc812f_11d19c07503__7ff3
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SECURITY_CREDENTIAL
END;

-- ----------------------------------------------------------------------- 
-- PRINCIPAL_PERMISSION 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PRINCIPAL_PERMISSION')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7ff2 nvarchar(256), @cnefc812f_11d19c07503__7ff1 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PRINCIPAL_PERMISSION'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ff2, @cnefc812f_11d19c07503__7ff1
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7ff2+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7ff1)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ff2, @cnefc812f_11d19c07503__7ff1
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PRINCIPAL_PERMISSION
END;

-- ----------------------------------------------------------------------- 
-- SECURITY_PERMISSION 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SECURITY_PERMISSION')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7ff0 nvarchar(256), @cnefc812f_11d19c07503__7fef nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SECURITY_PERMISSION'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ff0, @cnefc812f_11d19c07503__7fef
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7ff0+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fef)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7ff0, @cnefc812f_11d19c07503__7fef
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SECURITY_PERMISSION
END;

-- ----------------------------------------------------------------------- 
-- SECURITY_PRINCIPAL_ASSOC 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SECURITY_PRINCIPAL_ASSOC')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fee nvarchar(256), @cnefc812f_11d19c07503__7fed nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SECURITY_PRINCIPAL_ASSOC'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fee, @cnefc812f_11d19c07503__7fed
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fee+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fed)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fee, @cnefc812f_11d19c07503__7fed
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SECURITY_PRINCIPAL_ASSOC
END;

-- ----------------------------------------------------------------------- 
-- SECURITY_ATTRIBUTE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SECURITY_ATTRIBUTE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fec nvarchar(256), @cnefc812f_11d19c07503__7feb nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SECURITY_ATTRIBUTE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fec, @cnefc812f_11d19c07503__7feb
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fec+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7feb)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fec, @cnefc812f_11d19c07503__7feb
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SECURITY_ATTRIBUTE
END;

-- ----------------------------------------------------------------------- 
-- SECURITY_PRINCIPAL 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SECURITY_PRINCIPAL')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fea nvarchar(256), @cnefc812f_11d19c07503__7fe9 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SECURITY_PRINCIPAL'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fea, @cnefc812f_11d19c07503__7fe9
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fea+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fe9)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fea, @cnefc812f_11d19c07503__7fe9
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SECURITY_PRINCIPAL
END;

-- ----------------------------------------------------------------------- 
-- CUSTOM_WINDOW_STATE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CUSTOM_WINDOW_STATE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fe8 nvarchar(256), @cnefc812f_11d19c07503__7fe7 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'CUSTOM_WINDOW_STATE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fe8, @cnefc812f_11d19c07503__7fe7
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fe8+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fe7)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fe8, @cnefc812f_11d19c07503__7fe7
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE CUSTOM_WINDOW_STATE
END;

-- ----------------------------------------------------------------------- 
-- CUSTOM_PORTLET_MODE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CUSTOM_PORTLET_MODE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fe6 nvarchar(256), @cnefc812f_11d19c07503__7fe5 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'CUSTOM_PORTLET_MODE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fe6, @cnefc812f_11d19c07503__7fe5
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fe6+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fe5)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fe6, @cnefc812f_11d19c07503__7fe5
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE CUSTOM_PORTLET_MODE
END;

-- ----------------------------------------------------------------------- 
-- LOCALIZED_DISPLAY_NAME 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'LOCALIZED_DISPLAY_NAME')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fe4 nvarchar(256), @cnefc812f_11d19c07503__7fe3 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'LOCALIZED_DISPLAY_NAME'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fe4, @cnefc812f_11d19c07503__7fe3
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fe4+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fe3)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fe4, @cnefc812f_11d19c07503__7fe3
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE LOCALIZED_DISPLAY_NAME
END;

-- ----------------------------------------------------------------------- 
-- LOCALIZED_DESCRIPTION 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'LOCALIZED_DESCRIPTION')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fe2 nvarchar(256), @cnefc812f_11d19c07503__7fe1 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'LOCALIZED_DESCRIPTION'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fe2, @cnefc812f_11d19c07503__7fe1
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fe2+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fe1)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fe2, @cnefc812f_11d19c07503__7fe1
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE LOCALIZED_DESCRIPTION
END;

-- ----------------------------------------------------------------------- 
-- JETSPEED_SERVICE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'JETSPEED_SERVICE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fe0 nvarchar(256), @cnefc812f_11d19c07503__7fdf nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'JETSPEED_SERVICE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fe0, @cnefc812f_11d19c07503__7fdf
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fe0+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fdf)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fe0, @cnefc812f_11d19c07503__7fdf
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE JETSPEED_SERVICE
END;

-- ----------------------------------------------------------------------- 
-- USER_ATTRIBUTE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'USER_ATTRIBUTE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fde nvarchar(256), @cnefc812f_11d19c07503__7fdd nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'USER_ATTRIBUTE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fde, @cnefc812f_11d19c07503__7fdd
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fde+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fdd)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fde, @cnefc812f_11d19c07503__7fdd
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE USER_ATTRIBUTE
END;

-- ----------------------------------------------------------------------- 
-- USER_ATTRIBUTE_REF 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'USER_ATTRIBUTE_REF')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fdc nvarchar(256), @cnefc812f_11d19c07503__7fdb nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'USER_ATTRIBUTE_REF'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fdc, @cnefc812f_11d19c07503__7fdb
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fdc+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fdb)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fdc, @cnefc812f_11d19c07503__7fdb
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE USER_ATTRIBUTE_REF
END;

-- ----------------------------------------------------------------------- 
-- SECURITY_ROLE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SECURITY_ROLE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fda nvarchar(256), @cnefc812f_11d19c07503__7fd9 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SECURITY_ROLE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fda, @cnefc812f_11d19c07503__7fd9
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fda+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fd9)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fda, @cnefc812f_11d19c07503__7fd9
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SECURITY_ROLE
END;

-- ----------------------------------------------------------------------- 
-- SECURITY_ROLE_REFERENCE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'SECURITY_ROLE_REFERENCE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fd8 nvarchar(256), @cnefc812f_11d19c07503__7fd7 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'SECURITY_ROLE_REFERENCE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fd8, @cnefc812f_11d19c07503__7fd7
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fd8+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fd7)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fd8, @cnefc812f_11d19c07503__7fd7
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE SECURITY_ROLE_REFERENCE
END;

-- ----------------------------------------------------------------------- 
-- PORTLET_PREFERENCE_VALUE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_PREFERENCE_VALUE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fd6 nvarchar(256), @cnefc812f_11d19c07503__7fd5 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PORTLET_PREFERENCE_VALUE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fd6, @cnefc812f_11d19c07503__7fd5
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fd6+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fd5)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fd6, @cnefc812f_11d19c07503__7fd5
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PORTLET_PREFERENCE_VALUE
END;

-- ----------------------------------------------------------------------- 
-- PORTLET_PREFERENCE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_PREFERENCE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fd4 nvarchar(256), @cnefc812f_11d19c07503__7fd3 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PORTLET_PREFERENCE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fd4, @cnefc812f_11d19c07503__7fd3
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fd4+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fd3)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fd4, @cnefc812f_11d19c07503__7fd3
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PORTLET_PREFERENCE
END;

-- ----------------------------------------------------------------------- 
-- PORTLET_ENTITY 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_ENTITY')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fd2 nvarchar(256), @cnefc812f_11d19c07503__7fd1 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PORTLET_ENTITY'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fd2, @cnefc812f_11d19c07503__7fd1
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fd2+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fd1)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fd2, @cnefc812f_11d19c07503__7fd1
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PORTLET_ENTITY
END;

-- ----------------------------------------------------------------------- 
-- PARAMETER 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PARAMETER')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fd0 nvarchar(256), @cnefc812f_11d19c07503__7fcf nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PARAMETER'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fd0, @cnefc812f_11d19c07503__7fcf
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fd0+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fcf)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fd0, @cnefc812f_11d19c07503__7fcf
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PARAMETER
END;

-- ----------------------------------------------------------------------- 
-- PORTLET_CONTENT_TYPE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_CONTENT_TYPE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fce nvarchar(256), @cnefc812f_11d19c07503__7fcd nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PORTLET_CONTENT_TYPE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fce, @cnefc812f_11d19c07503__7fcd
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fce+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fcd)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fce, @cnefc812f_11d19c07503__7fcd
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PORTLET_CONTENT_TYPE
END;

-- ----------------------------------------------------------------------- 
-- LANGUAGE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'LANGUAGE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fcc nvarchar(256), @cnefc812f_11d19c07503__7fcb nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'LANGUAGE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fcc, @cnefc812f_11d19c07503__7fcb
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fcc+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fcb)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fcc, @cnefc812f_11d19c07503__7fcb
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE LANGUAGE
END;

-- ----------------------------------------------------------------------- 
-- PD_METADATA_FIELDS 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PD_METADATA_FIELDS')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fca nvarchar(256), @cnefc812f_11d19c07503__7fc9 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PD_METADATA_FIELDS'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fca, @cnefc812f_11d19c07503__7fc9
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fca+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fc9)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fca, @cnefc812f_11d19c07503__7fc9
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PD_METADATA_FIELDS
END;

-- ----------------------------------------------------------------------- 
-- PA_METADATA_FIELDS 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PA_METADATA_FIELDS')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fc8 nvarchar(256), @cnefc812f_11d19c07503__7fc7 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PA_METADATA_FIELDS'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fc8, @cnefc812f_11d19c07503__7fc7
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fc8+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fc7)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fc8, @cnefc812f_11d19c07503__7fc7
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PA_METADATA_FIELDS
END;

-- ----------------------------------------------------------------------- 
-- WEB_APPLICATION 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'WEB_APPLICATION')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fc6 nvarchar(256), @cnefc812f_11d19c07503__7fc5 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'WEB_APPLICATION'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fc6, @cnefc812f_11d19c07503__7fc5
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fc6+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fc5)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fc6, @cnefc812f_11d19c07503__7fc5
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE WEB_APPLICATION
END;

-- ----------------------------------------------------------------------- 
-- PORTLET_APPLICATION 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_APPLICATION')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fc4 nvarchar(256), @cnefc812f_11d19c07503__7fc3 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PORTLET_APPLICATION'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fc4, @cnefc812f_11d19c07503__7fc3
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fc4+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fc3)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fc4, @cnefc812f_11d19c07503__7fc3
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PORTLET_APPLICATION
END;

-- ----------------------------------------------------------------------- 
-- PORTLET_DEFINITION 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_DEFINITION')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fc2 nvarchar(256), @cnefc812f_11d19c07503__7fc1 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PORTLET_DEFINITION'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fc2, @cnefc812f_11d19c07503__7fc1
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fc2+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fc1)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fc2, @cnefc812f_11d19c07503__7fc1
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PORTLET_DEFINITION
END;

-- ----------------------------------------------------------------------- 
-- OJB_DMAP 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_DMAP')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fc0 nvarchar(256), @cnefc812f_11d19c07503__7fbf nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'OJB_DMAP'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fc0, @cnefc812f_11d19c07503__7fbf
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fc0+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fbf)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fc0, @cnefc812f_11d19c07503__7fbf
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE OJB_DMAP
END;

-- ----------------------------------------------------------------------- 
-- OJB_DSET_ENTRIES 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_DSET_ENTRIES')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fbe nvarchar(256), @cnefc812f_11d19c07503__7fbd nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'OJB_DSET_ENTRIES'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fbe, @cnefc812f_11d19c07503__7fbd
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fbe+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fbd)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fbe, @cnefc812f_11d19c07503__7fbd
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE OJB_DSET_ENTRIES
END;

-- ----------------------------------------------------------------------- 
-- OJB_DSET 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_DSET')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fbc nvarchar(256), @cnefc812f_11d19c07503__7fbb nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'OJB_DSET'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fbc, @cnefc812f_11d19c07503__7fbb
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fbc+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fbb)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fbc, @cnefc812f_11d19c07503__7fbb
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE OJB_DSET
END;

-- ----------------------------------------------------------------------- 
-- OJB_DLIST_ENTRIES 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_DLIST_ENTRIES')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fba nvarchar(256), @cnefc812f_11d19c07503__7fb9 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'OJB_DLIST_ENTRIES'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fba, @cnefc812f_11d19c07503__7fb9
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fba+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fb9)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fba, @cnefc812f_11d19c07503__7fb9
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE OJB_DLIST_ENTRIES
END;

-- ----------------------------------------------------------------------- 
-- OJB_DLIST 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_DLIST')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fb8 nvarchar(256), @cnefc812f_11d19c07503__7fb7 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'OJB_DLIST'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fb8, @cnefc812f_11d19c07503__7fb7
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fb8+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fb7)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fb8, @cnefc812f_11d19c07503__7fb7
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE OJB_DLIST
END;

-- ----------------------------------------------------------------------- 
-- OJB_NRM 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_NRM')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fb6 nvarchar(256), @cnefc812f_11d19c07503__7fb5 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'OJB_NRM'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fb6, @cnefc812f_11d19c07503__7fb5
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fb6+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fb5)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fb6, @cnefc812f_11d19c07503__7fb5
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE OJB_NRM
END;

-- ----------------------------------------------------------------------- 
-- OJB_LOCKENTRY 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_LOCKENTRY')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fb4 nvarchar(256), @cnefc812f_11d19c07503__7fb3 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'OJB_LOCKENTRY'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fb4, @cnefc812f_11d19c07503__7fb3
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fb4+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fb3)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fb4, @cnefc812f_11d19c07503__7fb3
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE OJB_LOCKENTRY
END;

-- ----------------------------------------------------------------------- 
-- OJB_HL_SEQ 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'OJB_HL_SEQ')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fb2 nvarchar(256), @cnefc812f_11d19c07503__7fb1 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'OJB_HL_SEQ'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fb2, @cnefc812f_11d19c07503__7fb1
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fb2+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fb1)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fb2, @cnefc812f_11d19c07503__7fb1
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE OJB_HL_SEQ
END;

-- ----------------------------------------------------------------------- 
-- CLUBS 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CLUBS')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fb0 nvarchar(256), @cnefc812f_11d19c07503__7faf nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'CLUBS'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fb0, @cnefc812f_11d19c07503__7faf
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fb0+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7faf)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fb0, @cnefc812f_11d19c07503__7faf
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE CLUBS
END;

-- ----------------------------------------------------------------------- 
-- PROFILE_PAGE_ASSOC 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PROFILE_PAGE_ASSOC')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fae nvarchar(256), @cnefc812f_11d19c07503__7fad nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PROFILE_PAGE_ASSOC'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fae, @cnefc812f_11d19c07503__7fad
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fae+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fad)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fae, @cnefc812f_11d19c07503__7fad
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PROFILE_PAGE_ASSOC
END;

-- ----------------------------------------------------------------------- 
-- PRINCIPAL_RULE_ASSOC 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PRINCIPAL_RULE_ASSOC')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fac nvarchar(256), @cnefc812f_11d19c07503__7fab nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PRINCIPAL_RULE_ASSOC'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fac, @cnefc812f_11d19c07503__7fab
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fac+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fab)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fac, @cnefc812f_11d19c07503__7fab
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PRINCIPAL_RULE_ASSOC
END;

-- ----------------------------------------------------------------------- 
-- RULE_CRITERION 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'RULE_CRITERION')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7faa nvarchar(256), @cnefc812f_11d19c07503__7fa9 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'RULE_CRITERION'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7faa, @cnefc812f_11d19c07503__7fa9
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7faa+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fa9)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7faa, @cnefc812f_11d19c07503__7fa9
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE RULE_CRITERION
END;

-- ----------------------------------------------------------------------- 
-- PROFILING_RULE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PROFILING_RULE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fa8 nvarchar(256), @cnefc812f_11d19c07503__7fa7 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PROFILING_RULE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fa8, @cnefc812f_11d19c07503__7fa7
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fa8+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fa7)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fa8, @cnefc812f_11d19c07503__7fa7
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PROFILING_RULE
END;

-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE_SEC_CONSTRAINTS_REF')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fa6 nvarchar(256), @cnefc812f_11d19c07503__7fa5 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PAGE_SEC_CONSTRAINTS_REF'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fa6, @cnefc812f_11d19c07503__7fa5
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fa6+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fa5)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fa6, @cnefc812f_11d19c07503__7fa5
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PAGE_SEC_CONSTRAINTS_REF
END;

-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINT_DEF 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE_SEC_CONSTRAINT_DEF')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fa4 nvarchar(256), @cnefc812f_11d19c07503__7fa3 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PAGE_SEC_CONSTRAINT_DEF'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fa4, @cnefc812f_11d19c07503__7fa3
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fa4+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fa3)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fa4, @cnefc812f_11d19c07503__7fa3
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PAGE_SEC_CONSTRAINT_DEF
END;

-- ----------------------------------------------------------------------- 
-- PAGE_SEC_CONSTRAINTS_DEF 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE_SEC_CONSTRAINTS_DEF')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fa2 nvarchar(256), @cnefc812f_11d19c07503__7fa1 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PAGE_SEC_CONSTRAINTS_DEF'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fa2, @cnefc812f_11d19c07503__7fa1
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fa2+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7fa1)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fa2, @cnefc812f_11d19c07503__7fa1
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PAGE_SEC_CONSTRAINTS_DEF
END;

-- ----------------------------------------------------------------------- 
-- PAGE_SECURITY 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE_SECURITY')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7fa0 nvarchar(256), @cnefc812f_11d19c07503__7f9f nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PAGE_SECURITY'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fa0, @cnefc812f_11d19c07503__7f9f
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7fa0+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f9f)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7fa0, @cnefc812f_11d19c07503__7f9f
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PAGE_SECURITY
END;

-- ----------------------------------------------------------------------- 
-- LINK_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'LINK_CONSTRAINTS_REF')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f9e nvarchar(256), @cnefc812f_11d19c07503__7f9d nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'LINK_CONSTRAINTS_REF'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f9e, @cnefc812f_11d19c07503__7f9d
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f9e+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f9d)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f9e, @cnefc812f_11d19c07503__7f9d
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE LINK_CONSTRAINTS_REF
END;

-- ----------------------------------------------------------------------- 
-- LINK_CONSTRAINT 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'LINK_CONSTRAINT')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f9c nvarchar(256), @cnefc812f_11d19c07503__7f9b nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'LINK_CONSTRAINT'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f9c, @cnefc812f_11d19c07503__7f9b
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f9c+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f9b)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f9c, @cnefc812f_11d19c07503__7f9b
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE LINK_CONSTRAINT
END;

-- ----------------------------------------------------------------------- 
-- LINK_METADATA 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'LINK_METADATA')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f9a nvarchar(256), @cnefc812f_11d19c07503__7f99 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'LINK_METADATA'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f9a, @cnefc812f_11d19c07503__7f99
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f9a+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f99)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f9a, @cnefc812f_11d19c07503__7f99
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE LINK_METADATA
END;

-- ----------------------------------------------------------------------- 
-- LINK 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'LINK')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f98 nvarchar(256), @cnefc812f_11d19c07503__7f97 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'LINK'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f98, @cnefc812f_11d19c07503__7f97
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f98+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f97)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f98, @cnefc812f_11d19c07503__7f97
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE LINK
END;

-- ----------------------------------------------------------------------- 
-- FRAGMENT_PREF_VALUE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FRAGMENT_PREF_VALUE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f96 nvarchar(256), @cnefc812f_11d19c07503__7f95 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FRAGMENT_PREF_VALUE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f96, @cnefc812f_11d19c07503__7f95
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f96+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f95)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f96, @cnefc812f_11d19c07503__7f95
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FRAGMENT_PREF_VALUE
END;

-- ----------------------------------------------------------------------- 
-- FRAGMENT_PREF 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FRAGMENT_PREF')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f94 nvarchar(256), @cnefc812f_11d19c07503__7f93 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FRAGMENT_PREF'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f94, @cnefc812f_11d19c07503__7f93
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f94+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f93)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f94, @cnefc812f_11d19c07503__7f93
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FRAGMENT_PREF
END;

-- ----------------------------------------------------------------------- 
-- FRAGMENT_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FRAGMENT_CONSTRAINTS_REF')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f92 nvarchar(256), @cnefc812f_11d19c07503__7f91 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FRAGMENT_CONSTRAINTS_REF'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f92, @cnefc812f_11d19c07503__7f91
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f92+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f91)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f92, @cnefc812f_11d19c07503__7f91
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FRAGMENT_CONSTRAINTS_REF
END;

-- ----------------------------------------------------------------------- 
-- FRAGMENT_CONSTRAINT 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FRAGMENT_CONSTRAINT')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f90 nvarchar(256), @cnefc812f_11d19c07503__7f8f nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FRAGMENT_CONSTRAINT'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f90, @cnefc812f_11d19c07503__7f8f
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f90+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f8f)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f90, @cnefc812f_11d19c07503__7f8f
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FRAGMENT_CONSTRAINT
END;

-- ----------------------------------------------------------------------- 
-- FRAGMENT 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FRAGMENT')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f8e nvarchar(256), @cnefc812f_11d19c07503__7f8d nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FRAGMENT'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f8e, @cnefc812f_11d19c07503__7f8d
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f8e+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f8d)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f8e, @cnefc812f_11d19c07503__7f8d
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FRAGMENT
END;

-- ----------------------------------------------------------------------- 
-- PAGE_MENU_METADATA 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE_MENU_METADATA')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f8c nvarchar(256), @cnefc812f_11d19c07503__7f8b nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PAGE_MENU_METADATA'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f8c, @cnefc812f_11d19c07503__7f8b
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f8c+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f8b)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f8c, @cnefc812f_11d19c07503__7f8b
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PAGE_MENU_METADATA
END;

-- ----------------------------------------------------------------------- 
-- PAGE_MENU 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE_MENU')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f8a nvarchar(256), @cnefc812f_11d19c07503__7f89 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PAGE_MENU'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f8a, @cnefc812f_11d19c07503__7f89
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f8a+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f89)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f8a, @cnefc812f_11d19c07503__7f89
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PAGE_MENU
END;

-- ----------------------------------------------------------------------- 
-- PAGE_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE_CONSTRAINTS_REF')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f88 nvarchar(256), @cnefc812f_11d19c07503__7f87 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PAGE_CONSTRAINTS_REF'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f88, @cnefc812f_11d19c07503__7f87
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f88+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f87)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f88, @cnefc812f_11d19c07503__7f87
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PAGE_CONSTRAINTS_REF
END;

-- ----------------------------------------------------------------------- 
-- PAGE_CONSTRAINT 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE_CONSTRAINT')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f86 nvarchar(256), @cnefc812f_11d19c07503__7f85 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PAGE_CONSTRAINT'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f86, @cnefc812f_11d19c07503__7f85
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f86+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f85)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f86, @cnefc812f_11d19c07503__7f85
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PAGE_CONSTRAINT
END;

-- ----------------------------------------------------------------------- 
-- PAGE_METADATA 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE_METADATA')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f84 nvarchar(256), @cnefc812f_11d19c07503__7f83 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PAGE_METADATA'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f84, @cnefc812f_11d19c07503__7f83
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f84+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f83)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f84, @cnefc812f_11d19c07503__7f83
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PAGE_METADATA
END;

-- ----------------------------------------------------------------------- 
-- PAGE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f82 nvarchar(256), @cnefc812f_11d19c07503__7f81 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PAGE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f82, @cnefc812f_11d19c07503__7f81
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f82+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f81)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f82, @cnefc812f_11d19c07503__7f81
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PAGE
END;

-- ----------------------------------------------------------------------- 
-- FOLDER_MENU_METADATA 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FOLDER_MENU_METADATA')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f80 nvarchar(256), @cnefc812f_11d19c07503__7f7f nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FOLDER_MENU_METADATA'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f80, @cnefc812f_11d19c07503__7f7f
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f80+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f7f)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f80, @cnefc812f_11d19c07503__7f7f
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FOLDER_MENU_METADATA
END;

-- ----------------------------------------------------------------------- 
-- FOLDER_MENU 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FOLDER_MENU')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f7e nvarchar(256), @cnefc812f_11d19c07503__7f7d nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FOLDER_MENU'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f7e, @cnefc812f_11d19c07503__7f7d
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f7e+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f7d)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f7e, @cnefc812f_11d19c07503__7f7d
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FOLDER_MENU
END;

-- ----------------------------------------------------------------------- 
-- FOLDER_ORDER 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FOLDER_ORDER')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f7c nvarchar(256), @cnefc812f_11d19c07503__7f7b nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FOLDER_ORDER'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f7c, @cnefc812f_11d19c07503__7f7b
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f7c+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f7b)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f7c, @cnefc812f_11d19c07503__7f7b
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FOLDER_ORDER
END;

-- ----------------------------------------------------------------------- 
-- FOLDER_CONSTRAINTS_REF 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FOLDER_CONSTRAINTS_REF')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f7a nvarchar(256), @cnefc812f_11d19c07503__7f79 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FOLDER_CONSTRAINTS_REF'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f7a, @cnefc812f_11d19c07503__7f79
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f7a+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f79)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f7a, @cnefc812f_11d19c07503__7f79
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FOLDER_CONSTRAINTS_REF
END;

-- ----------------------------------------------------------------------- 
-- FOLDER_CONSTRAINT 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FOLDER_CONSTRAINT')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f78 nvarchar(256), @cnefc812f_11d19c07503__7f77 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FOLDER_CONSTRAINT'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f78, @cnefc812f_11d19c07503__7f77
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f78+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f77)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f78, @cnefc812f_11d19c07503__7f77
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FOLDER_CONSTRAINT
END;

-- ----------------------------------------------------------------------- 
-- FOLDER_METADATA 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FOLDER_METADATA')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f76 nvarchar(256), @cnefc812f_11d19c07503__7f75 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FOLDER_METADATA'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f76, @cnefc812f_11d19c07503__7f75
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f76+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f75)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f76, @cnefc812f_11d19c07503__7f75
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FOLDER_METADATA
END;

-- ----------------------------------------------------------------------- 
-- FOLDER 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'FOLDER')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f74 nvarchar(256), @cnefc812f_11d19c07503__7f73 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'FOLDER'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f74, @cnefc812f_11d19c07503__7f73
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f74+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f73)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f74, @cnefc812f_11d19c07503__7f73
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE FOLDER
END;

-- ----------------------------------------------------------------------- 
-- USER_ACTIVITY 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'USER_ACTIVITY')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f72 nvarchar(256), @cnefc812f_11d19c07503__7f71 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'USER_ACTIVITY'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f72, @cnefc812f_11d19c07503__7f71
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f72+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f71)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f72, @cnefc812f_11d19c07503__7f71
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE USER_ACTIVITY
END;

-- ----------------------------------------------------------------------- 
-- ADMIN_ACTIVITY 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'ADMIN_ACTIVITY')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f70 nvarchar(256), @cnefc812f_11d19c07503__7f6f nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'ADMIN_ACTIVITY'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f70, @cnefc812f_11d19c07503__7f6f
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f70+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f6f)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f70, @cnefc812f_11d19c07503__7f6f
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE ADMIN_ACTIVITY
END;

-- ----------------------------------------------------------------------- 
-- USER_STATISTICS 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'USER_STATISTICS')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f6e nvarchar(256), @cnefc812f_11d19c07503__7f6d nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'USER_STATISTICS'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f6e, @cnefc812f_11d19c07503__7f6d
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f6e+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f6d)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f6e, @cnefc812f_11d19c07503__7f6d
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE USER_STATISTICS
END;

-- ----------------------------------------------------------------------- 
-- PAGE_STATISTICS 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PAGE_STATISTICS')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f6c nvarchar(256), @cnefc812f_11d19c07503__7f6b nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PAGE_STATISTICS'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f6c, @cnefc812f_11d19c07503__7f6b
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f6c+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f6b)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f6c, @cnefc812f_11d19c07503__7f6b
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PAGE_STATISTICS
END;

-- ----------------------------------------------------------------------- 
-- PORTLET_STATISTICS 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'PORTLET_STATISTICS')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f6a nvarchar(256), @cnefc812f_11d19c07503__7f69 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'PORTLET_STATISTICS'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f6a, @cnefc812f_11d19c07503__7f69
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f6a+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f69)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f6a, @cnefc812f_11d19c07503__7f69
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE PORTLET_STATISTICS
END;

-- ----------------------------------------------------------------------- 
-- MEDIATYPE_TO_MIMETYPE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'MEDIATYPE_TO_MIMETYPE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f68 nvarchar(256), @cnefc812f_11d19c07503__7f67 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'MEDIATYPE_TO_MIMETYPE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f68, @cnefc812f_11d19c07503__7f67
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f68+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f67)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f68, @cnefc812f_11d19c07503__7f67
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE MEDIATYPE_TO_MIMETYPE
END;

-- ----------------------------------------------------------------------- 
-- MEDIATYPE_TO_CAPABILITY 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'MEDIATYPE_TO_CAPABILITY')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f66 nvarchar(256), @cnefc812f_11d19c07503__7f65 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'MEDIATYPE_TO_CAPABILITY'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f66, @cnefc812f_11d19c07503__7f65
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f66+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f65)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f66, @cnefc812f_11d19c07503__7f65
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE MEDIATYPE_TO_CAPABILITY
END;

-- ----------------------------------------------------------------------- 
-- CLIENT_TO_MIMETYPE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CLIENT_TO_MIMETYPE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f64 nvarchar(256), @cnefc812f_11d19c07503__7f63 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'CLIENT_TO_MIMETYPE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f64, @cnefc812f_11d19c07503__7f63
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f64+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f63)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f64, @cnefc812f_11d19c07503__7f63
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE CLIENT_TO_MIMETYPE
END;

-- ----------------------------------------------------------------------- 
-- CLIENT_TO_CAPABILITY 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CLIENT_TO_CAPABILITY')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f62 nvarchar(256), @cnefc812f_11d19c07503__7f61 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'CLIENT_TO_CAPABILITY'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f62, @cnefc812f_11d19c07503__7f61
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f62+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f61)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f62, @cnefc812f_11d19c07503__7f61
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE CLIENT_TO_CAPABILITY
END;

-- ----------------------------------------------------------------------- 
-- CAPABILITY 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CAPABILITY')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f60 nvarchar(256), @cnefc812f_11d19c07503__7f5f nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'CAPABILITY'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f60, @cnefc812f_11d19c07503__7f5f
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f60+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f5f)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f60, @cnefc812f_11d19c07503__7f5f
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE CAPABILITY
END;

-- ----------------------------------------------------------------------- 
-- MIMETYPE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'MIMETYPE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f5e nvarchar(256), @cnefc812f_11d19c07503__7f5d nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'MIMETYPE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f5e, @cnefc812f_11d19c07503__7f5d
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f5e+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f5d)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f5e, @cnefc812f_11d19c07503__7f5d
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE MIMETYPE
END;

-- ----------------------------------------------------------------------- 
-- CLIENT 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'CLIENT')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f5c nvarchar(256), @cnefc812f_11d19c07503__7f5b nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'CLIENT'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f5c, @cnefc812f_11d19c07503__7f5b
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f5c+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f5b)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f5c, @cnefc812f_11d19c07503__7f5b
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE CLIENT
END;

-- ----------------------------------------------------------------------- 
-- MEDIA_TYPE 
-- ----------------------------------------------------------------------- 

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'MEDIA_TYPE')
BEGIN
  DECLARE @tnefc812f_11d19c07503__7f5a nvarchar(256), @cnefc812f_11d19c07503__7f59 nvarchar(256)
  DECLARE refcursor CURSOR FOR
  SELECT object_name(objs.parent_obj) tablename, objs.name constraintname
    FROM sysobjects objs JOIN sysconstraints cons ON objs.id = cons.constid
    WHERE objs.xtype != 'PK' AND object_name(objs.parent_obj) = 'MEDIA_TYPE'  OPEN refcursor
  FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f5a, @cnefc812f_11d19c07503__7f59
  WHILE @@FETCH_STATUS = 0
    BEGIN
      EXEC ('ALTER TABLE '+@tnefc812f_11d19c07503__7f5a+' DROP CONSTRAINT '+@cnefc812f_11d19c07503__7f59)
      FETCH NEXT FROM refcursor INTO @tnefc812f_11d19c07503__7f5a, @cnefc812f_11d19c07503__7f59
    END
  CLOSE refcursor
  DEALLOCATE refcursor
  DROP TABLE MEDIA_TYPE
END;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='TR' AND name='trig_SECURITY_PRINCIPAL')
    DROP TRIGGER trig_SECURITY_PRINCIPAL;

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='TR' AND name='trig_PREFS_NODE')
    DROP TRIGGER trig_PREFS_NODE;
