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
package org.apache.jetspeed.cps;

import java.util.Properties;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4jFactory;
import org.apache.log4j.PropertyConfigurator;
import org.apache.commons.configuration.Configuration;
import org.apache.fulcrum.ServiceManager;
import org.apache.fulcrum.BaseServiceBroker;

/**
 * This is a singleton utility class that acts as a Services broker.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class CommonPortletServices extends BaseServiceBroker implements ServiceManager, CPSConstants
{
    /** The single instance of this class. */
    private static CommonPortletServices instance = new CommonPortletServices();

    private static final Log log = LogFactory.getLog(CommonPortletServices.class);

    private boolean initialized = false;
    
    /**
     * This constructor is protected to force clients to use
     * getInstance() to access this class.
     */
    protected CommonPortletServices()
    {
        super();
    }

    /**
     * The method through which this class is accessed as a broker.
     *
     * @return The single instance of this class.
     */
    public static CommonPortletServices getInstance()
    {        
        return instance;
    }

    /**
     * 
     * <p>
     * init
     * </p>
     * Initializes th CPS with the <code>configuration</code>
     * and <code>applicationRoot provided</code>.
     * 
     * @param configuration Configuration to use to configure all CPS services
     * @param applicationRoot The application root of the system
     * @param initLog4j <code>true</code> means that CPS will attempt configure
     * Log4j, <code>false</code> Log4j will not be configured (used if the implementation
     * system already initializes Log4j)
     * @throws CPSInitializationException
     *
     */
    public void init(Configuration configuration, String applicationRoot, boolean initLog4j) throws CPSInitializationException
    {
        try
        {
            if (initialized)
            {
                return;
            }
            
            //
            // bootstrap the initable services
            //
            this.setApplicationRoot(applicationRoot);
            this.setConfiguration(configuration);

            if (initLog4j)
            {

                String log4jFile = configuration.getString(LOG4J_CONFIG_FILE, LOG4J_CONFIG_FILE_DEFAULT);
                log4jFile = this.getRealPath(log4jFile);
                Properties p = new Properties();
                p.load(new FileInputStream(log4jFile));
                p.setProperty(APPLICATION_ROOT_KEY, applicationRoot);
                PropertyConfigurator.configure(p);
                log.info("Configured log4j from " + log4jFile);

                // Set up Commons Logging to use the Log4J Logging
                System.getProperties().setProperty(LogFactory.class.getName(), Log4jFactory.class.getName());
            }
            else
            {
                log.info("Skipping Log4j configuration");
            }

            this.init();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            log.error(e.toString());
            throw new CPSInitializationException("CPS Initialization exception!", e);
        }

    }

    /**
     * 
     * <p>
     * init
     * </p>
     * Initializes th CPS with the <code>configuration</code>
     * and <code>applicationRoot provided</code>.  Will configure Log4j
     * by default.  This is the same acalling <code>init(Configuration, AppRoot, true)</code>
     * 
     * @param configuration
     * @param applicationRoot
     * @throws CPSInitializationException
     *
     */
    public void init(Configuration configuration, String applicationRoot) throws CPSInitializationException
    {
        init(configuration, applicationRoot, true);
    }

    public void shutdown() throws CPSException
    {
        getInstance().shutdownServices();
        System.gc();
    }

    /**
     * @param String name of the service.  For Fulcrum
     * services this is easily accessible from the Service's interface
     * via <code>SERVICE_NAME</code> field.
     * @return Object a service
     */
    public static CommonService getPortalService(String name)
    {

        Object service = getInstance().getService(name);
        if (service instanceof CommonService)
        {
            return (CommonService) service;
        }
        return null;
    }
    
/*
    private void publishServices()
    throws CPSInitializationException
    {
        try
        {
            Hashtable env = new Hashtable();
             env.put(Context.INITIAL_CONTEXT_FACTORY,  
                "com.sun.jndi.fscontext.FSContextFactory");
             env.put(Context.PROVIDER_URL, "file:/");
            env.put(Context.OBJECT_FACTORIES, "foo.bar.ObjFactory");
            env.put("foo", "bar");            
            Context ctx = new InitialContext(env);        
            ctx.bind("cps/services", this);
        }
        catch (NamingException e)
        {
            throw new CPSInitializationException(e.toString()); 
        }
    }
*/ 
    public boolean isInitialized()
    {
        return initialized;
    }
    
}