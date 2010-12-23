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
package org.apache.jetspeed.maven.mojo;

import java.util.ArrayList;

import org.apache.jetspeed.maven.utils.Artifacts;
import org.apache.jetspeed.maven.utils.UnpackResources;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * AbstractUnpackMojo provides extracting specific folders within a
 * (remote) resource jar to specific output folders.
 * 
 * @version $Id$
 * @goal unpack
 */
public abstract class AbstractUnpackMojo extends AbstractMojo
{
    private Artifacts artifacts;

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if ( isSkip() )
        {
            this.getLog().info( "Skipping unpack" );
            return;
        }

        artifacts = new Artifacts(getPluginArtifacts());
        
        if (getUnpack() != null)
        {
            getUnpack().unpack(artifacts, getProject().getBuild().getDirectory(), getLog(), isVerbose());
        }
    }
    
    protected abstract boolean isSkip();
    
    protected abstract ArrayList getPluginArtifacts();
    
    protected abstract UnpackResources getUnpack();
    
    protected abstract MavenProject getProject();
    
    protected abstract boolean isVerbose();
}
