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

import java.util.Set;

import org.apache.jetspeed.maven.utils.Artifacts;
import org.apache.jetspeed.maven.utils.UnpackResources;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * UnpackResourcesMojo provides extracting specific folders within a (remote) resource bundle jar to specific output folders.
 * 
 * @version $Id$
 * @goal unpack
 */
public class UnpackMojo extends AbstractMojo
{
    /**
     * When true, skip the execution.
     * @parameter default-value="false"
     */
    private boolean skip;
    
    /**
     * When true, INFO log copied/skipped resources
     * @parameter default-value="false"
     */
    private boolean verbose;
    
    /**
     * @parameter
     */
    private UnpackResources unpack;
    
    /** @parameter expression="${plugin.introducedDependencyArtifacts}"
     *  @readonly
     **/
    private Set pluginDependencyArtifacts;
    private Artifacts artifacts;
    
    /** The Maven project.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            this.getLog().info( "Skipping unpack" );
            return;
        }

        artifacts = new Artifacts(pluginDependencyArtifacts);
        
        if (unpack != null)
        {
            unpack.unpack(artifacts, project.getBuild().getDirectory(), getLog(), false);
        }
    }
}
