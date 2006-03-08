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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Hierarchy;

/**
 * IsolatedLog4JLogger routes all commons-logging logging using Log4J to an ContextClassLoader
 * specific Hierarchy.
 * </p>
 * <p>
 * For web or application servers providing (and enforcing) commons-logging and
 * Log4J from a shared context (like JBoss), configuring Log4J loggers and appenders
 * from within a (web) application overrides and resets the global 
 * Log4J LoggerRepository.
 * </p>
 * <p>
 * Capturing root logging for logging events from within the web application
 * for example isn't possible using a Log4J propery or xml configuration without
 * routing <em>ALL</em> root logging through the new (web application specific)
 * configuration.
 * </p>
 * <p>
 * <em>It is possible using the Log4J API directly instead of configuration files, 
 * but that requires hardcoded knowledge about how the logging is to be done.</em>
 * </p>
 * <p>
 * Furthermore, if another application later on reconfigures the root logging again, the
 * current root logging configuration is closed, detached and rerouted to the new configuration.
 * </p>
 * <p>
 * The same applies of course to common named loggers like capturing springframework logging. 
 * </p>
 * <p>
 * The only way to prevent this <em>stealing</em> of logging configuration is allowing only
 * one log4J configuration for the whole web or application server.<br/>
 * As this has to be done in a web or application server specific manner, setting up Jetspeed
 * for different servers will become rather complex and difficult to automate.
 * </p>
 * <p>
 * The IsolatedLog4JLogger solves these problems by routing all logging through a statically
 * configured ContextClassLoader isolated LoggerRepository.
 * </p>
 * Using this class requires a commons-logging.properties file in the WEB-INF/classes
 * folder containing:
 * <pre>
 *   org.apache.commons.logging.Log=org.apache.jetspeed.util.IsolatedLog4JLogger
 * </pre>
 * </p>
 * <p>
 * During web application initialization, preferably from a ServletContextListener or
 * a Servlet init method loaded with a load-on-startup value of 0 (zero), a new 
 * ContextClassLoader (e.g. web application) specific LoggerRepository as well as 
 * the initialization of Log4J should be configured as follows:
 * <pre>
 *   Properties p = new Properties();
 *   p.load(new FileInputStream(log4jFile));
 *   // Create a new LoggerRepository
 *   Hierarchy h = new Hierarchy(new RootCategory(Level.INFO));
 *   // Configure the LoggerRepository with our own Log4J configuration
 *   new PropertyConfigurator().doConfigure(p,h);
 *   // set the LoggerRepository to be used for the current ContextClassLoader
 *   IsolatedLog4JLogger.setHierarchy(h);
 * </pre>
 * Instead of using a PropertyConfigurator a DomConfigurator could be used likewise. 
 * </p>
 * <p>
 * TODO: It might be useful to have this utility class available for usage by Portlet
 * Applications too. Because this class <em>must</em> be included within a web application
 * classpath, a separate jetspeed-tools or jetspeed-utils library will have to be created
 * for it (and possibly other utility classes as well) which then can be put in the web
 * application lib folder. 
 * </p>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class IsolatedLog4JLogger implements Log
{
    private static Hierarchy hierarchy;
    private static HashMap notIsolatedLoggers = new HashMap();

    private Log4JLogger logger; // the wrapped Log4JLogger 
    
    public static void setHierarchy(Hierarchy hierarchy)
    {
        synchronized (IsolatedLog4JLogger.class)
        {
            if ( IsolatedLog4JLogger.hierarchy == null )
            {
                IsolatedLog4JLogger.hierarchy = hierarchy;
                if ( notIsolatedLoggers.size() > 0 )
                {
                    // Reroute existing IsolatedLog4JLogger instances
                    // which were created before the new LoggerRepository.
                    // Note: This situation should be prevented as much as
                    //       possible by calling setHierarchy from
                    //       a ServletContextListener or a Servlet its init method
                    //       which has a load-on-startup value of 0 (zero).
                    Iterator iter = notIsolatedLoggers.entrySet().iterator();
                    while (iter.hasNext())
                    {
                        Map.Entry entry = (Map.Entry)iter.next();
                        IsolatedLog4JLogger logger = (IsolatedLog4JLogger)entry.getKey();
                        logger.setLogger(new Log4JLogger(hierarchy.getLogger((String)entry.getValue())));
                    }
                }
                notIsolatedLoggers = null;
            }
        }
    }
    
    public IsolatedLog4JLogger(String name)
    {
        synchronized (IsolatedLog4JLogger.class)
        {
            if ( hierarchy == null )
            {
                // A LogFactory.getLog(name) is called before
                // our ContextClassLoader Hierarchy could be set.
                // Temporarily save this instance so it can be
                // rerouted one the Hierarchy is set.
                logger = new Log4JLogger(name);
                notIsolatedLoggers.put(this,name);
            }
            else
            {
                logger = new Log4JLogger(hierarchy.getLogger(name));               
            }
        }
    }
    
    private void setLogger(Log4JLogger logger)
    {
        this.logger = logger;
    }

    private Log4JLogger getLogger()
    {
        synchronized (IsolatedLog4JLogger.class)
        {
            return logger;
        }
    }

    public void debug(Object arg0)
    {
        getLogger().debug(arg0);
    }
    public void debug(Object arg0, Throwable arg1)
    {
        getLogger().debug(arg0, arg1);
    }
    public boolean equals(Object obj)
    {
        return getLogger().equals(obj);
    }
    public void error(Object arg0)
    {
        getLogger().error(arg0);
    }
    public void error(Object arg0, Throwable arg1)
    {
        getLogger().error(arg0, arg1);
    }
    public void fatal(Object arg0)
    {
        getLogger().fatal(arg0);
    }
    public void fatal(Object arg0, Throwable arg1)
    {
        getLogger().fatal(arg0, arg1);
    }
    public void info(Object arg0)
    {
        getLogger().info(arg0);
    }
    public void info(Object arg0, Throwable arg1)
    {
        getLogger().info(arg0, arg1);
    }
    public boolean isDebugEnabled()
    {
        return getLogger().isDebugEnabled();
    }
    public boolean isErrorEnabled()
    {
        return getLogger().isErrorEnabled();
    }
    public boolean isFatalEnabled()
    {
        return getLogger().isFatalEnabled();
    }
    public boolean isInfoEnabled()
    {
        return getLogger().isInfoEnabled();
    }
    public boolean isTraceEnabled()
    {
        return getLogger().isTraceEnabled();
    }
    public boolean isWarnEnabled()
    {
        return getLogger().isWarnEnabled();
    }
    public String toString()
    {
        return getLogger().toString();
    }
    public void trace(Object arg0)
    {
        getLogger().trace(arg0);
    }
    public void trace(Object arg0, Throwable arg1)
    {
        getLogger().trace(arg0, arg1);
    }
    public void warn(Object arg0)
    {
        getLogger().warn(arg0);
    }
    public void warn(Object arg0, Throwable arg1)
    {
        getLogger().warn(arg0, arg1);
    }
}
