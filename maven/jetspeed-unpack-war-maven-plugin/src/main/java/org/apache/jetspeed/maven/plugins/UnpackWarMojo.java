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

import org.apache.jetspeed.maven.utils.UnpackResources;
import org.apache.jetspeed.maven.mojo.AbstractUnpackMojo;
import org.apache.maven.project.MavenProject;

/**
 * UnpackWarMojo provides extraction services.
 * 
 * @version $Id$
 * @goal unpack
 */
public class UnpackWarMojo extends AbstractUnpackMojo
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
    
    /** @parameter expression="${plugin.artifacts}"
     *  @readonly
     **/
    private ArrayList pluginArtifacts;
    
    /** The Maven project.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    protected boolean isSkip()
    {
        return skip;
    }
    
    protected ArrayList getPluginArtifacts()
    {
        return pluginArtifacts;
    }
    
    protected UnpackResources getUnpack()
    {
        return unpack;
    }
    
    protected MavenProject getProject()
    {
        return project;
    }

    protected boolean isVerbose()
    {
        return verbose;
    }
}
