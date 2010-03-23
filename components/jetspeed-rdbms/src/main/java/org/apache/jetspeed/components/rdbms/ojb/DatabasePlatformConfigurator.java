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
package org.apache.jetspeed.components.rdbms.ojb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.metadata.JdbcMetadataUtils;
import org.apache.ojb.broker.metadata.MetadataManager;

/**
 * Dynamically configures Database Platform for OJB by looking at the connection string
 * and figuring out the OJB platform using an OJB metadata utility
 * Its important to get this right otherwise you will be sending the wrong (most likely HSQL)
 * flavor of SQL statements to the backend database.
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: $
 *          
 */
public class DatabasePlatformConfigurator
{
    private static final Logger log = LoggerFactory.getLogger(DatabasePlatformConfigurator.class);
    
    private DataSource ds;
    private String jcdAlias;
    
    public DatabasePlatformConfigurator(DataSource ds, String jndiName)
    {
        this.ds = ds;
        this.jcdAlias = jndiName;
    }
    
    public void init()
    throws Exception
    {
        ConnectionRepository cr = MetadataManager.getInstance().connectionRepository();
        JdbcConnectionDescriptor jcd = cr.getDescriptor(new PBKey(jcdAlias));
        if (jcd == null)
        {
            jcd = new JdbcConnectionDescriptor();
            jcd.setJcdAlias(jcdAlias);
            cr.addDescriptor(jcd);
        }
        
        JdbcMetadataUtils jdbcMetadataUtils = new JdbcMetadataUtils ();
        jdbcMetadataUtils.fillJCDFromDataSource(jcd, ds, null, null);
        String platform = jcd.getDbms();
        if (JdbcMetadataUtils.PLATFORM_ORACLE.equals(platform)) 
        {
            // Postprocess to find Oracle version.
                platform = updateOraclePlatform (jcd, ds, platform);
        }
        // if platform has explicitly been set, the value takes precedence
        if (platform != null) 
        {
            if (!platform.equals(jcd.getDbms())) 
            {
                log.warn ("Automatically derived RDBMS platform \"" + jcd.getDbms()
                          + "\" differs from explicitly set platform \"" + platform + "\""); 
            }
            jcd.setDbms(platform);
        } 
        else 
        {
            platform = jcd.getDbms();
        }
        if (log.isInfoEnabled()) {
        	log.info("Detected database platform: " + platform);
        }
    }
 
    /**
     * @param jcd
     * @throws LookupException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * throws SQLException
     */
    private String updateOraclePlatform(JdbcConnectionDescriptor jcd, DataSource ds, String platform)
        throws LookupException, IllegalAccessException, InstantiationException, SQLException 
    {
        Connection con = null;
        try 
        {
            con = ds.getConnection();
            DatabaseMetaData metaData = con.getMetaData();
            int rdbmsVersion = 0;
            try 
            {
                // getDatabaseMajorVersion exists since 1.4, so it may
                // not be defined for the driver used.
                rdbmsVersion = metaData.getDatabaseMajorVersion();
            } 
            catch (Throwable t) 
            {
                String dbVersion = metaData.getDatabaseProductVersion();
                String relKey = "Release";
                String major = dbVersion;
                int startPos = dbVersion.indexOf(relKey);
                if (startPos < 0)
                {
                    log.warn ("Cannot determine Oracle version, no \"Release\" in procuct version: \"" + dbVersion + "\"");
                    return platform;
                }
                startPos += relKey.length();
                int dotPos = dbVersion.indexOf('.', startPos);
                if (dotPos > 0) {
                    major = dbVersion.substring(startPos, dotPos).trim();
                }
                try
                {
                    rdbmsVersion = Integer.parseInt(major);
                }
                catch (NumberFormatException e)
                {
                    log.warn ("Cannot determine Oracle version, product version \"" + dbVersion + "\" not layed out as \"... Release N.M.....\"");
                    return platform;
                }
                if (log.isDebugEnabled())
                {
                    log.debug ("Extracted Oracle major version " + rdbmsVersion + " from product version \"" + dbVersion + "\"");
                }
            }
            if (rdbmsVersion >= 9) 
            {
                jcd.setDbms(JdbcMetadataUtils.PLATFORM_ORACLE9I);
                return JdbcMetadataUtils.PLATFORM_ORACLE9I;
            }
        }
        finally
        {
            if (con != null) 
            {
                con.close ();
            }
        }
        return platform;
    }
    
    
}