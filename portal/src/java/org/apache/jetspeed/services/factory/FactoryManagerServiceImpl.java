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
package org.apache.jetspeed.services.factory;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration.Configuration;

import org.apache.pluto.factory.Factory;
import org.apache.pluto.services.factory.FactoryManagerService;
import org.apache.pluto.util.StringUtils;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;

/**
 * <p>
 * Manages the life-time of portal-to-container shared factories as defined by Pluto's factory interfaces.
 * A factory must derive from <a href='http://jakarta.apache.org/pluto/apidocs/org/apache/pluto/factory/Factory.html'>org.apache.pluto.factory.Factory</a> and implement the
 * <CODE>init()</CODE> and <CODE>destroy()</CODE> methods to meet Pluto's factory contract.
 * Factories create the shared classes between the portal and Pluto container. 
 * Implementations are created by portal provided factories. Most of the shared
 * classes are implementations of the Java Portlet API interfaces. 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 * @see <a href="org.apache.jetspeed.services.factory.FactoryManagerService">FactoryManagerService</a>
 */
public class FactoryManagerServiceImpl 
    extends BaseCommonService
    implements FactoryManagementService, FactoryManagerService
{
    private final static Log log = LogFactory.getLog(FactoryManagerServiceImpl.class);
    
    private final static String CONFIG_FACTORY_PRE = "plutofactory.";

    /** Map of factories, keyed off Pluto interface name to portal factory class */
    private Map  factoryMap  = new HashMap();
    
    /** List of portal factory classes */    
    private List factoryList = new LinkedList();


    public FactoryManagerServiceImpl()
    {
    }

    /**
     * This is the early initialization method called by the <code>Service</code> framework. 
     * 
     * @exception throws a <code>CPSInitializationException</code> if the service
     * fails to initialize.
     */
    public void init() 
        throws CPSInitializationException 
    {
        log.info("FactoryManagerService init");
        
        if (isInitialized()) 
        {
            return;        
        }

        initConfiguration();        

        // initialization done
        setInit(true);

     }

    /**
     * This is the shutdown method called by the 
     * Turbine <code>Service</code> framework
     */
    public void shutdown() 
    {
        log.info("Shutdown for FactoryManagerService called ");
        
        // destroy the services in reverse order 
        for (Iterator iterator = factoryList.iterator (); iterator.hasNext (); )
        {
            Factory factory = (Factory) iterator.next ();

            try
            {
                factory.destroy ();
            }
            catch (Exception exc)
            {
                log.error("FactoryManager: Factory couldn't be destroyed.", exc);
            }
        }
        factoryList.clear();
        factoryMap.clear();        
    }

    /**
     ** Initializes all factories specified in the configuration beginning with 'factory.'.
     ** By specifying a different implementation of the factory the behaviour
     ** of the portlet container can be modified.
     **
     **
     ** @exception    InitializationException
     **               if initializing any of the factories fails
     **/

   // protected void initConfiguration (ServletConfig config, Properties aProperties) throws Exception
    protected void initConfiguration () 
        throws CPSInitializationException
    {
        ServletConfig servletConfig = Jetspeed.getEngine().getServletConfig();
        Configuration config = Jetspeed.getContext().getConfiguration();
        Iterator configNames = config.getKeys(CONFIG_FACTORY_PRE);

        Map factoryImpls = new HashMap();
        Map factoryProps = new HashMap();

        String lastFactoryInterfaceName = null;
        while (configNames.hasNext())
        {
            String configName = (String)configNames.next();
            if (configName.startsWith(CONFIG_FACTORY_PRE))
            {
                String name = configName.substring(CONFIG_FACTORY_PRE.length());
                if ((lastFactoryInterfaceName!=null) &&
                    (name.startsWith(lastFactoryInterfaceName)) )
                {
                    String propertyName = name.substring(lastFactoryInterfaceName.length()+1);
                    String propertyValue = config.getString(configName);
                    Map properties = (Map)factoryProps.get(lastFactoryInterfaceName);
                    properties.put(propertyName, propertyValue);
                }
                else
                {
                    String factoryInterfaceName = name;
                    String factoryImplName = config.getString(configName);
                    factoryImpls.put(factoryInterfaceName, factoryImplName);
                    factoryProps.put(factoryInterfaceName, new HashMap());
                    // remember interface name to get all properties
                    lastFactoryInterfaceName = factoryInterfaceName;
                }
            }
        }

        int numAll = 0;

        for (Iterator iter = factoryImpls.keySet().iterator(); iter.hasNext (); )
        {
            String factoryInterfaceName = (String) iter.next ();

            numAll++;

            // try to get hold of the factory

            Class factoryInterface;

            try
            {
                factoryInterface = Class.forName (factoryInterfaceName);
            }
            catch (ClassNotFoundException exc)
            {
                log.error("FactoryManager: A factory with name " + factoryInterfaceName + " cannot be found.");
                continue;
            }

            String factoryImplName = (String)factoryImpls.get(factoryInterfaceName);

            Class factoryImpl = null;

            Factory factory = null;

            try
            {
                factoryImpl = Class.forName (factoryImplName);

                factory = (Factory) factoryImpl.newInstance ();

                Map props = (Map)factoryProps.get(factoryInterfaceName);

                log.info(StringUtils.nameOf (factoryInterface) + " initializing...");

                factory.init (servletConfig, props);
                log.info(StringUtils.nameOf (factoryInterface) + " done.");
            }
            catch (ClassNotFoundException exc)
            {
                String msg = "FactoryManager: A factory implementation with name " + factoryImplName + " cannot be found."; 
                log.error(msg, exc);
                throw new CPSInitializationException(msg, exc);
            }
            catch (ClassCastException exc)
            {
                String msg = "FactoryManager: Factory implementation " 
                    + factoryImplName + " is not a factory of the required type.";
                log.error (msg, exc);
                throw new CPSInitializationException(msg, exc);
            }
            catch (InstantiationException exc)
            {
                String msg = "FactoryManager: Factory implementation " + factoryImplName + " cannot be instantiated."; 
                log.error(msg , exc);
                throw new CPSInitializationException(msg, exc);
            }
            catch (Exception exc)
            {
                String msg = "FactoryManager: An unidentified error occurred";                
                log.error(msg, exc);
                throw new CPSInitializationException(msg, exc);
            }

            if (factory != null)
            {
                factoryMap.put (factoryInterface, factory);

                // build up list in reverse order for later destruction

                factoryList.add (0, factory);

            }
        }
        log.info("FactoryManager: Factories initialized (" + numAll + " successful).");
    }


    /* (non-Javadoc)
     * @see org.apache.pluto.services.factory.FactoryManagerService#getFactory(java.lang.Class)
     */
    public Factory getFactory (Class managedInterface)
    {
        // at this state the services map is read-only,
        // therefore we can go without synchronization

        return ((Factory) factoryMap.get (managedInterface));
    }

}
