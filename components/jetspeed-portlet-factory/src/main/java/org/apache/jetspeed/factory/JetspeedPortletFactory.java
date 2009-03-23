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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletURLGenerationListener;
import javax.portlet.PreferencesValidator;
import javax.portlet.UnavailableException;
import javax.portlet.filter.PortletFilter;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.ContainerInfo;
import org.apache.jetspeed.container.JetspeedPortletConfig;
import org.apache.jetspeed.container.JetspeedPortletConfigImpl;
import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.jetspeed.container.JetspeedPortletContextImpl;
import org.apache.jetspeed.om.portlet.Filter;
import org.apache.jetspeed.om.portlet.Listener;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;

/**
 * <p>
 * JetspeedPortletFactory
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 * 
 */
public class JetspeedPortletFactory implements PortletFactory
{
    private static final Log log = LogFactory.getLog(JetspeedPortletFactory.class);

    private Map<String, Map<String, PortletInstance>> portletCache;
    private Map<String, Map<String, PreferencesValidator>> validatorCache;
    private Map<String, Map<String, PortletFilterInstance>> portletFilterCache;
    private Map<String, List<PortletURLGenerationListener>> portletListenerCache;
    private final Map<String, PortletFactoryInfo> classLoaderMap;
    private PortalContext portalContext;

    /**
     * Flag whether this factory will create proxy instances for actual portlet
     * instances or not.
     */
    private boolean portletProxyUsed;

    /**
     * Flag whether the instantiated proxy will switch edit_defaults mode to
     * edit mode automatically or not.
     */
    private boolean autoSwitchEditDefaultsModeToEditMode;

    /**
     * Flag whether the instantiated proxy will switch config mode to built-in
     * config edit page or not.
     */
    private boolean autoSwitchConfigMode;

    private String customConfigModePortletUniqueName;

    public JetspeedPortletFactory()
    {
        this(false, false);
    }
    
    public JetspeedPortletFactory(boolean autoSwitchConfigMode, boolean autoSwitchEditDefaultsModeToEditMode)
    {
        this.portletCache = Collections.synchronizedMap(new HashMap<String, Map<String, PortletInstance>>());
        this.validatorCache = Collections.synchronizedMap(new HashMap<String, Map<String, PreferencesValidator>>());
        this.classLoaderMap = Collections.synchronizedMap(new HashMap<String, PortletFactoryInfo>());
        this.portletFilterCache = Collections.synchronizedMap(new HashMap<String, Map<String, PortletFilterInstance>>());
        this.portletListenerCache = Collections.synchronizedMap(new HashMap<String, List<PortletURLGenerationListener>>());
        this.autoSwitchConfigMode = autoSwitchConfigMode;
        this.autoSwitchEditDefaultsModeToEditMode = autoSwitchEditDefaultsModeToEditMode;
        this.portletProxyUsed = (this.autoSwitchConfigMode || this.autoSwitchEditDefaultsModeToEditMode);
    }
    
