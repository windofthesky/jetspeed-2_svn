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
package org.apache.jetspeed.tools.db.serializer;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.administration.PortalConfigurationConstants;
import org.apache.jetspeed.components.JetspeedBeanDefinitionFilter;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.util.Slf4JLoggerToolsLogger;
import org.apache.jetspeed.serializer.JetspeedSerializer;
import org.apache.jetspeed.serializer.JetspeedSerializerApplication;
import org.apache.jetspeed.serializer.SerializerException;
import org.apache.jetspeed.tools.ToolsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Jetspeed Serializer Application
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedSerializerApplicationImpl implements JetspeedSerializerApplication
{    
    private static final Logger logger = LoggerFactory.getLogger(JetspeedSerializerApplicationImpl.class);
    
    public static void main(String[] args) throws Exception
    {
        JetspeedSerializerApplicationImpl app = new JetspeedSerializerApplicationImpl();
        
        String propertyFileName = null;
        String applicationRootPath = null;
        String categories = null;
        
        boolean doExport = false;
        boolean doImport = false;
        
        String fileName = null;
        String exportName = null;
        
        if (args == null)
        {
            System.out.println("Usage for import: -I importFiles (, delimited if multiple) -a applicationRootFolder -c <categories | categories filter key (requires -p)> [-p <categories properties file>]");
            System.out.println("Usage for export: -E exportFile  -a applicationRootFolder -c <categories | categories filter key (requires -p)> [-p <categories properties file>] [-N <exportName>");
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
            else if (args[n].equals("-E"))
            {
                doExport = true;
                fileName = args[++n];
            } 
            else if (args[n].equals("-I"))
            {
                doImport = true;
                fileName = args[++n];
            } 
            else if (args[n].equals("-N"))
            {
                exportName = args[++n];
            }
            else
            {
                throw new IllegalArgumentException("Unknown argument: " + args[n]);
            }
        }
        
        if ((!doImport) && (!doExport))
        {
          throw new IllegalArgumentException("Either import or export have to be defined (-I or -E followed by the filename");
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
        
        if (doImport)
        {
            app.importFiles(logger, applicationRootPath, categories, propertyFileName, null, getTokens(fileName));
        }
        else if (doExport)
        {
            app.export(logger, applicationRootPath, categories, propertyFileName, null, fileName, exportName);
        }
    }
        
    
    public static  String[] getTokens(String _line)
    {
        if ((_line == null) || (_line.length() == 0))
            return null;
        
        StringTokenizer st = new StringTokenizer(_line, ",");
        int count = st.countTokens();
        String tokens[] = new String[count];
        for (int i = 0; i < count; i++)
        {
            tokens[i] = st.nextToken();
        }
        return tokens;
    }
    
    public JetspeedSerializerApplicationImpl()
    {        
    }
       
    public void importFiles(ToolsLogger logger, String applicationRootPath, String categories, String filterPropertiesFileName, Properties initProperties, String[] seedFiles) throws SerializerException
    {        
        importFiles(new Slf4JLoggerToolsLogger(logger), applicationRootPath, categories, filterPropertiesFileName, initProperties, seedFiles);
    }
    
    
    public void importFiles(Logger logger, String applicationRootPath, String categories, String filterPropertiesFileName, Properties initProperties, String[] seedFiles) throws SerializerException
    {        
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        SpringComponentManager scm = null;
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
            
            scm = new SpringComponentManager(filter, bootConfig, appConfig, applicationRootPath, initProperties, true);
            scm.start();
            Configuration config = scm.lookupComponent("portal_configuration");
            if (config != null)
            {
            	new JetspeedActions(config.getStringArray(PortalConfigurationConstants.SUPPORTED_PORTLET_MODES), config.getStringArray(PortalConfigurationConstants.SUPPORTED_WINDOW_STATES));
            }
            JetspeedSerializer serializer = scm.lookupComponent(JetspeedSerializer.class.getName());
            HashMap settings = new HashMap();
            settings.put(JetspeedSerializer.KEY_LOGGER, logger);
            if (seedFiles != null)
            {
                for (int i = 0; i < seedFiles.length; i++)
                {
                    serializer.importData(seedFiles[i], settings);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error(e.getMessage(),e);
            if (e instanceof SerializerException)
            {
                throw (SerializerException)e;
            }
            throw new SerializerException(SerializerException.IMPORT_ERROR.create(e.getMessage()));
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            if (scm != null)
            {
                scm.stop();
            }
        }
    }

    public void export(ToolsLogger logger, String applicationRootPath, String categories, String filterPropertiesFileName, Properties initProperties, String exportFile, String exportName) throws SerializerException
    {
        export(new Slf4JLoggerToolsLogger(logger), applicationRootPath, categories, filterPropertiesFileName, initProperties, exportFile, exportName);
    }
    
    public void export(Logger logger, String applicationRootPath, String categories, String filterPropertiesFileName, Properties initProperties, String exportFile, String exportName) throws SerializerException
    {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        SpringComponentManager scm = null;
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
            
            scm = new SpringComponentManager(filter, bootConfig, appConfig, applicationRootPath, true);
            scm.start();
            Configuration config = scm.lookupComponent("portal_configuration");
            if (config != null)
            {
            	new JetspeedActions(config.getStringArray(PortalConfigurationConstants.SUPPORTED_PORTLET_MODES), config.getStringArray(PortalConfigurationConstants.SUPPORTED_WINDOW_STATES));
            }            
            JetspeedSerializer serializer = scm.lookupComponent(JetspeedSerializer.class.getName());
            HashMap settings = new HashMap();
            settings.put(JetspeedSerializer.KEY_LOGGER, logger);
            if (exportFile != null)
            {
                if (exportName == null)
                {
                    exportName = JetspeedSerializer.DEFAULT_TAG_SNAPSHOT_NAME;
                }
                serializer.exportData(exportName, exportFile, settings);
            }
            scm.stop();
        }
        catch (Exception e)
        {
            if (e instanceof SerializerException)
            {
                throw (SerializerException)e;
            }
            logger.error(e.getMessage(),e);
            throw new SerializerException(SerializerException.EXPORT_ERROR.create(e.getMessage()));
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            if (scm != null)
            {
                scm.stop();
            }
        }
    }
}