INSERT INTO PREFS_NODE VALUES(1,NULL,'',0,'/',to_date('2004-05-22 14:57:53','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 14:57:53','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_NODE VALUES(101,1,'user',0,'/user',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_NODE VALUES(102,101,'admin',0,'/user/admin',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_NODE VALUES(103,102,'userinfo',0,'/user/admin/userinfo',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(1,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,'/user/admin',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_KEY VALUES(61,'user.name.given',3,to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_VALUE VALUES(41,103,61,0,NULL,0,0.0E0,'Test Dude',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_KEY VALUES(62,'user.name.family',3,to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_VALUE VALUES(42,103,62,0,NULL,0,0.0E0,'Dudley',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(2,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,'/user/manager',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(3,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,'/user/user',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(4,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,'/user/tomcat',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(5,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,'/user/jetspeed',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(6,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,'/role/admin',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(7,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,'/role/manager',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(8,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,'/role/user',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(9,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl',0,'/user/guest',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(10,'org.apache.jetspeed.security.JetspeedRolePrincipalImpl',0,'/role/guest',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(11,'org.apache.jetspeed.security.JetspeedGroupPrincipalImpl',0,'/group/accounting',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(12,'org.apache.jetspeed.security.JetspeedGroupPrincipalImpl',0,'/group/marketing',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(13,'org.apache.jetspeed.security.JetspeedGroupPrincipalImpl',0,'/group/engineering',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_CREDENTIAL VALUES(1,1,'admin',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_CREDENTIAL VALUES(2,2,'manager',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_CREDENTIAL VALUES(3,3,'user',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_CREDENTIAL VALUES(4,4,'tomcat',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_CREDENTIAL VALUES(5,5,'jetspeed',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_CREDENTIAL VALUES(9,9,'guest',0,'org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_USER_ROLE VALUES(1,6);
INSERT INTO SECURITY_USER_ROLE VALUES(1,7);
INSERT INTO SECURITY_USER_ROLE VALUES(1,8);
INSERT INTO SECURITY_USER_ROLE VALUES(2,7);
INSERT INTO SECURITY_USER_ROLE VALUES(2,8);
INSERT INTO SECURITY_USER_ROLE VALUES(3,8);
INSERT INTO SECURITY_USER_ROLE VALUES(9,10);
INSERT INTO SECURITY_USER_ROLE VALUES(5,7);