    public void setPortalContext(PortalContext portalContext)
    {
        this.portalContext = portalContext;
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
            classLoaderMap.put(pa.getName(), new PortletFactoryInfo(cl, null)); // TODO 2.2: determine if PortletFactoryInfo is still needed
        }
    }

    public void unregisterPortletApplication(PortletApplication pa)
    {
        String paName = pa.getName();
        PortletFactoryInfo info = this.classLoaderMap.remove(paName);
        
        if (info != null)
        {
            Map<String, PortletInstance> portletInstanceCache = this.portletCache.remove(paName);
            Map<String, PortletFilterInstance> portletFilterInstanceCache = this.portletFilterCache.remove(paName);
            
            ClassLoader paCl = info.getClassLoader();
            ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
            
            try
            {
                Thread.currentThread().setContextClassLoader(paCl);

                if (portletInstanceCache != null && !portletInstanceCache.isEmpty())
                {
                    synchronized (portletInstanceCache)
                    {
                        for (PortletInstance portlet : portletInstanceCache.values())
                        {
                            try
                            {
                                portlet.destroy();
                            }
                            catch (Exception e)
                            {
                                String msg = "Exception occurred during destroying portlet " + portlet.getClass();
                                log.error(msg, e);
                            }
                        }
                    }
                }
                
                if (portletFilterInstanceCache != null && !portletFilterInstanceCache.isEmpty())
                {
                    synchronized (portletFilterInstanceCache)
                    {
                        for (PortletFilterInstance portletFilter : portletFilterInstanceCache.values())
                        {
                            try
                            {
                                portletFilter.destroy();
                            }
                            catch (Exception e)
                            {
                                String msg = "Exception occurred during destroying portlet " + portletFilter.getClass();
                                log.error(msg, e);
                            }
                        }
                    }
                }
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(currentContextClassLoader);
            }
            
            this.validatorCache.remove(paName);
            this.portletListenerCache.remove(paName);
        }
    }

    public PreferencesValidator getPreferencesValidator(PortletDefinition pd)
    {
        PreferencesValidator validator = null;
        
        try
        {
            String paName = pd.getApplication().getName();
            String pdName = pd.getPortletName();
            
            Map<String, PreferencesValidator> instanceCache = this.validatorCache.get(paName);
            validator = (instanceCache != null) ? instanceCache.get(pdName) : null;
            
            if (validator == null)
            {
                String className = pd.getPreferenceValidatorClassname();
                
                if (className != null)
                {
                    PortletFactoryInfo info = classLoaderMap.get(paName);
                    
                    if (info != null)
                    {
                        ClassLoader paCl = info.getClassLoader();
                        
                        if (paCl == null) 
                        { 
                            throw new UnavailableException("Portlet Application " + paName + " not available");
                        }
                        
                        try
                        {
                            Class<?> clazz = paCl.loadClass(className);
                            validator = (PreferencesValidator) clazz.newInstance();
                            
                            if (instanceCache == null)
                            {
                                instanceCache = Collections.synchronizedMap(new HashMap<String, PreferencesValidator>());
                                this.validatorCache.put(paName, instanceCache);
                            }
                            
                            instanceCache.put(pdName, validator);
                        }
                        catch (Exception e)
                        {
                            String msg = "Cannot create PreferencesValidator instance "+className+" for Portlet "+pdName;
                            log.error(msg, e);
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
     * Gets a portlet by either creating it or returning a handle to it from the
     * portlet 'cache'
     * 
     * @param portletDefinition
     *            The definition of the portlet
     * @return PortletInstance
     * @throws PortletException
     */
    public PortletInstance getPortletInstance(ServletContext servletContext, PortletDefinition pd) throws PortletException
    {
        return getPortletInstance(servletContext, pd, this.portletProxyUsed);
    }
    
    /**
     * Gets a portlet by either creating it or returning a handle to it from the
     * portlet 'cache'
     * 
     * @param portletDefinition
     *            The definition of the portlet
     * @return PortletInstance
     * @throws PortletException
     */
    public PortletInstance getPortletInstance(ServletContext servletContext, PortletDefinition pd, boolean proxyUsed) throws PortletException
    {
        PortletInstance portlet = null;
        PortletApplication pa = pd.getApplication();
        String paName = pa.getName();
        String pdName = pd.getPortletName();
        
        try
        {
            Map<String, PortletInstance> instanceCache = this.portletCache.get(paName);
            
            if (instanceCache != null)
            {
                portlet = instanceCache.get(pdName);
            }
            
            // If the current cached portlet instance is a proxied object as it is not expected,
            // recreate the portlet instance.
            if (portlet != null && this.portletProxyUsed && !proxyUsed && portlet.isProxyInstance())
            {
                portlet = null;
            }
            
            if (portlet == null)
            {
                PortletFactoryInfo info = classLoaderMap.get(paName);
                
                if (info != null)
                {
                    ClassLoader paCl = info.getClassLoader();
                    
                    if (paCl == null) 
                    { 
                        throw new UnavailableException("Portlet Application " + paName + " not available");
                    }
                    
                    ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
                    
                    try
                    {
                        Class<?> clazz = paCl.loadClass(pd.getPortletClass());
                        
                        try
                        {
                            Thread.currentThread().setContextClassLoader(paCl);
                            // wrap new Portlet inside PortletInstance which ensures
                            // the destroy
                            // method will wait for all its invocation threads to
                            // complete
                            // and thereby releasing all its ClassLoader locks as
                            // needed for local portlets.
    
                            if (proxyUsed)
                            {
                                portlet = new JetspeedPortletProxyInstance(pd
                                        .getPortletName(), (Portlet) clazz
                                        .newInstance(),
                                        this.autoSwitchEditDefaultsModeToEditMode,
                                        this.autoSwitchConfigMode,
                                        this.customConfigModePortletUniqueName);
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
                        String msg = "Cannot create Portlet instance " + pd.getPortletClass() + " for Portlet Application " + paName;                        
                        log.error(msg, e);
                        throw new UnavailableException(msg);
                    }
                    
                    JetspeedPortletContext portletContext = new JetspeedPortletContextImpl(servletContext, pa, ContainerInfo.getInfo(), portalContext.getConfiguration());                    
                    JetspeedPortletConfig portletConfig = new JetspeedPortletConfigImpl(portletContext, pd); 
                    
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
                        log.error("Failed to initialize Portlet "+pd.getPortletClass()+" for Portlet Application "+paName, e1);
                        throw e1;
                    }
                    
                    if (instanceCache == null)
                    {
                        instanceCache = Collections.synchronizedMap(new HashMap<String, PortletInstance>());
                        this.portletCache.put(paName, instanceCache);
                    }
                    
                    instanceCache.put(pdName, portlet);
                }
            }
        }
        catch (PortletException pe)
        {
            throw pe;
        }
        catch (Throwable e)
        {
            log.error("PortletFactory: Failed to load portlet " + pd.getPortletClass(), e);
            throw new UnavailableException("Failed to load portlet " + pd.getPortletClass() + ": " + e.toString());
        }
        
        return portlet;
    }

    public void updatePortletConfig(PortletDefinition pd)
    {
        if (pd != null)
        {
            Map<String, PortletInstance> instanceCache = portletCache.get(pd.getApplication().getName());
            PortletInstance instance = instanceCache != null ? instanceCache.get(pd.getPortletName()) : null;
            
            if (instance != null)
            {
                JetspeedPortletConfigImpl config = (JetspeedPortletConfigImpl) instance.getConfig();
                config.setPortletDefinition(pd);
            }                
        }
    }

    public ClassLoader getPortletApplicationClassLoader(PortletApplication pa)
    {
        ClassLoader paClassLoader = null;
        
        if (pa != null) 
        { 
            PortletFactoryInfo info = classLoaderMap.get(pa.getName());
            
            if (info != null)
            {
                paClassLoader = info.getClassLoader();
            }
        }
        
        return paClassLoader;
    }

    public boolean isPortletApplicationRegistered(PortletApplication pa)
    {
        return getPortletApplicationClassLoader(pa) != null;
    }

    public List<PortletURLGenerationListener> getPortletApplicationListeners(PortletApplication pa) throws PortletException
    {
        String paName = pa.getName();
        List<PortletURLGenerationListener> cacheListeners = this.portletListenerCache.get(paName);
        
        if (cacheListeners == null)
        {
            List<? extends Listener> paListenerList = pa.getListeners();
            
            if (paListenerList != null)
            {
                cacheListeners = new ArrayList<PortletURLGenerationListener>();
                ClassLoader paCl = getPortletApplicationClassLoader(pa);
                
                if (paCl == null) 
                { 
                    throw new UnavailableException("Portlet Application " + paName + " not available");
                }
                
                for (Listener listener : paListenerList)
                {
                    try
                    {
                        Class<? extends Object> clazz = paCl.loadClass(listener.getListenerClass());
                        PortletURLGenerationListener listenerInstance = (PortletURLGenerationListener) clazz.newInstance();
                        cacheListeners.add(listenerInstance);
                    }
                    catch (ClassNotFoundException e)
                    {
                        String message = "The listener class isn't found: " + listener.getListenerClass();
                        log.error(message);
                    }
                    catch (InstantiationException e)
                    {
                        String message = "The listener class instantiation fail: " + listener.getListenerClass();
                        log.error(message);
                    }
                    catch (IllegalAccessException e)
                    {
                        String message = "IllegalAccessException on the listener class: " + listener.getListenerClass();
                        log.error(message);
                    }
                }
                
                this.portletListenerCache.put(paName, cacheListeners);
            }
        }
        
        if (cacheListeners != null)
        {
            return Collections.unmodifiableList(cacheListeners);
        }
        else
        {
            return Collections.emptyList();
        }
    }
    
    public PortletFilterInstance getPortletFilterInstance(PortletApplication pa, String filterName) throws PortletException
    {
        String paName = pa.getName();
        PortletFilterInstance filterInstance = null;
        
        Map<String, PortletFilterInstance> cacheFilters = this.portletFilterCache.get(paName);
        
        if (cacheFilters != null) 
        {
            filterInstance = cacheFilters.get(filterName);
        }
        
        if (filterInstance == null)
        {
            Filter filter = pa.getFilter(filterName);
            
            if (filter != null)
            {
                ClassLoader paCl = getPortletApplicationClassLoader(pa);
                
                if (paCl == null) 
                { 
                    throw new UnavailableException("Portlet Application " + paName + " not available");
                }
                
                try
                {
                    Class<? extends Object> clazz = paCl.loadClass(filter.getFilterClass());
                    PortletFilter portletFilter = (PortletFilter) clazz.newInstance();
                    filterInstance = new JetspeedPortletFilterInstance(filter, portletFilter);
                }
                catch (ClassNotFoundException e)
                {
                    String message = "The filter class isn't found: " + filter.getFilterClass();
                    log.error(message);
                    throw new UnavailableException(message);
                }
                catch (InstantiationException e)
                {
                    String message = "The filter class instantiation fail: " + filter.getFilterClass();
                    log.error(message);
                    throw new UnavailableException(message);
                }
                catch (IllegalAccessException e)
                {
                    String message = "IllegalAccessException on the filter class: " + filter.getFilterClass();
                    log.error(message);
                    throw new UnavailableException(message);
                }
                
                if (cacheFilters == null)
                {
                    cacheFilters = Collections.synchronizedMap(new HashMap<String, PortletFilterInstance>());
                    this.portletFilterCache.put(paName, cacheFilters);
                }

                cacheFilters.put(filterName, filterInstance);
            }
        }
        
        return filterInstance;
    }

}
