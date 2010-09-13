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

import org.apache.commons.codec.binary.Base64;
import org.apache.jetspeed.security.PermissionFactory;
import org.apache.jetspeed.security.SecurityDomain;

/**
 * Jetspeed Migration for Security component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedSecurityMigration implements JetspeedMigration
{
    private static final int SYSTEM_SECURITY_DOMAIN_ID = 0;
    private static final int DEFAULT_SECURITY_DOMAIN_ID = 1;
    
    private static final char[] CREDENTIAL_VALUE_SCRAMBLER = "Jestspeed-2 is getting ready for release".toCharArray();
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#getName()
     */
    public String getName()
    {
        return "Security";
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
            securityDomainQueryStatement.executeQuery("SELECT DOMAIN_ID FROM SECURITY_DOMAIN WHERE DOMAIN_ID = 0");
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
        
        // SECURITY_DOMAIN
        int maxSecurityDomainId = -1;
        PreparedStatement securityDomainInsertStatement = targetConnection.prepareStatement("INSERT INTO SECURITY_DOMAIN (DOMAIN_ID, DOMAIN_NAME, REMOTE, ENABLED, OWNER_DOMAIN_ID) VALUES (?, ?, ?, ?, ?)");
        Statement securityDomainQueryStatement = sourceConnection.createStatement();
        securityDomainQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet securityDomainResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                securityDomainInsertStatement.setInt(1, SYSTEM_SECURITY_DOMAIN_ID);
                securityDomainInsertStatement.setString(2, SecurityDomain.SYSTEM_NAME);
                securityDomainInsertStatement.setShort(3, (short)0);
                securityDomainInsertStatement.setShort(4, (short)1);
                securityDomainInsertStatement.setNull(5, Types.INTEGER);
                securityDomainInsertStatement.executeUpdate();
                rowsMigrated++;
                migrationListener.rowMigrated(targetConnection);

                securityDomainInsertStatement.setInt(1, DEFAULT_SECURITY_DOMAIN_ID);
                securityDomainInsertStatement.setString(2, SecurityDomain.DEFAULT_NAME);
                securityDomainInsertStatement.setShort(3, (short)0);
                securityDomainInsertStatement.setShort(4, (short)1);
                securityDomainInsertStatement.setNull(5, Types.INTEGER);
                securityDomainInsertStatement.executeUpdate();
                rowsMigrated++;
                migrationListener.rowMigrated(targetConnection);
                maxSecurityDomainId = DEFAULT_SECURITY_DOMAIN_ID;
                
                securityDomainResultSet = securityDomainQueryStatement.executeQuery("SELECT NAME FROM SSO_SITE");
                while (securityDomainResultSet.next())
                {
                    securityDomainInsertStatement.setInt(1, ++maxSecurityDomainId);
                    securityDomainInsertStatement.setString(2, securityDomainResultSet.getString(1));
                    securityDomainInsertStatement.setShort(3, (short)1);
                    securityDomainInsertStatement.setShort(4, (short)1);
                    securityDomainInsertStatement.setInt(5, DEFAULT_SECURITY_DOMAIN_ID);
                    securityDomainInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                securityDomainResultSet = securityDomainQueryStatement.executeQuery("SELECT DOMAIN_ID, DOMAIN_NAME, REMOTE, ENABLED, OWNER_DOMAIN_ID FROM SECURITY_DOMAIN");
                while (securityDomainResultSet.next())
                {
                    securityDomainInsertStatement.setInt(1, securityDomainResultSet.getInt(1));
                    securityDomainInsertStatement.setString(2, securityDomainResultSet.getString(2));
                    Static.setNullableShort(securityDomainResultSet, 3, securityDomainInsertStatement);
                    Static.setNullableShort(securityDomainResultSet, 4, securityDomainInsertStatement);
                    Static.setNullableInt(securityDomainResultSet, 5, securityDomainInsertStatement);
                    securityDomainInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        securityDomainResultSet.close();
        securityDomainQueryStatement.close();
        securityDomainInsertStatement.close();
        
        // SECURITY_PRINCIPAL
        PreparedStatement securityPrincipalInsertStatement = targetConnection.prepareStatement("INSERT INTO SECURITY_PRINCIPAL (PRINCIPAL_ID, PRINCIPAL_TYPE, PRINCIPAL_NAME, IS_MAPPED, IS_ENABLED, IS_READONLY, IS_REMOVABLE, CREATION_DATE, MODIFIED_DATE, DOMAIN_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement securityPrincipalQueryStatement = sourceConnection.createStatement();
        securityPrincipalQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet securityPrincipalResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                securityPrincipalResultSet = securityPrincipalQueryStatement.executeQuery("SELECT PRINCIPAL_ID, IS_MAPPING_ONLY, IS_ENABLED, FULL_PATH, CREATION_DATE, MODIFIED_DATE FROM SECURITY_PRINCIPAL");
                PreparedStatement ssoSiteNameQueryStatement = sourceConnection.prepareStatement("SELECT NAME FROM SSO_SITE WHERE SITE_ID = ?");
                PreparedStatement domainQueryStatement = targetConnection.prepareStatement("SELECT DOMAIN_ID FROM SECURITY_DOMAIN WHERE DOMAIN_NAME = ?");
                while (securityPrincipalResultSet.next())
                {
                    String fullPath = securityPrincipalResultSet.getString(4);
                    String principalType = null;
                    String principalName = null;
                    int domainId = DEFAULT_SECURITY_DOMAIN_ID;
                    if (fullPath.startsWith("/user/"))
                    {
                        principalType = "user";
                        principalName = fullPath.substring(6);
                    }
                    else if (fullPath.startsWith("/role/"))
                    {
                        principalType = "role";
                        principalName = fullPath.substring(6);
                    }
                    else if (fullPath.startsWith("/group/"))
                    {
                        principalType = "group";
                        principalName = fullPath.substring(7);
                    }
                    else if (fullPath.startsWith("/sso/"))
                    {
                        principalType = "sso_user";
                        principalName = fullPath.substring(fullPath.lastIndexOf("/")+1);
                        int ssoSiteId = Integer.parseInt(fullPath.substring(5, fullPath.indexOf("/", 5)));
                        
                        String ssoSiteName = null;
                        ssoSiteNameQueryStatement.setInt(1, ssoSiteId);
                        ResultSet ssoSiteNameResultSet = ssoSiteNameQueryStatement.executeQuery();
                        if (ssoSiteNameResultSet.next())
                        {
                            ssoSiteName = ssoSiteNameResultSet.getString(1);
                        }
                        else
                        {
                            throw new SQLException("Unable to find SSO site name for id: "+ssoSiteId);
                        }
                        ssoSiteNameResultSet.close();
                        
                        domainQueryStatement.setString(1, ssoSiteName);
                        ResultSet domainResultSet = domainQueryStatement.executeQuery();
                        if (domainResultSet.next())
                        {
                            domainId = domainResultSet.getInt(1);
                        }
                        else
                        {
                            throw new SQLException("Unable to find domain id for SSO site name: "+ssoSiteName);
                        }
                        domainResultSet.close();
                    }
                    if ((principalType != null) && (principalName != null))
                    {
                        securityPrincipalInsertStatement.setInt(1, securityPrincipalResultSet.getInt(1));
                        securityPrincipalInsertStatement.setString(2, principalType);
                        securityPrincipalInsertStatement.setString(3, principalName);
                        securityPrincipalInsertStatement.setShort(4, securityPrincipalResultSet.getShort(2));
                        securityPrincipalInsertStatement.setShort(5, securityPrincipalResultSet.getShort(3));
                        securityPrincipalInsertStatement.setShort(6, (short)0);
                        securityPrincipalInsertStatement.setShort(7, (short)1);
                        securityPrincipalInsertStatement.setTimestamp(8, securityPrincipalResultSet.getTimestamp(5));
                        securityPrincipalInsertStatement.setTimestamp(9, securityPrincipalResultSet.getTimestamp(6));
                        securityPrincipalInsertStatement.setInt(10, domainId);
                        securityPrincipalInsertStatement.executeUpdate();
                        rowsMigrated++;
                        migrationListener.rowMigrated(targetConnection);
                    }
                }
                domainQueryStatement.close();
                ssoSiteNameQueryStatement.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                securityPrincipalResultSet = securityPrincipalQueryStatement.executeQuery("SELECT PRINCIPAL_ID, PRINCIPAL_TYPE, PRINCIPAL_NAME, IS_MAPPED, IS_ENABLED, IS_READONLY, IS_REMOVABLE, CREATION_DATE, MODIFIED_DATE, DOMAIN_ID FROM SECURITY_PRINCIPAL");
                while (securityPrincipalResultSet.next())
                {
                    securityPrincipalInsertStatement.setInt(1, securityPrincipalResultSet.getInt(1));
                    securityPrincipalInsertStatement.setString(2, securityPrincipalResultSet.getString(2));
                    securityPrincipalInsertStatement.setString(3, securityPrincipalResultSet.getString(3));
                    securityPrincipalInsertStatement.setShort(4, securityPrincipalResultSet.getShort(4));
                    securityPrincipalInsertStatement.setShort(5, securityPrincipalResultSet.getShort(5));
                    securityPrincipalInsertStatement.setShort(6, securityPrincipalResultSet.getShort(6));
                    securityPrincipalInsertStatement.setShort(7, securityPrincipalResultSet.getShort(7));
                    securityPrincipalInsertStatement.setTimestamp(8, securityPrincipalResultSet.getTimestamp(8));
                    securityPrincipalInsertStatement.setTimestamp(9, securityPrincipalResultSet.getTimestamp(9));
                    securityPrincipalInsertStatement.setInt(10, securityPrincipalResultSet.getInt(10));
                    securityPrincipalInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        securityPrincipalResultSet.close();
        securityPrincipalQueryStatement.close();
        securityPrincipalInsertStatement.close();
        
        // SECURITY_ATTRIBUTE
        boolean securityAttributeRowsMigrated = false;
        PreparedStatement securityAttributeInsertStatement = targetConnection.prepareStatement("INSERT INTO SECURITY_ATTRIBUTE (ATTR_ID, PRINCIPAL_ID, ATTR_NAME, ATTR_VALUE) VALUES (?, ?, ?, ?)");
        Statement securityAttributeQueryStatement = sourceConnection.createStatement();
        securityAttributeQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet securityAttributeResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                securityAttributeResultSet = securityAttributeQueryStatement.executeQuery("SELECT V.PROPERTY_VALUE_ID, NV.FULL_PATH, V.PROPERTY_NAME, V.PROPERTY_VALUE FROM PREFS_PROPERTY_VALUE V, PREFS_NODE NV WHERE NV.NODE_ID = V.NODE_ID AND NV.FULL_PATH LIKE '%/userinfo'");
                PreparedStatement principalQueryStatement = targetConnection.prepareStatement("SELECT PRINCIPAL_ID FROM SECURITY_PRINCIPAL WHERE PRINCIPAL_TYPE = ? AND PRINCIPAL_NAME = ?");
                while (securityAttributeResultSet.next())
                {
                    String fullPath = securityAttributeResultSet.getString(2);
                    String principalType = null;
                    String principalName = null;
                    if (fullPath.startsWith("/user/"))
                    {
                        principalType = "user";
                        principalName = fullPath.substring(6, fullPath.length()-9);
                    }
                    else if (fullPath.startsWith("/role/"))
                    {
                        principalType = "role";
                        principalName = fullPath.substring(6, fullPath.length()-9);
                    }
                    else if (fullPath.startsWith("/group/"))
                    {
                        principalType = "group";
                        principalName = fullPath.substring(7, fullPath.length()-9);
                    }
                    if ((principalType != null) && (principalName != null))
                    {
                        int principalId = 0;
                        principalQueryStatement.setString(1, principalType);
                        principalQueryStatement.setString(2, principalName);
                        ResultSet principalResultSet = principalQueryStatement.executeQuery();
                        if (principalResultSet.next())
                        {
                            principalId = principalResultSet.getInt(1);
                        }
                        else
                        {
                            throw new SQLException("Unable to find security principal id for principal: "+principalType+"/"+principalName);
                        }
                        principalResultSet.close();
                    
                        securityAttributeInsertStatement.setInt(1, securityAttributeResultSet.getInt(1));
                        securityAttributeInsertStatement.setInt(2, principalId);
                        securityAttributeInsertStatement.setString(3, securityAttributeResultSet.getString(3));
                        securityAttributeInsertStatement.setString(4, securityAttributeResultSet.getString(4));
                        securityAttributeInsertStatement.executeUpdate();
                        rowsMigrated++;
                        migrationListener.rowMigrated(targetConnection);
                        securityAttributeRowsMigrated = true;
                    }
                }                
                principalQueryStatement.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                securityAttributeResultSet = securityAttributeQueryStatement.executeQuery("SELECT ATTR_ID, PRINCIPAL_ID, ATTR_NAME, ATTR_VALUE FROM SECURITY_ATTRIBUTE");
                while (securityAttributeResultSet.next())
                {
                    securityAttributeInsertStatement.setInt(1, securityAttributeResultSet.getInt(1));
                    securityAttributeInsertStatement.setInt(2, securityAttributeResultSet.getInt(2));
                    securityAttributeInsertStatement.setString(3, securityAttributeResultSet.getString(3));
                    securityAttributeInsertStatement.setString(4, securityAttributeResultSet.getString(4));
                    securityAttributeInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        securityAttributeResultSet.close();
        securityAttributeQueryStatement.close();
        securityAttributeInsertStatement.close();
        
        // SECURITY_PRINCIPAL_ASSOC
        PreparedStatement securityPrincipalAssocInsertStatement = targetConnection.prepareStatement("INSERT INTO SECURITY_PRINCIPAL_ASSOC (ASSOC_NAME, FROM_PRINCIPAL_ID, TO_PRINCIPAL_ID) VALUES (?, ?, ?)");
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                Statement securityPrincipalAssocQueryStatement1 = sourceConnection.createStatement();
                securityPrincipalAssocQueryStatement1.setFetchSize(FETCH_SIZE);
                ResultSet securityPrincipalAssocResultSet = securityPrincipalAssocQueryStatement1.executeQuery("SELECT USER_ID, ROLE_ID FROM SECURITY_USER_ROLE");
                while (securityPrincipalAssocResultSet.next())
                {
                    securityPrincipalAssocInsertStatement.setString(1, "isMemberOf");
                    securityPrincipalAssocInsertStatement.setInt(2, securityPrincipalAssocResultSet.getInt(1));
                    securityPrincipalAssocInsertStatement.setInt(3, securityPrincipalAssocResultSet.getInt(2));
                    securityPrincipalAssocInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                securityPrincipalAssocResultSet.close();
                securityPrincipalAssocQueryStatement1.close();

                Statement securityPrincipalAssocQueryStatement2 = sourceConnection.createStatement();
                securityPrincipalAssocQueryStatement2.setFetchSize(FETCH_SIZE);
                securityPrincipalAssocResultSet = securityPrincipalAssocQueryStatement2.executeQuery("SELECT USER_ID, GROUP_ID FROM SECURITY_USER_GROUP");
                while (securityPrincipalAssocResultSet.next())
                {
                    securityPrincipalAssocInsertStatement.setString(1, "isMemberOf");
                    securityPrincipalAssocInsertStatement.setInt(2, securityPrincipalAssocResultSet.getInt(1));
                    securityPrincipalAssocInsertStatement.setInt(3, securityPrincipalAssocResultSet.getInt(2));
                    securityPrincipalAssocInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                securityPrincipalAssocResultSet.close();
                securityPrincipalAssocQueryStatement2.close();

                Statement securityPrincipalAssocQueryStatement3 = sourceConnection.createStatement();
                securityPrincipalAssocQueryStatement3.setFetchSize(FETCH_SIZE);
                securityPrincipalAssocResultSet = securityPrincipalAssocQueryStatement3.executeQuery("SELECT GROUP_ID, ROLE_ID FROM SECURITY_GROUP_ROLE");
                while (securityPrincipalAssocResultSet.next())
                {
                    securityPrincipalAssocInsertStatement.setString(1, "isMemberOf");
                    securityPrincipalAssocInsertStatement.setInt(2, securityPrincipalAssocResultSet.getInt(1));
                    securityPrincipalAssocInsertStatement.setInt(3, securityPrincipalAssocResultSet.getInt(2));
                    securityPrincipalAssocInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                securityPrincipalAssocResultSet.close();
                securityPrincipalAssocQueryStatement3.close();
                
                Statement ssoPrincipalQueryStatement = sourceConnection.createStatement();
                ssoPrincipalQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet ssoPrincipalResultSet = ssoPrincipalQueryStatement.executeQuery("SELECT PRINCIPAL_ID, FULL_PATH FROM SECURITY_PRINCIPAL WHERE FULL_PATH LIKE '/sso/%'");
                PreparedStatement principalQueryStatement = targetConnection.prepareStatement("SELECT PRINCIPAL_ID FROM SECURITY_PRINCIPAL WHERE PRINCIPAL_TYPE = 'user' AND DOMAIN_ID = "+DEFAULT_SECURITY_DOMAIN_ID+" AND PRINCIPAL_NAME = ?");
                while (ssoPrincipalResultSet.next())
                {
                    String fullPath = ssoPrincipalResultSet.getString(2);
                    int ssoPrincipalNameIndex = fullPath.lastIndexOf("/");
                    int principalNameIndex = fullPath.lastIndexOf("/", ssoPrincipalNameIndex-1);
                    String principalName = fullPath.substring(principalNameIndex+1, ssoPrincipalNameIndex);

                    int principalId = 0;
                    principalQueryStatement.setString(1, principalName);
                    ResultSet principalResultSet = principalQueryStatement.executeQuery();
                    if (principalResultSet.next())
                    {
                        principalId = principalResultSet.getInt(1);
                    }
                    else
                    {
                        throw new SQLException("Unable to find security principal id for principal: "+principalName);
                    }
                    principalResultSet.close();
                    
                    int ssoPrincipalId = ssoPrincipalResultSet.getInt(1);
                    securityPrincipalAssocInsertStatement.setString(1, "isRemoteIdFor");
                    securityPrincipalAssocInsertStatement.setInt(2, ssoPrincipalId);
                    securityPrincipalAssocInsertStatement.setInt(3, principalId);
                    securityPrincipalAssocInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                principalQueryStatement.close();
                ssoPrincipalResultSet.close();
                ssoPrincipalQueryStatement.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                Statement securityPrincipalAssocQueryStatement = sourceConnection.createStatement();
                securityPrincipalAssocQueryStatement.setFetchSize(FETCH_SIZE);
                ResultSet securityPrincipalAssocResultSet = securityPrincipalAssocQueryStatement.executeQuery("SELECT ASSOC_NAME, FROM_PRINCIPAL_ID, TO_PRINCIPAL_ID FROM SECURITY_PRINCIPAL_ASSOC");
                while (securityPrincipalAssocResultSet.next())
                {
                    securityPrincipalAssocInsertStatement.setString(1, securityPrincipalAssocResultSet.getString(1));
                    securityPrincipalAssocInsertStatement.setInt(2, securityPrincipalAssocResultSet.getInt(2));
                    securityPrincipalAssocInsertStatement.setInt(3, securityPrincipalAssocResultSet.getInt(3));
                    securityPrincipalAssocInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                securityPrincipalAssocResultSet.close();
                securityPrincipalAssocQueryStatement.close();
            }
            break;
        }
        securityPrincipalAssocInsertStatement.close();
        
        // SECURITY_PERMISSION
        PreparedStatement securityPermissionInsertStatement = targetConnection.prepareStatement("INSERT INTO SECURITY_PERMISSION (PERMISSION_ID, PERMISSION_TYPE, NAME, ACTIONS) VALUES (?, ?, ?, ?)");
        Statement securityPermissionQueryStatement = sourceConnection.createStatement();
        securityPermissionQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet securityPermissionResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                securityPermissionResultSet = securityPermissionQueryStatement.executeQuery("SELECT PERMISSION_ID, CLASSNAME, NAME, ACTIONS FROM SECURITY_PERMISSION");
                while (securityPermissionResultSet.next())
                {
                    String className = securityPermissionResultSet.getString(2);
                    String permissionType = null;
                    if (className.equals("org.apache.jetspeed.security.FolderPermission"))
                    {
                        permissionType = PermissionFactory.FOLDER_PERMISSION;
                    }
                    else if (className.equals("org.apache.jetspeed.security.PagePermission"))
                    {
                        permissionType = PermissionFactory.PAGE_PERMISSION;
                    }
                    else if (className.equals("org.apache.jetspeed.security.PortletPermission"))
                    {
                        permissionType = PermissionFactory.PORTLET_PERMISSION;
                    }
                    else if (className.equals("org.apache.jetspeed.security.FragmentPermission"))
                    {
                        permissionType = PermissionFactory.FRAGMENT_PERMISSION;
                    }
                    if (permissionType != null)
                    {
                        securityPermissionInsertStatement.setInt(1, securityPermissionResultSet.getInt(1));
                        securityPermissionInsertStatement.setString(2, permissionType);
                        String name = securityPermissionResultSet.getString(3);
                        if (permissionType.equals(PermissionFactory.PORTLET_PERMISSION))
                        {
                            String migratedPortlet = PORTLET_NAME_2_1_X_TO_2_2_X_MIGRATION_MAP.get(name);
                            name = ((migratedPortlet != null) ? migratedPortlet : name);
                        }
                        securityPermissionInsertStatement.setString(3, securityPermissionResultSet.getString(3));
                        securityPermissionInsertStatement.setString(4, securityPermissionResultSet.getString(4));
                        securityPermissionInsertStatement.executeUpdate();
                        rowsMigrated++;
                        migrationListener.rowMigrated(targetConnection);
                    }
                }
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                securityPermissionResultSet = securityPermissionQueryStatement.executeQuery("SELECT PERMISSION_ID, PERMISSION_TYPE, NAME, ACTIONS FROM SECURITY_PERMISSION");
                while (securityPermissionResultSet.next())
                {
                    securityPermissionInsertStatement.setInt(1, securityPermissionResultSet.getInt(1));
                    securityPermissionInsertStatement.setString(2, securityPermissionResultSet.getString(2));
                    securityPermissionInsertStatement.setString(3, securityPermissionResultSet.getString(3));
                    securityPermissionInsertStatement.setString(4, securityPermissionResultSet.getString(4));
                    securityPermissionInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        securityPermissionResultSet.close();
        securityPermissionQueryStatement.close();
        securityPermissionInsertStatement.close();
        
        // PRINCIPAL_PERMISSION
        PreparedStatement principalPermissionInsertStatement = targetConnection.prepareStatement("INSERT INTO PRINCIPAL_PERMISSION (PRINCIPAL_ID, PERMISSION_ID) VALUES (?, ?)");
        Statement principalPermissionQueryStatement = sourceConnection.createStatement();
        principalPermissionQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet principalPermissionResultSet = principalPermissionQueryStatement.executeQuery("SELECT PRINCIPAL_ID, PERMISSION_ID FROM PRINCIPAL_PERMISSION");
        while (principalPermissionResultSet.next())
        {
            principalPermissionInsertStatement.setInt(1, principalPermissionResultSet.getInt(1));
            principalPermissionInsertStatement.setInt(2, principalPermissionResultSet.getInt(2));
            principalPermissionInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        principalPermissionResultSet.close();
        principalPermissionQueryStatement.close();
        principalPermissionInsertStatement.close();

        // SECURITY_CREDENTIAL
        PreparedStatement securityCredentialInsertStatement = targetConnection.prepareStatement("INSERT INTO SECURITY_CREDENTIAL (CREDENTIAL_ID, PRINCIPAL_ID, CREDENTIAL_VALUE, TYPE, UPDATE_ALLOWED, IS_STATE_READONLY, UPDATE_REQUIRED, IS_ENCODED, IS_ENABLED, AUTH_FAILURES, IS_EXPIRED, CREATION_DATE, MODIFIED_DATE, PREV_AUTH_DATE, LAST_AUTH_DATE, EXPIRATION_DATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement securityCredentialQueryStatement = sourceConnection.createStatement();
        securityCredentialQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet securityCredentialResultSet = null;
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                securityCredentialResultSet = securityCredentialQueryStatement.executeQuery("SELECT CREDENTIAL_ID, PRINCIPAL_ID, COLUMN_VALUE, TYPE, UPDATE_REQUIRED, IS_ENCODED, IS_ENABLED, AUTH_FAILURES, IS_EXPIRED, CREATION_DATE, MODIFIED_DATE, PREV_AUTH_DATE, LAST_AUTH_DATE, EXPIRATION_DATE FROM SECURITY_CREDENTIAL");
                PreparedStatement principalTypeQueryStatement = targetConnection.prepareStatement("SELECT PRINCIPAL_TYPE FROM SECURITY_PRINCIPAL WHERE PRINCIPAL_ID = ?");
                while (securityCredentialResultSet.next())
                {
                    int principalId = securityCredentialResultSet.getInt(2);
                    String principalType = null;
                    principalTypeQueryStatement.setInt(1, principalId);
                    ResultSet principalTypeResultSet = principalTypeQueryStatement.executeQuery();
                    if (principalTypeResultSet.next())
                    {
                        principalType = principalTypeResultSet.getString(1);
                    }
                    else
                    {
                        throw new SQLException("Unable to find principal type for principal id: "+principalId);
                    }
                    principalTypeResultSet.close();

                    String credentialValue = securityCredentialResultSet.getString(3);
                    boolean encoded = (securityCredentialResultSet.getShort(6) != 0);
                    if (principalType.equals("sso_user") && !encoded)
                    {
                        credentialValue = unscrambleCredentialValue(credentialValue);
                    }
                    
                    securityCredentialInsertStatement.setInt(1, securityCredentialResultSet.getInt(1));
                    securityCredentialInsertStatement.setInt(2, principalId);
                    securityCredentialInsertStatement.setString(3, credentialValue);
                    securityCredentialInsertStatement.setShort(4, securityCredentialResultSet.getShort(4));
                    securityCredentialInsertStatement.setShort(5, (short)1);
                    securityCredentialInsertStatement.setShort(6, (short)0);
                    securityCredentialInsertStatement.setShort(7, securityCredentialResultSet.getShort(5));
                    securityCredentialInsertStatement.setShort(8, (encoded ? (short)1 : (short)0));
                    securityCredentialInsertStatement.setShort(9, securityCredentialResultSet.getShort(7));
                    securityCredentialInsertStatement.setShort(10, securityCredentialResultSet.getShort(8));
                    securityCredentialInsertStatement.setShort(11, securityCredentialResultSet.getShort(9));
                    securityCredentialInsertStatement.setTimestamp(12, securityCredentialResultSet.getTimestamp(10));
                    securityCredentialInsertStatement.setTimestamp(13, securityCredentialResultSet.getTimestamp(11));
                    securityCredentialInsertStatement.setTimestamp(14, securityCredentialResultSet.getTimestamp(12));
                    securityCredentialInsertStatement.setTimestamp(15, securityCredentialResultSet.getTimestamp(13));
                    securityCredentialInsertStatement.setDate(16, securityCredentialResultSet.getDate(14));
                    securityCredentialInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                principalTypeQueryStatement.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                securityCredentialResultSet = securityCredentialQueryStatement.executeQuery("SELECT CREDENTIAL_ID, PRINCIPAL_ID, CREDENTIAL_VALUE, TYPE, UPDATE_ALLOWED, IS_STATE_READONLY, UPDATE_REQUIRED, IS_ENCODED, IS_ENABLED, AUTH_FAILURES, IS_EXPIRED, CREATION_DATE, MODIFIED_DATE, PREV_AUTH_DATE, LAST_AUTH_DATE, EXPIRATION_DATE FROM SECURITY_CREDENTIAL");
                while (securityCredentialResultSet.next())
                {
                    securityCredentialInsertStatement.setInt(1, securityCredentialResultSet.getInt(1));
                    securityCredentialInsertStatement.setInt(2, securityCredentialResultSet.getInt(2));
                    securityCredentialInsertStatement.setString(3, securityCredentialResultSet.getString(3));
                    securityCredentialInsertStatement.setShort(4, securityCredentialResultSet.getShort(4));
                    securityCredentialInsertStatement.setShort(5, securityCredentialResultSet.getShort(5));
                    securityCredentialInsertStatement.setShort(6, securityCredentialResultSet.getShort(6));
                    securityCredentialInsertStatement.setShort(7, securityCredentialResultSet.getShort(7));
                    securityCredentialInsertStatement.setShort(8, securityCredentialResultSet.getShort(8));
                    securityCredentialInsertStatement.setShort(9, securityCredentialResultSet.getShort(9));
                    securityCredentialInsertStatement.setShort(10, securityCredentialResultSet.getShort(10));
                    securityCredentialInsertStatement.setShort(11, securityCredentialResultSet.getShort(11));
                    securityCredentialInsertStatement.setTimestamp(12, securityCredentialResultSet.getTimestamp(12));
                    securityCredentialInsertStatement.setTimestamp(13, securityCredentialResultSet.getTimestamp(13));
                    securityCredentialInsertStatement.setTimestamp(14, securityCredentialResultSet.getTimestamp(14));
                    securityCredentialInsertStatement.setTimestamp(15, securityCredentialResultSet.getTimestamp(15));
                    securityCredentialInsertStatement.setDate(16, securityCredentialResultSet.getDate(16));
                    securityCredentialInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
            }
            break;
        }
        securityCredentialResultSet.close();
        securityCredentialQueryStatement.close();
        securityCredentialInsertStatement.close();

        // OJB_HL_SEQ
        PreparedStatement ojbInsertStatement = targetConnection.prepareStatement("INSERT INTO OJB_HL_SEQ (TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION) VALUES (?, ?, ?, ?, ?)");
        Statement ojbQueryStatement = sourceConnection.createStatement();
        ResultSet ojbResultSet = ojbQueryStatement.executeQuery("SELECT TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION FROM OJB_HL_SEQ WHERE TABLENAME IN ('SEQ_SECURITY_PRINCIPAL', 'SEQ_SECURITY_ATTRIBUTE', 'SEQ_SECURITY_PERMISSION', 'SEQ_SECURITY_CREDENTIAL', 'SEQ_SECURITY_DOMAIN')");
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
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                ojbQueryStatement = sourceConnection.createStatement();
                ojbResultSet = ojbQueryStatement.executeQuery("SELECT TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION FROM OJB_HL_SEQ WHERE TABLENAME IN ('SEQ_PREFS_PROPERTY_VALUE')");
                while (ojbResultSet.next())
                {
                    String tableName = ojbResultSet.getString(1);
                    String migratedTableName = null;
                    if (tableName.equals("SEQ_PREFS_PROPERTY_VALUE"))
                    {
                        if (securityAttributeRowsMigrated)
                        {
                            migratedTableName = "SEQ_SECURITY_ATTRIBUTE";
                        }
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

                if (maxSecurityDomainId > -1)
                {
                    int grabSize = 20;
                    int version = (maxSecurityDomainId+(grabSize-1))/grabSize;
                    int maxKey = version*grabSize;
                    ojbInsertStatement.setString(1, "SEQ_SECURITY_DOMAIN");
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
        
        return new JetspeedMigrationResultImpl(rowsMigrated);
    }
    
    /**
     * Unscramble a 2.1.X scrambled SSO remote password.
     * 
     * @param credentialValue scrambled credential value
     * @return clear text password
     */
    private String unscrambleCredentialValue(String credentialValue)
    {
        byte[] valueBytes = credentialValue.getBytes();
        valueBytes = Base64.decodeBase64(valueBytes);
        char[] valueChars = new String(valueBytes).toCharArray();
        int len = Math.min(valueChars.length, CREDENTIAL_VALUE_SCRAMBLER.length);
        char[] unscrambledValueChars = new char[len];
        for (int i = 0; (i < len); i++)
        {
            unscrambledValueChars[i] = (char)(valueChars[i]^CREDENTIAL_VALUE_SCRAMBLER[i]);
        }
        return new String(unscrambledValueChars);
    }
}
