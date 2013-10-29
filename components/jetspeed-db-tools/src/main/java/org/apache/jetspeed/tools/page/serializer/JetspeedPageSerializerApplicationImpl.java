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

package org.apache.jetspeed.tools.page.serializer;

import org.apache.jetspeed.components.JetspeedBeanDefinitionFilter;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.util.Slf4JToolsLogger;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.page.JetspeedPageSerializerApplication;
import org.apache.jetspeed.page.PageSerializer;
import org.apache.jetspeed.page.PageSerializer.Result;
import org.apache.jetspeed.tools.ToolsLogger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

/**
 * Commandline or standalone execution of JetspeedPageSerializer
 * 
 * @version $Id$
 *
 */
public class JetspeedPageSerializerApplicationImpl implements JetspeedPageSerializerApplication
{
    private static final ToolsLogger logger = new Slf4JToolsLogger(LoggerFactory.getLogger(JetspeedPageSerializerApplicationImpl.class));
    
    public static void main(String[] args) throws Exception
    {
        JetspeedPageSerializerApplicationImpl app = new JetspeedPageSerializerApplicationImpl();
        
        String propertyFileName = null;
        String applicationRootPath = null;
        String psmlPagesPath = null;
        String categories = null;
        boolean doExport = false;
        boolean doImport = false;                
        String rootFolder = null;
        
        if (args == null)
        {
            if (args == null)
            {
                System.out.println("Usage for import: -I rootFolder [-psml psmlPagesPath] -c <categories | categories filter key (requires -p)> [-p <categories properties file>]");
                System.out.println("Usage for export: -E rootFolder [-psml psmlPagesPath] -c <categories | categories filter key (requires -p)> [-p <categories properties file>]");
            }
        }

        // Parse all the command-line arguments
        for (int n = 0; n < args.length; n++)
        {
            if (args[n].equals("-c"))
                categories = args[++n];
            else if (args[n].equals("-p"))
                propertyFileName = args[++n];
            else if (args[n].equals("-a"))
                applicationRootPath = args[++n];
            else if (args[n].equals("-I"))
            {
                doImport = true;
                rootFolder = args[++n];
            } 
            else if (args[n].equals("-E"))
            {
                doExport = true;
                rootFolder = args[++n];
            } 
            else if (args[n].equals("-psml"))
            {
                psmlPagesPath = args[++n];
            }
            else
            {
                throw new IllegalArgumentException("Unknown argument: " + args[n]);
            }
        }
        
        if ((!doImport) && (!doExport))
        {
          throw new IllegalArgumentException("Either import or export have to be defined (-I or -E followed by the rootFolder");
        }

        if ((doImport) && (doExport))
        {
            throw new IllegalArgumentException("Only one - either import or export - can be requested");
        }
        

        if (categories == null)
        {
            throw new IllegalArgumentException("Argument -c defining the assembly categories filters (or the propertyFile key to it) is required");
        }
        
        if (applicationRootPath == null)
        {
            throw new IllegalArgumentException("Argument -a specifying the path to (web)application root, is required");
        }
        Properties initProperties = new Properties();
        if (psmlPagesPath != null)
        {
            initProperties.put("psml.pages.path", psmlPagesPath);
        }
        if (doImport)
        {
            app.execute(logger, applicationRootPath, categories, propertyFileName, initProperties, rootFolder, true);
        }
        if (doExport)
        {
            app.execute(logger, applicationRootPath, categories, propertyFileName, initProperties, rootFolder, false);
        }
    }            

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.page.serializer.JetspeedPageSerializerApplication#importPages(org.apache.jetspeed.tools.ToolsLogger, java.lang.String, java.lang.String, java.lang.String, java.util.Properties, java.lang.String)
     */
    public Result importPages(ToolsLogger logger, String applicationRootPath, String categories, String filterPropertiesFileName, Properties initProperties, String rootFolder) throws JetspeedException
    {
        return execute(logger, applicationRootPath, categories, filterPropertiesFileName, initProperties, rootFolder, true);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.page.serializer.JetspeedPageSerializerApplication#exportPages(org.apache.jetspeed.tools.ToolsLogger, java.lang.String, java.lang.String, java.lang.String, java.util.Properties, java.lang.String)
     */
    public Result exportPages(ToolsLogger logger, String applicationRootPath, String categories, String filterPropertiesFileName, Properties initProperties, String rootFolder) throws JetspeedException
    {
        return execute(logger, applicationRootPath, categories, filterPropertiesFileName, initProperties, rootFolder, false);
    }

    private Result execute(ToolsLogger logger, String applicationRootPath, String categories, String filterPropertiesFileName, Properties initProperties, String rootFolder, boolean importing) throws JetspeedException
    {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        SpringComponentManager scm = null;
        PageSerializer.Result result = null;
        try
        {
            JetspeedBeanDefinitionFilter filter = null;
            
            if (filterPropertiesFileName != null)
            {
                filter = new JetspeedBeanDefinitionFilter("file:"+filterPropertiesFileName, categories);
            }
            else
            {
                filter = new JetspeedBeanDefinitionFilter(categories);
            }
            
            String assemblyRootPath = "file:"+applicationRootPath+"/WEB-INF/assembly";
            String[] bootConfig = {assemblyRootPath+"/boot/*.xml"};
            String[] appConfig = { assemblyRootPath+"/*.xml", assemblyRootPath+"/override/*.xml" };                       
            
            ClassLoader extendedClassLoader = contextClassLoader;
            File webInfClasses = new File(applicationRootPath, "WEB-INF/classes");
            if (webInfClasses.exists())
            {
                extendedClassLoader = new URLClassLoader(new URL[]{webInfClasses.toURL()}, contextClassLoader);
            }
            Thread.currentThread().setContextClassLoader(extendedClassLoader);
            
            if (initProperties == null)
            {
                initProperties = new Properties();
            }
            initProperties.put("page.manager.permissions.security", "false");
            initProperties.put("page.manager.constraints.security", "false");
            
            scm = new SpringComponentManager(filter, bootConfig, appConfig, applicationRootPath, initProperties, true);
            scm.start();
            
            PageSerializer serializer = scm.lookupComponent(PageSerializer.class.getName());
            if (importing)
            {
                result = serializer.importPages(logger, rootFolder);
            }
            else
            {
                result = serializer.exportPages(logger, rootFolder);
            }
        }
        catch (Exception e)
        {
            logger.error(e);
            if (e instanceof JetspeedException)
            {
                throw (JetspeedException)e;
            }
            throw new JetspeedException(e);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            if (scm != null)
            {
                scm.stop();
            }
        }
        return result;
    }
}
