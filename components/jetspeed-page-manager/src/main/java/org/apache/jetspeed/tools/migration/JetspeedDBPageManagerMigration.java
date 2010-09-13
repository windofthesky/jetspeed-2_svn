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

/**
 * Jetspeed Migration for Database Page Manager component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedDBPageManagerMigration implements JetspeedMigration
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#getName()
     */
    public String getName()
    {
        return "DB Page Manager";
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#detectSourceVersion(java.sql.Connection, int)
     */
    public int detectSourceVersion(Connection sourceConnection, int sourceVersion) throws SQLException
    {
        // detect version of page manager schema
        int sourcePageManagerVersion = JETSPEED_SCHEMA_VERSION_2_1_3;
        try
        {
            Statement fragmentPropQueryStatement = sourceConnection.createStatement();
            fragmentPropQueryStatement.executeQuery("SELECT PROP_ID FROM FRAGMENT_PROP WHERE PROP_ID = 0");
            sourcePageManagerVersion = JETSPEED_SCHEMA_VERSION_2_2_1;
        }
        catch (SQLException sqle)
        {
        }
        try
        {
            Statement fragmentQueryStatement = sourceConnection.createStatement();
            fragmentQueryStatement.executeQuery("SELECT FRAGMENT_STRING_ID FROM FRAGMENT WHERE FRAGMENT_ID = 0");
            if (sourceVersion > JETSPEED_SCHEMA_VERSION_2_1_4)
            {
                sourcePageManagerVersion = JETSPEED_SCHEMA_VERSION_2_2_1;
            }
            else
            {
                sourcePageManagerVersion = JETSPEED_SCHEMA_VERSION_2_1_4;
            }
        }
        catch (SQLException sqle)
        {
        }
        return ((sourcePageManagerVersion >= sourceVersion) ? sourcePageManagerVersion : sourceVersion);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#migrate(java.sql.Connection, int, java.sql.Connection, org.apache.jetspeed.tools.migration.JetspeedMigrationListener)
     */
    public JetspeedMigrationResult migrate(Connection sourceConnection, int sourceVersion, Connection targetConnection, JetspeedMigrationListener migrationListener) throws SQLException
    {
        int rowsMigrated = 0;
        
        // FOLDER
        PreparedStatement folderInsertStatement = targetConnection.prepareStatement("INSERT INTO FOLDER (FOLDER_ID, PARENT_ID, PATH, NAME, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, DEFAULT_PAGE_NAME, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement folderQueryStatement = sourceConnection.createStatement();
        folderQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet folderResultSet = folderQueryStatement.executeQuery("SELECT FOLDER_ID, PARENT_ID, PATH, NAME, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, DEFAULT_PAGE_NAME, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL FROM FOLDER");
        while (folderResultSet.next())
        {
            folderInsertStatement.setInt(1, folderResultSet.getInt(1));
            Static.setNullableInt(folderResultSet, 2, folderInsertStatement);
            folderInsertStatement.setString(3, folderResultSet.getString(3));
            folderInsertStatement.setString(4, folderResultSet.getString(4));
            folderInsertStatement.setString(5, folderResultSet.getString(5));
            folderInsertStatement.setString(6, folderResultSet.getString(6));
            folderInsertStatement.setShort(7, folderResultSet.getShort(7));
            folderInsertStatement.setString(8, folderResultSet.getString(8));
            String decorator = folderResultSet.getString(9);
            switch (sourceVersion)
            {
                case JETSPEED_SCHEMA_VERSION_2_1_3:
                case JETSPEED_SCHEMA_VERSION_2_1_4:
                {
                    String migratedDecorator = LAYOUT_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.get(decorator);
                    decorator = ((migratedDecorator != null) ? migratedDecorator : decorator);
                }
                break;
            }
            folderInsertStatement.setString(9, decorator);
            decorator = folderResultSet.getString(10);
            switch (sourceVersion)
            {
                case JETSPEED_SCHEMA_VERSION_2_1_3:
                case JETSPEED_SCHEMA_VERSION_2_1_4:
                {
                    String migratedDecorator = PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.get(decorator);
                    decorator = ((migratedDecorator != null) ? migratedDecorator : decorator);
                }
                break;
            }
            folderInsertStatement.setString(10, decorator);
            folderInsertStatement.setString(11, folderResultSet.getString(11));
            folderInsertStatement.setString(12, folderResultSet.getString(12));
            folderInsertStatement.setString(13, folderResultSet.getString(13));
            folderInsertStatement.setString(14, folderResultSet.getString(14));
            folderInsertStatement.setString(15, folderResultSet.getString(15));
            folderInsertStatement.setString(16, folderResultSet.getString(16));
            folderInsertStatement.setString(17, folderResultSet.getString(17));
            folderInsertStatement.setString(18, folderResultSet.getString(18));
            folderInsertStatement.setString(19, folderResultSet.getString(19));
            folderInsertStatement.setString(20, folderResultSet.getString(20));
            folderInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        folderResultSet.close();
        folderQueryStatement.close();
        folderInsertStatement.close();

        // FOLDER_METADATA
        PreparedStatement folderMetadataInsertStatement = targetConnection.prepareStatement("INSERT INTO FOLDER_METADATA (METADATA_ID, FOLDER_ID, NAME, LOCALE, VALUE) VALUES (?, ?, ?, ?, ?)");
        Statement folderMetadataQueryStatement = sourceConnection.createStatement();
        folderMetadataQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet folderMetadataResultSet = folderMetadataQueryStatement.executeQuery("SELECT METADATA_ID, FOLDER_ID, NAME, LOCALE, VALUE FROM FOLDER_METADATA");
        while (folderMetadataResultSet.next())
        {
            folderMetadataInsertStatement.setInt(1, folderMetadataResultSet.getInt(1));
            folderMetadataInsertStatement.setInt(2, folderMetadataResultSet.getInt(2));
            folderMetadataInsertStatement.setString(3, folderMetadataResultSet.getString(3));
            folderMetadataInsertStatement.setString(4, folderMetadataResultSet.getString(4));
            folderMetadataInsertStatement.setString(5, folderMetadataResultSet.getString(5));
            folderMetadataInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        folderMetadataResultSet.close();
        folderMetadataQueryStatement.close();
        folderMetadataInsertStatement.close();

        // FOLDER_CONSTRAINT
        PreparedStatement folderConstraintInsertStatement = targetConnection.prepareStatement("INSERT INTO FOLDER_CONSTRAINT (CONSTRAINT_ID, FOLDER_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL) VALUES (?, ?, ?, ?, ?, ?, ?)");
        Statement folderConstraintQueryStatement = sourceConnection.createStatement();
        folderConstraintQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet folderConstraintResultSet = folderConstraintQueryStatement.executeQuery("SELECT CONSTRAINT_ID, FOLDER_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL FROM FOLDER_CONSTRAINT");
        while (folderConstraintResultSet.next())
        {
            folderConstraintInsertStatement.setInt(1, folderConstraintResultSet.getInt(1));
            folderConstraintInsertStatement.setInt(2, folderConstraintResultSet.getInt(2));
            folderConstraintInsertStatement.setInt(3, folderConstraintResultSet.getInt(3));
            folderConstraintInsertStatement.setString(4, folderConstraintResultSet.getString(4));
            folderConstraintInsertStatement.setString(5, folderConstraintResultSet.getString(5));
            folderConstraintInsertStatement.setString(6, folderConstraintResultSet.getString(6));
            folderConstraintInsertStatement.setString(7, folderConstraintResultSet.getString(7));
            folderConstraintInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        folderConstraintResultSet.close();
        folderConstraintQueryStatement.close();
        folderConstraintInsertStatement.close();

        // FOLDER_CONSTRAINTS_REF
        PreparedStatement folderConstraintsRefInsertStatement = targetConnection.prepareStatement("INSERT INTO FOLDER_CONSTRAINTS_REF (CONSTRAINTS_REF_ID, FOLDER_ID, APPLY_ORDER, NAME) VALUES (?, ?, ?, ?)");
        Statement folderConstraintsRefQueryStatement = sourceConnection.createStatement();
        folderConstraintsRefQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet folderConstraintsRefResultSet = folderConstraintsRefQueryStatement.executeQuery("SELECT CONSTRAINTS_REF_ID, FOLDER_ID, APPLY_ORDER, NAME FROM FOLDER_CONSTRAINTS_REF");
        while (folderConstraintsRefResultSet.next())
        {
            folderConstraintsRefInsertStatement.setInt(1, folderConstraintsRefResultSet.getInt(1));
            folderConstraintsRefInsertStatement.setInt(2, folderConstraintsRefResultSet.getInt(2));
            folderConstraintsRefInsertStatement.setInt(3, folderConstraintsRefResultSet.getInt(3));
            folderConstraintsRefInsertStatement.setString(4, folderConstraintsRefResultSet.getString(4));
            folderConstraintsRefInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        folderConstraintsRefResultSet.close();
        folderConstraintsRefQueryStatement.close();
        folderConstraintsRefInsertStatement.close();

        // FOLDER_ORDER
        PreparedStatement folderOrderInsertStatement = targetConnection.prepareStatement("INSERT INTO FOLDER_ORDER (ORDER_ID, FOLDER_ID, SORT_ORDER, NAME) VALUES (?, ?, ?, ?)");
        Statement folderOrderQueryStatement = sourceConnection.createStatement();
        folderOrderQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet folderOrderResultSet = folderOrderQueryStatement.executeQuery("SELECT ORDER_ID, FOLDER_ID, SORT_ORDER, NAME FROM FOLDER_ORDER");
        while (folderOrderResultSet.next())
        {
            folderOrderInsertStatement.setInt(1, folderOrderResultSet.getInt(1));
            folderOrderInsertStatement.setInt(2, folderOrderResultSet.getInt(2));
            folderOrderInsertStatement.setInt(3, folderOrderResultSet.getInt(3));
            folderOrderInsertStatement.setString(4, folderOrderResultSet.getString(4));
            folderOrderInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        folderOrderResultSet.close();
        folderOrderQueryStatement.close();
        folderOrderInsertStatement.close();

        // FOLDER_MENU
        PreparedStatement folderMenuInsertStatement = targetConnection.prepareStatement("INSERT INTO FOLDER_MENU (MENU_ID, CLASS_NAME, PARENT_ID, FOLDER_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement folderMenuQueryStatement = sourceConnection.createStatement();
        folderMenuQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet folderMenuResultSet = folderMenuQueryStatement.executeQuery("SELECT MENU_ID, CLASS_NAME, PARENT_ID, FOLDER_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST FROM FOLDER_MENU");
        while (folderMenuResultSet.next())
        {
            folderMenuInsertStatement.setInt(1, folderMenuResultSet.getInt(1));
            folderMenuInsertStatement.setString(2, folderMenuResultSet.getString(2));
            Static.setNullableInt(folderMenuResultSet, 3, folderMenuInsertStatement);
            Static.setNullableInt(folderMenuResultSet, 4, folderMenuInsertStatement);
            Static.setNullableInt(folderMenuResultSet, 5, folderMenuInsertStatement);
            folderMenuInsertStatement.setString(6, folderMenuResultSet.getString(6));
            folderMenuInsertStatement.setString(7, folderMenuResultSet.getString(7));
            folderMenuInsertStatement.setString(8, folderMenuResultSet.getString(8));
            folderMenuInsertStatement.setString(9, folderMenuResultSet.getString(9));
            folderMenuInsertStatement.setString(10, folderMenuResultSet.getString(10));
            Static.setNullableInt(folderMenuResultSet, 11, folderMenuInsertStatement);
            Static.setNullableShort(folderMenuResultSet, 12, folderMenuInsertStatement);
            Static.setNullableShort(folderMenuResultSet, 13, folderMenuInsertStatement);
            folderMenuInsertStatement.setString(14, folderMenuResultSet.getString(14));
            folderMenuInsertStatement.setString(15, folderMenuResultSet.getString(15));
            folderMenuInsertStatement.setString(16, folderMenuResultSet.getString(16));
            Static.setNullableShort(folderMenuResultSet, 17, folderMenuInsertStatement);
            folderMenuInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        folderMenuResultSet.close();
        folderMenuQueryStatement.close();
        folderMenuInsertStatement.close();

        // FOLDER_MENU_METADATA
        PreparedStatement folderMenuMetadataInsertStatement = targetConnection.prepareStatement("INSERT INTO FOLDER_MENU_METADATA (METADATA_ID, MENU_ID, NAME, LOCALE, VALUE) VALUES (?, ?, ?, ?, ?)");
        Statement folderMenuMetadataQueryStatement = sourceConnection.createStatement();
        folderMenuMetadataQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet folderMenuMetadataResultSet = folderMenuMetadataQueryStatement.executeQuery("SELECT METADATA_ID, MENU_ID, NAME, LOCALE, VALUE FROM FOLDER_MENU_METADATA");
        while (folderMenuMetadataResultSet.next())
        {
            folderMenuMetadataInsertStatement.setInt(1, folderMenuMetadataResultSet.getInt(1));
            folderMenuMetadataInsertStatement.setInt(2, folderMenuMetadataResultSet.getInt(2));
            folderMenuMetadataInsertStatement.setString(3, folderMenuMetadataResultSet.getString(3));
            folderMenuMetadataInsertStatement.setString(4, folderMenuMetadataResultSet.getString(4));
            folderMenuMetadataInsertStatement.setString(5, folderMenuMetadataResultSet.getString(5));
            folderMenuMetadataInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        folderMenuMetadataResultSet.close();
        folderMenuMetadataQueryStatement.close();
        folderMenuMetadataInsertStatement.close();

        // PAGE
        PreparedStatement pageInsertStatement = targetConnection.prepareStatement("INSERT INTO PAGE (PAGE_ID, CLASS_NAME, PARENT_ID, PATH, CONTENT_TYPE, IS_INHERITABLE, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement pageQueryStatement = sourceConnection.createStatement();
        pageQueryStatement.setFetchSize(FETCH_SIZE);
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            {
                ResultSet pageResultSet = pageQueryStatement.executeQuery("SELECT PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL FROM PAGE");
                while (pageResultSet.next())
                {
                    pageInsertStatement.setInt(1, pageResultSet.getInt(1));
                    pageInsertStatement.setString(2, "org.apache.jetspeed.om.page.impl.PageImpl");
                    pageInsertStatement.setInt(3, pageResultSet.getInt(2));
                    pageInsertStatement.setString(4, pageResultSet.getString(3));
                    pageInsertStatement.setNull(5, Types.VARCHAR);
                    pageInsertStatement.setNull(6, Types.SMALLINT);
                    pageInsertStatement.setString(7, pageResultSet.getString(4));
                    pageInsertStatement.setString(8, pageResultSet.getString(5));
                    pageInsertStatement.setString(9, pageResultSet.getString(6));
                    pageInsertStatement.setString(10, pageResultSet.getString(7));
                    Static.setNullableShort(pageResultSet, 8, pageInsertStatement, 11);
                    pageInsertStatement.setString(12, pageResultSet.getString(9));
                    String decorator = pageResultSet.getString(10);
                    switch (sourceVersion)
                    {
                        case JETSPEED_SCHEMA_VERSION_2_1_3:
                        case JETSPEED_SCHEMA_VERSION_2_1_4:
                        {
                            String migratedDecorator = LAYOUT_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.get(decorator);
                            decorator = ((migratedDecorator != null) ? migratedDecorator : decorator);
                        }
                        break;
                    }
                    pageInsertStatement.setString(13, decorator);
                    decorator = pageResultSet.getString(11);
                    switch (sourceVersion)
                    {
                        case JETSPEED_SCHEMA_VERSION_2_1_3:
                        case JETSPEED_SCHEMA_VERSION_2_1_4:
                        {
                            String migratedDecorator = PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.get(decorator);
                            decorator = ((migratedDecorator != null) ? migratedDecorator : decorator);
                        }
                        break;
                    }
                    pageInsertStatement.setString(14, decorator);
                    pageInsertStatement.setString(15, pageResultSet.getString(12));
                    pageInsertStatement.setString(16, pageResultSet.getString(13));
                    pageInsertStatement.setString(17, pageResultSet.getString(14));
                    pageInsertStatement.setString(18, pageResultSet.getString(15));
                    pageInsertStatement.setString(19, pageResultSet.getString(16));
                    pageInsertStatement.setString(20, pageResultSet.getString(17));
                    pageInsertStatement.setString(21, pageResultSet.getString(18));
                    pageInsertStatement.setString(22, pageResultSet.getString(19));
                    pageInsertStatement.setString(23, pageResultSet.getString(20));
                    pageInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                pageResultSet.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                ResultSet pageResultSet = pageQueryStatement.executeQuery("SELECT PAGE_ID, CLASS_NAME, PARENT_ID, PATH, CONTENT_TYPE, IS_INHERITABLE, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL FROM PAGE");
                while (pageResultSet.next())
                {
                    pageInsertStatement.setInt(1, pageResultSet.getInt(1));
                    pageInsertStatement.setString(2, pageResultSet.getString(2));
                    pageInsertStatement.setInt(3, pageResultSet.getInt(3));
                    pageInsertStatement.setString(4, pageResultSet.getString(4));
                    pageInsertStatement.setString(5, pageResultSet.getString(5));
                    Static.setNullableShort(pageResultSet, 6, pageInsertStatement);
                    pageInsertStatement.setString(7, pageResultSet.getString(7));
                    pageInsertStatement.setString(8, pageResultSet.getString(8));
                    pageInsertStatement.setString(9, pageResultSet.getString(9));
                    pageInsertStatement.setString(10, pageResultSet.getString(10));
                    Static.setNullableShort(pageResultSet, 11, pageInsertStatement);
                    pageInsertStatement.setString(12, pageResultSet.getString(12));
                    pageInsertStatement.setString(13, pageResultSet.getString(13));
                    pageInsertStatement.setString(14, pageResultSet.getString(14));
                    pageInsertStatement.setString(15, pageResultSet.getString(15));
                    pageInsertStatement.setString(16, pageResultSet.getString(16));
                    pageInsertStatement.setString(17, pageResultSet.getString(17));
                    pageInsertStatement.setString(18, pageResultSet.getString(18));
                    pageInsertStatement.setString(19, pageResultSet.getString(19));
                    pageInsertStatement.setString(20, pageResultSet.getString(20));
                    pageInsertStatement.setString(21, pageResultSet.getString(21));
                    pageInsertStatement.setString(22, pageResultSet.getString(22));
                    pageInsertStatement.setString(23, pageResultSet.getString(23));
                    pageInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                pageResultSet.close();
            }
            break;
        }
        pageQueryStatement.close();
        pageInsertStatement.close();

        // PAGE_METADATA
        PreparedStatement pageMetadataInsertStatement = targetConnection.prepareStatement("INSERT INTO PAGE_METADATA (METADATA_ID, PAGE_ID, NAME, LOCALE, VALUE) VALUES (?, ?, ?, ?, ?)");
        Statement pageMetadataQueryStatement = sourceConnection.createStatement();
        pageMetadataQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet pageMetadataResultSet = pageMetadataQueryStatement.executeQuery("SELECT METADATA_ID, PAGE_ID, NAME, LOCALE, VALUE FROM PAGE_METADATA");
        while (pageMetadataResultSet.next())
        {
            pageMetadataInsertStatement.setInt(1, pageMetadataResultSet.getInt(1));
            pageMetadataInsertStatement.setInt(2, pageMetadataResultSet.getInt(2));
            pageMetadataInsertStatement.setString(3, pageMetadataResultSet.getString(3));
            pageMetadataInsertStatement.setString(4, pageMetadataResultSet.getString(4));
            pageMetadataInsertStatement.setString(5, pageMetadataResultSet.getString(5));
            pageMetadataInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        pageMetadataResultSet.close();
        pageMetadataQueryStatement.close();
        pageMetadataInsertStatement.close();

        // PAGE_CONSTRAINT
        PreparedStatement pageConstraintInsertStatement = targetConnection.prepareStatement("INSERT INTO PAGE_CONSTRAINT (CONSTRAINT_ID, PAGE_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL) VALUES (?, ?, ?, ?, ?, ?, ?)");
        Statement pageConstraintQueryStatement = sourceConnection.createStatement();
        pageConstraintQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet pageConstraintResultSet = pageConstraintQueryStatement.executeQuery("SELECT CONSTRAINT_ID, PAGE_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL FROM PAGE_CONSTRAINT");
        while (pageConstraintResultSet.next())
        {
            pageConstraintInsertStatement.setInt(1, pageConstraintResultSet.getInt(1));
            pageConstraintInsertStatement.setInt(2, pageConstraintResultSet.getInt(2));
            pageConstraintInsertStatement.setInt(3, pageConstraintResultSet.getInt(3));
            pageConstraintInsertStatement.setString(4, pageConstraintResultSet.getString(4));
            pageConstraintInsertStatement.setString(5, pageConstraintResultSet.getString(5));
            pageConstraintInsertStatement.setString(6, pageConstraintResultSet.getString(6));
            pageConstraintInsertStatement.setString(7, pageConstraintResultSet.getString(7));
            pageConstraintInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        pageConstraintResultSet.close();
        pageConstraintQueryStatement.close();
        pageConstraintInsertStatement.close();

        // PAGE_CONSTRAINTS_REF
        PreparedStatement pageConstraintsRefInsertStatement = targetConnection.prepareStatement("INSERT INTO PAGE_CONSTRAINTS_REF (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES (?, ?, ?, ?)");
        Statement pageConstraintsRefQueryStatement = sourceConnection.createStatement();
        pageConstraintsRefQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet pageConstraintsRefResultSet = pageConstraintsRefQueryStatement.executeQuery("SELECT CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME FROM PAGE_CONSTRAINTS_REF");
        while (pageConstraintsRefResultSet.next())
        {
            pageConstraintsRefInsertStatement.setInt(1, pageConstraintsRefResultSet.getInt(1));
            pageConstraintsRefInsertStatement.setInt(2, pageConstraintsRefResultSet.getInt(2));
            pageConstraintsRefInsertStatement.setInt(3, pageConstraintsRefResultSet.getInt(3));
            pageConstraintsRefInsertStatement.setString(4, pageConstraintsRefResultSet.getString(4));
            pageConstraintsRefInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        pageConstraintsRefResultSet.close();
        pageConstraintsRefQueryStatement.close();
        pageConstraintsRefInsertStatement.close();
        
        // PAGE_MENU
        PreparedStatement pageMenuInsertStatement = targetConnection.prepareStatement("INSERT INTO PAGE_MENU (MENU_ID, CLASS_NAME, PARENT_ID, PAGE_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement pageMenuQueryStatement = sourceConnection.createStatement();
        pageMenuQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet pageMenuResultSet = pageMenuQueryStatement.executeQuery("SELECT MENU_ID, CLASS_NAME, PARENT_ID, PAGE_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST FROM PAGE_MENU");
        while (pageMenuResultSet.next())
        {
            pageMenuInsertStatement.setInt(1, pageMenuResultSet.getInt(1));
            pageMenuInsertStatement.setString(2, pageMenuResultSet.getString(2));
            Static.setNullableInt(pageMenuResultSet, 3, pageMenuInsertStatement);
            Static.setNullableInt(pageMenuResultSet, 4, pageMenuInsertStatement);
            Static.setNullableInt(pageMenuResultSet, 5, pageMenuInsertStatement);
            pageMenuInsertStatement.setString(6, pageMenuResultSet.getString(6));
            pageMenuInsertStatement.setString(7, pageMenuResultSet.getString(7));
            pageMenuInsertStatement.setString(8, pageMenuResultSet.getString(8));
            pageMenuInsertStatement.setString(9, pageMenuResultSet.getString(9));
            pageMenuInsertStatement.setString(10, pageMenuResultSet.getString(10));
            Static.setNullableInt(pageMenuResultSet, 11, pageMenuInsertStatement);
            Static.setNullableShort(pageMenuResultSet, 12, pageMenuInsertStatement);
            Static.setNullableShort(pageMenuResultSet, 13, pageMenuInsertStatement);
            pageMenuInsertStatement.setString(14, pageMenuResultSet.getString(14));
            pageMenuInsertStatement.setString(15, pageMenuResultSet.getString(15));
            pageMenuInsertStatement.setString(16, pageMenuResultSet.getString(16));
            Static.setNullableShort(pageMenuResultSet, 17, pageMenuInsertStatement);
            pageMenuInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        pageMenuResultSet.close();
        pageMenuQueryStatement.close();
        pageMenuInsertStatement.close();

        // PAGE_MENU_METADATA
        PreparedStatement pageMenuMetadataInsertStatement = targetConnection.prepareStatement("INSERT INTO PAGE_MENU_METADATA (METADATA_ID, MENU_ID, NAME, LOCALE, VALUE) VALUES (?, ?, ?, ?, ?)");
        Statement pageMenuMetadataQueryStatement = sourceConnection.createStatement();
        pageMenuMetadataQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet pageMenuMetadataResultSet = pageMenuMetadataQueryStatement.executeQuery("SELECT METADATA_ID, MENU_ID, NAME, LOCALE, VALUE FROM PAGE_MENU_METADATA");
        while (pageMenuMetadataResultSet.next())
        {
            pageMenuMetadataInsertStatement.setInt(1, pageMenuMetadataResultSet.getInt(1));
            pageMenuMetadataInsertStatement.setInt(2, pageMenuMetadataResultSet.getInt(2));
            pageMenuMetadataInsertStatement.setString(3, pageMenuMetadataResultSet.getString(3));
            pageMenuMetadataInsertStatement.setString(4, pageMenuMetadataResultSet.getString(4));
            pageMenuMetadataInsertStatement.setString(5, pageMenuMetadataResultSet.getString(5));
            pageMenuMetadataInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        pageMenuMetadataResultSet.close();
        pageMenuMetadataQueryStatement.close();
        pageMenuMetadataInsertStatement.close();

        // FRAGMENT
        PreparedStatement fragmentInsertStatement = targetConnection.prepareStatement("INSERT INTO FRAGMENT (FRAGMENT_ID, CLASS_NAME, PARENT_ID, PAGE_ID, FRAGMENT_STRING_ID, FRAGMENT_STRING_REFID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, OWNER_PRINCIPAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement fragmentQueryStatement = sourceConnection.createStatement();
        fragmentQueryStatement.setFetchSize(FETCH_SIZE);
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            {
                ResultSet fragmentResultSet = fragmentQueryStatement.executeQuery("SELECT FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, OWNER_PRINCIPAL FROM FRAGMENT");
                while (fragmentResultSet.next())
                {
                    fragmentInsertStatement.setInt(1, fragmentResultSet.getInt(1));
                    fragmentInsertStatement.setString(2, "org.apache.jetspeed.om.page.impl.FragmentImpl");
                    Static.setNullableInt(fragmentResultSet, 2, fragmentInsertStatement, 3);
                    Static.setNullableInt(fragmentResultSet, 3, fragmentInsertStatement, 4);
                    fragmentInsertStatement.setNull(5, Types.VARCHAR);
                    fragmentInsertStatement.setNull(6, Types.VARCHAR);
                    String portlet = fragmentResultSet.getString(4);
                    switch (sourceVersion)
                    {
                        case JETSPEED_SCHEMA_VERSION_2_1_3:
                        {
                            String migratedPortlet = PORTLET_NAME_2_1_X_TO_2_2_X_MIGRATION_MAP.get(portlet);
                            portlet = ((migratedPortlet != null) ? migratedPortlet : portlet);
                        }
                        break;
                    }
                    fragmentInsertStatement.setString(7, portlet);
                    fragmentInsertStatement.setString(8, fragmentResultSet.getString(5));
                    fragmentInsertStatement.setString(9, fragmentResultSet.getString(6));
                    fragmentInsertStatement.setString(10, fragmentResultSet.getString(7));
                    fragmentInsertStatement.setString(11, fragmentResultSet.getString(8));
                    String decorator = fragmentResultSet.getString(9);
                    switch (sourceVersion)
                    {
                        case JETSPEED_SCHEMA_VERSION_2_1_3:
                        {
                            String migratedDecorator = PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.get(decorator);
                            decorator = ((migratedDecorator != null) ? migratedDecorator : decorator);
                        }
                        break;
                    }
                    fragmentInsertStatement.setString(12, decorator);
                    fragmentInsertStatement.setString(13, fragmentResultSet.getString(10));
                    fragmentInsertStatement.setString(14, fragmentResultSet.getString(11));
                    Static.setNullableInt(fragmentResultSet, 12, fragmentInsertStatement, 15);
                    Static.setNullableInt(fragmentResultSet, 13, fragmentInsertStatement, 16);
                    fragmentInsertStatement.setString(17, fragmentResultSet.getString(14));
                    Static.setNullableFloat(fragmentResultSet, 15, fragmentInsertStatement, 18);
                    Static.setNullableFloat(fragmentResultSet, 16, fragmentInsertStatement, 19);
                    Static.setNullableFloat(fragmentResultSet, 17, fragmentInsertStatement, 20);
                    Static.setNullableFloat(fragmentResultSet, 18, fragmentInsertStatement, 21);
                    Static.setNullableFloat(fragmentResultSet, 19, fragmentInsertStatement, 22);
                    fragmentInsertStatement.setString(23, fragmentResultSet.getString(20));            
                    fragmentInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                fragmentResultSet.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            {
                ResultSet fragmentResultSet = fragmentQueryStatement.executeQuery("SELECT FRAGMENT_ID, PARENT_ID, PAGE_ID, FRAGMENT_STRING_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, OWNER_PRINCIPAL FROM FRAGMENT");
                while (fragmentResultSet.next())
                {
                    fragmentInsertStatement.setInt(1, fragmentResultSet.getInt(1));
                    fragmentInsertStatement.setString(2, "org.apache.jetspeed.om.page.impl.FragmentImpl");
                    Static.setNullableInt(fragmentResultSet, 2, fragmentInsertStatement, 3);
                    Static.setNullableInt(fragmentResultSet, 3, fragmentInsertStatement, 4);
                    fragmentInsertStatement.setString(5, fragmentResultSet.getString(4));
                    fragmentInsertStatement.setNull(6, Types.VARCHAR);
                    String portlet = fragmentResultSet.getString(5);
                    String migratedPortlet = PORTLET_NAME_2_1_X_TO_2_2_X_MIGRATION_MAP.get(portlet);
                    portlet = ((migratedPortlet != null) ? migratedPortlet : portlet);
                    fragmentInsertStatement.setString(7, portlet);
                    fragmentInsertStatement.setString(8, fragmentResultSet.getString(6));
                    fragmentInsertStatement.setString(9, fragmentResultSet.getString(7));
                    fragmentInsertStatement.setString(10, fragmentResultSet.getString(8));
                    fragmentInsertStatement.setString(11, fragmentResultSet.getString(9));
                    String decorator = fragmentResultSet.getString(10);
                    String migratedDecorator = PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.get(decorator);
                    decorator = ((migratedDecorator != null) ? migratedDecorator : decorator);                    
                    fragmentInsertStatement.setString(12, decorator);
                    fragmentInsertStatement.setString(13, fragmentResultSet.getString(11));
                    fragmentInsertStatement.setString(14, fragmentResultSet.getString(12));
                    Static.setNullableInt(fragmentResultSet, 13, fragmentInsertStatement, 15);
                    Static.setNullableInt(fragmentResultSet, 14, fragmentInsertStatement, 16);
                    fragmentInsertStatement.setString(17, fragmentResultSet.getString(15));
                    Static.setNullableFloat(fragmentResultSet, 16, fragmentInsertStatement, 18);
                    Static.setNullableFloat(fragmentResultSet, 17, fragmentInsertStatement, 19);
                    Static.setNullableFloat(fragmentResultSet, 18, fragmentInsertStatement, 20);
                    Static.setNullableFloat(fragmentResultSet, 19, fragmentInsertStatement, 21);
                    Static.setNullableFloat(fragmentResultSet, 20, fragmentInsertStatement, 22);
                    fragmentInsertStatement.setString(23, fragmentResultSet.getString(21));            
                    fragmentInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                fragmentResultSet.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                ResultSet fragmentResultSet = fragmentQueryStatement.executeQuery("SELECT FRAGMENT_ID, CLASS_NAME, PARENT_ID, PAGE_ID, FRAGMENT_STRING_ID, FRAGMENT_STRING_REFID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, OWNER_PRINCIPAL FROM FRAGMENT");
                while (fragmentResultSet.next())
                {
                    fragmentInsertStatement.setInt(1, fragmentResultSet.getInt(1));
                    fragmentInsertStatement.setString(2, fragmentResultSet.getString(2));
                    Static.setNullableInt(fragmentResultSet, 3, fragmentInsertStatement);
                    Static.setNullableInt(fragmentResultSet, 4, fragmentInsertStatement);
                    fragmentInsertStatement.setString(5, fragmentResultSet.getString(5));
                    fragmentInsertStatement.setString(6, fragmentResultSet.getString(6));
                    fragmentInsertStatement.setString(7, fragmentResultSet.getString(7));
                    fragmentInsertStatement.setString(8, fragmentResultSet.getString(8));
                    fragmentInsertStatement.setString(9, fragmentResultSet.getString(9));
                    fragmentInsertStatement.setString(10, fragmentResultSet.getString(10));
                    fragmentInsertStatement.setString(11, fragmentResultSet.getString(11));
                    fragmentInsertStatement.setString(12, fragmentResultSet.getString(12));
                    fragmentInsertStatement.setString(13, fragmentResultSet.getString(13));
                    fragmentInsertStatement.setString(14, fragmentResultSet.getString(14));
                    Static.setNullableInt(fragmentResultSet, 15, fragmentInsertStatement);
                    Static.setNullableInt(fragmentResultSet, 16, fragmentInsertStatement);
                    fragmentInsertStatement.setString(17, fragmentResultSet.getString(17));
                    Static.setNullableFloat(fragmentResultSet, 18, fragmentInsertStatement);
                    Static.setNullableFloat(fragmentResultSet, 19, fragmentInsertStatement);
                    Static.setNullableFloat(fragmentResultSet, 20, fragmentInsertStatement);
                    Static.setNullableFloat(fragmentResultSet, 21, fragmentInsertStatement);
                    Static.setNullableFloat(fragmentResultSet, 22, fragmentInsertStatement);
                    fragmentInsertStatement.setString(23, fragmentResultSet.getString(23));            
                    fragmentInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                fragmentResultSet.close();
            }
            break;
        }
        fragmentQueryStatement.close();
        fragmentInsertStatement.close();

        // FRAGMENT_CONSTRAINT
        PreparedStatement fragmentConstraintInsertStatement = targetConnection.prepareStatement("INSERT INTO FRAGMENT_CONSTRAINT (CONSTRAINT_ID, FRAGMENT_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL) VALUES (?, ?, ?, ?, ?, ?, ?)");
        Statement fragmentConstraintQueryStatement = sourceConnection.createStatement();
        fragmentConstraintQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet fragmentConstraintResultSet = fragmentConstraintQueryStatement.executeQuery("SELECT CONSTRAINT_ID, FRAGMENT_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL FROM FRAGMENT_CONSTRAINT");
        while (fragmentConstraintResultSet.next())
        {
            fragmentConstraintInsertStatement.setInt(1, fragmentConstraintResultSet.getInt(1));
            fragmentConstraintInsertStatement.setInt(2, fragmentConstraintResultSet.getInt(2));
            fragmentConstraintInsertStatement.setInt(3, fragmentConstraintResultSet.getInt(3));
            fragmentConstraintInsertStatement.setString(4, fragmentConstraintResultSet.getString(4));
            fragmentConstraintInsertStatement.setString(5, fragmentConstraintResultSet.getString(5));
            fragmentConstraintInsertStatement.setString(6, fragmentConstraintResultSet.getString(6));
            fragmentConstraintInsertStatement.setString(7, fragmentConstraintResultSet.getString(7));
            fragmentConstraintInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        fragmentConstraintResultSet.close();
        fragmentConstraintQueryStatement.close();
        fragmentConstraintInsertStatement.close();

        // FRAGMENT_CONSTRAINTS_REF
        PreparedStatement fragmentConstraintsRefInsertStatement = targetConnection.prepareStatement("INSERT INTO FRAGMENT_CONSTRAINTS_REF (CONSTRAINTS_REF_ID, FRAGMENT_ID, APPLY_ORDER, NAME) VALUES (?, ?, ?, ?)");
        Statement fragmentConstraintsRefQueryStatement = sourceConnection.createStatement();
        fragmentConstraintsRefQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet fragmentConstraintsRefResultSet = fragmentConstraintsRefQueryStatement.executeQuery("SELECT CONSTRAINTS_REF_ID, FRAGMENT_ID, APPLY_ORDER, NAME FROM FRAGMENT_CONSTRAINTS_REF");
        while (fragmentConstraintsRefResultSet.next())
        {
            fragmentConstraintsRefInsertStatement.setInt(1, fragmentConstraintsRefResultSet.getInt(1));
            fragmentConstraintsRefInsertStatement.setInt(2, fragmentConstraintsRefResultSet.getInt(2));
            fragmentConstraintsRefInsertStatement.setInt(3, fragmentConstraintsRefResultSet.getInt(3));
            fragmentConstraintsRefInsertStatement.setString(4, fragmentConstraintsRefResultSet.getString(4));
            fragmentConstraintsRefInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        fragmentConstraintsRefResultSet.close();
        fragmentConstraintsRefQueryStatement.close();
        fragmentConstraintsRefInsertStatement.close();
        
        // FRAGMENT_PREF
        PreparedStatement fragmentPrefInsertStatement = targetConnection.prepareStatement("INSERT INTO FRAGMENT_PREF (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY) VALUES (?, ?, ?, ?)");
        Statement fragmentPrefQueryStatement = sourceConnection.createStatement();
        fragmentPrefQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet fragmentPrefResultSet = fragmentPrefQueryStatement.executeQuery("SELECT PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY FROM FRAGMENT_PREF");
        while (fragmentPrefResultSet.next())
        {
            fragmentPrefInsertStatement.setInt(1, fragmentPrefResultSet.getInt(1));
            fragmentPrefInsertStatement.setInt(2, fragmentPrefResultSet.getInt(2));
            fragmentPrefInsertStatement.setString(3, fragmentPrefResultSet.getString(3));
            fragmentPrefInsertStatement.setShort(4, fragmentPrefResultSet.getShort(4));
            fragmentPrefInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        fragmentPrefResultSet.close();
        fragmentPrefQueryStatement.close();
        fragmentPrefInsertStatement.close();

        // FRAGMENT_PREF_VALUE
        PreparedStatement fragmentPrefValueInsertStatement = targetConnection.prepareStatement("INSERT INTO FRAGMENT_PREF_VALUE (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE) VALUES (?, ?, ?, ?)");
        Statement fragmentPrefValueQueryStatement = sourceConnection.createStatement();
        fragmentPrefValueQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet fragmentPrefValueResultSet = fragmentPrefValueQueryStatement.executeQuery("SELECT PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE FROM FRAGMENT_PREF_VALUE");
        while (fragmentPrefValueResultSet.next())
        {
            fragmentPrefValueInsertStatement.setInt(1, fragmentPrefValueResultSet.getInt(1));
            fragmentPrefValueInsertStatement.setInt(2, fragmentPrefValueResultSet.getInt(2));
            fragmentPrefValueInsertStatement.setInt(3, fragmentPrefValueResultSet.getInt(3));
            fragmentPrefValueInsertStatement.setString(4, fragmentPrefValueResultSet.getString(4));
            fragmentPrefValueInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        fragmentPrefValueResultSet.close();
        fragmentPrefValueQueryStatement.close();
        fragmentPrefValueInsertStatement.close();

        // FRAGMENT_PROP
        int fragmentPropRowsMigrated = 0;
        PreparedStatement fragmentPropInsertStatement = targetConnection.prepareStatement("INSERT INTO FRAGMENT_PROP (PROP_ID, FRAGMENT_ID, NAME, SCOPE, SCOPE_VALUE, VALUE) VALUES (?, ?, ?, ?, ?, ?)");
        Statement fragmentPropQueryStatement = sourceConnection.createStatement();
        fragmentPropQueryStatement.setFetchSize(FETCH_SIZE);
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            {
                ResultSet fragmentResultSet = fragmentPropQueryStatement.executeQuery("SELECT FRAGMENT_ID, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2 FROM FRAGMENT WHERE EXT_PROP_NAME_1 IS NOT NULL OR EXT_PROP_NAME_2 IS NOT NULL");
                while (fragmentResultSet.next())
                {
                    if ((fragmentResultSet.getString(2) != null) && (fragmentResultSet.getString(3) != null))
                    {
                        fragmentPropInsertStatement.setInt(1, fragmentPropRowsMigrated++);
                        fragmentPropInsertStatement.setInt(2, fragmentResultSet.getInt(1));
                        fragmentPropInsertStatement.setString(3, fragmentResultSet.getString(2));
                        fragmentPropInsertStatement.setNull(4, Types.VARCHAR);
                        fragmentPropInsertStatement.setNull(5, Types.VARCHAR);
                        fragmentPropInsertStatement.setString(6, fragmentResultSet.getString(3));
                        fragmentPropInsertStatement.executeUpdate();
                        rowsMigrated++;
                        migrationListener.rowMigrated(targetConnection);
                    }
                    if ((fragmentResultSet.getString(4) != null) && (fragmentResultSet.getString(5) != null))
                    {
                        fragmentPropInsertStatement.setInt(1, fragmentPropRowsMigrated++);
                        fragmentPropInsertStatement.setInt(2, fragmentResultSet.getInt(1));
                        fragmentPropInsertStatement.setString(3, fragmentResultSet.getString(4));
                        fragmentPropInsertStatement.setNull(4, Types.VARCHAR);
                        fragmentPropInsertStatement.setNull(5, Types.VARCHAR);
                        fragmentPropInsertStatement.setString(6, fragmentResultSet.getString(5));
                        fragmentPropInsertStatement.executeUpdate();
                        rowsMigrated++;
                        migrationListener.rowMigrated(targetConnection);
                    }
                }
                fragmentResultSet.close();
            }
            break;
            case JETSPEED_SCHEMA_VERSION_2_2_1:
            {
                ResultSet fragmentPropResultSet = fragmentPropQueryStatement.executeQuery("SELECT PROP_ID, FRAGMENT_ID, NAME, SCOPE, SCOPE_VALUE, VALUE FROM FRAGMENT_PROP");
                while (fragmentPropResultSet.next())
                {
                    fragmentPropInsertStatement.setInt(1, fragmentPropResultSet.getInt(1));
                    fragmentPropInsertStatement.setInt(2, fragmentPropResultSet.getInt(2));
                    fragmentPropInsertStatement.setString(3, fragmentPropResultSet.getString(3));
                    fragmentPropInsertStatement.setString(4, fragmentPropResultSet.getString(4));
                    fragmentPropInsertStatement.setString(5, fragmentPropResultSet.getString(5));
                    fragmentPropInsertStatement.setString(6, fragmentPropResultSet.getString(6));
                    fragmentPropInsertStatement.executeUpdate();
                    rowsMigrated++;
                    migrationListener.rowMigrated(targetConnection);
                }
                fragmentPropResultSet.close();
            }
        }
        fragmentPropQueryStatement.close();
        fragmentPropInsertStatement.close();

        // LINK
        PreparedStatement linkInsertStatement = targetConnection.prepareStatement("INSERT INTO LINK (LINK_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, TARGET, URL, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement linkQueryStatement = sourceConnection.createStatement();
        linkQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet linkResultSet = linkQueryStatement.executeQuery("SELECT LINK_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, TARGET, URL, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL FROM LINK");
        while (linkResultSet.next())
        {
            linkInsertStatement.setInt(1, linkResultSet.getInt(1));
            linkInsertStatement.setInt(2, linkResultSet.getInt(2));
            linkInsertStatement.setString(3, linkResultSet.getString(3));
            linkInsertStatement.setString(4, linkResultSet.getString(4));
            linkInsertStatement.setString(5, linkResultSet.getString(5));
            linkInsertStatement.setString(6, linkResultSet.getString(6));
            linkInsertStatement.setString(7, linkResultSet.getString(7));
            linkInsertStatement.setShort(8, linkResultSet.getShort(8));
            linkInsertStatement.setString(9, linkResultSet.getString(9));
            linkInsertStatement.setString(10, linkResultSet.getString(10));
            linkInsertStatement.setString(11, linkResultSet.getString(11));
            linkInsertStatement.setString(12, linkResultSet.getString(12));
            linkInsertStatement.setString(13, linkResultSet.getString(13));
            linkInsertStatement.setString(14, linkResultSet.getString(14));
            linkInsertStatement.setString(15, linkResultSet.getString(15));
            linkInsertStatement.setString(16, linkResultSet.getString(16));
            linkInsertStatement.setString(17, linkResultSet.getString(17));
            linkInsertStatement.setString(18, linkResultSet.getString(18));
            linkInsertStatement.setString(19, linkResultSet.getString(19));
            linkInsertStatement.setString(20, linkResultSet.getString(20));
            linkInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        linkResultSet.close();
        linkQueryStatement.close();
        linkInsertStatement.close();

        // LINK_METADATA
        PreparedStatement linkMetadataInsertStatement = targetConnection.prepareStatement("INSERT INTO LINK_METADATA (METADATA_ID, LINK_ID, NAME, LOCALE, VALUE) VALUES (?, ?, ?, ?, ?)");
        Statement linkMetadataQueryStatement = sourceConnection.createStatement();
        linkMetadataQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet linkMetadataResultSet = linkMetadataQueryStatement.executeQuery("SELECT METADATA_ID, LINK_ID, NAME, LOCALE, VALUE FROM LINK_METADATA");
        while (linkMetadataResultSet.next())
        {
            linkMetadataInsertStatement.setInt(1, linkMetadataResultSet.getInt(1));
            linkMetadataInsertStatement.setInt(2, linkMetadataResultSet.getInt(2));
            linkMetadataInsertStatement.setString(3, linkMetadataResultSet.getString(3));
            linkMetadataInsertStatement.setString(4, linkMetadataResultSet.getString(4));
            linkMetadataInsertStatement.setString(5, linkMetadataResultSet.getString(5));
            linkMetadataInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        linkMetadataResultSet.close();
        linkMetadataQueryStatement.close();
        linkMetadataInsertStatement.close();

        // LINK_CONSTRAINT
        PreparedStatement linkConstraintInsertStatement = targetConnection.prepareStatement("INSERT INTO LINK_CONSTRAINT (CONSTRAINT_ID, LINK_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL) VALUES (?, ?, ?, ?, ?, ?, ?)");
        Statement linkConstraintQueryStatement = sourceConnection.createStatement();
        linkConstraintQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet linkConstraintResultSet = linkConstraintQueryStatement.executeQuery("SELECT CONSTRAINT_ID, LINK_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL FROM LINK_CONSTRAINT");
        while (linkConstraintResultSet.next())
        {
            linkConstraintInsertStatement.setInt(1, linkConstraintResultSet.getInt(1));
            linkConstraintInsertStatement.setInt(2, linkConstraintResultSet.getInt(2));
            linkConstraintInsertStatement.setInt(3, linkConstraintResultSet.getInt(3));
            linkConstraintInsertStatement.setString(4, linkConstraintResultSet.getString(4));
            linkConstraintInsertStatement.setString(5, linkConstraintResultSet.getString(5));
            linkConstraintInsertStatement.setString(6, linkConstraintResultSet.getString(6));
            linkConstraintInsertStatement.setString(7, linkConstraintResultSet.getString(7));
            linkConstraintInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        linkConstraintResultSet.close();
        linkConstraintQueryStatement.close();
        linkConstraintInsertStatement.close();

        // LINK_CONSTRAINTS_REF
        PreparedStatement linkConstraintsRefInsertStatement = targetConnection.prepareStatement("INSERT INTO LINK_CONSTRAINTS_REF (CONSTRAINTS_REF_ID, LINK_ID, APPLY_ORDER, NAME) VALUES (?, ?, ?, ?)");
        Statement linkConstraintsRefQueryStatement = sourceConnection.createStatement();
        linkConstraintsRefQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet linkConstraintsRefResultSet = linkConstraintsRefQueryStatement.executeQuery("SELECT CONSTRAINTS_REF_ID, LINK_ID, APPLY_ORDER, NAME FROM LINK_CONSTRAINTS_REF");
        while (linkConstraintsRefResultSet.next())
        {
            linkConstraintsRefInsertStatement.setInt(1, linkConstraintsRefResultSet.getInt(1));
            linkConstraintsRefInsertStatement.setInt(2, linkConstraintsRefResultSet.getInt(2));
            linkConstraintsRefInsertStatement.setInt(3, linkConstraintsRefResultSet.getInt(3));
            linkConstraintsRefInsertStatement.setString(4, linkConstraintsRefResultSet.getString(4));
            linkConstraintsRefInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        linkConstraintsRefResultSet.close();
        linkConstraintsRefQueryStatement.close();
        linkConstraintsRefInsertStatement.close();

        // PAGE_SECURITY
        PreparedStatement pageSecurityInsertStatement = targetConnection.prepareStatement("INSERT INTO PAGE_SECURITY (PAGE_SECURITY_ID, PARENT_ID, PATH, NAME, VERSION, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement pageSecurityQueryStatement = sourceConnection.createStatement();
        pageSecurityQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet pageSecurityResultSet = pageSecurityQueryStatement.executeQuery("SELECT PAGE_SECURITY_ID, PARENT_ID, PATH, NAME, VERSION, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE FROM PAGE_SECURITY");
        while (pageSecurityResultSet.next())
        {
            pageSecurityInsertStatement.setInt(1, pageSecurityResultSet.getInt(1));
            pageSecurityInsertStatement.setInt(2, pageSecurityResultSet.getInt(2));
            pageSecurityInsertStatement.setString(3, pageSecurityResultSet.getString(3));
            pageSecurityInsertStatement.setString(4, pageSecurityResultSet.getString(4));
            pageSecurityInsertStatement.setString(5, pageSecurityResultSet.getString(5));
            pageSecurityInsertStatement.setString(6, pageSecurityResultSet.getString(6));
            pageSecurityInsertStatement.setString(7, pageSecurityResultSet.getString(7));
            pageSecurityInsertStatement.setString(8, pageSecurityResultSet.getString(8));
            pageSecurityInsertStatement.setString(9, pageSecurityResultSet.getString(9));
            pageSecurityInsertStatement.setString(10, pageSecurityResultSet.getString(10));
            pageSecurityInsertStatement.setString(11, pageSecurityResultSet.getString(11));
            pageSecurityInsertStatement.setString(12, pageSecurityResultSet.getString(12));
            pageSecurityInsertStatement.setString(13, pageSecurityResultSet.getString(13));
            pageSecurityInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        pageSecurityResultSet.close();
        pageSecurityQueryStatement.close();
        pageSecurityInsertStatement.close();

        // PAGE_SEC_CONSTRAINTS_DEF
        PreparedStatement pageSecurityConstraintsDefInsertStatement = targetConnection.prepareStatement("INSERT INTO PAGE_SEC_CONSTRAINTS_DEF (CONSTRAINTS_DEF_ID, PAGE_SECURITY_ID, NAME) VALUES (?, ?, ?)");
        Statement pageSecurityConstraintsDefQueryStatement = sourceConnection.createStatement();
        pageSecurityConstraintsDefQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet pageSecurityConstraintsDefResultSet = pageSecurityConstraintsDefQueryStatement.executeQuery("SELECT CONSTRAINTS_DEF_ID, PAGE_SECURITY_ID, NAME FROM PAGE_SEC_CONSTRAINTS_DEF");
        while (pageSecurityConstraintsDefResultSet.next())
        {
            pageSecurityConstraintsDefInsertStatement.setInt(1, pageSecurityConstraintsDefResultSet.getInt(1));
            pageSecurityConstraintsDefInsertStatement.setInt(2, pageSecurityConstraintsDefResultSet.getInt(2));
            pageSecurityConstraintsDefInsertStatement.setString(3, pageSecurityConstraintsDefResultSet.getString(3));
            pageSecurityConstraintsDefInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        pageSecurityConstraintsDefResultSet.close();
        pageSecurityConstraintsDefQueryStatement.close();
        pageSecurityConstraintsDefInsertStatement.close();

        // PAGE_SEC_CONSTRAINT_DEF
        PreparedStatement pageSecurityConstraintDefInsertStatement = targetConnection.prepareStatement("INSERT INTO PAGE_SEC_CONSTRAINT_DEF (CONSTRAINT_DEF_ID, CONSTRAINTS_DEF_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL) VALUES (?, ?, ?, ?, ?, ?, ?)");
        Statement pageSecurityConstraintDefQueryStatement = sourceConnection.createStatement();
        pageSecurityConstraintDefQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet pageSecurityConstraintDefResultSet = pageSecurityConstraintDefQueryStatement.executeQuery("SELECT CONSTRAINT_DEF_ID, CONSTRAINTS_DEF_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL FROM PAGE_SEC_CONSTRAINT_DEF");
        while (pageSecurityConstraintDefResultSet.next())
        {
            pageSecurityConstraintDefInsertStatement.setInt(1, pageSecurityConstraintDefResultSet.getInt(1));
            pageSecurityConstraintDefInsertStatement.setInt(2, pageSecurityConstraintDefResultSet.getInt(2));
            pageSecurityConstraintDefInsertStatement.setInt(3, pageSecurityConstraintDefResultSet.getInt(3));
            pageSecurityConstraintDefInsertStatement.setString(4, pageSecurityConstraintDefResultSet.getString(4));
            pageSecurityConstraintDefInsertStatement.setString(5, pageSecurityConstraintDefResultSet.getString(5));
            pageSecurityConstraintDefInsertStatement.setString(6, pageSecurityConstraintDefResultSet.getString(6));
            pageSecurityConstraintDefInsertStatement.setString(7, pageSecurityConstraintDefResultSet.getString(7));
            pageSecurityConstraintDefInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        pageSecurityConstraintDefResultSet.close();
        pageSecurityConstraintDefQueryStatement.close();
        pageSecurityConstraintDefInsertStatement.close();

        // PAGE_SEC_CONSTRAINTS_REF
        PreparedStatement pageSecurityConstraintsRefInsertStatement = targetConnection.prepareStatement("INSERT INTO PAGE_SEC_CONSTRAINTS_REF (CONSTRAINTS_REF_ID, PAGE_SECURITY_ID, APPLY_ORDER, NAME) VALUES (?, ?, ?, ?)");
        Statement pageSecurityConstraintsRefQueryStatement = sourceConnection.createStatement();
        pageSecurityConstraintsRefQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet pageSecurityConstraintsRefResultSet = pageSecurityConstraintsRefQueryStatement.executeQuery("SELECT CONSTRAINTS_REF_ID, PAGE_SECURITY_ID, APPLY_ORDER, NAME FROM PAGE_SEC_CONSTRAINTS_REF");
        while (pageSecurityConstraintsRefResultSet.next())
        {
            pageSecurityConstraintsRefInsertStatement.setInt(1, pageSecurityConstraintsRefResultSet.getInt(1));
            pageSecurityConstraintsRefInsertStatement.setInt(2, pageSecurityConstraintsRefResultSet.getInt(2));
            pageSecurityConstraintsRefInsertStatement.setInt(3, pageSecurityConstraintsRefResultSet.getInt(3));
            pageSecurityConstraintsRefInsertStatement.setString(4, pageSecurityConstraintsRefResultSet.getString(4));
            pageSecurityConstraintsRefInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        pageSecurityConstraintsRefResultSet.close();
        pageSecurityConstraintsRefQueryStatement.close();
        pageSecurityConstraintsRefInsertStatement.close();
        
        // OJB_HL_SEQ
        PreparedStatement ojbInsertStatement = targetConnection.prepareStatement("INSERT INTO OJB_HL_SEQ (TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION) VALUES (?, ?, ?, ?, ?)");
        Statement ojbQueryStatement = sourceConnection.createStatement();
        ResultSet ojbResultSet = ojbQueryStatement.executeQuery("SELECT TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION FROM OJB_HL_SEQ WHERE TABLENAME IN ('SEQ_FOLDER', 'SEQ_FOLDER_CONSTRAINT', 'SEQ_FOLDER_CONSTRAINTS_REF', 'SEQ_FOLDER_MENU', 'SEQ_FOLDER_MENU_METADATA', 'SEQ_FOLDER_METADATA', 'SEQ_FOLDER_ORDER', 'SEQ_FRAGMENT', 'SEQ_FRAGMENT_CONSTRAINT', 'SEQ_FRAGMENT_CONSTRAINTS_REF', 'SEQ_FRAGMENT_PREF', 'SEQ_FRAGMENT_PREF_VALUE', 'SEQ_FRAGMENT_PROP', 'SEQ_LINK', 'SEQ_LINK_CONSTRAINT', 'SEQ_LINK_CONSTRAINTS_REF', 'SEQ_LINK_METADATA', 'SEQ_PAGE', 'SEQ_PAGE_CONSTRAINT', 'SEQ_PAGE_CONSTRAINTS_REF', 'SEQ_PAGE_MENU', 'SEQ_PAGE_MENU_METADATA', 'SEQ_PAGE_METADATA', 'SEQ_PAGE_SECURITY', 'SEQ_PAGE_SEC_CONSTRAINTS_DEF', 'SEQ_PAGE_SEC_CONSTRAINTS_REF', 'SEQ_PAGE_SEC_CONSTRAINT_DEF')");
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
        switch (sourceVersion)
        {
            case JETSPEED_SCHEMA_VERSION_2_1_3:
            case JETSPEED_SCHEMA_VERSION_2_1_4:
            case JETSPEED_SCHEMA_VERSION_2_2_0:
            {
                if (fragmentPropRowsMigrated > 0)
                {
                    int grabSize = 20;
                    int version = (fragmentPropRowsMigrated+(grabSize-1))/grabSize;
                    int maxKey = version*grabSize;
                    ojbInsertStatement.setString(1, "SEQ_FRAGMENT_PROP");
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
        ojbResultSet.close();
        ojbQueryStatement.close();
        ojbInsertStatement.close();
        
        return new JetspeedMigrationResultImpl(rowsMigrated);
    }
}
