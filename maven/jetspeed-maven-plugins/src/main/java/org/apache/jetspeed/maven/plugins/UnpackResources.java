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

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @version $Id$
 *
 */
public class UnpackResources
{
    private String resourceBundle;
    private String targetBaseDirectory;
    private PlexusConfiguration resources;
    private boolean overwrite;
    
    public String getResourceBundle()
    {
        return resourceBundle;
    }
    public void setResourceBundle(String resourceBundle)
    {
        this.resourceBundle = resourceBundle;
    }
    public PlexusConfiguration getResources()
    {
        return resources;
    }
    public void setResources(PlexusConfiguration resources)
    {
        this.resources = resources;
    }
    public String getTargetBaseDirectory()
    {
        return targetBaseDirectory;
    }
    public void setTargetBaseDirectory(String targetBaseDirectory)
    {
        this.targetBaseDirectory = targetBaseDirectory;
    }
    public boolean isOverwrite()
    {
        return overwrite;
    }
    public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
    }
}
