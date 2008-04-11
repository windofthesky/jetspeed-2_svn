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

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.jetspeed.serializer.JetspeedSerializerApplication;
import org.apache.jetspeed.serializer.SerializerException;
import org.apache.jetspeed.tools.ToolsLogger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public class Serializer
{
    private static final String SERIALIZER_APPLICATION_CLASS_NAME = "org.apache.jetspeed.tools.db.serializer.JetspeedSerializerApplicationImpl";

    private String filterPropertiesFileName;    
    private String categories;
    private String applicationRootPath;
    private String[] files;
    private Map initProperties;

    public boolean isConfigered() throws MojoExecutionException
    {
        boolean configured = false;
        
        if (files != null && files.length > 0)
        {
            for (int i = 0; i < files.length; i++)
            {
                if (files[i] == null || files[i].length() == 0)
                {
                    throw new MojoExecutionException( "Serializer seed element ["+i+"] is empty");
                }
                
                if (!(new File(files[i]).exists()))
                {
                    throw new MojoExecutionException( "Serializer seed file or directory "+files[i]+" not found");
                }
            }
            if (applicationRootPath == null)
            {
                throw new MojoExecutionException("Serializer applicationRootPath is required");
            }
            configured = true;
        }
        return configured;
    }
    
    public void execute(Log log) throws MojoExecutionException
    {
        
        JetspeedSerializerApplication app = null;
        try
        {
            Class dfClass = Class.forName(SERIALIZER_APPLICATION_CLASS_NAME, true, Thread.currentThread().getContextClassLoader());
            app = (JetspeedSerializerApplication) dfClass.newInstance();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Cannot find or load JetspeedSerializerApplication class "+ SERIALIZER_APPLICATION_CLASS_NAME, e);
        }
        
        ToolsLogger logger = new MavenToolsLogger(log);
        
        Properties props = null;
        if (initProperties != null)
        {
            props = new Properties();
            // working around an odd Maven on MacOS issue which stored empty mapped properties actually as null values,
            // which isn't allowed for properties
            Iterator iter = initProperties.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry)iter.next();
                if (entry.getValue() == null)
                {
                    entry.setValue("");
                }
            }
            props.putAll(initProperties);
        }
        
        try
        {
            if (files != null && files.length > 0)
            {
                app.importFiles(logger, applicationRootPath, categories, filterPropertiesFileName, props, files);
            }
        }
        catch (SerializerException se)
        {
            throw new MojoExecutionException("Serializer error: ",se);
        }
    }
}
