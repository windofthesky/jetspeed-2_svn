INSERT INTO PREFS_NODE VALUES(1,NULL,'',0,'/','2004-05-22 14:57:53.586','2004-05-22 14:57:53.586');
INSERT INTO PREFS_NODE VALUES(101,1,'user',0,'/user','2004-05-22 16:27:12.472','2004-05-22 16:27:12.472');
INSERT INTO PREFS_NODE VALUES(102,101,'admin',0,'/user/admin','2004-05-22 16:27:12.482','2004-05-22 16:27:12.482');
INSERT INTO PREFS_NODE VALUES(103,102,'userinfo',0,'/user/admin/userinfo','2004-05-22 16:27:12.522','2004-05-22 16:27:12.532');
INSERT INTO SECURITY_PRINCIPAL VALUES(1,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/admin','2004-05-22 16:27:12.442','2004-05-22 16:27:12.442');
INSERT INTO PREFS_PROPERTY_VALUE VALUES(41,103,'user.name.given','Test Dude','2004-05-22 16:27:12.562','2004-05-22 16:27:12.562');
INSERT INTO PREFS_PROPERTY_VALUE VALUES(42,103,'user.name.family','Dudley','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(2,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/manager','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(3,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/user','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(4,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/tomcat','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(5,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/jetspeed','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(6,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/admin','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(7,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/manager','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(8,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/user','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(9,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/guest','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(10,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/guest','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(11,'org.apache.jetspeed.security.JetspeedGroupPrincipalImpl',0,1,'/group/accounting','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(12,'org.apache.jetspeed.security.JetspeedGroupPrincipalImpl',0,1,'/group/marketing','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PRINCIPAL VALUES(13,'org.apache.jetspeed.security.JetspeedGroupPrincipalImpl',0,1,'/group/engineering','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_CREDENTIAL VALUES(1,1,'admin',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',1,0,1,0,0,'2004-05-22 16:27:12.572','2004-05-22 16:27:12.572',null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(2,2,'manager',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2004-05-22 16:27:12.572','2004-05-22 16:27:12.572',null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(3,3,'user',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2004-05-22 16:27:12.572','2004-05-22 16:27:12.572',null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(4,4,'tomcat',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2004-05-22 16:27:12.572','2004-05-22 16:27:12.572',null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(5,5,'jetspeed',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2004-05-22 16:27:12.572','2004-05-22 16:27:12.572',null,null,null);
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
INSERT INTO SECURITY_PERMISSION VALUES(1,'org.apache.jetspeed.security.FolderPermission','<<ALL FILES>>','view, edit','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(2,'org.apache.jetspeed.security.FolderPermission','<<ALL FILES>>','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(3,'org.apache.jetspeed.security.FolderPermission','/','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(4,'org.apache.jetspeed.security.FolderPermission','/*','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(5,'org.apache.jetspeed.security.FolderPermission','/non-java/-','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(6,'org.apache.jetspeed.security.FolderPermission','/third-party/-','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(7,'org.apache.jetspeed.security.FolderPermission','/Public','view, edit','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(8,'org.apache.jetspeed.security.FolderPermission','/Public/-','view, edit','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(9,'org.apache.jetspeed.security.FolderPermission','/anotherdir/-','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(10,'org.apache.jetspeed.security.FolderPermission','/top-links/-','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(11,'org.apache.jetspeed.security.PagePermission','/default-page.psml','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(12,'org.apache.jetspeed.security.PagePermission','/rss.psml','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(13,'org.apache.jetspeed.security.FolderPermission','/_user/user','view, edit','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(14,'org.apache.jetspeed.security.FolderPermission','/_user/user/-','view, edit','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
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


INSERT INTO SECURITY_PRINCIPAL VALUES(50,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/subsite','2005-01-01 00:00:00.000','2005-01-01 00:00:00.000');
INSERT INTO SECURITY_PRINCIPAL VALUES(51,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/subsite2','2005-01-01 00:00:00.000','2005-01-01 00:00:00.000');
INSERT INTO SECURITY_CREDENTIAL VALUES(50,50,'subsite',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2005-01-01 00:00:00.000','2005-01-01 00:00:00.000',null,null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(51,51,'subsite2',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2005-01-01 00:00:00.000','2005-01-01 00:00:00.000',null,null,null);

INSERT INTO SECURITY_PRINCIPAL VALUES(52,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/subsite','2005-01-01 00:00:00.000','2005-01-01 00:00:00.000');
INSERT INTO SECURITY_PRINCIPAL VALUES(53,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,1,'/role/subsite2','2005-01-01 00:00:00.000','2005-01-01 00:00:00.000');

INSERT INTO SECURITY_USER_ROLE VALUES(50,52);
INSERT INTO SECURITY_USER_ROLE VALUES(50,53);
INSERT INTO SECURITY_USER_ROLE VALUES(51,52);
INSERT INTO SECURITY_USER_ROLE VALUES(51,53);

INSERT INTO SECURITY_PERMISSION VALUES(50,'org.apache.jetspeed.security.FolderPermission','/__subsite-root','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(51,'org.apache.jetspeed.security.FolderPermission','/__subsite-root/-','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(52,'org.apache.jetspeed.security.FolderPermission','/__subsite-root/_role/subsite','view, edit','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(53,'org.apache.jetspeed.security.FolderPermission','/__subsite-root/_role/subsite/-','view, edit','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(54,'org.apache.jetspeed.security.FolderPermission','/__subsite-root/_role/subsite2','view, edit','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(55,'org.apache.jetspeed.security.FolderPermission','/__subsite-root/_role/subsite2/-','view, edit','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
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

INSERT INTO SECURITY_PERMISSION VALUES(100,'org.apache.jetspeed.security.PortletPermission','j2-admin::*','view, edit','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO PRINCIPAL_PERMISSION VALUES(6,100);
