INSERT INTO PREFS_NODE VALUES(1,NULL,'',0,'/','2004-05-22 14:57:53.586','2004-05-22 14:57:53.586');
INSERT INTO PREFS_NODE VALUES(101,1,'user',0,'/user','2004-05-22 16:27:12.472','2004-05-22 16:27:12.472');
INSERT INTO PREFS_NODE VALUES(102,101,'admin',0,'/user/admin','2004-05-22 16:27:12.482','2004-05-22 16:27:12.482');
INSERT INTO PREFS_NODE VALUES(103,102,'userinfo',0,'/user/admin/userinfo','2004-05-22 16:27:12.522','2004-05-22 16:27:12.532');
INSERT INTO SECURITY_PRINCIPAL VALUES(1,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,1,'/user/admin','2004-05-22 16:27:12.442','2004-05-22 16:27:12.442');
INSERT INTO PREFS_PROPERTY_KEY VALUES(61,'user.name.given',3,'2004-05-22 16:27:12.532','2004-05-22 16:27:12.532');
INSERT INTO PREFS_PROPERTY_VALUE VALUES(41,103,61,0,NULL,0,0.0E0,'Test Dude','2004-05-22 16:27:12.562','2004-05-22 16:27:12.562');
INSERT INTO PREFS_PROPERTY_KEY VALUES(62,'user.name.family',3,'2004-05-22 16:27:12.532','2004-05-22 16:27:12.532');
INSERT INTO PREFS_PROPERTY_VALUE VALUES(42,103,62,0,NULL,0,0.0E0,'Dudley','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
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
INSERT INTO SECURITY_CREDENTIAL VALUES(1,1,'admin',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2004-05-22 16:27:12.572','2004-05-22 16:27:12.572',null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(2,2,'manager',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2004-05-22 16:27:12.572','2004-05-22 16:27:12.572',null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(3,3,'user',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2004-05-22 16:27:12.572','2004-05-22 16:27:12.572',null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(4,4,'tomcat',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2004-05-22 16:27:12.572','2004-05-22 16:27:12.572',null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(5,5,'jetspeed',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2004-05-22 16:27:12.572','2004-05-22 16:27:12.572',null,null);
INSERT INTO SECURITY_CREDENTIAL VALUES(9,9,'guest',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',0,0,1,0,0,'2004-05-22 16:27:12.572','2004-05-22 16:27:12.572',null,null);
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
INSERT INTO SECURITY_PERMISSION VALUES(3,'org.apache.jetspeed.security.FolderPermission','/*','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(4,'org.apache.jetspeed.security.FolderPermission','/non-java/-','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(5,'org.apache.jetspeed.security.FolderPermission','/third-party/-','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(6,'org.apache.jetspeed.security.FolderPermission','/anotherdir/-','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(7,'org.apache.jetspeed.security.FolderPermission','/top-links/-','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(8,'org.apache.jetspeed.security.PagePermission','/default-page.psml','view','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO SECURITY_PERMISSION VALUES(9,'org.apache.jetspeed.security.FolderPermission','/_user/user/-','view, edit','2004-05-22 16:27:12.572','2004-05-22 16:27:12.572');
INSERT INTO PRINCIPAL_PERMISSION VALUES(3,9);
INSERT INTO PRINCIPAL_PERMISSION VALUES(6,1);
INSERT INTO PRINCIPAL_PERMISSION VALUES(7,2);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,3);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,4);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,5);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,6);
INSERT INTO PRINCIPAL_PERMISSION VALUES(8,7);
INSERT INTO PRINCIPAL_PERMISSION VALUES(10,8);
