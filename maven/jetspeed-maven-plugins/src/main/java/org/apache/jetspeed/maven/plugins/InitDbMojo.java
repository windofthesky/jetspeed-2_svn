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
package org.apache.jetspeed.maven.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.downloader.Downloader;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @version $Id$
 * @goal init-db
 * @aggregator
 */
public class InitDbMojo extends AbstractMojo
{
    /** The optional resources dependency definition containing (db init) resources to unpack.
     * @parameter
     */
    private UnpackResources unpackResources;
    
    /**
     * @parameter
     */
    private SQLScript[] sqlScripts;
    
    /**
     * @parameter
     */
    private SeedConfig seedConfig;
    
    /**
     * Database username.  If not given, it will be looked up through 
     * settings.xml's server with ${settingsKey} as key.
     * @parameter expression="${username}" 
     */
    private String username;

    /**
     * Database password. If not given, it will be looked up through settings.xml's 
     * server with ${settingsKey} as key
     * @parameter expression="${password}" 
     */
    private String password;

    /**
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    private Settings settings;
    
    /**
     * Server's id in settings.xml to look up username and password.
     * Default to ${url} if not given.
     * @parameter expression="${settingsKey}" 
     */
    private String settingsKey;    

    /**
     * Database URL
     * @parameter expression="${url}" 
     * @required
     */
    private String url;

    /**
     * Database driver classname
     * @parameter expression="${driver}" 
     * @required
     */
    private String driver;

    /**
     * @parameter default-value="true"
     */
    private boolean escapeProcessing;
    
    /**
     * @parameter default-value=";"
     */
    private String sqlDelimiter;
    
    /**
     * @parameter default-value="normal";
     */
    private String sqlDelimiterType;
    
    /**
     * When true, skip the execution.
     * @parameter default-value="false"
     */
    private boolean skip;
    
    /**
     * The local repository taken from Maven's runtime. Typically $HOME/.m2/repository.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * The remote repositories used as specified in your POM.
     *
     * @parameter expression="${project.repositories}"
     * @required
     * @readonly
     */
    private List remoteRepositories;

    /** The Maven project.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The Maven session.
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession mavenSession;
    
    /**
     * Artifact downloader.
     *
     * @component
     */
    private Downloader downloader;

