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
 * Jetspeed Migration for Capabilities component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedCapabilitiesMigration implements JetspeedMigration
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#getName()
     */
    public String getName()
    {
        return "Capabilities";
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#detectSourceVersion(java.sql.Connection, int)
     */
    public int detectSourceVersion(Connection sourceConnection, int sourceVersion) throws SQLException
    {
        // no migration required in capabilities schema
        return ((sourceVersion > JETSPEED_SCHEMA_VERSION_UNKNOWN) ? sourceVersion : JETSPEED_SCHEMA_VERSION_2_1_3);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigration#migrate(java.sql.Connection, int, java.sql.Connection, org.apache.jetspeed.tools.migration.JetspeedMigrationListener)
     */
    public JetspeedMigrationResult migrate(Connection sourceConnection, int sourceVersion, Connection targetConnection, JetspeedMigrationListener migrationListener) throws SQLException
    {
        int rowsMigrated = 0;
        
        // MEDIA_TYPE
        PreparedStatement mediaTypeInsertStatement = targetConnection.prepareStatement("INSERT INTO MEDIA_TYPE (MEDIATYPE_ID, NAME, CHARACTER_SET, TITLE, DESCRIPTION) VALUES (?, ?, ?, ?, ?)");
        Statement mediaTypeQueryStatement = sourceConnection.createStatement();
        mediaTypeQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet mediaTypeResultSet = mediaTypeQueryStatement.executeQuery("SELECT MEDIATYPE_ID, NAME, CHARACTER_SET, TITLE, DESCRIPTION FROM MEDIA_TYPE");
        while (mediaTypeResultSet.next())
        {
            mediaTypeInsertStatement.setInt(1, mediaTypeResultSet.getInt(1));
            mediaTypeInsertStatement.setString(2, mediaTypeResultSet.getString(2));
            mediaTypeInsertStatement.setString(3, mediaTypeResultSet.getString(3));
            mediaTypeInsertStatement.setString(4, mediaTypeResultSet.getString(4));
            mediaTypeInsertStatement.setString(5, mediaTypeResultSet.getString(5));
            mediaTypeInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        mediaTypeResultSet.close();
        mediaTypeQueryStatement.close();
        mediaTypeInsertStatement.close();
        
        // CLIENT
        PreparedStatement clientInsertStatement = targetConnection.prepareStatement("INSERT INTO CLIENT (CLIENT_ID, EVAL_ORDER, NAME, USER_AGENT_PATTERN, MANUFACTURER, MODEL, VERSION, PREFERRED_MIMETYPE_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        Statement clientQueryStatement = sourceConnection.createStatement();
        clientQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet clientResultSet = clientQueryStatement.executeQuery("SELECT CLIENT_ID, EVAL_ORDER, NAME, USER_AGENT_PATTERN, MANUFACTURER, MODEL, VERSION, PREFERRED_MIMETYPE_ID FROM CLIENT");
        while (clientResultSet.next())
        {
            clientInsertStatement.setInt(1, clientResultSet.getInt(1));
            clientInsertStatement.setInt(2, clientResultSet.getInt(2));
            clientInsertStatement.setString(3, clientResultSet.getString(3));
            clientInsertStatement.setString(4, clientResultSet.getString(4));
            clientInsertStatement.setString(5, clientResultSet.getString(5));
            clientInsertStatement.setString(6, clientResultSet.getString(6));
            clientInsertStatement.setString(7, clientResultSet.getString(7));
            clientInsertStatement.setInt(8, clientResultSet.getInt(8));
            clientInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        clientResultSet.close();
        clientQueryStatement.close();
        clientInsertStatement.close();
        
        // MIMETYPE
        PreparedStatement mimeTypeInsertStatement = targetConnection.prepareStatement("INSERT INTO MIMETYPE (MIMETYPE_ID, NAME) VALUES (?, ?)");
        Statement mimeTypeQueryStatement = sourceConnection.createStatement();
        mimeTypeQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet mimeTypeResultSet = mimeTypeQueryStatement.executeQuery("SELECT MIMETYPE_ID, NAME FROM MIMETYPE");
        while (mimeTypeResultSet.next())
        {
            mimeTypeInsertStatement.setInt(1, mimeTypeResultSet.getInt(1));
            mimeTypeInsertStatement.setString(2, mimeTypeResultSet.getString(2));
            mimeTypeInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        mimeTypeResultSet.close();
        mimeTypeQueryStatement.close();
        mimeTypeInsertStatement.close();
        
        // CAPABILITY
        PreparedStatement capabilityInsertStatement = targetConnection.prepareStatement("INSERT INTO CAPABILITY (CAPABILITY_ID, CAPABILITY) VALUES (?, ?)");
        Statement capabilityQueryStatement = sourceConnection.createStatement();
        capabilityQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet capabilityResultSet = capabilityQueryStatement.executeQuery("SELECT CAPABILITY_ID, CAPABILITY FROM CAPABILITY");
        while (capabilityResultSet.next())
        {
            capabilityInsertStatement.setInt(1, capabilityResultSet.getInt(1));
            capabilityInsertStatement.setString(2, capabilityResultSet.getString(2));
            capabilityInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);            
        }
        capabilityResultSet.close();
        capabilityQueryStatement.close();
        capabilityInsertStatement.close();

        // CLIENT_TO_CAPABILITY
        PreparedStatement clientToCapabilityInsertStatement = targetConnection.prepareStatement("INSERT INTO CLIENT_TO_CAPABILITY (CLIENT_ID, CAPABILITY_ID) VALUES (?, ?)");
        Statement clientToCapabilityQueryStatement = sourceConnection.createStatement();
        clientToCapabilityQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet clientToCapabilityResultSet = clientToCapabilityQueryStatement.executeQuery("SELECT CLIENT_ID, CAPABILITY_ID FROM CLIENT_TO_CAPABILITY");
        while (clientToCapabilityResultSet.next())
        {
            clientToCapabilityInsertStatement.setInt(1, clientToCapabilityResultSet.getInt(1));
            clientToCapabilityInsertStatement.setInt(2, clientToCapabilityResultSet.getInt(2));
            clientToCapabilityInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        clientToCapabilityResultSet.close();
        clientToCapabilityQueryStatement.close();
        clientToCapabilityInsertStatement.close();

        // CLIENT_TO_MIMETYPE
        PreparedStatement clientToMimeTypeInsertStatement = targetConnection.prepareStatement("INSERT INTO CLIENT_TO_MIMETYPE (CLIENT_ID, MIMETYPE_ID) VALUES (?, ?)");
        Statement clientToMimeTypeQueryStatement = sourceConnection.createStatement();
        clientToMimeTypeQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet clientToMimeTypeResultSet = clientToMimeTypeQueryStatement.executeQuery("SELECT CLIENT_ID, MIMETYPE_ID FROM CLIENT_TO_MIMETYPE");
        while (clientToMimeTypeResultSet.next())
        {
            clientToMimeTypeInsertStatement.setInt(1, clientToMimeTypeResultSet.getInt(1));
            clientToMimeTypeInsertStatement.setInt(2, clientToMimeTypeResultSet.getInt(2));
            clientToMimeTypeInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        clientToMimeTypeResultSet.close();
        clientToMimeTypeQueryStatement.close();
        clientToMimeTypeInsertStatement.close();

        // MEDIATYPE_TO_CAPABILITY
        PreparedStatement mediaTypeToCapabilityInsertStatement = targetConnection.prepareStatement("INSERT INTO MEDIATYPE_TO_CAPABILITY (MEDIATYPE_ID, CAPABILITY_ID) VALUES (?, ?)");
        Statement mediaTypeToCapabilityQueryStatement = sourceConnection.createStatement();
        mediaTypeToCapabilityQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet mediaTypeToCapabilityResultSet = mediaTypeToCapabilityQueryStatement.executeQuery("SELECT MEDIATYPE_ID, CAPABILITY_ID FROM MEDIATYPE_TO_CAPABILITY");
        while (mediaTypeToCapabilityResultSet.next())
        {
            mediaTypeToCapabilityInsertStatement.setInt(1, mediaTypeToCapabilityResultSet.getInt(1));
            mediaTypeToCapabilityInsertStatement.setInt(2, mediaTypeToCapabilityResultSet.getInt(2));
            mediaTypeToCapabilityInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        mediaTypeToCapabilityResultSet.close();
        mediaTypeToCapabilityQueryStatement.close();
        mediaTypeToCapabilityInsertStatement.close();

        // MEDIATYPE_TO_MIMETYPE
        PreparedStatement mediaTypeToMimeTypeInsertStatement = targetConnection.prepareStatement("INSERT INTO MEDIATYPE_TO_MIMETYPE (MEDIATYPE_ID, MIMETYPE_ID) VALUES (?, ?)");
        Statement mediaTypeToMimeTypeQueryStatement = sourceConnection.createStatement();
        mediaTypeToMimeTypeQueryStatement.setFetchSize(FETCH_SIZE);
        ResultSet mediaTypeToMimeTypeResultSet = mediaTypeToMimeTypeQueryStatement.executeQuery("SELECT MEDIATYPE_ID, MIMETYPE_ID FROM MEDIATYPE_TO_MIMETYPE");
        while (mediaTypeToMimeTypeResultSet.next())
        {
            mediaTypeToMimeTypeInsertStatement.setInt(1, mediaTypeToMimeTypeResultSet.getInt(1));
            mediaTypeToMimeTypeInsertStatement.setInt(2, mediaTypeToMimeTypeResultSet.getInt(2));
            mediaTypeToMimeTypeInsertStatement.executeUpdate();
            rowsMigrated++;
            migrationListener.rowMigrated(targetConnection);
        }
        mediaTypeToMimeTypeResultSet.close();
        mediaTypeToMimeTypeQueryStatement.close();
        mediaTypeToMimeTypeInsertStatement.close();
        
        // OJB_HL_SEQ
        PreparedStatement ojbInsertStatement = targetConnection.prepareStatement("INSERT INTO OJB_HL_SEQ (TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION) VALUES (?, ?, ?, ?, ?)");
        Statement ojbQueryStatement = sourceConnection.createStatement();
        ResultSet ojbResultSet = ojbQueryStatement.executeQuery("SELECT TABLENAME, FIELDNAME, MAX_KEY, GRAB_SIZE, VERSION FROM OJB_HL_SEQ WHERE TABLENAME IN ('SEQ_CAPABILITY', 'SEQ_CLIENT', 'SEQ_MEDIA_TYPE', 'SEQ_MIMETYPE')");
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
