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
 * Jetspeed Migration for Statistics component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedStatisticsMigration implements JetspeedMigration
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#getName()
     */
    public String getName()
    {
        return "Statistics";
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#detectSourceVersion(java.sql.Connection, int)
     */
    public int detectSourceVersion(Connection sourceConnection, int sourceVersion) throws SQLException
    {
        // no migration required in statistics schema
        return ((sourceVersion > JETSPEED_SCHEMA_VERSION_UNKNOWN) ? sourceVersion : JETSPEED_SCHEMA_VERSION_2_1_3);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#migrate(java.sql.Connection, int, java.sql.Connection, org.apache.jetspeed.tools.migration.JetspeedMigrationListener)
     */
    public JetspeedMigrationResult migrate(Connection sourceConnection, int sourceVersion, Connection targetConnection, JetspeedMigrationListener migrationListener) throws SQLException
    {
        int rowsMigrated = 0;
                
        // PORTLET_STATISTICS
        PreparedStatement portletStatisticsInsertStatement = targetConnection.prepareStatement("INSERT INTO PORTLET_STATISTICS (IPADDRESS, USER_NAME, TIME_STAMP, PAGE, PORTLET, STATUS, ELAPSED_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)");
        Statement portletStatisticsQueryStatement = sourceConnection.createStatement();
        portletStatisticsQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet portletStatisticsResultSet = portletStatisticsQueryStatement.executeQuery("SELECT IPADDRESS, USER_NAME, TIME_STAMP, PAGE, PORTLET, STATUS, ELAPSED_TIME FROM PORTLET_STATISTICS");
        while (portletStatisticsResultSet.next())
        {
            portletStatisticsInsertStatement.setString(1, portletStatisticsResultSet.getString(1));
            portletStatisticsInsertStatement.setString(2, portletStatisticsResultSet.getString(2));
            portletStatisticsInsertStatement.setTimestamp(3, portletStatisticsResultSet.getTimestamp(3));
            portletStatisticsInsertStatement.setString(4, portletStatisticsResultSet.getString(4));
            String portlet = portletStatisticsResultSet.getString(5);
            switch (sourceVersion)
            {
                case JETSPEED_SCHEMA_VERSION_2_1_3:
                case JETSPEED_SCHEMA_VERSION_2_1_4:
                {
                    String migratedPortlet = PORTLET_NAME_2_1_X_TO_2_2_X_MIGRATION_MAP.get(portlet);
                    portlet = ((migratedPortlet != null) ? migratedPortlet : portlet);
                }
                break;
            }
            portletStatisticsInsertStatement.setString(5, portlet);
            Static.setNullableInt(portletStatisticsResultSet, 6, portletStatisticsInsertStatement);
            Static.setNullableLong(portletStatisticsResultSet, 7, portletStatisticsInsertStatement);
            portletStatisticsInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        portletStatisticsResultSet.close();
        portletStatisticsQueryStatement.close();
        portletStatisticsInsertStatement.close();

        // PAGE_STATISTICS
        PreparedStatement pageStatisticsInsertStatement = targetConnection.prepareStatement("INSERT INTO PAGE_STATISTICS (IPADDRESS, USER_NAME, TIME_STAMP, PAGE, STATUS, ELAPSED_TIME) VALUES (?, ?, ?, ?, ?, ?)");
        Statement pageStatisticsQueryStatement = sourceConnection.createStatement();
        pageStatisticsQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet pageStatisticsResultSet = pageStatisticsQueryStatement.executeQuery("SELECT IPADDRESS, USER_NAME, TIME_STAMP, PAGE, STATUS, ELAPSED_TIME FROM PAGE_STATISTICS");
        while (pageStatisticsResultSet.next())
        {
            pageStatisticsInsertStatement.setString(1, pageStatisticsResultSet.getString(1));
            pageStatisticsInsertStatement.setString(2, pageStatisticsResultSet.getString(2));
            pageStatisticsInsertStatement.setTimestamp(3, pageStatisticsResultSet.getTimestamp(3));
            pageStatisticsInsertStatement.setString(4, pageStatisticsResultSet.getString(4));
            Static.setNullableInt(pageStatisticsResultSet, 5, pageStatisticsInsertStatement);
            Static.setNullableLong(pageStatisticsResultSet, 6, pageStatisticsInsertStatement);
            pageStatisticsInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        pageStatisticsResultSet.close();
        pageStatisticsQueryStatement.close();
        pageStatisticsInsertStatement.close();

        // USER_STATISTICS
        PreparedStatement userStatisticsInsertStatement = targetConnection.prepareStatement("INSERT INTO USER_STATISTICS (IPADDRESS, USER_NAME, TIME_STAMP, STATUS, ELAPSED_TIME) VALUES (?, ?, ?, ?, ?)");
        Statement userStatisticsQueryStatement = sourceConnection.createStatement();
        userStatisticsQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet userStatisticsResultSet = userStatisticsQueryStatement.executeQuery("SELECT IPADDRESS, USER_NAME, TIME_STAMP, STATUS, ELAPSED_TIME FROM USER_STATISTICS");
        while (userStatisticsResultSet.next())
        {
            userStatisticsInsertStatement.setString(1, userStatisticsResultSet.getString(1));
            userStatisticsInsertStatement.setString(2, userStatisticsResultSet.getString(2));
            userStatisticsInsertStatement.setTimestamp(3, userStatisticsResultSet.getTimestamp(3));
            Static.setNullableInt(userStatisticsResultSet, 4, userStatisticsInsertStatement);
            Static.setNullableLong(userStatisticsResultSet, 5, userStatisticsInsertStatement);
            userStatisticsInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        userStatisticsResultSet.close();
        userStatisticsQueryStatement.close();
        userStatisticsInsertStatement.close();

        // ADMIN_ACTIVITY
        PreparedStatement adminActivityInsertStatement = targetConnection.prepareStatement("INSERT INTO ADMIN_ACTIVITY (ACTIVITY, CATEGORY, ADMIN, USER_NAME, TIME_STAMP, IPADDRESS, ATTR_NAME, ATTR_VALUE_BEFORE, ATTR_VALUE_AFTER, DESCRIPTION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement adminActivityQueryStatement = sourceConnection.createStatement();
        adminActivityQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet adminActivityResultSet = adminActivityQueryStatement.executeQuery("SELECT ACTIVITY, CATEGORY, ADMIN, USER_NAME, TIME_STAMP, IPADDRESS, ATTR_NAME, ATTR_VALUE_BEFORE, ATTR_VALUE_AFTER, DESCRIPTION FROM ADMIN_ACTIVITY");
        while (adminActivityResultSet.next())
        {
            adminActivityInsertStatement.setString(1, adminActivityResultSet.getString(1));
            adminActivityInsertStatement.setString(2, adminActivityResultSet.getString(2));
            adminActivityInsertStatement.setString(3, adminActivityResultSet.getString(3));
            adminActivityInsertStatement.setString(4, adminActivityResultSet.getString(4));
            adminActivityInsertStatement.setTimestamp(5, adminActivityResultSet.getTimestamp(5));
            adminActivityInsertStatement.setString(6, adminActivityResultSet.getString(6));
            adminActivityInsertStatement.setString(7, adminActivityResultSet.getString(7));
            adminActivityInsertStatement.setString(8, adminActivityResultSet.getString(8));
            adminActivityInsertStatement.setString(9, adminActivityResultSet.getString(9));
            adminActivityInsertStatement.setString(10, adminActivityResultSet.getString(10));
            adminActivityInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        adminActivityResultSet.close();
        adminActivityQueryStatement.close();
        adminActivityInsertStatement.close();

        // USER_ACTIVITY
        PreparedStatement userActivityInsertStatement = targetConnection.prepareStatement("INSERT INTO USER_ACTIVITY (ACTIVITY, CATEGORY, USER_NAME, TIME_STAMP, IPADDRESS, ATTR_NAME, ATTR_VALUE_BEFORE, ATTR_VALUE_AFTER, DESCRIPTION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        Statement userActivityQueryStatement = sourceConnection.createStatement();
        userActivityQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet userActivityResultSet = userActivityQueryStatement.executeQuery("SELECT ACTIVITY, CATEGORY, USER_NAME, TIME_STAMP, IPADDRESS, ATTR_NAME, ATTR_VALUE_BEFORE, ATTR_VALUE_AFTER, DESCRIPTION FROM USER_ACTIVITY");
        while (userActivityResultSet.next())
        {
            userActivityInsertStatement.setString(1, userActivityResultSet.getString(1));
            userActivityInsertStatement.setString(2, userActivityResultSet.getString(2));
            userActivityInsertStatement.setString(3, userActivityResultSet.getString(3));
            userActivityInsertStatement.setTimestamp(4, userActivityResultSet.getTimestamp(4));
            userActivityInsertStatement.setString(5, userActivityResultSet.getString(5));
            userActivityInsertStatement.setString(6, userActivityResultSet.getString(6));
            userActivityInsertStatement.setString(7, userActivityResultSet.getString(7));
            userActivityInsertStatement.setString(8, userActivityResultSet.getString(8));
            userActivityInsertStatement.setString(9, userActivityResultSet.getString(9));
            userActivityInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        userActivityResultSet.close();
        userActivityQueryStatement.close();
        userActivityInsertStatement.close();
        
        return new JetspeedMigrationResultImpl(rowsMigrated);
    }
}
