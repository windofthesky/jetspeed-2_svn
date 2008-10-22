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
package org.apache.jetspeed.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PreferencesValidator;
import javax.portlet.UnavailableException;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.container.JetspeedPortletConfig;
import org.apache.jetspeed.container.PortalAccessor;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.portlet.PortletObjectProxy;
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

    private Map portletCache;
    private Map validatorCache;
    
    private static final Log log = LogFactory.getLog(JetspeedPortletFactory.class);
    private final Map classLoaderMap;
    
    /**
     * Flag whether this factory will create proxy instances for actual portlet instances or not.
     */
    private boolean portletProxyUsed;
    
    /**
     * Flag whether the instantiated proxy will switch edit_defaults mode to edit mode automatically or not.
     */
    private boolean autoSwitchEditDefaultsModeToEditMode;
    
    /**
     * Flag whether the instantiated proxy will switch config mode to built-in config edit page or not.
     */
    private boolean autoSwitchConfigMode;
    
    private String customConfigModePortletUniqueName;
    
    public JetspeedPortletFactory()
    {
        this(false, false);
    }
    
    public JetspeedPortletFactory(boolean autoSwitchConfigMode, boolean autoSwitchEditDefaultsModeToEditMode)
    {
        this.portletCache =  Collections.synchronizedMap(new HashMap());
        this.validatorCache = Collections.synchronizedMap(new HashMap());
        classLoaderMap = Collections.synchronizedMap(new HashMap());
        
        this.autoSwitchConfigMode = autoSwitchConfigMode;
        this.autoSwitchEditDefaultsModeToEditMode = autoSwitchEditDefaultsModeToEditMode;
        
        this.portletProxyUsed = (this.autoSwitchConfigMode || this.autoSwitchEditDefaultsModeToEditMode);
    }
    
    public void setPortletProxyUsed(boolean portletProxyUsed)
    {
        this.portletProxyUsed = portletProxyUsed;
    }
    
    public boolean getPortletProxyUsed()
    {
        return this.portletProxyUsed;
    }
    
    public void setCustomConfigModePortletUniqueName(String customConfigModePortletUniqueName)
    {
        this.customConfigModePortletUniqueName = customConfigModePortletUniqueName;
    }
    
    public String getCustomConfigModePortletUniqueName()
    {
        return this.customConfigModePortletUniqueName;
    }

    public void registerPortletApplication(PortletApplication pa, ClassLoader cl)
        {
            synchronized (classLoaderMap)
            {
                unregisterPortletApplication(pa);
                classLoaderMap.put(pa.getName(), cl);
            }
    }
    
    public void unregisterPortletApplication(PortletApplication pa)
    {
        synchronized (classLoaderMap)
        {
            synchronized (portletCache)
            {
                ClassLoader cl = (ClassLoader) classLoaderMap.remove(pa.getName());
                if (cl != null)
                {
                    ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();

                    HashMap instanceCache = (HashMap)portletCache.remove(pa.getName());
                    if (instanceCache != null)
                    {
                        Iterator instances = instanceCache.values().iterator();
                        while (instances.hasNext())
                        {
                            Portlet portlet = (Portlet)instances.next();
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
                    }
                    validatorCache.remove(pa.getName());
                }
            }
        }
    }
    
    public PreferencesValidator getPreferencesValidator(PortletDefinition pd)
    {
        PreferencesValidator validator = null;
        try
        {
            String paName = ((PortletApplication)pd.getPortletApplicationDefinition()).getName();
            String pdName = pd.getName();
            
            synchronized (validatorCache)
            {
                HashMap instanceCache = (HashMap)validatorCache.get(paName);
                validator = instanceCache != null ? (PreferencesValidator)instanceCache.get(pdName) : null;
                if ( validator == null )
                {
                    String className = ((PortletDefinitionComposite)pd).getPreferenceValidatorClassname();
                    if ( className != null )
                    {
                        ClassLoader paCl = (ClassLoader)classLoaderMap.get(paName);
                        if ( paCl == null )
                        {
                            throw new UnavailableException("Portlet Application "+paName+" not available");
                        }
                        
                        ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
                        try
                        {
                            Class clazz = paCl.loadClass(className);
                            try
                            {
                                Thread.currentThread().setContextClassLoader(paCl);
                                validator = (PreferencesValidator)clazz.newInstance();
                                if (instanceCache == null)
                                {
                                    instanceCache = new HashMap();
                                    validatorCache.put(paName, instanceCache);
                                }
                                instanceCache.put(pdName, validator);
                            }
                            finally
                            {
                                Thread.currentThread().setContextClassLoader(currentContextClassLoader);
                            }
                        }
                        catch (Exception e)
                        {
                            String msg = "Cannot create PreferencesValidator instance "+className+" for Portlet "+pdName;
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
        PortletApplication pa = (PortletApplication)pd.getPortletApplicationDefinition();
        String paName = pa.getName();
        String pdName = pd.getName();

        try
        {                        
          synchronized (portletCache)
          {
            HashMap instanceCache = (HashMap)portletCache.get(paName);
            portlet = instanceCache != null ? (PortletInstance)instanceCache.get(pdName) : null;
            if (null != portlet)
            {
                return portlet;
            }
            
            ClassLoader paCl = (ClassLoader)classLoaderMap.get(paName);
            if ( paCl == null )
            {
                throw new UnavailableException("Portlet Application "+paName+" not available");
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
                
                if (this.portletProxyUsed && !PortletObjectProxy.isPortletObjectProxied())
                {
                    portlet = new JetspeedPortletProxyInstance(pdName, (Portlet)clazz.newInstance(), this.autoSwitchEditDefaultsModeToEditMode, this.autoSwitchConfigMode, this.customConfigModePortletUniqueName);
                }
                else
                {
                    portlet = new JetspeedPortletInstance(pdName, (Portlet)clazz.newInstance());
                }
            }
              finally
            {
                Thread.currentThread().setContextClassLoader(currentContextClassLoader);
              }
            }
            catch (Exception e)
            {
                String msg = "Cannot create Portlet instance "+pd.getClassName()+" for Portlet Application "+paName;
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
                log.error("Failed to initialize Portlet "+pd.getClassName()+" for Portlet Application "+paName, e1);
                throw e1;
            }            
            if (instanceCache == null)
            {
                instanceCache = new HashMap();
                portletCache.put(paName, instanceCache);
            }
            instanceCache.put(pdName, portlet);
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
    
    public void updatePortletConfig(PortletDefinition pd)
    {
        if (pd != null)
        {
            synchronized (portletCache)
            {
                //System.out.println("$$$$ updating portlet config for " + pd.getName());
                String key = pd.getId().toString();
                HashMap instanceCache = (HashMap)portletCache.get(((PortletApplication)pd.getPortletApplicationDefinition()).getName());
                PortletInstance instance = instanceCache != null ? (PortletInstance)instanceCache.get(pd.getName()) : null;
                if (instance != null)
                {
                    JetspeedPortletConfig config = (JetspeedPortletConfig)instance.getConfig();
                    config.setPortletDefinition(pd);
                }
            }
        }
    }
    
    public ClassLoader getPortletApplicationClassLoader(PortletApplication pa)
    {
        synchronized (classLoaderMap)
        {
          if ( pa != null )
        {
              return (ClassLoader)classLoaderMap.get(pa.getName());
        }
          return null;
        }
    }
    
    public boolean isPortletApplicationRegistered(PortletApplication pa)
    {
        return getPortletApplicationClassLoader(pa) != null;
    }
}
