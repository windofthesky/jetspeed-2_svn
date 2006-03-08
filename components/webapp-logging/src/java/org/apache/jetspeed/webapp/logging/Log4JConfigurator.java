/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.webapp.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.RootCategory;

public class Log4JConfigurator implements ServletContextListener
{
    /** The optional web.xml context parameter name for the Log4J Config File (location). 
     *  The specified location is interpreted as begin (webapp) context relative.
     */
    public static final String LOG4J_CONFIG_FILE = "log4j.config.file";

    /** The default value for the Log4J Config File (location) */
    public static final String LOG4J_CONFIG_FILE_DEFAULT = "/WEB-INF/log4j.properties";

    /** The optional web.xml context parameter name for the Log4J Config webApplicationRoot (property) */
    public static final String LOG4J_CONFIG_WEB_APPLICATION_ROOT_KEY = "log4j.config.webApplicationRoot.key";

    /** The default value for the Log4J Config webApplicationRoot (property) */
    public static final String LOG4J_CONFIG_WEB_APPLICATION_ROOT_KEY_DEFAULT = "webApplicationRoot";
    
    private Hierarchy isolatedHierarchy;
    
    private static Log log;

    public void contextInitialized(ServletContextEvent event)
    {
        String log4JConfigFile = event.getServletContext().getInitParameter(LOG4J_CONFIG_FILE);
        if ( log4JConfigFile == null || log4JConfigFile.length() == 0 )
        {
            log4JConfigFile = LOG4J_CONFIG_FILE_DEFAULT;
        }
        String rootPath = event.getServletContext().getRealPath("");
        InputStream input = event.getServletContext().getResourceAsStream(log4JConfigFile);
        if ( input != null )
        {
            try
            {
                Properties p = new Properties();
                p.load(input);
                String waRootKey = event.getServletContext().getInitParameter(LOG4J_CONFIG_WEB_APPLICATION_ROOT_KEY);
                if ( waRootKey == null || waRootKey.length() == 0 )
                {
                    waRootKey = LOG4J_CONFIG_WEB_APPLICATION_ROOT_KEY_DEFAULT;
                }
                p.setProperty(waRootKey,rootPath);
                isolatedHierarchy = new Hierarchy(new RootCategory(Level.INFO));
                new PropertyConfigurator().doConfigure(p,isolatedHierarchy);
                IsolatedLog4JLogger.setHierarchy(isolatedHierarchy);
                log = LogFactory.getLog(this.getClass());
                log.info("IsolatedLog4JLogger configured");
            }
            catch (IOException ioe)
            {
                event.getServletContext().log("Failed to configure Log4J from "+event.getServletContext().getServletContextName(),ioe);
            }
        }
        else
        {
            event.getServletContext().log(event.getServletContext().getServletContextName()+" Log4JConfigurator: "+rootPath+log4JConfigFile+" not found.");
        }
    }

    public void contextDestroyed(ServletContextEvent event)
    {
        if ( log != null )
        {
            log.info("Shutting down IsolatedLog4JLogger");
            log = null;
        }
        // flush Logger cache which might be kept in a shared context if
        // commons-logging isn't loaded through this ContextClassLoader
        LogFactory.release(Thread.currentThread().getContextClassLoader());
        // shutdown Log4J hierarchy (log4J keeps log files locked on Windows otherwise)
        if (isolatedHierarchy != null)
        {
        	isolatedHierarchy.shutdown();
        }
    }
}