    /**
     * Artifact repository factory component.
     *
     * @component
     */
    private ArtifactRepositoryFactory artifactRepositoryFactory;
    
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            this.getLog().info( "Skipping init-db" );
            return;
        }

        if ( unpackResources != null && !isEmpty(unpackResources.getResourceBundle()) )
        {
            Resources[] resources = null;
            if ( unpackResources.getResources() == null )
            {
                // extract all to targetBaseDirectory
                resources = new Resources[1];
                resources[0] = new Resources();
            }
            else
            {
                PlexusConfiguration[] configs = unpackResources.getResources().getChildren("unpack");
                if ( configs.length == 0 )
                {
                    throw new MojoExecutionException("No unpack definitions specified");
                }
                resources = new Resources[configs.length];
                try
                {
                    for ( int i = 0; i < configs.length; i++ )
                    {
                        resources[i] = new Resources(configs[i]);
                    }
                }
                catch (Exception e)
                {
                    throw new MojoExecutionException("Failed to parse the unpackResources resources configuration(s)",e);
                }
            }
            File file = ResourceBundleUnpacker.getRemoteResourceBundle(unpackResources.getResourceBundle(), downloader, localRepository, remoteRepositories, artifactRepositoryFactory, mavenSession);
            if ( isEmpty(unpackResources.getTargetBaseDirectory()) )
            {
                unpackResources.setTargetBaseDirectory(project.getBuild().getDirectory());
            }
            ResourceBundleUnpacker.unpackResources(file, unpackResources.getTargetBaseDirectory(), resources, getLog());
        }
        
        if ( (sqlScripts != null && sqlScripts.length > 0) || seedConfig != null )
        {
            loadUserInfoFromSettings();
            
            // validate connection configuration early
            Connection connection = getConnection();
            
            getLog().info("Running init-db against "+url+" for user "+username);
            
            if ( sqlScripts != null )
            {
                Statement statement = null;
                try
                {
                    statement = connection.createStatement();
                    statement.setEscapeProcessing(escapeProcessing);

                    for ( int i = 0; i < sqlScripts.length; i++ )
                    {
                        if ( sqlScripts[i] != null )
                        {
                            runSqlScript(connection, statement, sqlScripts[i]);
                        }
                    }
                }
                catch (SQLException e)
                {
                    throw new MojoExecutionException("Unexpected SQL exception: ", e);
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
                    try
                    {
                        connection.close();
                        connection = null;
                    }
                    catch ( SQLException ex )
                    {
                        // ignore
                    }
                    
                }
            }
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
    private void runSqlScript( Connection connection, Statement statement, SQLScript script ) throws MojoExecutionException
    {
        if ( !isEmpty(script.getPath()) )
        {
            File scriptFile = new File(script.getPath());
            if ( !scriptFile.exists() || !scriptFile.isFile() )
            {
                throw new MojoExecutionException("SQL script file "+scriptFile.getAbsolutePath()+" not found");
            }
            else
            {
                getLog().info( "Executing SQL script file: " + scriptFile.getAbsolutePath() );
                Reader reader = null;
                try
                {
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
                        
                        if ( ( sqlDelimiterType.equals( "normal" ) && sql.toString().endsWith( sqlDelimiter ) )
                            || ( sqlDelimiterType.equals( "row" ) && line.equals( sqlDelimiter ) ) )
                        {
                            execSQL( connection, statement, sql.substring( 0, sql.length() - sqlDelimiter.length() ), script.isIgnoreErrors() );
                            sql.replace( 0, sql.length(), "" );
                        }
                    }
                    
                    // Catch any statements not followed by specified delimiter
                    if ( !sql.equals( "" ) )
                    {
                        execSQL( connection, statement, sql.toString(), script.isIgnoreErrors() );
                    }
                }
                catch (Exception e)
                {
                    
                }
                finally
                {
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
    }

    /**
     * Exec the sql statement.
     */
    private void execSQL( Connection connection, Statement statement, String sql, boolean ignoreErrors ) throws MojoExecutionException
    {
        // Check and ignore empty statements
        if ( "".equals( sql.trim() ) )
        {
            return;
        }

        ResultSet resultSet = null;
        try
        {
            getLog().debug( "SQL: " + sql );

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
                getLog().debug( updateCountTotal + " rows affected" );

                StringBuffer line = new StringBuffer();
                line.append( updateCountTotal ).append( " rows affected" );
            }

            SQLWarning warning = connection.getWarnings();
            while ( warning != null )
            {
                getLog().debug( warning + " sql warning" );
                warning = warning.getNextWarning();
            }
            connection.clearWarnings();
        }
        catch ( SQLException e )
        {
            getLog().error( "Failed to execute: " + sql );
            if ( !ignoreErrors )
            {
                throw new MojoExecutionException("Failed to execute: " + sql, e);
            }
            getLog().error( e.toString() );
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

    /**
     * Load username password from settings if user has not set them in JVM properties
     */
    private void loadUserInfoFromSettings()
    {
        if ( this.settingsKey == null )
        {
            this.settingsKey = url;
        }

        if ( ( username == null || password == null ) && ( settings != null ) )
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

    private Connection getConnection() throws MojoExecutionException
    {
        try
        {
            getLog().debug( "connecting to " + url );
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

            Connection conn = driverInstance.connect( url, info );

            if ( conn == null )
            {
                throw new SQLException( "No suitable Driver for " + url );
            }

            conn.setAutoCommit( true );
            return conn;
        }
        catch ( SQLException e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    private static boolean isEmpty(String value)
    {
        return value == null || value.length() == 0;
    }
}
