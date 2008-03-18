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

/**
 * @version $Id$
 *
 */
public class SeedConfig
{
    private String propertiesFile;
    private String applicationPath;
    private String bootConfigFiles;
    private String configFiles;
    private String logLevel;
    private String options;
    
    public String getApplicationPath()
    {
        return applicationPath;
    }
    public void setApplicationPath(String applicationPath)
    {
        this.applicationPath = applicationPath;
    }
    public String getBootConfigFiles()
    {
        return bootConfigFiles;
    }
    public void setBootConfigFiles(String bootConfigFiles)
    {
        this.bootConfigFiles = bootConfigFiles;
    }
    public String getConfigFiles()
    {
        return configFiles;
    }
    public void setConfigFiles(String configFiles)
    {
        this.configFiles = configFiles;
    }
    public String getLogLevel()
    {
        return logLevel;
    }
    public void setLogLevel(String logLevel)
    {
        this.logLevel = logLevel;
    }
    public String getOptions()
    {
        return options;
    }
    public void setOptions(String options)
    {
        this.options = options;
    }
    public String getPropertiesFile()
    {
        return propertiesFile;
    }
    public void setPropertiesFile(String propertiesFile)
    {
        this.propertiesFile = propertiesFile;
    }
    
    
}
