INSERT INTO PREFS_NODE VALUES(1,NULL,'',0,'/',to_date('2004-05-22 14:57:53','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 14:57:53','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_NODE VALUES(101,1,'user',0,'/user',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_NODE VALUES(102,101,'admin',0,'/user/admin',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_NODE VALUES(103,102,'userinfo',0,'/user/admin/userinfo',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO SECURITY_PRINCIPAL VALUES(1,'org.apache.jetspeed.security.JetspeedUserPrincipalImpl','/user/admin',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_KEY VALUES(61,'user.name.given',3,to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_VALUE VALUES(41,103,61,0,NULL,0,0.0E0,'Test Dude',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_KEY VALUES(62,'user.name.family',3,to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO PREFS_PROPERTY_VALUE VALUES(42,103,62,0,NULL,0,0.0E0,'Dudley',to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'),to_date('2004-05-22 16:27:12','YYYY-MM-DD HH24:MI:SS'));
