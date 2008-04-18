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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.page.JetspeedPageSerializerApplication;
import org.apache.jetspeed.tools.ToolsLogger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * @version $Id$
 *
 */
public class PageSerializer
{
    private static final String PAGE_SERIALIZER_APPLICATION_CLASS_NAME = "org.apache.jetspeed.tools.page.serializer.JetspeedPageSerializerApplicationImpl";

    private String filterPropertiesFileName;    
    private String categories;
    private String applicationRootPath;
    private String psmlPagesPath;
    private String rootFolder = "/";
    private boolean importing = true;
    private Map initProperties;

    public boolean isConfigered() throws MojoExecutionException
    {
        boolean configured = false;
        
        if (applicationRootPath == null)
        {
            throw new MojoExecutionException("PageSerializer applicationRootPath is required");
        }
        configured = true;
        return configured;
    }
    
    public void execute(Log log) throws MojoExecutionException
    {
        
        JetspeedPageSerializerApplication app = null;
        try
        {
            Class dfClass = Class.forName(PAGE_SERIALIZER_APPLICATION_CLASS_NAME, true, Thread.currentThread().getContextClassLoader());
            app = (JetspeedPageSerializerApplication) dfClass.newInstance();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Cannot find or load JetspeedPageSerializerApplication class "+ PAGE_SERIALIZER_APPLICATION_CLASS_NAME, e);
        }
        
        ToolsLogger logger = new MavenToolsLogger(log);
        
        Properties props = new Properties();
        if (initProperties != null)
        {
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
        if (psmlPagesPath != null)
        {
            props.put("psml.pages.path", psmlPagesPath);
        }
        
        try
        {
            if (importing)
            {
                app.importPages(logger, applicationRootPath, categories, filterPropertiesFileName, props, rootFolder);
            }
            else
            {
                app.exportPages(logger, applicationRootPath, categories, filterPropertiesFileName, props, rootFolder);
            }
        }
        catch (JetspeedException je)
        {
            throw new MojoExecutionException("PageSerializer error: ",je);
        }
    }
}
