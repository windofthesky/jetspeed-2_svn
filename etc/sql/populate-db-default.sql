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

------------------------------------------
-- The Jetspeed Internal Portlet App
------------------------------------------
-- INSERT INTO WEB_APPLICATION VALUES(1,'/jetspeed');
-- INSERT INTO PORTLET_APPLICATION VALUES (1, 'jetspeed', 'jetspeed', '1.0', 0, NULL, 1);
-- INSERT INTO PORTLET_DEFINITION VALUES(1, 'Layout',
-- 'org.apache.jetspeed.portlets.layout.GenericLayoutPortlet',1,'Layout','-1',NULL);

------------------------------------------
-- Add seed data for Capability
------------------------------------------
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(1,'HTML_3_2');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(2,'HTML_4_0');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(3,'HTML_TABLE');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(4,'HTML_NESTED_TABLE');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(5,'HTML_IMAGE');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(6, 'HTML_FORM');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(7, 'HTML_FRAME');

INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(8,'HTML_JAVA');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(9,'HTML_JAVA1_0');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(10,'HTML_JAVA1_1');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(11,'HTML_JAVA1_2');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(12,'HTML_JAVA_JRE');


INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(13,'HTML_JSCRIPT');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(14,'HTML_JSCRIPT1_0');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(15,'HTML_JSCRIPT1_1');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(16,'HTML_JSCRIPT1_2');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(17,'HTML_JAVASCRIPT');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(18,'HTML_JAVASCRIPT_1_0');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(19,'HTML_JAVASCRIPT_1_1');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(20,'HTML_JAVASCRIPT_1_2');

INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(21,'HTML_PLUGIN');
 
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(22,'HTML_ACTIVEX');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(23,'HTML_PLUGIN');

INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(24,'HTML_CSS1');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(25,'HTML_CSS2');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(26,'HTML_CSSP');

INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(27,'HTML_IFRAME');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(28,'HTML_LAYER');

INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(29,'HTML_DOM_IE');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(30,'HTML_DOM_NS4');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(31,'HTML_DOM_1');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(32,'HTML_DOM_2');

INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(33,'WML_1_0');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(34,'WML_1_1');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(35,'WML_TABLE');

INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(36,'XML_XSLT');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(37,'XML_XPATH');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(38,'XML_XINCLUDE');

INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(39,'HTTP_1_1');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(40,'HTTP_COOKIE');

INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(41,'HTML_XML');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(42,'HTML_XSL');
INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES(43,'HTML_DOM');

---------------------------------------------------------------------------
-- MIME TYPES
---------------------------------------------------------------------------
INSERT INTO MIMETYPE (MIMETYPE_ID, NAME) VALUES(1,'text/html');
INSERT INTO MIMETYPE (MIMETYPE_ID, NAME) VALUES(2,'text/vnd.wap.wml');
INSERT INTO MIMETYPE (MIMETYPE_ID, NAME) VALUES(3,'text/vxml');
INSERT INTO MIMETYPE (MIMETYPE_ID, NAME) VALUES(4,'text/xml');
INSERT INTO MIMETYPE (MIMETYPE_ID, NAME) VALUES(5,'text/xhtml');
INSERT INTO MIMETYPE (MIMETYPE_ID, NAME) VALUES(6,'application/xhtml+xml');

----------------------------------------------------------------------------
-- Supported clients
----------------------------------------------------------------------------
INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(1, 4,'ie5','.*MSIE 5.*','Microsoft','None','5.5',1);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(2, 5, 'ns4','.*Mozilla/4.*','Netscape','None','4.75',1);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(3, 6, 'mozilla','.*Mozilla/5.*','Mozilla','Mozilla','1.x',1);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(4, 7, 'lynx','Lynx.*','GNU','None','',1);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(5, 8, 'nokia_generic','Nokia.*','Nokia','Generic','',2);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(6, 10, 'up','UP.*|.*UP\.Browser.*','United Planet','Generic','',2);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(7, 11, 'sonyericsson','Ercis.*|SonyE.*','SonyEricsson','Generic','',2);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(8, 12, 'wapalizer','Wapalizer.*','Wapalizer','Generic','',2);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(9, 13, 'klondike','Klondike.*','Klondike','Generic','',2);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(10, 14, 'wml_generic','.*WML.*|.*WAP.*|.*Wap.*|.*wml.*','Generic','Generic','',2);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(11, 15, 'vxml_generic','.*VoiceXML.*','Generic','Generic','',3);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(12, 16, 'nuance','Nuance.*','Nuance','Generic','',3);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(13, 17, 'agentxml','agentxml/1.0.*','Unknown','Generic','',4);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(14, 18, 'opera7','.*Opera/7.*','Opera','Opera7','7.x',1);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(15, 1, 'ie5mac','.*MSIE 5.*Mac.*','Microsoft','None','5.*',1);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(16, 2, 'safari','.*Mac.*Safari.*','Apple','None','5.*',1);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(17, 3,'ie6','.*MSIE 6.*','Microsoft','None','6.0',1);

