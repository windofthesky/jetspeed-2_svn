INSERT INTO PREFS_NODE VALUES(1,NULL,'',0,'/',to_date('2004-05-22 14:57:53','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 14:57:53','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_NODE VALUES(101,1,'user',0,'/user',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_NODE VALUES(102,101,'admin',0,'/user/admin',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_NODE VALUES(103,102,'userinfo',0,'/user/admin/userinfo',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(1,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/admin',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_KEY VALUES(61,'user.name.given',3,to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_VALUE VALUES(41,103,61,0,NULL,0,0.0E0,'Test Dude',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_KEY VALUES(62,'user.name.family',3,to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_VALUE VALUES(42,103,62,0,NULL,0,0.0E0,'Dudley',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(2,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/manager',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(3,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/user',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(4,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/tomcat',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(5,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/jetspeed',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(6,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/admin',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(7,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/manager',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(8,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/user',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(9,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/guest',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(10,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/guest',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(11,'org.apache.jetspeed.security.JetspeedGroupPrincipalImpl',0,1,'/group/accounting',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(12,'org.apache.jetspeed.security.JetspeedGroupPrincipalImpl',0,1,'/group/marketing',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(13,'org.apache.jetspeed.security.JetspeedGroupPrincipalImpl',0,1,'/group/engineering',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_CREDENTIAL VALUES(1,1,'admin',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(2,2,'manager',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(3,3,'user',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(4,4,'tomcat',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(5,5,'jetspeed',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),null,null,null);
INSERT INTO SECURITY_USER_ROLE VALUES(1,6);
INSERT INTO SECURITY_USER_ROLE VALUES(1,7);
INSERT INTO SECURITY_USER_ROLE VALUES(1,8);
INSERT INTO SECURITY_USER_ROLE VALUES(2,7);
INSERT INTO SECURITY_USER_ROLE VALUES(2,8);
INSERT INTO SECURITY_USER_ROLE VALUES(3,8);
INSERT INTO SECURITY_USER_ROLE VALUES(9,10);
INSERT INTO SECURITY_USER_ROLE VALUES(5,7);
INSERT INTO SECURITY_USER_GROUP VALUES(3,11);
INSERT INTO SECURITY_USER_GROUP VALUES(5,13);
INSERT INTO SECURITY_PERMISSION VALUES(1,'org.apache.jetspeed.security.FolderPermission','<<ALL FILES>>','view, edit',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(2,'org.apache.jetspeed.security.FolderPermission','<<ALL FILES>>','view',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(3,'org.apache.jetspeed.security.FolderPermission','/','view',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(4,'org.apache.jetspeed.security.FolderPermission','/*','view',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(5,'org.apache.jetspeed.security.FolderPermission','/non-java/-','view',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(6,'org.apache.jetspeed.security.FolderPermission','/third-party/-','view',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(7,'org.apache.jetspeed.security.FolderPermission','/Public','view, edit',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(8,'org.apache.jetspeed.security.FolderPermission','/Public/-','view, edit',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(9,'org.apache.jetspeed.security.FolderPermission','/anotherdir/-','view',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(10,'org.apache.jetspeed.security.FolderPermission','/top-links/-','view',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(11,'org.apache.jetspeed.security.PagePermission','/default-page.psml','view',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(12,'org.apache.jetspeed.security.PagePermission','/rss.psml','view',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(13,'org.apache.jetspeed.security.FolderPermission','/_user/user','view, edit',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(14,'org.apache.jetspeed.security.FolderPermission','/_user/user/-','view, edit',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PRINCIPAL_PERMISSION VALUES(3,13);
INSERT INTO PRINCIPAL_PERMISSION VALUES(3,14);
INSERT INTO PRINCIPAL_PERMISSION VALUES(6,1);
INSERT INTO PRINCIPAL_PERMISSION VALUES(7,2);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,3);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,4);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,5);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,6);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,8);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,9);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,10);
INSERT INTO PRINCIPAL_PERMISSION VALUES(10,3);
INSERT INTO PRINCIPAL_PERMISSION VALUES(10,7);
INSERT INTO PRINCIPAL_PERMISSION VALUES(10,8);
INSERT INTO PRINCIPAL_PERMISSION VALUES(10,11);
INSERT INTO PRINCIPAL_PERMISSION VALUES(10,12);


INSERT INTO SECURITY_PRINCIPAL VALUES(50,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/subsite',to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'),to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(51,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/subsite2',to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'),to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_CREDENTIAL VALUES(50,50,'subsite',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'),to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'),null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(51,51,'subsite2',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'),to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'),null,null,null);

INSERT INTO SECURITY_PRINCIPAL VALUES(52,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/subsite',to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'),to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(53,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/subsite2',to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'),to_date('2005-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'));

INSERT INTO SECURITY_USER_ROLE VALUES(50,52);
INSERT INTO SECURITY_USER_ROLE VALUES(50,53);
INSERT INTO SECURITY_USER_ROLE VALUES(51,52);
INSERT INTO SECURITY_USER_ROLE VALUES(51,53);

insert into PROFILING_RULE values ('subsite-role-fallback-home',
     'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule',
     'A rule based on role fallback algorithm with specified subsite and home page');
insert into RULE_CRITERION values (50, 'subsite-role-fallback-home', 0, 'navigation', 'navigation', 'subsite-root', 2);
insert into RULE_CRITERION values (51, 'subsite-role-fallback-home', 1, 'role', 'role', null, 2);
insert into RULE_CRITERION values (52, 'subsite-role-fallback-home', 2, 'path', 'path', 'subsite-default-page', 0);

insert into PROFILING_RULE values ('subsite2-role-fallback-home', 
     'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule',
     'A rule based on role fallback algorithm with specified subsite and home page');
insert into RULE_CRITERION values (53, 'subsite2-role-fallback-home', 0, 'navigation', 'navigation', 'subsite-root', 2);
insert into RULE_CRITERION values (54, 'subsite2-role-fallback-home', 1, 'role', 'role', null, 2);
insert into RULE_CRITERION values (55, 'subsite2-role-fallback-home', 2, 'path', 'path', 'subsite2-default-page', 0);

insert into PRINCIPAL_RULE_ASSOC values ('subsite', 'page', 'subsite-role-fallback-home');
insert into PRINCIPAL_RULE_ASSOC values ('subsite2', 'page', 'subsite2-role-fallback-home');

INSERT INTO SECURITY_PERMISSION VALUES(50,'org.apache.jetspeed.security.FolderPermission','/__subsite-root','view',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(51,'org.apache.jetspeed.security.FolderPermission','/__subsite-root/-','view',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(52,'org.apache.jetspeed.security.FolderPermission','/__subsite-root/_role/subsite','view, edit',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(53,'org.apache.jetspeed.security.FolderPermission','/__subsite-root/_role/subsite/-','view, edit',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(54,'org.apache.jetspeed.security.FolderPermission','/__subsite-root/_role/subsite2','view, edit',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PERMISSION VALUES(55,'org.apache.jetspeed.security.FolderPermission','/__subsite-root/_role/subsite2/-','view, edit',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PRINCIPAL_PERMISSION VALUES(50,52);
INSERT INTO PRINCIPAL_PERMISSION VALUES(50,53);
INSERT INTO PRINCIPAL_PERMISSION VALUES(51,54);
INSERT INTO PRINCIPAL_PERMISSION VALUES(51,55);
INSERT INTO PRINCIPAL_PERMISSION VALUES(52,3);
INSERT INTO PRINCIPAL_PERMISSION VALUES(52,50);
INSERT INTO PRINCIPAL_PERMISSION VALUES(52,51);
INSERT INTO PRINCIPAL_PERMISSION VALUES(53,3);
INSERT INTO PRINCIPAL_PERMISSION VALUES(53,50);
INSERT INTO PRINCIPAL_PERMISSION VALUES(53,51);
