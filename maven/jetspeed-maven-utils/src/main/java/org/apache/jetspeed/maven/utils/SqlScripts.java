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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.StringTokenizer;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * @version $Id$
 *
 */
public class SqlScripts
{
    public static class Script
    {
        String delimiter;
        String delimiterType;
        String path;
        Boolean ignoreErrors;        
        Boolean escapeProcessing;
    }
    
    private String delimiter = ";";
    private String delimiterType = "normal";
    private Boolean escapeProcessing = Boolean.TRUE;
    private Boolean ignoreErrors = Boolean.FALSE;
    private Script[] scripts;
    
    public boolean isConfigered() throws MojoExecutionException
    {
        if (scripts != null && scripts.length > 0)
        {
            for (int i = 0; i < scripts.length; i++)
            {
                scripts[i].delimiter = getValue(scripts[i].delimiter, delimiter);
                scripts[i].delimiterType = getValue(scripts[i].delimiterType, delimiterType);
                scripts[i].escapeProcessing = getValue(scripts[i].escapeProcessing, escapeProcessing);
                scripts[i].ignoreErrors = getValue(scripts[i].ignoreErrors, ignoreErrors);
                if (scripts[i].path==null||scripts[i].path.length()==0)
                {
                    throw new MojoExecutionException( "sql script["+i+"] path not specified" );
                }
            }
            return true;
        }
        return false;
    }
    
    private static String getValue(String value, String defaultValue)
    {
        return value != null ? value : defaultValue;
    }
    
    private static Boolean getValue(Boolean value, Boolean defaultValue)
    {
        return value != null ? value : defaultValue;
    }
    
    public void execute(DbConnection dbConnection, Log log) throws MojoExecutionException
    {
        Connection connection = dbConnection.getConnection();
        
        log.info("Running sql scripts against: "+dbConnection.getUrl()+" for user: "+dbConnection.getUsername());
        
        try
        {
            for ( int i = 0; i < scripts.length; i++ )
            {
                runScript(connection, scripts[i], log);
            }
        }
        finally
        {
            if ( connection != null )
            {
                try
                {
                    connection.close();
                }
                catch ( SQLException ex )
                {
                    // ignore
                }
            }
        }
    }

    /**
     * read in lines and execute them
     */
    private void runScript( Connection connection, Script script, Log log ) throws MojoExecutionException
    {
        File scriptFile = new File(script.path);
        if ( !scriptFile.exists() || !scriptFile.isFile() )
        {
            throw new MojoExecutionException("SQL script file "+scriptFile.getAbsolutePath()+" not found");
        }
        else
        {
            log.info( "Executing SQL script file: " + scriptFile.getAbsolutePath() );
            Reader reader = null;
            Statement statement = null;
            try
            {
                statement = connection.createStatement();
                statement.setEscapeProcessing(script.escapeProcessing.booleanValue());
                
                reader = new FileReader(scriptFile);

                StringBuffer sql = new StringBuffer();
                String line;

                BufferedReader in = new BufferedReader( reader );

                while ( ( line = in.readLine() ) != null )
                {
                    line = line.trim();

                    if ( line.startsWith( "//" ) )
                    {
                        continue;
                    }
                    if ( line.startsWith( "--" ) )
                    {
                        continue;
                    }
                    StringTokenizer st = new StringTokenizer( line );
                    if ( st.hasMoreTokens() )
                    {
                        String token = st.nextToken();
                        if ( "REM".equalsIgnoreCase( token ) )
                        {
                            continue;
                        }
                    }
                    sql.append( " " ).append( line );

                    // SQL defines "--" as a comment to EOL
                    // and in Oracle it may contain a hint
                    // so we cannot just remove it, instead we must end it
                    if ( line.indexOf( "--" ) >= 0 )
                    {
                        sql.append( "\n" );
                    }
                    
                    if ( ( script.delimiterType.equals( "normal" ) && sql.toString().endsWith( script.delimiter ) )
                        || ( script.delimiterType.equals( "row" ) && line.equals( script.delimiter ) ) )
                    {
                        execSQL( connection, statement, sql.substring( 0, sql.length() - script.delimiter.length() ), script.ignoreErrors.booleanValue(), log );
                        sql.replace( 0, sql.length(), "" );
                    }
                }
                
                // Catch any statements not followed by specified delimiter
                if ( !sql.equals( "" ) )
                {
                    execSQL( connection, statement, sql.toString(), script.ignoreErrors.booleanValue(), log );
                }
            }
            catch (Exception e)
            {
                throw new MojoExecutionException("Unexpected error", e);
            }
            finally
            {
                if ( statement != null )
                {
                    try
                    {
                        statement.close();
                    }
                    catch ( SQLException ex )
                    {
                        // ignore
                    }
                }
                if ( reader != null )
                {
                    try
                    {
                        reader.close();
                    }
                    catch (Exception e)
                    {
                        // ignore
                    }
                }
            }
        }
    }

    /**
     * Exec the sql statement.
     */
    private void execSQL( Connection connection, Statement statement, String sql, boolean ignoreErrors, Log log ) throws MojoExecutionException
    {
        // Check and ignore empty statements
        if ( "".equals( sql.trim() ) )
        {
            return;
        }

        ResultSet resultSet = null;
        try
        {
            log.debug( "SQL: " + sql );

            boolean ret;
            int updateCount, updateCountTotal = 0;

            ret = statement.execute( sql );
            updateCount = statement.getUpdateCount();
            resultSet = statement.getResultSet();
            do
            {
                if ( !ret )
                {
                    if ( updateCount != -1 )
                    {
                        updateCountTotal += updateCount;
                    }
                }
                ret = statement.getMoreResults();
                if ( ret )
                {
                    updateCount = statement.getUpdateCount();
                    resultSet = statement.getResultSet();
                }
            }
            while ( ret );

            if ( updateCountTotal > 0)
            {
                log.debug( updateCountTotal + " rows affected" );

                StringBuffer line = new StringBuffer();
                line.append( updateCountTotal ).append( " rows affected" );
            }

            SQLWarning warning = connection.getWarnings();
            while ( warning != null )
            {
                log.debug( warning + " sql warning" );
                warning = warning.getNextWarning();
            }
            connection.clearWarnings();
        }
        catch ( SQLException e )
        {
            log.error( "Failed to execute: " + sql );
            if ( !ignoreErrors )
            {
                throw new MojoExecutionException("Failed to execute: " + sql, e);
            }
            log.error( e.toString() );
        }
        finally
        {
            if ( resultSet != null )
            {
                try
                {
                    resultSet.close();
                }
                catch (SQLException e)
                {
                    throw new MojoExecutionException("Unexpected SQL exception: ", e);
                }
            }
        }
    }
}