INSERT INTO CLIENT (CLIENT_ID,EVAL_ORDER,NAME,	USER_AGENT_PATTERN,MANUFACTURER,MODEL ,VERSION, PREFERRED_MIMETYPE_ID)
VALUES(18, 9, 'xhtml-basic','DoCoMo/2.0.*|KDDI-.*UP\.Browser.*|J-PHONE/5.0.*|Vodafone/1.0/.*','WAP','Generic','',6);



----------------------------------------------------------------------------------------
-- Supported Media types
----------------------------------------------------------------------------------------
INSERT INTO MEDIA_TYPE (MEDIATYPE_ID,NAME, CHARACTER_SET, TITLE, DESCRIPTION)
VALUES(1,'html','UTF-8','HTML','Rich HTML for HTML 4.0 compliants browsers');

INSERT INTO MEDIA_TYPE (MEDIATYPE_ID,NAME, CHARACTER_SET, TITLE, DESCRIPTION)
VALUES(2,'wml','UTF-8','WML','Format for mobile phones and PDAs compatible with WML 1.1');

INSERT INTO MEDIA_TYPE (MEDIATYPE_ID,NAME, CHARACTER_SET, TITLE, DESCRIPTION)
VALUES(3,'vxml','UTF-8','VoiceXML','Format suitable for use with an audio VoiceXML server');

INSERT INTO MEDIA_TYPE (MEDIATYPE_ID,NAME, CHARACTER_SET, TITLE, DESCRIPTION)
VALUES(4,'xml','','XML','XML 1.0');

INSERT INTO MEDIA_TYPE (MEDIATYPE_ID,NAME, CHARACTER_SET, TITLE, DESCRIPTION)
VALUES(5,'xhtml-basic','UTF-8','XHTML','XHTML Basic');

------------------------------------------------------
-- Client association
------------------------------------------------------

-- Client To Capability

INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,1);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,8);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,17);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,3);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,4);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,6);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,7);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,5);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,22);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,24);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,25);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,26);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,27);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,29);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(1,40);

INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,1);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,8);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,17);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,3);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,6);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,7);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,5);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,24);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,28);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,21);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,30);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(2,40);

INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,1);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,2);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,8);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,12);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,17);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,3);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,4);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,6);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,7);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,27);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,5);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,24);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,25);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,26);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,31);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,23);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(3,40);

INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(4,3);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(4,4);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(4,6);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(4,7);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(4,40);

INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,1);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,2);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,3);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,8);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,12);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,17);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,4);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,6);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,7);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,5);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,27);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,24);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,25);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,26);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,31);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,40);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(14,23);

INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(15,1);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(15,8);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(15,17);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(15,3);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(15,6);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(15,7);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(15,5);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(15,21);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(15,24);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(15,30);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(15,40);

INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,1);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,8);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,17);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,3);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,4);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,6);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,7);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,5);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,22);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,24);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,25);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,26);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,27);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,29);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(16,40);

INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,1);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,8);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,17);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,3);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,4);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,6);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,7);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,5);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,22);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,24);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,25);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,26);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,27);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,29);
INSERT INTO CLIENT_TO_CAPABILITY(CLIENT_ID,CAPABILITY_ID )VALUES(17,40);

-- Client To Mimetype

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(1,1);
INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(1,4);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(2,1);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(3,1);
INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(3,5);
INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(3,4);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(4,1);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(5,2);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(6,2);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(7,2);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(8,2);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(9,2);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(10,2);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(11,3);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(12,3);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(13,4);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(14,1);
INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(14,4);
INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(14,5);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(15,1);
INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(16,1);
INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(16,4);
INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(16,5);
INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(17,1);
INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(17,4);
INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(17,5);

INSERT INTO CLIENT_TO_MIMETYPE(CLIENT_ID ,MIMETYPE_ID)VALUES(18,6);

----------------------------------------------------
-- Media Type association
----------------------------------------------------

-- Media Type To Capability

-- Media Type To Mimetype

