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

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.downloader.Downloader;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * UnpackResourcesMojo provides extracting specific folders within a (remote) resource bundle jar to specific output folders.
 * 
 * @version $Id$
 * @goal unpack-resources
 * @aggregator
 */
public class UnpackResourcesMojo extends AbstractMojo
{
    
    /** The name (prefix) of the (possibly remote) resourceBundle jar containing the resources to unpack.
     * Name must be of form groupId:artifactId:version, like ${project.groupId}:jetspeed-portal-resources:${project.version}
     * @parameter
     * @required
     */
    private String resourceBundle;
    
    /**
     * The target base directory where resources will be unpacked to.
     * @parameter expression="${project.build.directory}"
     */
    private String targetBaseDirectory;
    
    /**
     * When true, skip the execution.
     * @parameter default-value="false"
     */
    private boolean skip;
    
    /**
     * @parameter
     */
    private PlexusConfiguration resources;
    
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
    
    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            this.getLog().info( "Skipping unpack-resources" );
            return;
        }
        
        Resources[] unpackResources = null;
        
        if ( resources == null )
        {
            // extract all to targetBaseDirectory
            unpackResources = new Resources[1];
            unpackResources[0] = new Resources();
        }
        else
        {
            PlexusConfiguration[] configs = resources.getChildren("unpack");
            if ( configs.length == 0 )
            {
                throw new MojoExecutionException("No unpack definitions specified");
            }
            unpackResources = new Resources[configs.length];
            try
            {
                for ( int i = 0; i < configs.length; i++ )
                {
                    unpackResources[i] = new Resources(configs[i]);
                }
            }
            catch (Exception e)
            {
                throw new MojoExecutionException("Failed to parse the resources configuration(s)",e);
            }
        }
        if ( targetBaseDirectory == null || targetBaseDirectory.length() == 0 )
        {
            targetBaseDirectory = project.getBuild().getDirectory();
        }
        File file = ResourceBundleUnpacker.getRemoteResourceBundle(resourceBundle, downloader, localRepository, remoteRepositories, artifactRepositoryFactory, mavenSession);
        ResourceBundleUnpacker.unpackResources(file, targetBaseDirectory, unpackResources, getLog());
    }
}
