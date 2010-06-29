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
 * Jetspeed Migration for Profiler component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedProfilerMigration implements JetspeedMigration
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#getName()
     */
    public String getName()
    {
        return "Profiler";
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#detectSourceVersion(java.sql.Connection, int)
     */
    public int detectSourceVersion(Connection sourceConnection, int sourceVersion) throws SQLException
    {
        // no migration required in profiler schema
        return ((sourceVersion > JETSPEED_SCHEMA_VERSION_UNKNOWN) ? sourceVersion : JETSPEED_SCHEMA_VERSION_2_1_3);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#migrate(java.sql.Connection, int, java.sql.Connection)
     */
    public JetspeedMigrationResult migrate(Connection sourceConnection, int sourceVersion, Connection targetConnection) throws SQLException
    {
        int rowsMigrated = 0;
        
        // PROFILING_RULE
        PreparedStatement profilingRuleInsertStatement = targetConnection.prepareStatement("INSERT INTO PROFILING_RULE (RULE_ID, CLASS_NAME, TITLE) VALUES (?, ?, ?)");
        Statement profilingRuleQueryStatement = sourceConnection.createStatement();
        profilingRuleQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet profilingRuleResultSet = profilingRuleQueryStatement.executeQuery("SELECT RULE_ID, CLASS_NAME, TITLE FROM PROFILING_RULE");
        while (profilingRuleResultSet.next())
        {
            profilingRuleInsertStatement.setString(1, profilingRuleResultSet.getString(1));
            profilingRuleInsertStatement.setString(2, profilingRuleResultSet.getString(2));
            profilingRuleInsertStatement.setString(3, profilingRuleResultSet.getString(3));
            profilingRuleInsertStatement.executeUpdate();
            rowsMigrated++;
        }
        profilingRuleResultSet.close();
        profilingRuleQueryStatement.close();
        profilingRuleInsertStatement.close();
        
        // RULE_CRITERION
        PreparedStatement ruleCriterionInsertStatement = targetConnection.prepareStatement("INSERT INTO RULE_CRITERION (CRITERION_ID, RULE_ID, FALLBACK_ORDER, REQUEST_TYPE, NAME, COLUMN_VALUE, FALLBACK_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?)");
        Statement ruleCriterionQueryStatement = sourceConnection.createStatement();
        ruleCriterionQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet ruleCriterionResultSet = ruleCriterionQueryStatement.executeQuery("SELECT CRITERION_ID, RULE_ID, FALLBACK_ORDER, REQUEST_TYPE, NAME, COLUMN_VALUE, FALLBACK_TYPE FROM RULE_CRITERION");
        while (ruleCriterionResultSet.next())
        {
            ruleCriterionInsertStatement.setString(1, ruleCriterionResultSet.getString(1));
            ruleCriterionInsertStatement.setString(2, ruleCriterionResultSet.getString(2));
            ruleCriterionInsertStatement.setInt(3, ruleCriterionResultSet.getInt(3));
            ruleCriterionInsertStatement.setString(4, ruleCriterionResultSet.getString(4));
            ruleCriterionInsertStatement.setString(5, ruleCriterionResultSet.getString(5));
            ruleCriterionInsertStatement.setString(6, ruleCriterionResultSet.getString(6));
            Static.setNullableInt(ruleCriterionResultSet, 7, ruleCriterionInsertStatement);
            ruleCriterionInsertStatement.executeUpdate();
            rowsMigrated++;
        }
        ruleCriterionResultSet.close();
        ruleCriterionQueryStatement.close();
        ruleCriterionInsertStatement.close();
        
        // PRINCIPAL_RULE_ASSOC
        PreparedStatement principalRuleAssocInsertStatement = targetConnection.prepareStatement("INSERT INTO PRINCIPAL_RULE_ASSOC (PRINCIPAL_NAME, LOCATOR_NAME, RULE_ID) VALUES (?, ?, ?)");
        Statement principalRuleAssocQueryStatement = sourceConnection.createStatement();
        principalRuleAssocQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet principalRuleAssocResultSet = principalRuleAssocQueryStatement.executeQuery("SELECT PRINCIPAL_NAME, LOCATOR_NAME, RULE_ID FROM PRINCIPAL_RULE_ASSOC");
        while (principalRuleAssocResultSet.next())
        {
            principalRuleAssocInsertStatement.setString(1, principalRuleAssocResultSet.getString(1));
            principalRuleAssocInsertStatement.setString(2, principalRuleAssocResultSet.getString(2));
            principalRuleAssocInsertStatement.setString(3, principalRuleAssocResultSet.getString(3));
            principalRuleAssocInsertStatement.executeUpdate();
            rowsMigrated++;
        }
        principalRuleAssocResultSet.close();
        principalRuleAssocQueryStatement.close();
        principalRuleAssocInsertStatement.close();
        
        // PROFILE_PAGE_ASSOC
        PreparedStatement profilePageAssocInsertStatement = targetConnection.prepareStatement("INSERT INTO PROFILE_PAGE_ASSOC (LOCATOR_HASH, PAGE_ID) VALUES (?, ?)");
        Statement profilePageAssocQueryStatement = sourceConnection.createStatement();
        profilePageAssocQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet profilePageAssocResultSet = profilePageAssocQueryStatement.executeQuery("SELECT LOCATOR_HASH, PAGE_ID FROM PROFILE_PAGE_ASSOC");
        while (profilePageAssocResultSet.next())
        {
            profilePageAssocInsertStatement.setString(1, profilePageAssocResultSet.getString(1));
            profilePageAssocInsertStatement.setString(2, profilePageAssocResultSet.getString(2));
            profilePageAssocInsertStatement.executeUpdate();
            rowsMigrated++;
        }
        profilePageAssocResultSet.close();
        profilePageAssocQueryStatement.close();
        profilePageAssocInsertStatement.close();
        
        // OJB_HL_SEQ
        PreparedStatement ojbInsertStatement = targetConnection.prepareStatement("INSERT INTO OJB_HL_SEQ (TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION) VALUES (?, ?, ?, ?, ?)");
        Statement ojbQueryStatement = sourceConnection.createStatement();
        ResultSet ojbResultSet = ojbQueryStatement.executeQuery("SELECT TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION FROM OJB_HL_SEQ WHERE TABLENAME IN ('SEQ_RULE_CRITERION')");
        while (ojbResultSet.next())
        {
            ojbInsertStatement.setString(1, ojbResultSet.getString(1));
            ojbInsertStatement.setString(2, ojbResultSet.getString(2));
            ojbInsertStatement.setInt(3, ojbResultSet.getInt(3));
            ojbInsertStatement.setInt(4, ojbResultSet.getInt(4));
            ojbInsertStatement.setInt(5, ojbResultSet.getInt(5));
            ojbInsertStatement.executeUpdate();
            rowsMigrated++;
        }
        ojbResultSet.close();
        ojbQueryStatement.close();
        ojbInsertStatement.close();
        
        return new JetspeedMigrationResultImpl(rowsMigrated);
    }
}
