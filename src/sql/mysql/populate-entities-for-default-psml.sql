INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 1, PD.ID, '1' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'TwoColumns';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 2, PD.ID, '2' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'VelocityTwoColumns';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 3, PD.ID, '3' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'HelloPortlet';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 4, PD.ID, '4' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'JMXPortlet';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 5, PD.ID, '5' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'PreferencePortlet';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 6, PD.ID, '6' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'DisplayRequestPortlet';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 7, PD.ID, '7' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'PickANumberPortlet';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 8, PD.ID, '8' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'AttributeScopePortlet';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 9, PD.ID, '9' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'CustomerList';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 10, PD.ID, '10' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'CustomerInfo';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 11, PD.ID, '11' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'CustomerDetail';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 12, PD.ID, '12' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'LoginPortlet';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 13, PD.ID, '13' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'PortletApplicationBrowser';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 14, PD.ID, '14' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'PortletApplicationDetail';
INSERT INTO PORTLET_ENTITY (OID, PORTLET_DEFINITION_ID, ID)
  SELECT 15, PD.ID, '15' FROM PORTLET_DEFINITION PD WHERE PD.NAME = 'StrutsPortletDemo';

