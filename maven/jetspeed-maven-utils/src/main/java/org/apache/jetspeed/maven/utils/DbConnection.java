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
package org.apache.jetspeed.maven.utils;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

/**
 * @version $Id$
 * 
 */
public class DbConnection
{
    private String username;
    private String password;
    private String driver;
    private String url;
    private String settingsKey;
    
    public String getUrl()
    {
        return url;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void checkSettings(Settings settings)
    {
        if (username==null && password==null && settings!=null && settingsKey!=null)
        {
            Server server = settings.getServer( settingsKey );

            if ( server != null )
            {
                if ( username == null )
                {
                    username = server.getUsername();
                }

                if ( password == null )
                {
                    password = server.getPassword();
                }
            }
        }

        if ( username == null )
        {
            username = "";
        }

        if ( password == null )
        {
            password = "";
        }
    }
    
    
    public Connection getConnection() throws MojoExecutionException
    {
        Connection connection = null;
        
        if (driver == null)
        {
            throw new MojoExecutionException( "dbConnection.driver attribute not specified" );
        }
        
        if (url == null)
        {
            throw new MojoExecutionException( "dbConnection.url attribute not specified" );
        }
        
        try
        {
            Properties info = new Properties();
            info.put( "user", username );
            info.put( "password", password );
            Driver driverInstance = null;
            try
            {
                Class dc = Class.forName( driver );
                driverInstance = (Driver) dc.newInstance();
            }
            catch ( ClassNotFoundException e )
            {
                throw new MojoExecutionException( "Driver class not found: " + driver, e );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Failure loading driver: " + driver, e );
            }

            connection = driverInstance.connect( url, info );

            if ( connection == null )
            {
                throw new SQLException( "No suitable Driver for " + url );
            }

            connection.setAutoCommit( true );
        }
        catch ( SQLException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
        
        return connection;
    }
}