INSERT INTO MEDIATYPE_TO_MIMETYPE(MEDIATYPE_ID ,MIMETYPE_ID)VALUES(1,1);
INSERT INTO MEDIATYPE_TO_MIMETYPE(MEDIATYPE_ID ,MIMETYPE_ID)VALUES(2,2);
INSERT INTO MEDIATYPE_TO_MIMETYPE(MEDIATYPE_ID ,MIMETYPE_ID)VALUES(3,3);
INSERT INTO MEDIATYPE_TO_MIMETYPE(MEDIATYPE_ID ,MIMETYPE_ID)VALUES(4,4);
INSERT INTO MEDIATYPE_TO_MIMETYPE(MEDIATYPE_ID ,MIMETYPE_ID)VALUES(5,6);

-----------------------
-- Profiler
-----------------------
insert into PROFILING_RULE values ('j1', 
   'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule',
   'The default profiling rule following the Jetspeed-1 hard-coded profiler fallback algorithm.');
insert into RULE_CRITERION values ('1', 'j1', 0, 'path.session', 'page', 'default-page', 0);
insert into RULE_CRITERION values ('2', 'j1', 1, 'group.role.user', 'user', null, 0);
insert into RULE_CRITERION values ('3', 'j1', 2, 'mediatype', 'mediatype', null, 1);
insert into RULE_CRITERION values ('4', 'j1', 3, 'language', 'language', null, 1);
insert into RULE_CRITERION values ('5', 'j1', 4, 'country', 'country', null, 1);

insert into PROFILING_RULE values ('role-fallback', 
     'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule',
     'A role based fallback algorithm based on Jetspeed-1 role-based fallback');
insert into RULE_CRITERION values ('6', 'role-fallback', 0, 'role', 'role', null, 2);
insert into RULE_CRITERION values ('7', 'role-fallback', 1, 'path.session', 'page', 'default-page', 0);

insert into PROFILING_RULE values ('path', 
   'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule',
   'use a path to locate.');
insert into RULE_CRITERION values ('10', 'path', 0, 'path', 'path', '/', 0);

insert into PROFILING_RULE values ('role-group', 
     'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule',
     'A role based fallback algorithm that searches all groups and roles for a user');
insert into RULE_CRITERION values ('11', 'role-group', 0, 'role', 'role', null, 2);
insert into RULE_CRITERION values ('12', 'role-group', 1, 'navigation', 'navigation', '/', 2);
insert into RULE_CRITERION values ('13', 'role-group', 2, 'group', 'group', null, 2);

insert into PROFILING_RULE values ('group-fallback', 
     'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule',
     'A role based fallback algorithm based on Jetspeed-1 group-based fallback');
insert into RULE_CRITERION values ('15', 'group-fallback', 0, 'group', 'group', null, 2);
insert into RULE_CRITERION values ('16', 'group-fallback', 1, 'path.session', 'page', 'default-page', 0);

insert into PROFILING_RULE values ('security', 
   'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule',
   'The security profiling rule needed for credential change requirements.');
insert into RULE_CRITERION values ('17', 'security', 0, 'hard.coded', 'page', '/my-account.psml', 0);

insert into PROFILING_RULE values ('j2', 
   'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule',
   'The default profiling rule for users and mediatype minus language and country.');
insert into RULE_CRITERION values ('18', 'j2', 0, 'path.session', 'page', 'default-page', 0);
insert into RULE_CRITERION values ('19', 'j2', 1, 'group.role.user', 'user', null, 0);
insert into RULE_CRITERION values ('20', 'j2', 2, 'mediatype', 'mediatype', null, 1);

insert into PROFILING_RULE values ('user-role-fallback', 
     'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule',
     'A role based fallback algorithm based on Jetspeed-1 role-based fallback');
insert into RULE_CRITERION values ('30', 'user-role-fallback', 0, 'user', 'user', null, 2);
insert into RULE_CRITERION values ('31', 'user-role-fallback', 1, 'navigation', 'navigation', '/', 2);
insert into RULE_CRITERION values ('32', 'user-role-fallback', 2, 'role', 'role', null, 2);
insert into RULE_CRITERION values ('33', 'user-role-fallback', 3, 'path.session', 'page', 'default-page', 1);

insert into PROFILING_RULE values ('user-rolecombo-fallback', 
     'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule',
     'A role based fallback algorithm based on Jetspeed-1 role-based fallback');
insert into RULE_CRITERION values ('40', 'user-rolecombo-fallback', 0, 'user', 'user', null, 2);
insert into RULE_CRITERION values ('41', 'user-rolecombo-fallback', 1, 'navigation', 'navigation', '/', 2);
insert into RULE_CRITERION values ('42', 'user-rolecombo-fallback', 2, 'rolecombo', 'role', null, 2);
insert into RULE_CRITERION values ('43', 'user-rolecombo-fallback', 3, 'path.session', 'page', 'default-page', 1);

