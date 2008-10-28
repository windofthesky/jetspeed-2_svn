
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='TR' AND name='trig_SECURITY_PRINCIPAL')
    DROP TRIGGER trig_SECURITY_PRINCIPAL
/


CREATE TRIGGER trig_SECURITY_PRINCIPAL
  ON SECURITY_PRINCIPAL
  INSTEAD OF DELETE 
  AS 
  
  SET NOCOUNT ON;

  DELETE FROM SSO_PRINCIPAL_TO_REMOTE
    WHERE REMOTE_PRINCIPAL_ID IN (SELECT PRINCIPAL_ID FROM DELETED)
;

  DELETE FROM SECURITY_USER_ROLE
    WHERE USER_ID IN (SELECT PRINCIPAL_ID FROM DELETED)
;

  DELETE FROM SECURITY_USER_GROUP
    WHERE USER_ID IN (SELECT PRINCIPAL_ID FROM DELETED)
;

  DELETE FROM SECURITY_GROUP_ROLE
    WHERE ROLE_ID IN (SELECT PRINCIPAL_ID FROM DELETED)
;

  DELETE FROM SECURITY_PRINCIPAL
    WHERE PRINCIPAL_ID IN (SELECT PRINCIPAL_ID FROM DELETED)
/