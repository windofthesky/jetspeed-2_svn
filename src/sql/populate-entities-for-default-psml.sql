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

-- this is a temporary script to populate entities for the XML Page Registry
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (1, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'TwoColumns'), '1');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (2, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'VelocityTwoColumns'), '2');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (3, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'HelloPortlet'), '3');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (4, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'JMXPortlet'), '4');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (5, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'PreferencePortlet'), '5');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (6, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'DisplayRequestPortlet'), '6');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (7, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'PickANumberPortlet'), '7');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (8, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'AttributeScopePortlet'), '8');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (9, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'CustomerList'), '9');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (10, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'CustomerInfo'), '10');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (11, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'CustomerDetail'), '11');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (12, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'LoginPortlet'), '12');
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID) 
  VALUES (13, (SELECT ID FROM PORTLET_DEFINITION WHERE NAME = 'PortletApplicationBrowser'), '13');
