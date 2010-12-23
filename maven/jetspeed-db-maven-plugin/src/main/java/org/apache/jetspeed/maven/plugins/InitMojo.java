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

import java.util.ArrayList;

import org.apache.jetspeed.maven.utils.Artifacts;
import org.apache.jetspeed.maven.utils.DbConnection;
import org.apache.jetspeed.maven.utils.PageSerializer;
import org.apache.jetspeed.maven.utils.Serializer;
import org.apache.jetspeed.maven.utils.SqlScripts;
import org.apache.jetspeed.maven.utils.UnpackResources;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

/**
 * @version $Id$
 * @goal init
 */
public class InitMojo extends AbstractMojo
{
    /**
     * Database connection
     * @parameter 
     */
    private DbConnection connection;

    /**
     * @parameter
     */
    private SqlScripts sql;
    
    /**
     * @parameter
     */
    private Serializer seed;
    
    /**
     * @parameter
     */
    private PageSerializer psml;
    
    /**
     * @parameter
     */
    private UnpackResources unpack;
    
    /**
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    private Settings settings;
    
    /**
     * When true, skip the execution.
     * @parameter default-value="false"
     */
    private boolean skip;
    
    /** @parameter expression="${plugin.artifacts}"
     *  @readonly
     **/
    private ArrayList pluginArtifacts;
    private Artifacts artifacts;
    
    /** The Maven project.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            this.getLog().info( "Skipping db init" );
            return;
        }
        
        artifacts = new Artifacts(pluginArtifacts);
        
        if (unpack != null)
        {
            unpack.unpack(artifacts, project.getBuild().getDirectory(), getLog(), false);
        }
        
        if (sql != null && sql.isConfigered())
        {
            if (connection == null)
            {
                throw new MojoExecutionException("Cannot execute sql without a connection configuration");
            }
            connection.checkSettings(settings);
            sql.execute(connection, getLog());
        }
        
        if (seed != null && seed.isConfigered())
        {
            seed.execute(getLog());
        }
        
        if (psml != null && psml.isConfigered())
        {
            psml.execute(getLog());
        }
    }
}
