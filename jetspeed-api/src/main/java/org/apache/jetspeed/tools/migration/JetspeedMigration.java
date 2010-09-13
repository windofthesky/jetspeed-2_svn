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
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Jetspeed Migration interface.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface JetspeedMigration
{
    static final int FETCH_SIZE = 10000;
    
    static final int JETSPEED_SCHEMA_VERSION_UNKNOWN = 0;
    static final int JETSPEED_SCHEMA_VERSION_2_1_3 = 213;
    static final int JETSPEED_SCHEMA_VERSION_2_1_4 = 214;
    static final int JETSPEED_SCHEMA_VERSION_2_2_0 = 220;
    static final int JETSPEED_SCHEMA_VERSION_2_2_1 = 221;

    static final Map<String,String> PORTLET_NAME_2_1_X_TO_2_2_X_MIGRATION_MAP = new HashMap<String,String>();
    static final Map<String,String> LAYOUT_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP = new HashMap<String,String>();
    static final Map<String,String> PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP = new HashMap<String,String>();

    /**
     * Get migration name.
     * 
     * @return name
     */
    String getName();
    
    /**
     * Detect Jetspeed schema version of source database.
     * 
     * @param sourceConnection connection to source database
     * @param sourceVersion detected minimal source schema version
     * @return new version detected
     * @throws SQLException on SQL error or version incompatibility
     */
    int detectSourceVersion(Connection sourceConnection, int sourceVersion) throws SQLException;
    
    /**
     * Migrate data from source to target database.
     * 
     * @param sourceConnection connection to source database
     * @param sourceVersion source version
     * @param targetConnection connection to target database
     * @param migrationListener migration listener
     * @return migration result
     * @throws SQLException on SQL error or version incompatibility
     */
    JetspeedMigrationResult migrate(Connection sourceConnection, int sourceVersion, Connection targetConnection, JetspeedMigrationListener migrationListener) throws SQLException;

    /**
     * Static utility functions class.
     */
    class Static
    {
        /**
         * Initialize 2.1.X -> 2.2.X migration mappings. 
         */
        static
        {
            PORTLET_NAME_2_1_X_TO_2_2_X_MIGRATION_MAP.put("jetspeed-layouts::VelocityTwoColumns2575", "jetspeed-layouts::VelocityTwoColumns");
            PORTLET_NAME_2_1_X_TO_2_2_X_MIGRATION_MAP.put("demo::ContentViewer", "j2-admin::ContentViewer");
            PORTLET_NAME_2_1_X_TO_2_2_X_MIGRATION_MAP.put("demo::IFramePortlet", "webcontent::IFramePortlet");
            PORTLET_NAME_2_1_X_TO_2_2_X_MIGRATION_MAP.put("demo::SSODemoHelp", "j2-admin::SSODemoHelp");
            PORTLET_NAME_2_1_X_TO_2_2_X_MIGRATION_MAP.put("demo::SSOIFramePortlet", "j2-admin::SSOIFramePortlet");
            PORTLET_NAME_2_1_X_TO_2_2_X_MIGRATION_MAP.put("demo::SSOWebContentPortlet", "j2-admin::SSOWebContentPortlet");

            LAYOUT_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("blueocean", "jetspeed");
            LAYOUT_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("jscookmenu", "jetspeed");
            LAYOUT_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("simple", "jetspeed");
            LAYOUT_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("sunflower", "jetspeed");
            LAYOUT_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("thesolution", "jetspeed");
            LAYOUT_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("tigris", "jetspeed");
            LAYOUT_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("wap", "jetspeed");
            
            PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("blueocean", "jetspeed");
            PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("dalmation", "jetspeed");
            PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("greengrass", "jetspeed");
            PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("metal", "jetspeed");
            PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("minty-blue", "jetspeed");
            PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("pretty-single-portlet", "jetspeed");
            PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("simple", "jetspeed");
            PORTLET_DECORATOR_2_1_X_TO_2_2_X_MIGRATION_MAP.put("tigris", "jetspeed");
        }
        
        /**
         * Set nullable integer column from query result set on prepared statement.
         * 
         * @param resultSet query result set
         * @param columnIndex column index
         * @param insertStatement insert prepared statement
         * @throws SQLException on error set
         */
        static void setNullableInt(ResultSet resultSet, int columnIndex, PreparedStatement insertStatement) throws SQLException
        {
            int intColumn = resultSet.getInt(columnIndex);
            if ((intColumn != 0) || !resultSet.wasNull())
            {
                insertStatement.setInt(columnIndex, intColumn);
            }
            else
            {
                insertStatement.setNull(columnIndex, Types.INTEGER);
            }
        }
    
        /**
         * Set nullable integer column from query result set on prepared statement.
         * 
         * @param resultSet query result set
         * @param columnIndex column index
         * @param insertStatement insert prepared statement
         * @param insertColumnIndex insert column index
         * @throws SQLException on error set
         */
        static void setNullableInt(ResultSet resultSet, int columnIndex, PreparedStatement insertStatement, int insertColumnIndex) throws SQLException
        {
            int intColumn = resultSet.getInt(columnIndex);
            if ((intColumn != 0) || !resultSet.wasNull())
            {
                insertStatement.setInt(insertColumnIndex, intColumn);
            }
            else
            {
                insertStatement.setNull(insertColumnIndex, Types.INTEGER);
            }
        }
    
        /**
         * Set nullable short column from query result set on prepared statement.
         * 
         * @param resultSet query result set
         * @param columnIndex column index
         * @param insertStatement insert prepared statement
         * @throws SQLException on error set
         */
        static void setNullableShort(ResultSet resultSet, int columnIndex, PreparedStatement insertStatement) throws SQLException
        {
            short shortColumn = resultSet.getShort(columnIndex);
            if ((shortColumn != 0) || !resultSet.wasNull())
            {
                insertStatement.setShort(columnIndex, shortColumn);
            }
            else
            {
                insertStatement.setNull(columnIndex, Types.SMALLINT);
            }
        }
    
        /**
         * Set nullable short column from query result set on prepared statement.
         * 
         * @param resultSet query result set
         * @param columnIndex column index
         * @param insertStatement insert prepared statement
         * @param insertColumnIndex insert column index
         * @throws SQLException on error set
         */
        static void setNullableShort(ResultSet resultSet, int columnIndex, PreparedStatement insertStatement, int insertColumnIndex) throws SQLException
        {
            short shortColumn = resultSet.getShort(columnIndex);
            if ((shortColumn != 0) || !resultSet.wasNull())
            {
                insertStatement.setShort(insertColumnIndex, shortColumn);
            }
            else
            {
                insertStatement.setNull(insertColumnIndex, Types.SMALLINT);
            }
        }
    
        /**
         * Set nullable long column from query result set on prepared statement.
         * 
         * @param resultSet query result set
         * @param columnIndex column index
         * @param insertStatement insert prepared statement
         * @throws SQLException on error set
         */
        static void setNullableLong(ResultSet resultSet, int columnIndex, PreparedStatement insertStatement) throws SQLException
        {
            long longColumn = resultSet.getLong(columnIndex);
            if ((longColumn != 0) || !resultSet.wasNull())
            {
                insertStatement.setLong(columnIndex, longColumn);
            }
            else
            {
                insertStatement.setNull(columnIndex, Types.BIGINT);
            }
        }
    
        /**
         * Set nullable long column from query result set on prepared statement.
         * 
         * @param resultSet query result set
         * @param columnIndex column index
         * @param insertStatement insert prepared statement
         * @param insertColumnIndex insert column index
         * @throws SQLException on error set
         */
        static void setNullableLong(ResultSet resultSet, int columnIndex, PreparedStatement insertStatement, int insertColumnIndex) throws SQLException
        {
            long longColumn = resultSet.getLong(columnIndex);
            if ((longColumn != 0) || !resultSet.wasNull())
            {
                insertStatement.setLong(insertColumnIndex, longColumn);
            }
            else
            {
                insertStatement.setNull(insertColumnIndex, Types.BIGINT);
            }
        }
    
        /**
         * Set nullable float column from query result set on prepared statement.
         * 
         * @param resultSet query result set
         * @param columnIndex column index
         * @param insertStatement insert prepared statement
         * @throws SQLException on error set
         */
        static void setNullableFloat(ResultSet resultSet, int columnIndex, PreparedStatement insertStatement) throws SQLException
        {
            float floatColumn = resultSet.getFloat(columnIndex);
            if ((floatColumn != 0.0) || !resultSet.wasNull())
            {
                insertStatement.setFloat(columnIndex, floatColumn);
            }
            else
            {
                insertStatement.setNull(columnIndex, Types.FLOAT);
            }
        }

        /**
         * Set nullable float column from query result set on prepared statement.
         * 
         * @param resultSet query result set
         * @param columnIndex column index
         * @param insertStatement insert prepared statement
         * @param insertColumnIndex insert column index
         * @throws SQLException on error set
         */
        static void setNullableFloat(ResultSet resultSet, int columnIndex, PreparedStatement insertStatement, int insertColumnIndex) throws SQLException
        {
            float floatColumn = resultSet.getFloat(columnIndex);
            if ((floatColumn != 0.0) || !resultSet.wasNull())
            {
                insertStatement.setFloat(insertColumnIndex, floatColumn);
            }
            else
            {
                insertStatement.setNull(insertColumnIndex, Types.FLOAT);
            }
        }
    }
}
