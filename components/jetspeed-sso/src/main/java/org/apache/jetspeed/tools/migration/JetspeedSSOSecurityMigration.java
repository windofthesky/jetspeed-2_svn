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

/**
 * Jetspeed Migration for SSO Security component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedSSOSecurityMigration implements JetspeedMigration
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#getName()
     */
    public String getName()
    {
        return "SSO Security";
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#detectSourceVersion(java.sql.Connection, int)
     */
    public int detectSourceVersion(Connection sourceConnection, int sourceVersion) throws SQLException
    {
        // detect version of security schema
        int sourceSecurityVersion = JETSPEED_SCHEMA_VERSION_2_1_3;
        try
        {
            Statement securityDomainQueryStatement = sourceConnection.createStatement();
            securityDomainQueryStatement.executeQuery("SELECT DOMAIN_ID FROM SSO_SITE WHERE SITE_ID = 0");
            sourceSecurityVersion = JETSPEED_SCHEMA_VERSION_2_2_0;
        }
        catch (SQLException sqle)
        {
        }
        return ((sourceSecurityVersion >= sourceVersion) ? sourceSecurityVersion : sourceVersion);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#migrate(java.sql.Connection, int, java.sql.Connection, org.apache.jetspeed.tools.migration.JetspeedMigrationListener)
     */
    public JetspeedMigrationResult migrate(Connection sourceConnection, int sourceVersion, Connection targetConnection, JetspeedMigrationListener migrationListener) throws SQLException
    {
        int rowsMigrated = 0;
        
        // SSO_SITE
        PreparedStatement ssoSiteInsertStatement = targetConnection.prepareStatement("INSERT INTO SSO_SITE (SITE_ID, NAME, URL, ALLOW_USER_SET, REQUIRES_CERTIFICATE, CHALLENGE_RESPONSE_AUTH, FORM_AUTH, FORM_USER_FIELD, FORM_PWD_FIELD, REALM, DOMAIN_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement ssoSiteQueryStatement = sourceConnection.createStatement();
        ssoSiteQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet ssoSiteResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                ssoSiteResultSet = ssoSiteQueryStatement.executeQuery("SELECT SITE_ID, NAME, URL, ALLOW_USER_SET, REQUIRES_CERTIFICATE, CHALLENGE_RESPONSE_AUTH, FORM_AUTH, FORM_USER_FIELD, FORM_PWD_FIELD, REALM FROM SSO_SITE");
                PreparedStatement domainQueryStatement = targetConnection.prepareStatement("SELECT DOMAIN_ID FROM SECURITY_DOMAIN WHERE DOMAIN_NAME = ?");
                while (ssoSiteResultSet.next())
                {
                    String name = ssoSiteResultSet.getString(2);
                    int domainId = 0;
                    domainQueryStatement.setString(1, name);
                    ResultSet domainResultSet = domainQueryStatement.executeQuery();
                    if (domainResultSet.next())
                    {
                        domainId = domainResultSet.getInt(1);
                    }
                    else
                    {
                        throw new SQLException("Unable to find security domain id for SSO site: "+name);
                    }
                    domainResultSet.close();
                    
                    ssoSiteInsertStatement.setInt(1, ssoSiteResultSet.getInt(1));
                    ssoSiteInsertStatement.setString(2, name);
                    ssoSiteInsertStatement.setString(3, ssoSiteResultSet.getString(3));
                    Static.setNullableShort(ssoSiteResultSet, 4, ssoSiteInsertStatement);
                    Static.setNullableShort(ssoSiteResultSet, 5, ssoSiteInsertStatement);
                    Static.setNullableShort(ssoSiteResultSet, 6, ssoSiteInsertStatement);
                    Static.setNullableShort(ssoSiteResultSet, 7, ssoSiteInsertStatement);
                    ssoSiteInsertStatement.setString(8, ssoSiteResultSet.getString(8));
                    ssoSiteInsertStatement.setString(9, ssoSiteResultSet.getString(9));
                    ssoSiteInsertStatement.setString(10, ssoSiteResultSet.getString(10));
                    ssoSiteInsertStatement.setInt(11, domainId);
                    ssoSiteInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                domainQueryStatement.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                ssoSiteResultSet = ssoSiteQueryStatement.executeQuery("SELECT SITE_ID, NAME, URL, ALLOW_USER_SET, REQUIRES_CERTIFICATE, CHALLENGE_RESPONSE_AUTH, FORM_AUTH, FORM_USER_FIELD, FORM_PWD_FIELD, REALM, DOMAIN_ID FROM SSO_SITE");
                while (ssoSiteResultSet.next())
                {
                    ssoSiteInsertStatement.setInt(1, ssoSiteResultSet.getInt(1));
                    ssoSiteInsertStatement.setString(2, ssoSiteResultSet.getString(2));
                    ssoSiteInsertStatement.setString(3, ssoSiteResultSet.getString(3));
                    Static.setNullableShort(ssoSiteResultSet, 4, ssoSiteInsertStatement);
                    Static.setNullableShort(ssoSiteResultSet, 5, ssoSiteInsertStatement);
                    Static.setNullableShort(ssoSiteResultSet, 6, ssoSiteInsertStatement);
                    Static.setNullableShort(ssoSiteResultSet, 7, ssoSiteInsertStatement);
                    ssoSiteInsertStatement.setString(8, ssoSiteResultSet.getString(8));
                    ssoSiteInsertStatement.setString(9, ssoSiteResultSet.getString(9));
                    ssoSiteInsertStatement.setString(10, ssoSiteResultSet.getString(10));
                    ssoSiteInsertStatement.setInt(11, ssoSiteResultSet.getInt(11));
                    ssoSiteInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        ssoSiteResultSet.close();
        ssoSiteQueryStatement.close();
        ssoSiteInsertStatement.close();
        
        // OJB_HL_SEQ
        PreparedStatement ojbInsertStatement = targetConnection.prepareStatement("INSERT INTO OJB_HL_SEQ (TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION) VALUES (?, ?, ?, ?, ?)");
        Statement ojbQueryStatement = sourceConnection.createStatement();
        ResultSet ojbResultSet = ojbQueryStatement.executeQuery("SELECT TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION FROM OJB_HL_SEQ WHERE TABLENAME IN ('SEQ_SSO_SITE')");
        while (ojbResultSet.next())
        {
            ojbInsertStatement.setString(1, ojbResultSet.getString(1));
            ojbInsertStatement.setString(2, ojbResultSet.getString(2));
            ojbInsertStatement.setInt(3, ojbResultSet.getInt(3));
            ojbInsertStatement.setInt(4, ojbResultSet.getInt(4));
            ojbInsertStatement.setInt(5, ojbResultSet.getInt(5));
            ojbInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        ojbResultSet.close();
        ojbQueryStatement.close();
        ojbInsertStatement.close();
        
        return new JetspeedMigrationResultImpl(rowsMigrated);
    }
}
