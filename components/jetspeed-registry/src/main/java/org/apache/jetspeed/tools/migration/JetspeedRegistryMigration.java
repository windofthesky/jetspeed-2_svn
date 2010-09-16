/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.tools.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.ojb.LocaleFieldConversion;

/**
 * Jetspeed Migration for Registry component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedRegistryMigration implements JetspeedMigration
{
    private static final String DEFAULT_LOCALE_STRING = (String)(new LocaleFieldConversion()).javaToSql(JetspeedLocale.getDefaultLocale());
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#getName()
     */
    public String getName()
    {
        return "Registry";
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#detectSourceVersion(java.sql.Connection, int)
     */
    public int detectSourceVersion(Connection sourceConnection, int sourceVersion) throws SQLException
    {
        // detect version of registry schema
        int sourceRegistryVersion = JETSPEED_SCHEMA_VERSION_2_1_3;
        try
        {
            Statement portletSupportsQueryStatement = sourceConnection.createStatement();
            portletSupportsQueryStatement.executeQuery("SELECT SUPPORTS_ID FROM PORTLET_SUPPORTS WHERE SUPPORTS_ID = 0");
            sourceRegistryVersion = JETSPEED_SCHEMA_VERSION_2_2_0;
        }
        catch (SQLException sqle)
        {
        }
        return ((sourceRegistryVersion >= sourceVersion) ? sourceRegistryVersion : sourceVersion);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#migrate(java.sql.Connection, int, java.sql.Connection, org.apache.jetspeed.tools.migration.JetspeedMigrationListener)
     */
    public JetspeedMigrationResult migrate(Connection sourceConnection, int sourceVersion, Connection targetConnection, JetspeedMigrationListener migrationListener) throws SQLException
    {
        List<LocalizedDescription> localizedDescriptions = new ArrayList<LocalizedDescription>();
        int rowsMigrated = 0;
        int rowsDropped = 0;
        
        // PORTLET_DEFINITION
        PreparedStatement portletDefinitionInsertStatement = targetConnection.prepareStatement("INSERT INTO PORTLET_DEFINITION (ID, NAME, CLASS_NAME, APPLICATION_ID, EXPIRATION_CACHE, RESOURCE_BUNDLE, PREFERENCE_VALIDATOR, SECURITY_REF, CACHE_SCOPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement portletDefinitionQueryStatement = sourceConnection.createStatement();
        portletDefinitionQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet portletDefinitionResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                portletDefinitionResultSet = portletDefinitionQueryStatement.executeQuery("SELECT ID, NAME, CLASS_NAME, APPLICATION_ID, EXPIRATION_CACHE, RESOURCE_BUNDLE, PREFERENCE_VALIDATOR, SECURITY_REF FROM PORTLET_DEFINITION");
                while (portletDefinitionResultSet.next())
                {
                    portletDefinitionInsertStatement.setInt(1, portletDefinitionResultSet.getInt(1));
                    portletDefinitionInsertStatement.setString(2, portletDefinitionResultSet.getString(2));
                    portletDefinitionInsertStatement.setString(3, portletDefinitionResultSet.getString(3));
                    portletDefinitionInsertStatement.setInt(4, portletDefinitionResultSet.getInt(4));
                    String expirationCache = portletDefinitionResultSet.getString(5);
                    if (expirationCache != null)
                    {
                        portletDefinitionInsertStatement.setInt(5, Integer.parseInt(expirationCache));
                    }
                    else
                    {
                        portletDefinitionInsertStatement.setNull(5, Types.INTEGER);
                    }
                    portletDefinitionInsertStatement.setString(6, portletDefinitionResultSet.getString(6));
                    portletDefinitionInsertStatement.setString(7, portletDefinitionResultSet.getString(7));
                    portletDefinitionInsertStatement.setString(8, portletDefinitionResultSet.getString(8));
                    portletDefinitionInsertStatement.setNull(9, Types.VARCHAR);
                    portletDefinitionInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                portletDefinitionResultSet = portletDefinitionQueryStatement.executeQuery("SELECT ID, NAME, CLASS_NAME, APPLICATION_ID, EXPIRATION_CACHE, RESOURCE_BUNDLE, PREFERENCE_VALIDATOR, SECURITY_REF, CACHE_SCOPE FROM PORTLET_DEFINITION");
                while (portletDefinitionResultSet.next())
                {
                    portletDefinitionInsertStatement.setInt(1, portletDefinitionResultSet.getInt(1));
                    portletDefinitionInsertStatement.setString(2, portletDefinitionResultSet.getString(2));
                    portletDefinitionInsertStatement.setString(3, portletDefinitionResultSet.getString(3));
                    portletDefinitionInsertStatement.setInt(4, portletDefinitionResultSet.getInt(4));
                    Static.setNullableInt(portletDefinitionResultSet, 5, portletDefinitionInsertStatement);
                    portletDefinitionInsertStatement.setString(6, portletDefinitionResultSet.getString(6));
                    portletDefinitionInsertStatement.setString(7, portletDefinitionResultSet.getString(7));
                    portletDefinitionInsertStatement.setString(8, portletDefinitionResultSet.getString(8));
                    portletDefinitionInsertStatement.setString(9, portletDefinitionResultSet.getString(9));
                    portletDefinitionInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        portletDefinitionResultSet.close();
        portletDefinitionQueryStatement.close();
        portletDefinitionInsertStatement.close();
        
        // PORTLET_APPLICATION
        PreparedStatement portletApplicationInsertStatement = targetConnection.prepareStatement("INSERT INTO PORTLET_APPLICATION (APPLICATION_ID, APP_NAME, CONTEXT_PATH, REVISION, VERSION, APP_TYPE, CHECKSUM, SECURITY_REF, DEFAULT_NAMESPACE, RESOURCE_BUNDLE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement portletApplicationQueryStatement = sourceConnection.createStatement();
        portletApplicationQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet portletApplicationResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                portletApplicationResultSet = portletApplicationQueryStatement.executeQuery("SELECT APPLICATION_ID, APP_NAME, VERSION, APP_TYPE, CHECKSUM, SECURITY_REF, DESCRIPTION, WEB_APP_ID FROM PORTLET_APPLICATION");
                PreparedStatement webApplicationQueryStatement = sourceConnection.prepareStatement("SELECT CONTEXT_ROOT FROM WEB_APPLICATION WHERE ID = ?");
                while (portletApplicationResultSet.next())
                {
                    String contextPath = null;
                    int webApplicationId = portletApplicationResultSet.getInt(8);
                    webApplicationQueryStatement.setInt(1, webApplicationId);
                    ResultSet webApplicationResultSet = webApplicationQueryStatement.executeQuery();
                    if (webApplicationResultSet.next())
                    {
                        contextPath = webApplicationResultSet.getString(1);
                    }
                    else
                    {
                        throw new SQLException("Unable to find web application for id: "+webApplicationId);
                    }
                    webApplicationResultSet.close();
                
                    portletApplicationInsertStatement.setInt(1, portletApplicationResultSet.getInt(1));
                    portletApplicationInsertStatement.setString(2, portletApplicationResultSet.getString(2));
                    portletApplicationInsertStatement.setString(3, contextPath);
                    portletApplicationInsertStatement.setInt(4, 0);
                    portletApplicationInsertStatement.setString(5, portletApplicationResultSet.getString(3));
                    Static.setNullableInt(portletApplicationResultSet, 4, portletApplicationInsertStatement, 6);
                    portletApplicationInsertStatement.setString(7, portletApplicationResultSet.getString(5));
                    portletApplicationInsertStatement.setString(8, portletApplicationResultSet.getString(6));
                    portletApplicationInsertStatement.setNull(9, Types.VARCHAR);
                    portletApplicationInsertStatement.setNull(10, Types.VARCHAR);
                    portletApplicationInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                    
                    int applicationId = portletApplicationResultSet.getInt(1);
                    String description = portletApplicationResultSet.getString(7);
                    if (description != null)
                    {
                        localizedDescriptions.add(new LocalizedDescription(applicationId, "org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl", description));
                    }
                }
                webApplicationQueryStatement.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                portletApplicationResultSet = portletApplicationQueryStatement.executeQuery("SELECT APPLICATION_ID, APP_NAME, CONTEXT_PATH, REVISION, VERSION, APP_TYPE, CHECKSUM, SECURITY_REF, DEFAULT_NAMESPACE, RESOURCE_BUNDLE FROM PORTLET_APPLICATION");
                while (portletApplicationResultSet.next())
                {
                    portletApplicationInsertStatement.setInt(1, portletApplicationResultSet.getInt(1));
                    portletApplicationInsertStatement.setString(2, portletApplicationResultSet.getString(2));
                    portletApplicationInsertStatement.setString(3, portletApplicationResultSet.getString(3));
                    portletApplicationInsertStatement.setInt(4, portletApplicationResultSet.getInt(4));
                    portletApplicationInsertStatement.setString(5, portletApplicationResultSet.getString(5));
                    Static.setNullableInt(portletApplicationResultSet, 6, portletApplicationInsertStatement);
                    portletApplicationInsertStatement.setString(7, portletApplicationResultSet.getString(7));
                    portletApplicationInsertStatement.setString(8, portletApplicationResultSet.getString(8));
                    portletApplicationInsertStatement.setString(9, portletApplicationResultSet.getString(9));
                    portletApplicationInsertStatement.setString(10, portletApplicationResultSet.getString(10));
                    portletApplicationInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        portletApplicationResultSet.close();
        portletApplicationQueryStatement.close();
        portletApplicationInsertStatement.close();

        // PA_METADATA_FIELDS
        PreparedStatement portletApplicationMetadataInsertStatement = targetConnection.prepareStatement("INSERT INTO PA_METADATA_FIELDS (ID, OBJECT_ID, COLUMN_VALUE, NAME, LOCALE_STRING) VALUES (?, ?, ?, ?, ?)");
        Statement portletApplicationMetadataQueryStatement = sourceConnection.createStatement();
        portletApplicationMetadataQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet portletApplicationMetadataResultSet = portletApplicationMetadataQueryStatement.executeQuery("SELECT ID, OBJECT_ID, COLUMN_VALUE, NAME, LOCALE_STRING FROM PA_METADATA_FIELDS");
        while (portletApplicationMetadataResultSet.next())
        {
            portletApplicationMetadataInsertStatement.setInt(1, portletApplicationMetadataResultSet.getInt(1));
            portletApplicationMetadataInsertStatement.setInt(2, portletApplicationMetadataResultSet.getInt(2));
            portletApplicationMetadataInsertStatement.setString(3, portletApplicationMetadataResultSet.getString(3));
            portletApplicationMetadataInsertStatement.setString(4, portletApplicationMetadataResultSet.getString(4));
            portletApplicationMetadataInsertStatement.setString(5, portletApplicationMetadataResultSet.getString(5));
            portletApplicationMetadataInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        portletApplicationMetadataResultSet.close();
        portletApplicationMetadataQueryStatement.close();
        portletApplicationMetadataInsertStatement.close();

        // PD_METADATA_FIELDS
        PreparedStatement portletDefinitionMetadataInsertStatement = targetConnection.prepareStatement("INSERT INTO PD_METADATA_FIELDS (ID, OBJECT_ID, COLUMN_VALUE, NAME, LOCALE_STRING) VALUES (?, ?, ?, ?, ?)");
        Statement portletDefinitionMetadataQueryStatement = sourceConnection.createStatement();
        portletDefinitionMetadataQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet portletDefinitionMetadataResultSet = portletDefinitionMetadataQueryStatement.executeQuery("SELECT ID, OBJECT_ID, COLUMN_VALUE, NAME, LOCALE_STRING FROM PD_METADATA_FIELDS");
        while (portletDefinitionMetadataResultSet.next())
        {
            portletDefinitionMetadataInsertStatement.setInt(1, portletDefinitionMetadataResultSet.getInt(1));
            portletDefinitionMetadataInsertStatement.setInt(2, portletDefinitionMetadataResultSet.getInt(2));
            portletDefinitionMetadataInsertStatement.setString(3, portletDefinitionMetadataResultSet.getString(3));
            portletDefinitionMetadataInsertStatement.setString(4, portletDefinitionMetadataResultSet.getString(4));
            portletDefinitionMetadataInsertStatement.setString(5, portletDefinitionMetadataResultSet.getString(5));
            portletDefinitionMetadataInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        portletDefinitionMetadataResultSet.close();
        portletDefinitionMetadataQueryStatement.close();
        portletDefinitionMetadataInsertStatement.close();

        // LANGUAGE
        PreparedStatement languageInsertStatement = targetConnection.prepareStatement("INSERT INTO LANGUAGE (ID, PORTLET_ID, LOCALE_STRING, SUPPORTED_LOCALE, TITLE, SHORT_TITLE, KEYWORDS) VALUES (?, ?, ?, ?, ?, ?, ?)");
        Statement languageQueryStatement = sourceConnection.createStatement();
        languageQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet languageResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                languageResultSet = languageQueryStatement.executeQuery("SELECT ID, PORTLET_ID, LOCALE_STRING, TITLE, SHORT_TITLE, KEYWORDS FROM LANGUAGE");
                while (languageResultSet.next())
                {
                    languageInsertStatement.setInt(1, languageResultSet.getInt(1));
                    languageInsertStatement.setInt(2, languageResultSet.getInt(2));
                    languageInsertStatement.setString(3, languageResultSet.getString(3));
                    languageInsertStatement.setShort(4, (short)1);
                    languageInsertStatement.setString(5, languageResultSet.getString(4));
                    languageInsertStatement.setString(6, languageResultSet.getString(5));
                    languageInsertStatement.setString(7, languageResultSet.getString(6));
                    languageInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                languageResultSet = languageQueryStatement.executeQuery("SELECT ID, PORTLET_ID, LOCALE_STRING, SUPPORTED_LOCALE, TITLE, SHORT_TITLE, KEYWORDS FROM LANGUAGE");
                while (languageResultSet.next())
                {
                    languageInsertStatement.setInt(1, languageResultSet.getInt(1));
                    languageInsertStatement.setInt(2, languageResultSet.getInt(2));
                    languageInsertStatement.setString(3, languageResultSet.getString(3));
                    languageInsertStatement.setShort(4, languageResultSet.getShort(4));
                    languageInsertStatement.setString(5, languageResultSet.getString(5));
                    languageInsertStatement.setString(6, languageResultSet.getString(6));
                    languageInsertStatement.setString(7, languageResultSet.getString(7));
                    languageInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        languageResultSet.close();
        languageQueryStatement.close();
        languageInsertStatement.close();

        // PORTLET_SUPPORTS
        PreparedStatement portletSupportsInsertStatement = targetConnection.prepareStatement("INSERT INTO PORTLET_SUPPORTS (SUPPORTS_ID, PORTLET_ID, MIME_TYPE, MODES, STATES) VALUES (?, ?, ?, ?, ?)");
        Statement portletSupportsQueryStatement = sourceConnection.createStatement();
        portletSupportsQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet portletSupportsResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                portletSupportsResultSet = portletSupportsQueryStatement.executeQuery("SELECT CONTENT_TYPE_ID, PORTLET_ID, CONTENT_TYPE, MODES FROM PORTLET_CONTENT_TYPE");
                while (portletSupportsResultSet.next())
                {
                    portletSupportsInsertStatement.setInt(1, portletSupportsResultSet.getInt(1));
                    portletSupportsInsertStatement.setInt(2, portletSupportsResultSet.getInt(2));
                    portletSupportsInsertStatement.setString(3, portletSupportsResultSet.getString(3));
                    portletSupportsInsertStatement.setString(4, portletSupportsResultSet.getString(4));
                    portletSupportsInsertStatement.setNull(5, Types.VARCHAR);
                    portletSupportsInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                portletSupportsResultSet = portletSupportsQueryStatement.executeQuery("SELECT SUPPORTS_ID, PORTLET_ID, MIME_TYPE, MODES, STATES FROM PORTLET_SUPPORTS");
                while (portletSupportsResultSet.next())
                {
                    portletSupportsInsertStatement.setInt(1, portletSupportsResultSet.getInt(1));
                    portletSupportsInsertStatement.setInt(2, portletSupportsResultSet.getInt(2));
                    portletSupportsInsertStatement.setString(3, portletSupportsResultSet.getString(3));
                    portletSupportsInsertStatement.setString(4, portletSupportsResultSet.getString(4));
                    portletSupportsInsertStatement.setString(5, portletSupportsResultSet.getString(5));
                    portletSupportsInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        portletSupportsResultSet.close();
        portletSupportsQueryStatement.close();
        portletSupportsInsertStatement.close();

        // PARAMETER
        PreparedStatement parameterInsertStatement = targetConnection.prepareStatement("INSERT INTO PARAMETER (PARAMETER_ID, OWNER_ID, OWNER_CLASS_NAME, NAME, PARAMETER_VALUE) VALUES (?, ?, ?, ?, ?)");
        Statement parameterQueryStatement = sourceConnection.createStatement();
        parameterQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet parameterResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                parameterResultSet = parameterQueryStatement.executeQuery("SELECT PARAMETER_ID, PARENT_ID, CLASS_NAME, NAME, PARAMETER_VALUE FROM PARAMETER");
                while (parameterResultSet.next())
                {
                    parameterInsertStatement.setInt(1, parameterResultSet.getInt(1));
                    parameterInsertStatement.setInt(2, parameterResultSet.getInt(2));
                    parameterInsertStatement.setString(3, parameterResultSet.getString(3));
                    parameterInsertStatement.setString(4, parameterResultSet.getString(4));
                    parameterInsertStatement.setString(5, parameterResultSet.getString(5));
                    parameterInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                parameterResultSet = parameterQueryStatement.executeQuery("SELECT PARAMETER_ID, OWNER_ID, OWNER_CLASS_NAME, NAME, PARAMETER_VALUE FROM PARAMETER");
                while (parameterResultSet.next())
                {
                    parameterInsertStatement.setInt(1, parameterResultSet.getInt(1));
                    parameterInsertStatement.setInt(2, parameterResultSet.getInt(2));
                    parameterInsertStatement.setString(3, parameterResultSet.getString(3));
                    parameterInsertStatement.setString(4, parameterResultSet.getString(4));
                    parameterInsertStatement.setString(5, parameterResultSet.getString(5));
                    parameterInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        parameterResultSet.close();
        parameterQueryStatement.close();
        parameterInsertStatement.close();

        // PORTLET_PREFERENCE
        PreparedStatement portletPreferenceInsertStatement = targetConnection.prepareStatement("INSERT INTO PORTLET_PREFERENCE (ID, DTYPE, APPLICATION_NAME, PORTLET_NAME, ENTITY_ID, USER_NAME, NAME, READONLY) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        Statement portletPreferenceQueryStatement = sourceConnection.createStatement();
        portletPreferenceQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet portletPreferenceResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                portletPreferenceResultSet = portletPreferenceQueryStatement.executeQuery("SELECT NODE_ID, NODE_TYPE, FULL_PATH FROM PREFS_NODE WHERE FULL_PATH LIKE '%/preferences/%'");
                PreparedStatement portletEntityQueryStatement = sourceConnection.prepareStatement("SELECT APP_NAME, PORTLET_NAME FROM PORTLET_ENTITY WHERE ID = ?");
                PreparedStatement readOnlyQueryStatement = sourceConnection.prepareStatement("SELECT PROPERTY_VALUE FROM PREFS_PROPERTY_VALUE WHERE PROPERTY_NAME = 'read_only' AND NODE_ID = ?");
                while (portletPreferenceResultSet.next())
                {
                    int nodeId = portletPreferenceResultSet.getInt(1);
                    int nodeType = portletPreferenceResultSet.getInt(2);
                    String fullPath = portletPreferenceResultSet.getString(3);
                    if (!fullPath.endsWith("/values") && !fullPath.endsWith("/size"))
                    {
                        String descriminatorType = null;
                        String applicationName = null;
                        String portletName = null;
                        String entityId = "_";
                        String userName = "_";
                        String name = null;
                        short readOnly = 0;
                        if ((nodeType == 0) && fullPath.startsWith("/portlet_entity/"))
                        {
                            int preferencesIndex = fullPath.indexOf("/preferences/", 16);
                            int userNameIndex = ((preferencesIndex != -1) ? fullPath.lastIndexOf("/", preferencesIndex-1) : -1);
                            if ((preferencesIndex != -1) && (userNameIndex > 16))
                            {
                                entityId = fullPath.substring(16, userNameIndex);
                                userName = fullPath.substring(userNameIndex+1, preferencesIndex);
                                name = fullPath.substring(preferencesIndex+13);
                                
                                portletEntityQueryStatement.setString(1, entityId);
                                ResultSet portletEntityResultSet = portletEntityQueryStatement.executeQuery();
                                if (portletEntityResultSet.next())
                                {
                                    descriminatorType = "user";
                                    applicationName = portletEntityResultSet.getString(1);
                                    portletName = portletEntityResultSet.getString(2);
                                }
                                portletEntityResultSet.close();
                            }
                        }
                        else if ((nodeType == 1) && fullPath.startsWith("/portlet_application/"))
                        {
                            int portletsIndex = fullPath.indexOf("/portlets/", 21);
                            int preferencesIndex = ((portletsIndex != -1) ? fullPath.indexOf("/preferences/", portletsIndex+10) : -1);
                            if ((portletsIndex != -1) && (preferencesIndex != -1))
                            {
                                descriminatorType = "portlet";
                                applicationName = fullPath.substring(21, portletsIndex);
                                portletName = fullPath.substring(portletsIndex+10, preferencesIndex);
                                name = fullPath.substring(preferencesIndex+13);

                                readOnlyQueryStatement.setInt(1, nodeId);
                                ResultSet readOnlyResultSet = readOnlyQueryStatement.executeQuery();
                                if (readOnlyResultSet.next())
                                {
                                    readOnly = (Boolean.parseBoolean(readOnlyResultSet.getString(1)) ? (short)1 : (short)0);
                                }
                                readOnlyResultSet.close();
                            }
                        }
                        
                        if ((descriminatorType != null) && (applicationName != null) && (portletName != null) && (name != null))
                        {
                            portletPreferenceInsertStatement.setInt(1, nodeId);
                            portletPreferenceInsertStatement.setString(2, descriminatorType);
                            portletPreferenceInsertStatement.setString(3, applicationName);
                            portletPreferenceInsertStatement.setString(4, portletName);
                            portletPreferenceInsertStatement.setString(5, entityId);
                            portletPreferenceInsertStatement.setString(6, userName);
                            portletPreferenceInsertStatement.setString(7, name);
                            portletPreferenceInsertStatement.setShort(8, readOnly);
                            portletPreferenceInsertStatement.executeUpdate();
                            rowsMigrated++;                            
                            migrationListener.rowMigrated(targetConnection);
                        }
                        else
                        {
                            rowsDropped++;
                            migrationListener.rowDropped(targetConnection);
                        }
                    }
                }
                readOnlyQueryStatement.close();
                portletEntityQueryStatement.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                portletPreferenceResultSet = portletPreferenceQueryStatement.executeQuery("SELECT ID, DTYPE, APPLICATION_NAME, PORTLET_NAME, ENTITY_ID, USER_NAME, NAME, READONLY FROM PORTLET_PREFERENCE");
                while (portletPreferenceResultSet.next())
                {
                    portletPreferenceInsertStatement.setInt(1, portletPreferenceResultSet.getInt(1));
                    portletPreferenceInsertStatement.setString(2, portletPreferenceResultSet.getString(2));
                    portletPreferenceInsertStatement.setString(3, portletPreferenceResultSet.getString(3));
                    portletPreferenceInsertStatement.setString(4, portletPreferenceResultSet.getString(4));
                    portletPreferenceInsertStatement.setString(5, portletPreferenceResultSet.getString(5));
                    portletPreferenceInsertStatement.setString(6, portletPreferenceResultSet.getString(6));
                    portletPreferenceInsertStatement.setString(7, portletPreferenceResultSet.getString(7));
                    portletPreferenceInsertStatement.setShort(8, portletPreferenceResultSet.getShort(8));
                    portletPreferenceInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }                
        portletPreferenceResultSet.close();
        portletPreferenceQueryStatement.close();
        portletPreferenceInsertStatement.close();

        // PORTLET_PREFERENCE_VALUE
        PreparedStatement portletPreferenceValueInsertStatement = targetConnection.prepareStatement("INSERT INTO PORTLET_PREFERENCE_VALUE (ID, PREF_ID, IDX, PREF_VALUE) VALUES (?, ?, ?, ?)");
        Statement portletPreferenceValueQueryStatement = sourceConnection.createStatement();
        portletPreferenceValueQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet portletPreferenceValueResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                portletPreferenceValueResultSet = portletPreferenceValueQueryStatement.executeQuery("SELECT V.PROPERTY_VALUE_ID, N.NODE_ID, V.PROPERTY_NAME, V.PROPERTY_VALUE FROM PREFS_PROPERTY_VALUE V, PREFS_NODE N, PREFS_NODE NV WHERE NV.NODE_ID = V.NODE_ID AND NV.FULL_PATH LIKE '%/values' AND N.NODE_ID = NV.PARENT_NODE_ID");
                PreparedStatement portletPreferenceIdQueryStatement = targetConnection.prepareStatement("SELECT ID FROM PORTLET_PREFERENCE WHERE ID = ?");
                while (portletPreferenceValueResultSet.next())
                {
                    int portletPreferenceId = portletPreferenceValueResultSet.getInt(2);
                    portletPreferenceIdQueryStatement.setInt(1, portletPreferenceId);
                    ResultSet portletPreferenceIdResultSet = portletPreferenceIdQueryStatement.executeQuery();
                    boolean portletPreferenceIdExists = portletPreferenceIdResultSet.next();
                    portletPreferenceIdResultSet.close();
                    
                    if (portletPreferenceIdExists)
                    {
                        portletPreferenceValueInsertStatement.setInt(1, portletPreferenceValueResultSet.getInt(1));
                        portletPreferenceValueInsertStatement.setInt(2, portletPreferenceId);
                        portletPreferenceValueInsertStatement.setShort(3, Short.parseShort(portletPreferenceValueResultSet.getString(3)));
                        portletPreferenceValueInsertStatement.setString(4, portletPreferenceValueResultSet.getString(4));
                        portletPreferenceValueInsertStatement.executeUpdate();
                        rowsMigrated++;
                        migrationListener.rowMigrated(targetConnection);
                    }
                    else
                    {
                        rowsDropped++;
                        migrationListener.rowDropped(targetConnection);
                    }
                }
                portletPreferenceIdQueryStatement.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                portletPreferenceValueResultSet = portletPreferenceValueQueryStatement.executeQuery("SELECT ID, PREF_ID, IDX, PREF_VALUE FROM PORTLET_PREFERENCE_VALUE");
                while (portletPreferenceValueResultSet.next())
                {
                    portletPreferenceValueInsertStatement.setInt(1, portletPreferenceValueResultSet.getInt(1));
                    portletPreferenceValueInsertStatement.setInt(2, portletPreferenceValueResultSet.getInt(2));
                    portletPreferenceValueInsertStatement.setShort(3, portletPreferenceValueResultSet.getShort(3));
                    portletPreferenceValueInsertStatement.setString(4, portletPreferenceValueResultSet.getString(4));
                    portletPreferenceValueInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }                
        portletPreferenceValueResultSet.close();
        portletPreferenceValueQueryStatement.close();
        portletPreferenceValueInsertStatement.close();

        // SECURITY_ROLE_REFERENCE
        PreparedStatement securityRoleReferenceInsertStatement = targetConnection.prepareStatement("INSERT INTO SECURITY_ROLE_REFERENCE (ID, PORTLET_DEFINITION_ID, ROLE_NAME, ROLE_LINK) VALUES (?, ?, ?, ?)");
        Statement securityRoleReferenceQueryStatement = sourceConnection.createStatement();
        securityRoleReferenceQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet securityRoleReferenceResultSet = securityRoleReferenceQueryStatement.executeQuery("SELECT ID, PORTLET_DEFINITION_ID, ROLE_NAME, ROLE_LINK FROM SECURITY_ROLE_REFERENCE");
        while (securityRoleReferenceResultSet.next())
        {
            securityRoleReferenceInsertStatement.setInt(1, securityRoleReferenceResultSet.getInt(1));
            securityRoleReferenceInsertStatement.setInt(2, securityRoleReferenceResultSet.getInt(2));
            securityRoleReferenceInsertStatement.setString(3, securityRoleReferenceResultSet.getString(3));
            securityRoleReferenceInsertStatement.setString(4, securityRoleReferenceResultSet.getString(4));
            securityRoleReferenceInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        securityRoleReferenceResultSet.close();
        securityRoleReferenceQueryStatement.close();
        securityRoleReferenceInsertStatement.close();

        // SECURITY_ROLE
        PreparedStatement securityRoleInsertStatement = targetConnection.prepareStatement("INSERT INTO SECURITY_ROLE (ID, APPLICATION_ID, NAME) VALUES (?, ?, ?)");
        Statement securityRoleQueryStatement = sourceConnection.createStatement();
        securityRoleQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet securityRoleResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                securityRoleResultSet = securityRoleQueryStatement.executeQuery("SELECT ID, WEB_APPLICATION_ID, ROLE_NAME, DESCRIPTION FROM SECURITY_ROLE");
                PreparedStatement webApplicationQueryStatement = sourceConnection.prepareStatement("SELECT CONTEXT_ROOT FROM WEB_APPLICATION WHERE ID = ?");
                PreparedStatement applicationQueryStatement = targetConnection.prepareStatement("SELECT APPLICATION_ID FROM PORTLET_APPLICATION WHERE CONTEXT_PATH = ?");
                while (securityRoleResultSet.next())
                {
                    String contextPath = null;
                    int webApplicationId = securityRoleResultSet.getInt(2);
                    webApplicationQueryStatement.setInt(1, webApplicationId);
                    ResultSet webApplicationResultSet = webApplicationQueryStatement.executeQuery();
                    if (webApplicationResultSet.next())
                    {
                        contextPath = webApplicationResultSet.getString(1);
                    }
                    webApplicationResultSet.close();
                    
                    int applicationId = 0;
                    if (contextPath != null)
                    {
                        applicationQueryStatement.setString(1, contextPath);
                        ResultSet applicationResultSet = applicationQueryStatement.executeQuery();
                        if (applicationResultSet.next())
                        {
                            applicationId = applicationResultSet.getInt(1);
                        }
                        applicationResultSet.close();
                    }
     
                    if ((contextPath != null) && (applicationId != 0))
                    {
                        securityRoleInsertStatement.setInt(1, securityRoleResultSet.getInt(1));
                        securityRoleInsertStatement.setInt(2, applicationId);
                        securityRoleInsertStatement.setString(3, securityRoleResultSet.getString(3));
                        securityRoleInsertStatement.executeUpdate();
                        rowsMigrated++;
                        migrationListener.rowMigrated(targetConnection);

                        int securityRoleId = securityRoleResultSet.getInt(1);
                        String description = securityRoleResultSet.getString(4);
                        if (description != null)
                        {
                            localizedDescriptions.add(new LocalizedDescription(securityRoleId, "org.apache.jetspeed.om.portlet.impl.SecurityRoleImpl", description));
                        }
                    }
                    else
                    {
                        rowsDropped++;
                        migrationListener.rowDropped(targetConnection);
                    }
                }
                applicationQueryStatement.close();
                webApplicationQueryStatement.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                securityRoleResultSet = securityRoleQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, NAME FROM SECURITY_ROLE");
                while (securityRoleResultSet.next())
                {
                    securityRoleInsertStatement.setInt(1, securityRoleResultSet.getInt(1));
                    securityRoleInsertStatement.setInt(2, securityRoleResultSet.getInt(2));
                    securityRoleInsertStatement.setString(3, securityRoleResultSet.getString(3));
                    securityRoleInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }                
        securityRoleResultSet.close();
        securityRoleQueryStatement.close();
        securityRoleInsertStatement.close();

        // USER_ATTRIBUTE_REF
        PreparedStatement userAttributeRefInsertStatement = targetConnection.prepareStatement("INSERT INTO USER_ATTRIBUTE_REF (ID, APPLICATION_ID, NAME, NAME_LINK) VALUES (?, ?, ?, ?)");
        Statement userAttributeRefQueryStatement = sourceConnection.createStatement();
        userAttributeRefQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet userAttributeRefResultSet = userAttributeRefQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, NAME, NAME_LINK FROM USER_ATTRIBUTE_REF");
        while (userAttributeRefResultSet.next())
        {
            userAttributeRefInsertStatement.setInt(1, userAttributeRefResultSet.getInt(1));
            userAttributeRefInsertStatement.setInt(2, userAttributeRefResultSet.getInt(2));
            userAttributeRefInsertStatement.setString(3, userAttributeRefResultSet.getString(3));
            userAttributeRefInsertStatement.setString(4, userAttributeRefResultSet.getString(4));
            userAttributeRefInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        userAttributeRefResultSet.close();
        userAttributeRefQueryStatement.close();
        userAttributeRefInsertStatement.close();

        // USER_ATTRIBUTE
        PreparedStatement userAttributeInsertStatement = targetConnection.prepareStatement("INSERT INTO USER_ATTRIBUTE (ID, APPLICATION_ID, NAME) VALUES (?, ?, ?)");
        Statement userAttributeQueryStatement = sourceConnection.createStatement();
        userAttributeQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet userAttributeResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                userAttributeResultSet = userAttributeQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, NAME, DESCRIPTION FROM USER_ATTRIBUTE");
                while (userAttributeResultSet.next())
                {
                    userAttributeInsertStatement.setInt(1, userAttributeResultSet.getInt(1));
                    userAttributeInsertStatement.setInt(2, userAttributeResultSet.getInt(2));
                    userAttributeInsertStatement.setString(3, userAttributeResultSet.getString(3));
                    userAttributeInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);

                    int userAttributeId = userAttributeResultSet.getInt(1);
                    String description = userAttributeResultSet.getString(4);
                    if (description != null)
                    {
                        localizedDescriptions.add(new LocalizedDescription(userAttributeId, "org.apache.jetspeed.om.portlet.impl.UserAttributeImpl", description));
                    }
                }
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                userAttributeResultSet = userAttributeQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, NAME FROM USER_ATTRIBUTE");
                while (userAttributeResultSet.next())
                {
                    userAttributeInsertStatement.setInt(1, userAttributeResultSet.getInt(1));
                    userAttributeInsertStatement.setInt(2, userAttributeResultSet.getInt(2));
                    userAttributeInsertStatement.setString(3, userAttributeResultSet.getString(3));
                    userAttributeInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }                
        userAttributeResultSet.close();
        userAttributeQueryStatement.close();
        userAttributeInsertStatement.close();

        // JETSPEED_SERVICE
        PreparedStatement jetspeedServiceInsertStatement = targetConnection.prepareStatement("INSERT INTO JETSPEED_SERVICE (ID, APPLICATION_ID, NAME) VALUES (?, ?, ?)");
        Statement jetspeedServiceQueryStatement = sourceConnection.createStatement();
        jetspeedServiceQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet jetspeedServiceResultSet = jetspeedServiceQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, NAME FROM JETSPEED_SERVICE");
        while (jetspeedServiceResultSet.next())
        {
            jetspeedServiceInsertStatement.setInt(1, jetspeedServiceResultSet.getInt(1));
            jetspeedServiceInsertStatement.setInt(2, jetspeedServiceResultSet.getInt(2));
            jetspeedServiceInsertStatement.setString(3, jetspeedServiceResultSet.getString(3));
            jetspeedServiceInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        jetspeedServiceResultSet.close();
        jetspeedServiceQueryStatement.close();
        jetspeedServiceInsertStatement.close();

        // CUSTOM_PORTLET_MODE
        PreparedStatement customPortletModeInsertStatement = targetConnection.prepareStatement("INSERT INTO CUSTOM_PORTLET_MODE (ID, APPLICATION_ID, CUSTOM_NAME, MAPPED_NAME, PORTAL_MANAGED) VALUES (?, ?, ?, ?, ?)");
        Statement customPortletModeQueryStatement = sourceConnection.createStatement();
        customPortletModeQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet customPortletModeResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                customPortletModeResultSet = customPortletModeQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, CUSTOM_NAME, MAPPED_NAME, DESCRIPTION FROM CUSTOM_PORTLET_MODE");
                while (customPortletModeResultSet.next())
                {
                    customPortletModeInsertStatement.setInt(1, customPortletModeResultSet.getInt(1));
                    customPortletModeInsertStatement.setInt(2, customPortletModeResultSet.getInt(2));
                    customPortletModeInsertStatement.setString(3, customPortletModeResultSet.getString(3));
                    customPortletModeInsertStatement.setString(4, customPortletModeResultSet.getString(4));
                    customPortletModeInsertStatement.setShort(5, (short)1);
                    customPortletModeInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);

                    int customPortletModeId = customPortletModeResultSet.getInt(1);
                    String description = customPortletModeResultSet.getString(5);
                    if (description != null)
                    {
                        localizedDescriptions.add(new LocalizedDescription(customPortletModeId, "org.apache.jetspeed.om.portlet.impl.CustomPortletModeImpl", description));
                    }
                }
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                customPortletModeResultSet = customPortletModeQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, CUSTOM_NAME, MAPPED_NAME, PORTAL_MANAGED FROM CUSTOM_PORTLET_MODE");
                while (customPortletModeResultSet.next())
                {
                    customPortletModeInsertStatement.setInt(1, customPortletModeResultSet.getInt(1));
                    customPortletModeInsertStatement.setInt(2, customPortletModeResultSet.getInt(2));
                    customPortletModeInsertStatement.setString(3, customPortletModeResultSet.getString(3));
                    customPortletModeInsertStatement.setString(4, customPortletModeResultSet.getString(4));
                    customPortletModeInsertStatement.setShort(5, customPortletModeResultSet.getShort(5));
                    customPortletModeInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }                
        customPortletModeResultSet.close();
        customPortletModeQueryStatement.close();
        customPortletModeInsertStatement.close();

        // CUSTOM_WINDOW_STATE
        PreparedStatement customWindowStateInsertStatement = targetConnection.prepareStatement("INSERT INTO CUSTOM_WINDOW_STATE (ID, APPLICATION_ID, CUSTOM_NAME, MAPPED_NAME) VALUES (?, ?, ?, ?)");
        Statement customWindowStateQueryStatement = sourceConnection.createStatement();
        customWindowStateQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet customWindowStateResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                customWindowStateResultSet = customWindowStateQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, CUSTOM_NAME, MAPPED_NAME, DESCRIPTION FROM CUSTOM_WINDOW_STATE");
                while (customWindowStateResultSet.next())
                {
                    customWindowStateInsertStatement.setInt(1, customWindowStateResultSet.getInt(1));
                    customWindowStateInsertStatement.setInt(2, customWindowStateResultSet.getInt(2));
                    customWindowStateInsertStatement.setString(3, customWindowStateResultSet.getString(3));
                    customWindowStateInsertStatement.setString(4, customWindowStateResultSet.getString(4));
                    customWindowStateInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);

                    int customWindowStateId = customWindowStateResultSet.getInt(1);
                    String description = customWindowStateResultSet.getString(5);
                    if (description != null)
                    {
                        localizedDescriptions.add(new LocalizedDescription(customWindowStateId, "org.apache.jetspeed.om.portlet.impl.CustomWindowStateImpl", description));
                    }
                }
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                customWindowStateResultSet = customWindowStateQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, CUSTOM_NAME, MAPPED_NAME FROM CUSTOM_WINDOW_STATE");
                while (customWindowStateResultSet.next())
                {
                    customWindowStateInsertStatement.setInt(1, customWindowStateResultSet.getInt(1));
                    customWindowStateInsertStatement.setInt(2, customWindowStateResultSet.getInt(2));
                    customWindowStateInsertStatement.setString(3, customWindowStateResultSet.getString(3));
                    customWindowStateInsertStatement.setString(4, customWindowStateResultSet.getString(4));
                    customWindowStateInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }                
        customWindowStateResultSet.close();
        customWindowStateQueryStatement.close();
        customWindowStateInsertStatement.close();

        // LOCALIZED_DESCRIPTION
        int maxLocalizedDescriptionId = 0;
        PreparedStatement localizedDescriptionInsertStatement = targetConnection.prepareStatement("INSERT INTO LOCALIZED_DESCRIPTION (ID, OWNER_ID, OWNER_CLASS_NAME, DESCRIPTION, LOCALE_STRING) VALUES (?, ?, ?, ?, ?)");
        Statement localizedDescriptionQueryStatement = sourceConnection.createStatement();
        localizedDescriptionQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet localizedDescriptionResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                localizedDescriptionResultSet = localizedDescriptionQueryStatement.executeQuery("SELECT ID, OBJECT_ID, CLASS_NAME, DESCRIPTION, LOCALE_STRING FROM LOCALIZED_DESCRIPTION");
                while (localizedDescriptionResultSet.next())
                {
                    String className = localizedDescriptionResultSet.getString(3);
                    String ownerClassName = null;
                    if (className.equals("org.apache.jetspeed.om.impl.ParameterDescriptionImpl"))
                    {
                        ownerClassName = "org.apache.jetspeed.om.portlet.impl.InitParamImpl";
                    }
                    else if (className.equals("org.apache.jetspeed.om.impl.PortletDescriptionImpl"))
                    {
                        ownerClassName = "org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl";
                    }
                    else if (className.equals("org.apache.jetspeed.om.impl.SecurityRoleRefDescriptionImpl"))
                    {
                        ownerClassName = "org.apache.jetspeed.om.portlet.impl.SecurityRoleRefImpl";
                    }
                    if (ownerClassName != null)
                    {
                        int localizedDescriptionId = localizedDescriptionResultSet.getInt(1);
                        if (localizedDescriptionId > maxLocalizedDescriptionId)
                        {
                            maxLocalizedDescriptionId = localizedDescriptionId;
                        }
                        localizedDescriptionInsertStatement.setInt(1, localizedDescriptionId);
                        localizedDescriptionInsertStatement.setInt(2, localizedDescriptionResultSet.getInt(2));
                        localizedDescriptionInsertStatement.setString(3, ownerClassName);
                        localizedDescriptionInsertStatement.setString(4, localizedDescriptionResultSet.getString(4));
                        localizedDescriptionInsertStatement.setString(5, localizedDescriptionResultSet.getString(5));
                        localizedDescriptionInsertStatement.executeUpdate();
                        rowsMigrated++;
                        migrationListener.rowMigrated(targetConnection);
                    }
                }                
                for (LocalizedDescription description : localizedDescriptions)
                {
                    localizedDescriptionInsertStatement.setInt(1, ++maxLocalizedDescriptionId);
                    localizedDescriptionInsertStatement.setInt(2, description.ownerId);
                    localizedDescriptionInsertStatement.setString(3, description.ownerClassName);
                    localizedDescriptionInsertStatement.setString(4, description.description);
                    localizedDescriptionInsertStatement.setString(5, DEFAULT_LOCALE_STRING);
                    localizedDescriptionInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                localizedDescriptionResultSet = localizedDescriptionQueryStatement.executeQuery("SELECT ID, OWNER_ID, OWNER_CLASS_NAME, DESCRIPTION, LOCALE_STRING FROM LOCALIZED_DESCRIPTION");
                while (localizedDescriptionResultSet.next())
                {
                    localizedDescriptionInsertStatement.setInt(1, localizedDescriptionResultSet.getInt(1));
                    localizedDescriptionInsertStatement.setInt(2, localizedDescriptionResultSet.getInt(2));
                    localizedDescriptionInsertStatement.setString(3, localizedDescriptionResultSet.getString(3));
                    localizedDescriptionInsertStatement.setString(4, localizedDescriptionResultSet.getString(4));
                    localizedDescriptionInsertStatement.setString(5, localizedDescriptionResultSet.getString(5));
                    localizedDescriptionInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }                
        localizedDescriptionResultSet.close();
        localizedDescriptionQueryStatement.close();
        localizedDescriptionInsertStatement.close();
        
        // LOCALIZED_DISPLAY_NAME
        PreparedStatement localizedDisplayNameInsertStatement = targetConnection.prepareStatement("INSERT INTO LOCALIZED_DISPLAY_NAME (ID, OWNER_ID, OWNER_CLASS_NAME, DISPLAY_NAME, LOCALE_STRING) VALUES (?, ?, ?, ?, ?)");
        Statement localizedDisplayNameQueryStatement = sourceConnection.createStatement();
        localizedDisplayNameQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet localizedDisplayNameResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                localizedDisplayNameResultSet = localizedDisplayNameQueryStatement.executeQuery("SELECT ID, OBJECT_ID, CLASS_NAME, DISPLAY_NAME, LOCALE_STRING FROM LOCALIZED_DISPLAY_NAME");
                while (localizedDisplayNameResultSet.next())
                {
                    String className = localizedDisplayNameResultSet.getString(3);
                    String ownerClassName = null;
                    if (className.equals("org.apache.jetspeed.om.impl.PortletDisplayNameImpl"))
                    {
                        ownerClassName = "org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl";
                    }
                    if (ownerClassName != null)
                    {
                        localizedDisplayNameInsertStatement.setInt(1, localizedDisplayNameResultSet.getInt(1));
                        localizedDisplayNameInsertStatement.setInt(2, localizedDisplayNameResultSet.getInt(2));
                        localizedDisplayNameInsertStatement.setString(3, ownerClassName);
                        localizedDisplayNameInsertStatement.setString(4, localizedDisplayNameResultSet.getString(4));
                        localizedDisplayNameInsertStatement.setString(5, localizedDisplayNameResultSet.getString(5));
                        localizedDisplayNameInsertStatement.executeUpdate();
                        rowsMigrated++;
                        migrationListener.rowMigrated(targetConnection);
                    }
                }
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                localizedDisplayNameResultSet = localizedDisplayNameQueryStatement.executeQuery("SELECT ID, OWNER_ID, OWNER_CLASS_NAME, DISPLAY_NAME, LOCALE_STRING FROM LOCALIZED_DISPLAY_NAME");
                while (localizedDisplayNameResultSet.next())
                {
                    localizedDisplayNameInsertStatement.setInt(1, localizedDisplayNameResultSet.getInt(1));
                    localizedDisplayNameInsertStatement.setInt(2, localizedDisplayNameResultSet.getInt(2));
                    localizedDisplayNameInsertStatement.setString(3, localizedDisplayNameResultSet.getString(3));
                    localizedDisplayNameInsertStatement.setString(4, localizedDisplayNameResultSet.getString(4));
                    localizedDisplayNameInsertStatement.setString(5, localizedDisplayNameResultSet.getString(5));
                    localizedDisplayNameInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }                
        localizedDisplayNameResultSet.close();
        localizedDisplayNameQueryStatement.close();
        localizedDisplayNameInsertStatement.close();

        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                // EVENT_DEFINITION
                PreparedStatement eventDefinitionInsertStatement = targetConnection.prepareStatement("INSERT INTO EVENT_DEFINITION (ID, APPLICATION_ID, LOCAL_PART, NAMESPACE, PREFIX, VALUE_TYPE) VALUES (?, ?, ?, ?, ?, ?)");
                Statement eventDefinitionQueryStatement = sourceConnection.createStatement();
                eventDefinitionQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet eventDefinitionResultSet = eventDefinitionQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, LOCAL_PART, NAMESPACE, PREFIX, VALUE_TYPE FROM EVENT_DEFINITION");
                while (eventDefinitionResultSet.next())
                {
                    eventDefinitionInsertStatement.setInt(1, eventDefinitionResultSet.getInt(1));
                    eventDefinitionInsertStatement.setInt(2, eventDefinitionResultSet.getInt(2));
                    eventDefinitionInsertStatement.setString(3, eventDefinitionResultSet.getString(3));
                    eventDefinitionInsertStatement.setString(4, eventDefinitionResultSet.getString(4));
                    eventDefinitionInsertStatement.setString(5, eventDefinitionResultSet.getString(5));
                    eventDefinitionInsertStatement.setString(6, eventDefinitionResultSet.getString(6));
                    eventDefinitionInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                eventDefinitionResultSet.close();
                eventDefinitionQueryStatement.close();
                eventDefinitionInsertStatement.close();

                // EVENT_ALIAS
                PreparedStatement eventAliasInsertStatement = targetConnection.prepareStatement("INSERT INTO EVENT_ALIAS (ID, OWNER_ID, LOCAL_PART, NAMESPACE, PREFIX) VALUES (?, ?, ?, ?, ?)");
                Statement eventAliasQueryStatement = sourceConnection.createStatement();
                eventAliasQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet eventAliasResultSet = eventAliasQueryStatement.executeQuery("SELECT ID, OWNER_ID, LOCAL_PART, NAMESPACE, PREFIX FROM EVENT_ALIAS");
                while (eventAliasResultSet.next())
                {
                    eventAliasInsertStatement.setInt(1, eventAliasResultSet.getInt(1));
                    eventAliasInsertStatement.setInt(2, eventAliasResultSet.getInt(2));
                    eventAliasInsertStatement.setString(3, eventAliasResultSet.getString(3));
                    eventAliasInsertStatement.setString(4, eventAliasResultSet.getString(4));
                    eventAliasInsertStatement.setString(5, eventAliasResultSet.getString(5));
                    eventAliasInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                eventAliasResultSet.close();
                eventAliasQueryStatement.close();
                eventAliasInsertStatement.close();

                // PARAMETER_ALIAS
                PreparedStatement parameterAliasInsertStatement = targetConnection.prepareStatement("INSERT INTO PARAMETER_ALIAS (ID, OWNER_ID, LOCAL_PART, NAMESPACE, PREFIX) VALUES (?, ?, ?, ?, ?)");
                Statement parameterAliasQueryStatement = sourceConnection.createStatement();
                parameterAliasQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet parameterAliasResultSet = parameterAliasQueryStatement.executeQuery("SELECT ID, OWNER_ID, LOCAL_PART, NAMESPACE, PREFIX FROM PARAMETER_ALIAS");
                while (parameterAliasResultSet.next())
                {
                    parameterAliasInsertStatement.setInt(1, parameterAliasResultSet.getInt(1));
                    parameterAliasInsertStatement.setInt(2, parameterAliasResultSet.getInt(2));
                    parameterAliasInsertStatement.setString(3, parameterAliasResultSet.getString(3));
                    parameterAliasInsertStatement.setString(4, parameterAliasResultSet.getString(4));
                    parameterAliasInsertStatement.setString(5, parameterAliasResultSet.getString(5));
                    parameterAliasInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                parameterAliasResultSet.close();
                parameterAliasQueryStatement.close();
                parameterAliasInsertStatement.close();

                // PUBLISHING_EVENT
                PreparedStatement publishingEventInsertStatement = targetConnection.prepareStatement("INSERT INTO PUBLISHING_EVENT (ID, OWNER_ID, LOCAL_PART, NAMESPACE, PREFIX) VALUES (?, ?, ?, ?, ?)");
                Statement publishingEventQueryStatement = sourceConnection.createStatement();
                publishingEventQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet publishingEventResultSet = publishingEventQueryStatement.executeQuery("SELECT ID, OWNER_ID, LOCAL_PART, NAMESPACE, PREFIX FROM PUBLISHING_EVENT");
                while (publishingEventResultSet.next())
                {
                    publishingEventInsertStatement.setInt(1, publishingEventResultSet.getInt(1));
                    publishingEventInsertStatement.setInt(2, publishingEventResultSet.getInt(2));
                    publishingEventInsertStatement.setString(3, publishingEventResultSet.getString(3));
                    publishingEventInsertStatement.setString(4, publishingEventResultSet.getString(4));
                    publishingEventInsertStatement.setString(5, publishingEventResultSet.getString(5));
                    publishingEventInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                publishingEventResultSet.close();
                publishingEventQueryStatement.close();
                publishingEventInsertStatement.close();

                // PROCESSING_EVENT
                PreparedStatement processingEventInsertStatement = targetConnection.prepareStatement("INSERT INTO PROCESSING_EVENT (ID, OWNER_ID, LOCAL_PART, NAMESPACE, PREFIX) VALUES (?, ?, ?, ?, ?)");
                Statement processingEventQueryStatement = sourceConnection.createStatement();
                processingEventQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet processingEventResultSet = processingEventQueryStatement.executeQuery("SELECT ID, OWNER_ID, LOCAL_PART, NAMESPACE, PREFIX FROM PROCESSING_EVENT");
                while (processingEventResultSet.next())
                {
                    processingEventInsertStatement.setInt(1, processingEventResultSet.getInt(1));
                    processingEventInsertStatement.setInt(2, processingEventResultSet.getInt(2));
                    processingEventInsertStatement.setString(3, processingEventResultSet.getString(3));
                    processingEventInsertStatement.setString(4, processingEventResultSet.getString(4));
                    processingEventInsertStatement.setString(5, processingEventResultSet.getString(5));
                    processingEventInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                processingEventResultSet.close();
                processingEventQueryStatement.close();
                processingEventInsertStatement.close();

                // NAMED_PARAMETER
                PreparedStatement namedParameterInsertStatement = targetConnection.prepareStatement("INSERT INTO NAMED_PARAMETER (ID, OWNER_ID, NAME) VALUES (?, ?, ?)");
                Statement namedParameterQueryStatement = sourceConnection.createStatement();
                namedParameterQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet namedParameterResultSet = namedParameterQueryStatement.executeQuery("SELECT ID, OWNER_ID, NAME FROM NAMED_PARAMETER");
                while (namedParameterResultSet.next())
                {
                    namedParameterInsertStatement.setInt(1, namedParameterResultSet.getInt(1));
                    namedParameterInsertStatement.setInt(2, namedParameterResultSet.getInt(2));
                    namedParameterInsertStatement.setString(3, namedParameterResultSet.getString(3));
                    namedParameterInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                namedParameterResultSet.close();
                namedParameterQueryStatement.close();
                namedParameterInsertStatement.close();
                
                // RUNTIME_OPTION
                PreparedStatement runtimeOptionInsertStatement = targetConnection.prepareStatement("INSERT INTO RUNTIME_OPTION (ID, OWNER_ID, OWNER_CLASS_NAME, NAME) VALUES (?, ?, ?, ?)");
                Statement runtimeOptionQueryStatement = sourceConnection.createStatement();
                runtimeOptionQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet runtimeOptionResultSet = runtimeOptionQueryStatement.executeQuery("SELECT ID, OWNER_ID, OWNER_CLASS_NAME, NAME FROM RUNTIME_OPTION");
                while (runtimeOptionResultSet.next())
                {
                    runtimeOptionInsertStatement.setInt(1, runtimeOptionResultSet.getInt(1));
                    runtimeOptionInsertStatement.setInt(2, runtimeOptionResultSet.getInt(2));
                    runtimeOptionInsertStatement.setString(3, runtimeOptionResultSet.getString(3));
                    runtimeOptionInsertStatement.setString(4, runtimeOptionResultSet.getString(4));
                    runtimeOptionInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                runtimeOptionResultSet.close();
                runtimeOptionQueryStatement.close();
                runtimeOptionInsertStatement.close();
                
                // RUNTIME_VALUE
                PreparedStatement runtimeValueInsertStatement = targetConnection.prepareStatement("INSERT INTO RUNTIME_VALUE (ID, OWNER_ID, RVALUE) VALUES (?, ?, ?)");
                Statement runtimeValueQueryStatement = sourceConnection.createStatement();
                runtimeValueQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet runtimeValueResultSet = runtimeValueQueryStatement.executeQuery("SELECT ID, OWNER_ID, RVALUE FROM RUNTIME_VALUE");
                while (runtimeValueResultSet.next())
                {
                    runtimeValueInsertStatement.setInt(1, runtimeValueResultSet.getInt(1));
                    runtimeValueInsertStatement.setInt(2, runtimeValueResultSet.getInt(2));
                    runtimeValueInsertStatement.setString(3, runtimeValueResultSet.getString(3));
                    runtimeValueInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                runtimeValueResultSet.close();
                runtimeValueQueryStatement.close();
                runtimeValueInsertStatement.close();

                // PUBLIC_PARAMETER
                PreparedStatement publicParameterInsertStatement = targetConnection.prepareStatement("INSERT INTO PUBLIC_PARAMETER (ID, APPLICATION_ID, LOCAL_PART, NAMESPACE, PREFIX, IDENTIFIER) VALUES (?, ?, ?, ?, ?, ?)");
                Statement publicParameterQueryStatement = sourceConnection.createStatement();
                publicParameterQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet publicParameterResultSet = publicParameterQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, LOCAL_PART, NAMESPACE, PREFIX, IDENTIFIER FROM PUBLIC_PARAMETER");
                while (publicParameterResultSet.next())
                {
                    publicParameterInsertStatement.setInt(1, publicParameterResultSet.getInt(1));
                    publicParameterInsertStatement.setInt(2, publicParameterResultSet.getInt(2));
                    publicParameterInsertStatement.setString(3, publicParameterResultSet.getString(3));
                    publicParameterInsertStatement.setString(4, publicParameterResultSet.getString(4));
                    publicParameterInsertStatement.setString(5, publicParameterResultSet.getString(5));
                    publicParameterInsertStatement.setString(6, publicParameterResultSet.getString(6));
                    publicParameterInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                publicParameterResultSet.close();
                publicParameterQueryStatement.close();
                publicParameterInsertStatement.close();

                // PORTLET_FILTER
                PreparedStatement portletFilterInsertStatement = targetConnection.prepareStatement("INSERT INTO PORTLET_FILTER (ID, APPLICATION_ID, FILTER_NAME, FILTER_CLASS) VALUES (?, ?, ?, ?)");
                Statement portletFilterQueryStatement = sourceConnection.createStatement();
                portletFilterQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet portletFilterResultSet = portletFilterQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, FILTER_NAME, FILTER_CLASS FROM PORTLET_FILTER");
                while (portletFilterResultSet.next())
                {
                    portletFilterInsertStatement.setInt(1, portletFilterResultSet.getInt(1));
                    portletFilterInsertStatement.setInt(2, portletFilterResultSet.getInt(2));
                    portletFilterInsertStatement.setString(3, portletFilterResultSet.getString(3));
                    portletFilterInsertStatement.setString(4, portletFilterResultSet.getString(4));
                    portletFilterInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                portletFilterResultSet.close();
                portletFilterQueryStatement.close();
                portletFilterInsertStatement.close();

                // FILTER_LIFECYCLE
                PreparedStatement filterLifecycleInsertStatement = targetConnection.prepareStatement("INSERT INTO FILTER_LIFECYCLE (ID, OWNER_ID, NAME) VALUES (?, ?, ?)");
                Statement filterLifecycleQueryStatement = sourceConnection.createStatement();
                filterLifecycleQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet filterLifecycleResultSet = filterLifecycleQueryStatement.executeQuery("SELECT ID, OWNER_ID, NAME FROM FILTER_LIFECYCLE");
                while (filterLifecycleResultSet.next())
                {
                    filterLifecycleInsertStatement.setInt(1, filterLifecycleResultSet.getInt(1));
                    filterLifecycleInsertStatement.setInt(2, filterLifecycleResultSet.getInt(2));
                    filterLifecycleInsertStatement.setString(3, filterLifecycleResultSet.getString(3));
                    filterLifecycleInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                filterLifecycleResultSet.close();
                filterLifecycleQueryStatement.close();
                filterLifecycleInsertStatement.close();

                // FILTER_MAPPING
                PreparedStatement filterMappingInsertStatement = targetConnection.prepareStatement("INSERT INTO FILTER_MAPPING (ID, APPLICATION_ID, FILTER_NAME) VALUES (?, ?, ?)");
                Statement filterMappingQueryStatement = sourceConnection.createStatement();
                filterMappingQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet filterMappingResultSet = filterMappingQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, FILTER_NAME FROM FILTER_MAPPING");
                while (filterMappingResultSet.next())
                {
                    filterMappingInsertStatement.setInt(1, filterMappingResultSet.getInt(1));
                    filterMappingInsertStatement.setInt(2, filterMappingResultSet.getInt(2));
                    filterMappingInsertStatement.setString(3, filterMappingResultSet.getString(3));
                    filterMappingInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                filterMappingResultSet.close();
                filterMappingQueryStatement.close();
                filterMappingInsertStatement.close();

                // FILTERED_PORTLET
                PreparedStatement filteredPortletInsertStatement = targetConnection.prepareStatement("INSERT INTO FILTERED_PORTLET (ID, OWNER_ID, NAME) VALUES (?, ?, ?)");
                Statement filteredPortletQueryStatement = sourceConnection.createStatement();
                filteredPortletQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet filteredPortletResultSet = filteredPortletQueryStatement.executeQuery("SELECT ID, OWNER_ID, NAME FROM FILTERED_PORTLET");
                while (filteredPortletResultSet.next())
                {
                    filteredPortletInsertStatement.setInt(1, filteredPortletResultSet.getInt(1));
                    filteredPortletInsertStatement.setInt(2, filteredPortletResultSet.getInt(2));
                    filteredPortletInsertStatement.setString(3, filteredPortletResultSet.getString(3));
                    filteredPortletInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                filteredPortletResultSet.close();
                filteredPortletQueryStatement.close();
                filteredPortletInsertStatement.close();

                // PORTLET_LISTENER
                PreparedStatement portletListenerInsertStatement = targetConnection.prepareStatement("INSERT INTO PORTLET_LISTENER (ID, APPLICATION_ID, LISTENER_CLASS) VALUES (?, ?, ?)");
                Statement portletListenerQueryStatement = sourceConnection.createStatement();
                portletListenerQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet portletListenerResultSet = portletListenerQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, LISTENER_CLASS FROM PORTLET_LISTENER");
                while (portletListenerResultSet.next())
                {
                    portletListenerInsertStatement.setInt(1, portletListenerResultSet.getInt(1));
                    portletListenerInsertStatement.setInt(2, portletListenerResultSet.getInt(2));
                    portletListenerInsertStatement.setString(3, portletListenerResultSet.getString(3));
                    portletListenerInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                portletListenerResultSet.close();
                portletListenerQueryStatement.close();
                portletListenerInsertStatement.close();

                // PA_SECURITY_CONSTRAINT
                PreparedStatement portletApplicationSecurityConstraintInsertStatement = targetConnection.prepareStatement("INSERT INTO PA_SECURITY_CONSTRAINT (ID, APPLICATION_ID, TRANSPORT) VALUES (?, ?, ?)");
                Statement portletApplicationSecurityConstraintQueryStatement = sourceConnection.createStatement();
                portletApplicationSecurityConstraintQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet portletApplicationSecurityConstraintResultSet = portletApplicationSecurityConstraintQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, TRANSPORT FROM PA_SECURITY_CONSTRAINT");
                while (portletApplicationSecurityConstraintResultSet.next())
                {
                    portletApplicationSecurityConstraintInsertStatement.setInt(1, portletApplicationSecurityConstraintResultSet.getInt(1));
                    portletApplicationSecurityConstraintInsertStatement.setInt(2, portletApplicationSecurityConstraintResultSet.getInt(2));
                    portletApplicationSecurityConstraintInsertStatement.setString(3, portletApplicationSecurityConstraintResultSet.getString(3));
                    portletApplicationSecurityConstraintInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                portletApplicationSecurityConstraintResultSet.close();
                portletApplicationSecurityConstraintQueryStatement.close();
                portletApplicationSecurityConstraintInsertStatement.close();

                // SECURED_PORTLET
                PreparedStatement securedPortletInsertStatement = targetConnection.prepareStatement("INSERT INTO SECURED_PORTLET (ID, OWNER_ID, NAME) VALUES (?, ?, ?)");
                Statement securedPortletQueryStatement = sourceConnection.createStatement();
                securedPortletQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet securedPortletResultSet = securedPortletQueryStatement.executeQuery("SELECT ID, OWNER_ID, NAME FROM SECURED_PORTLET");
                while (securedPortletResultSet.next())
                {
                    securedPortletInsertStatement.setInt(1, securedPortletResultSet.getInt(1));
                    securedPortletInsertStatement.setInt(2, securedPortletResultSet.getInt(2));
                    securedPortletInsertStatement.setString(3, securedPortletResultSet.getString(3));
                    securedPortletInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                securedPortletResultSet.close();
                securedPortletQueryStatement.close();
                securedPortletInsertStatement.close();

                // LOCALE_ENCODING_MAPPING      
                PreparedStatement localeEncodingMappingInsertStatement = targetConnection.prepareStatement("INSERT INTO LOCALE_ENCODING_MAPPING (ID, APPLICATION_ID, LOCALE_STRING, ENCODING) VALUES (?, ?, ?, ?)");
                Statement localeEncodingMappingQueryStatement = sourceConnection.createStatement();
                localeEncodingMappingQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet localeEncodingMappingResultSet = localeEncodingMappingQueryStatement.executeQuery("SELECT ID, APPLICATION_ID, LOCALE_STRING, ENCODING FROM LOCALE_ENCODING_MAPPING");
                while (localeEncodingMappingResultSet.next())
                {
                    localeEncodingMappingInsertStatement.setInt(1, localeEncodingMappingResultSet.getInt(1));
                    localeEncodingMappingInsertStatement.setInt(2, localeEncodingMappingResultSet.getInt(2));
                    localeEncodingMappingInsertStatement.setString(3, localeEncodingMappingResultSet.getString(3));
                    localeEncodingMappingInsertStatement.setString(4, localeEncodingMappingResultSet.getString(4));
                    localeEncodingMappingInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                localeEncodingMappingResultSet.close();
                localeEncodingMappingQueryStatement.close();
                localeEncodingMappingInsertStatement.close();                
            }
            break;
        }
        
        // OJB_HL_SEQ
        boolean localizedDescriptionSeqMigrated = false;
        PreparedStatement ojbInsertStatement = targetConnection.prepareStatement("INSERT INTO OJB_HL_SEQ (TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION) VALUES (?, ?, ?, ?, ?)");
        Statement ojbQueryStatement = sourceConnection.createStatement();
        ResultSet ojbResultSet = ojbQueryStatement.executeQuery("SELECT TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION FROM OJB_HL_SEQ WHERE TABLENAME IN ('SEQ_PORTLET_DEFINITION', 'SEQ_PORTLET_APPLICATION', 'SEQ_PA_METADATA_FIELDS', 'SEQ_PD_METADATA_FIELDS', 'SEQ_LANGUAGE', 'SEQ_PORTLET_SUPPORTS', 'SEQ_PORTLET_PREFERENCE', 'SEQ_PORTLET_PREFERENCE_VALUE', 'SEQ_PARAMETER', 'SEQ_SECURITY_ROLE_REFERENCE', 'SEQ_SECURITY_ROLE', 'SEQ_USER_ATTRIBUTE_REF', 'SEQ_USER_ATTRIBUTE', 'SEQ_JETSPEED_SERVICE', 'SEQ_CUSTOM_PORTLET_MODE', 'SEQ_CUSTOM_WINDOW_STATE', 'SEQ_LOCALIZED_DESCRIPTION', 'SEQ_LOCALIZED_DISPLAY_NAME', 'SEQ_EVENT_DEFINITION', 'SEQ_EVENT_ALIAS', 'SEQ_PARAMETER_ALIAS', 'SEQ_PUBLISHING_EVENT', 'SEQ_PROCESSING_EVENT', 'SEQ_NAMED_PARAMETER', 'SEQ_RUNTIME_OPTION', 'SEQ_RUNTIME_VALUE', 'SEQ_PUBLIC_PARAMETER', 'SEQ_PORTLET_FILTER', 'SEQ_FILTER_LIFECYCLE', 'SEQ_FILTER_MAPPING', 'SEQ_FILTERED_PORTLET', 'SEQ_PORTLET_LISTENER', 'SEQ_PA_SECURITY_CONSTRAINT', 'SEQ_SECURED_PORTLET', 'SEQ_LOCALE_ENCODING_MAPPING')");
        while (ojbResultSet.next())
        {
            String tableName = ojbResultSet.getString(1);
            int maxKey = ojbResultSet.getInt(3);
            int grabSize = ojbResultSet.getInt(4);
            int version = ojbResultSet.getInt(5);
            switch (sourceVersion)
            {
                case JETSPEED_SCHEMA_VERSION_2_1_3:
                case JETSPEED_SCHEMA_VERSION_2_1_4:
                {
                    if (tableName.equals("SEQ_LOCALIZED_DESCRIPTION") && !localizedDescriptions.isEmpty() && (maxLocalizedDescriptionId > 0))
                    {
                        version = (maxLocalizedDescriptionId+(grabSize-1))/grabSize;
                        maxKey = version*grabSize;
                        localizedDescriptionSeqMigrated = true;
                    }
                }
                break;
            }
            ojbInsertStatement.setString(1, tableName);
            ojbInsertStatement.setString(2, ojbResultSet.getString(2));
            ojbInsertStatement.setInt(3, maxKey);
            ojbInsertStatement.setInt(4, grabSize);
            ojbInsertStatement.setInt(5, version);
            ojbInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        ojbResultSet.close();
        ojbQueryStatement.close();
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                ojbQueryStatement = sourceConnection.createStatement();
                ojbResultSet = ojbQueryStatement.executeQuery("SELECT TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION FROM OJB_HL_SEQ WHERE TABLENAME IN ('SEQ_PORTLET_CONTENT_TYPE', 'SEQ_PREFS_NODE', 'SEQ_PREFS_PROPERTY_VALUE')");
                while (ojbResultSet.next())
                {
                    String tableName = ojbResultSet.getString(1);
                    String migratedTableName = null;
                    if (tableName.equals("SEQ_PORTLET_CONTENT_TYPE"))
                    {
                        migratedTableName = "SEQ_PORTLET_SUPPORTS";
                    }
                    else if (tableName.equals("SEQ_PREFS_NODE"))
                    {
                        migratedTableName = "SEQ_PORTLET_PREFERENCE";
                    }
                    else if (tableName.equals("SEQ_PREFS_PROPERTY_VALUE"))
                    {
                        migratedTableName = "SEQ_PORTLET_PREFERENCE_VALUE";
                    }
                    if (migratedTableName != null)
                    {
                        ojbInsertStatement.setString(1, migratedTableName);
                        ojbInsertStatement.setString(2, ojbResultSet.getString(2));
                        ojbInsertStatement.setInt(3, ojbResultSet.getInt(3));
                        ojbInsertStatement.setInt(4, ojbResultSet.getInt(4));
                        ojbInsertStatement.setInt(5, ojbResultSet.getInt(5));
                        ojbInsertStatement.executeUpdate();
                        rowsMigrated++;
                        migrationListener.rowMigrated(targetConnection);
                    }
                }
                ojbResultSet.close();
                ojbQueryStatement.close();
                
                if (!localizedDescriptionSeqMigrated && !localizedDescriptions.isEmpty() && (maxLocalizedDescriptionId > 0))
                {
                    int grabSize = 20;
                    int version = (maxLocalizedDescriptionId+(grabSize-1))/grabSize;
                    int maxKey = version*grabSize;
                    ojbInsertStatement.setString(1, "SEQ_LOCALIZED_DESCRIPTION");
                    ojbInsertStatement.setString(2, "deprecatedColumn");
                    ojbInsertStatement.setInt(3, maxKey);
                    ojbInsertStatement.setInt(4, grabSize);
                    ojbInsertStatement.setInt(5, version);
                    ojbInsertStatement.executeUpdate();
                    rowsMigrated++;                    
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        ojbInsertStatement.close();
        
        return new JetspeedMigrationResultImpl(rowsMigrated, rowsDropped);
    }
    
    /**
     * Migrated localized description.
     */
    private static class LocalizedDescription
    {
        private int ownerId;
        private String ownerClassName;
        private String description;
        
        private LocalizedDescription(int ownerId, String ownerClassName, String description)
        {
            this.ownerId = ownerId;
            this.ownerClassName = ownerClassName;
            this.description = description;
        }
    }
}
