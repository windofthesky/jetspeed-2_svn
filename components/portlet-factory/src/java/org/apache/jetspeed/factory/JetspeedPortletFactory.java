/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.factory;

import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PreferencesValidator;
import javax.portlet.UnavailableException;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.container.PortalAccessor;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * <p>
 * JetspeedPortletFactory
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JetspeedPortletFactory implements PortletFactory
{

    private HashMap portletCache;
    private HashMap validatorCache;
    
    private static final Log log = LogFactory.getLog(JetspeedPortletFactory.class);
    private final HashMap classLoaderMap;

    /**
     * 
     */
    public JetspeedPortletFactory()
    {
        this.portletCache = new HashMap();
        this.validatorCache = new HashMap();
        classLoaderMap = new HashMap();
    }

    public void registerPortletApplication(PortletApplication pa, ClassLoader cl)
        {
            synchronized (classLoaderMap)
            {
        unregisterPortletApplication(pa);
        classLoaderMap.put(pa.getId(), cl);
        }
    }
    
    public void unregisterPortletApplication(PortletApplication pa)
    {
        synchronized (classLoaderMap)
        {
            synchronized (portletCache)
            {
                ClassLoader cl = (ClassLoader) classLoaderMap.remove(pa.getId());
                if (cl != null)
                {
                    ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();

                    Iterator portletDefinitions = pa.getPortletDefinitions().iterator();
                    while (portletDefinitions.hasNext())
                    {
                        PortletDefinition pd = (PortletDefinition) portletDefinitions.next();
                        String pdId = pd.getId().toString();
                        Portlet portlet = (Portlet) portletCache.remove(pdId);
                        if (portlet != null)
                        {
                            try
                            {
                                Thread.currentThread().setContextClassLoader(cl);
                                portlet.destroy();
                            }
                            finally
                            {
                                Thread.currentThread().setContextClassLoader(currentContextClassLoader);
                            }
                        }
                        validatorCache.remove(pdId);
                    }
                }
            }
        }
    }
    
    public PreferencesValidator getPreferencesValidator(PortletDefinition pd)
    {
        PreferencesValidator validator = null;
        try
        {
            String pdId = pd.getId().toString();
            
            synchronized (validatorCache)
            {
                validator = (PreferencesValidator)validatorCache.get(pdId);
                if ( validator == null )
                {
                    String className = ((PortletDefinitionComposite)pd).getPreferenceValidatorClassname();
                    if ( className != null )
                    {
                        PortletApplication pa = (PortletApplication)pd.getPortletApplicationDefinition();
                        ClassLoader paCl = (ClassLoader)classLoaderMap.get(pa.getId());
                        if ( paCl == null )
                        {
                            throw new UnavailableException("Portlet Application "+pa.getName()+" not available");
                        }
                        
                        ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
                        try
                        {
                            Class clazz = paCl.loadClass(className);
                            try
                            {
                                Thread.currentThread().setContextClassLoader(paCl);
                                validator = (PreferencesValidator)clazz.newInstance();
                                validatorCache.put(pdId, validator);
                            }
                            finally
                            {
                                Thread.currentThread().setContextClassLoader(currentContextClassLoader);
                            }
                        }
                        catch (Exception e)
                        {
                            String msg = "Cannot create PreferencesValidator instance "+className+" for Portlet "+pd.getName();
                            log.error(msg,e);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error(e);
        }
        return validator;
    }

    /**
     * Gets a portlet by either creating it or returning a handle to it from the portlet 'cache'
     * 
     * @param portletDefinition The definition of the portlet
     * @return PortletInstance 
     * @throws PortletException
     */
    public PortletInstance getPortletInstance( ServletContext servletContext, PortletDefinition pd ) throws PortletException
    {
        PortletInstance portlet = null;
        String pdId = pd.getId().toString();
        PortletApplication pa = (PortletApplication)pd.getPortletApplicationDefinition();

        try
        {                        
          synchronized (portletCache)
          {
            portlet = (PortletInstance)portletCache.get(pdId);
            if (null != portlet)
            {
                return portlet;
            }
            
            ClassLoader paCl = (ClassLoader)classLoaderMap.get(pa.getId());
            if ( paCl == null )
            {
                throw new UnavailableException("Portlet Application "+pa.getName()+" not available");
            }
            
            ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
 
            try
            {
              Class clazz = paCl.loadClass(pd.getClassName());
              try
            {
                Thread.currentThread().setContextClassLoader(paCl);
                // wrap new Portlet inside PortletInstance which ensures the destroy
                // method will wait for all its invocation threads to complete
                // and thereby releasing all its ClassLoader locks as needed for local portlets.
                portlet = new JetspeedPortletInstance(pd.getName(), (Portlet)clazz.newInstance());
            }
              finally
            {
                Thread.currentThread().setContextClassLoader(currentContextClassLoader);
              }
            }
            catch (Exception e)
            {
                String msg = "Cannot create Portlet instance "+pd.getClassName()+" for Portlet Application "+pa.getName();
                log.error(msg,e);
                throw new UnavailableException(msg);
            }
      
            PortletContext portletContext = PortalAccessor.createPortletContext(servletContext, pa);            
            PortletConfig portletConfig = PortalAccessor.createPortletConfig(portletContext, pd);
            
            try
            {
              try
              {
                Thread.currentThread().setContextClassLoader(paCl);
            portlet.init(portletConfig);            
              }
              finally
              {
                Thread.currentThread().setContextClassLoader(currentContextClassLoader);
              }
            }
            catch (PortletException e1)
            {
                log.error("Failed to initialize Portlet "+pd.getClassName()+" for Portlet Application "+pa.getName(), e1);
                throw e1;
            }            
            portletCache.put(pdId, portlet);
          }
        }
        catch (PortletException pe)
        {
            throw pe;
        }
        catch (Throwable e)
        {
            log.error("PortletFactory: Failed to load portlet "+pd.getClassName(), e);
            throw new UnavailableException( "Failed to load portlet " + pd.getClassName() +": "+e.toString());
        }
        return portlet;
    }
    
    public ClassLoader getPortletApplicationClassLoader(PortletApplication pa)
    {
        synchronized (classLoaderMap)
        {
          if ( pa != null )
        {
              return (ClassLoader)classLoaderMap.get(pa.getId());
        }
          return null;
        }
    }
    
    public boolean isPortletApplicationRegistered(PortletApplication pa)
    {
        return getPortletApplicationClassLoader(pa) != null;
    }
}
