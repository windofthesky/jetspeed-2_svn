/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.services.factory;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import javax.servlet.ServletConfig;

import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration.Configuration;

import org.apache.pluto.factory.Factory;
import org.apache.pluto.services.factory.FactoryManagerService;
import org.apache.pluto.util.StringUtils;

import org.apache.jetspeed.Jetspeed;

/**
 * Manages the life-time of factories registered during container startup.
 * A service has to derive from {@link Factory} and implement the
 * <CODE>init()</CODE> and <CODE>destroy()</CODE> methods as appropriate.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class FactoryManagerServiceImpl 
    extends BaseService
    implements FactoryManagementService, FactoryManagerService
{
    private final static Log log = LogFactory.getLog(FactoryManagerServiceImpl.class);
    
    private final static String CONFIG_FACTORY_PRE = "plutofactory.";

    public FactoryManagerServiceImpl()
    {
    }

    /**
     * This is the early initialization method called by the 
     * <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public void init() 
        throws InitializationException 
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
        throws InitializationException
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
                throw new InitializationException(msg, exc);
            }
            catch (ClassCastException exc)
            {
                String msg = "FactoryManager: Factory implementation " 
                    + factoryImplName + " is not a factory of the required type.";
                log.error (msg, exc);
                throw new InitializationException(msg, exc);
            }
            catch (InstantiationException exc)
            {
                String msg = "FactoryManager: Factory implementation " + factoryImplName + " cannot be instantiated."; 
                log.error(msg , exc);
                throw new InitializationException(msg, exc);
            }
            catch (Exception exc)
            {
                String msg = "FactoryManager: An unidentified error occurred";                
                log.error(msg, exc);
                throw new InitializationException(msg, exc);
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


    /**
     ** Returns the service implementation for the given service class, or
     ** <CODE>null</CODE> if no such service is registered.
     **
     ** @param   aClass
     **          the service class
     **
     ** @return   the service implementation
     **/

    public Factory getFactory (Class theClass)
    {
        // at this state the services map is read-only,
        // therefore we can go without synchronization

        return ((Factory) factoryMap.get (theClass));
    }

    // --- PRIVATE MEMBERS --- //

    private Map  factoryMap  = new HashMap ();
    private List factoryList = new LinkedList ();

}