insert into PROFILING_RULE values ('subsite-role-fallback-home',
     'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule',
     'A rule based on role fallback algorithm with specified subsite and home page');
insert into RULE_CRITERION values ('50', 'subsite-role-fallback-home', 0, 'navigation', 'navigation', 'subsite-root', 2);
insert into RULE_CRITERION values ('51', 'subsite-role-fallback-home', 1, 'role', 'role', null, 2);
insert into RULE_CRITERION values ('52', 'subsite-role-fallback-home', 2, 'path', 'path', 'subsite-default-page', 0);

insert into PROFILING_RULE values ('subsite2-role-fallback-home', 
     'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule',
     'A rule based on role fallback algorithm with specified subsite and home page');
insert into RULE_CRITERION values ('53', 'subsite2-role-fallback-home', 0, 'navigation', 'navigation', 'subsite-root', 2);
insert into RULE_CRITERION values ('54', 'subsite2-role-fallback-home', 1, 'role', 'role', null, 2);
insert into RULE_CRITERION values ('55', 'subsite2-role-fallback-home', 2, 'path', 'path', 'subsite2-default-page', 0);
insert into PROFILING_RULE values ('ip-address', 
   'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule',
   'Resolves pages based on the clients remote IP address.');
insert into RULE_CRITERION values ('60', 'ip-address', 0, 'ip', 'ip', null, 0);

insert into PRINCIPAL_RULE_ASSOC values ( 'guest', 'page', 'j2' );
insert into PRINCIPAL_RULE_ASSOC values ( 'jetspeed', 'page', 'role-fallback' );
insert into PRINCIPAL_RULE_ASSOC values ( 'jetspeed', 'menu', 'role-group' );
insert into PRINCIPAL_RULE_ASSOC values ( 'user', 'page', 'j2' );
insert into PRINCIPAL_RULE_ASSOC values ( 'user', 'menu', 'role-group' );

insert into PRINCIPAL_RULE_ASSOC values ('subsite', 'page', 'subsite-role-fallback-home');
insert into PRINCIPAL_RULE_ASSOC values ('subsite2', 'page', 'subsite2-role-fallback-home');

insert into PRINCIPAL_RULE_ASSOC values ('geo', 'page', 'ip-address');

-- test data for db browser
insert into CLUBS values ('AC Milan', 'Italy', 'Milan', 'San Siro', 85700, 1926, '105x68m', '');
insert into CLUBS values ('Arsenal', 'England', 'London', 'Highbury', 38500, 1913, '101x65', 'Gunners');
insert into CLUBS values ('Barcelona', 'Spain', 'Barcelona', 'Nou Camp', 110000, 1957, '', '');
insert into CLUBS values ('Bayern Munich', 'Germany', 'Munich', 'Olympiastadion', 69000, 1971, '105x68m', '');
insert into CLUBS values ('Benfica', 'Portugal', 'Benfica', 'Estadio Da Luz', 80000, 1954, '', '');
insert into CLUBS values ('Boca Juniors', 'Argentina', 'Buenos Aires', 'La Bombonera', 60245, 1940, '', '');
insert into CLUBS values ('Chelsea', 'England', 'London', 'Stamford Bridge', 42500, 1877, '103x68m', 'The Blues');
insert into CLUBS values ('Cruz Azul', 'Mexico', 'Mexico City', 'Estadio Azteca', 114600, 1966, '', '');
insert into CLUBS values ('Flamenco', 'Brazil', 'Rio de Janeiro', 'Maracana', 70000, 1950, '110x75m', '');
insert into CLUBS values ('Inter Milan', 'Italy', 'Milan', 'San Siro', 85700, 1926, '105x68m', '');
insert into CLUBS values ('Juventus', 'Italy', 'Turin', 'Stadio Delle Alpi', 69041, 1990, '105x68m', '');
insert into CLUBS values ('Lazio', 'Italy', 'Rome', 'Stadio Olimpico', 82307, 1953, '105x67m', '');
insert into CLUBS values ('Manchester United', 'England', 'Manchester', 'Old Trafford', 67603, 1910, '106x69m', 'Red Devils');
insert into CLUBS values ('Paris Saint-Germain', 'France', 'Paris', 'Parc Des Princes', 48500, 1972, '', '');
insert into CLUBS values ('Real Madrid', 'Spain', 'Madrid', 'Santiago Bernabeu', 90000, 1947, '105x68m', '');
insert into CLUBS values ('River Plate', 'Argentina', 'Buenos Aires', 'El Monumental', 77000, 1942, '105x70m', 'Los Millionairos');
