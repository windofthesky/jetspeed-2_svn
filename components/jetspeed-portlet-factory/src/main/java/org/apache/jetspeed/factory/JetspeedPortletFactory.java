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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.portlet.GenericPortlet;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletURLGenerationListener;
import javax.portlet.PreferencesValidator;
import javax.portlet.UnavailableException;
import javax.portlet.filter.PortletFilter;
import javax.servlet.ServletContext;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.components.portletregistry.RegistryEventListener;
import org.apache.jetspeed.container.ContainerInfo;
import org.apache.jetspeed.container.JetspeedPortletConfig;
import org.apache.jetspeed.container.JetspeedPortletConfigImpl;
import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.jetspeed.container.JetspeedPortletContextImpl;
import org.apache.jetspeed.container.JetspeedServletContextProviderImpl;
import org.apache.jetspeed.om.portlet.Filter;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.Listener;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.util.GenericPortletUtils;
import org.apache.jetspeed.util.ReloadablePropertyResourceBundle;
import org.apache.pluto.container.RequestDispatcherService;
import org.apache.portals.bridges.common.ServletContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class JetspeedPortletFactory implements PortletFactory, RegistryEventListener
{
    private static final Logger log = LoggerFactory.getLogger(JetspeedPortletFactory.class);
    
    private Map<String, Map<String, PortletInstance>> portletCache;
    private Map<String, Map<String, PreferencesValidator>> validatorCache;
    private Map<String, Map<String, PortletFilterInstance>> portletFilterCache;
    private Map<String, List<PortletURLGenerationListener>> portletListenerCache;
    private Map<String, Map<Locale, ResourceBundle>> applicationResourceBundleCache;
    private Map<String, Map<String, Map<Locale, ResourceBundle>>> portletsResourceBundleCache;
    private final Map<String, ClassLoader> classLoaderMap;
    private PortalContext portalContext;
    private RequestDispatcherService rdService;
    private ServletContextProvider servletContextProvider;
    private Object cacheMutex = new Object();

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

    /**
     * Delegated portlet unique name for config mode
     */
    private String customConfigModePortletUniqueName;
    
    /**
     * Flag whether the instantiated proxy will switch preview mode to built-in
     * preview markup generating portlet or not.
     */
    private boolean autoSwitchPreviewMode;
    
    /**
     * Delegated portlet unique name for preview mode
     */
    private String customPreviewModePortletUniqueName;

    public JetspeedPortletFactory(RequestDispatcherService rdService)
    {
        this(rdService, false, false);
    }
    
    public JetspeedPortletFactory(RequestDispatcherService rdService, boolean autoSwitchConfigMode, boolean autoSwitchEditDefaultsModeToEditMode)
    {
        this(rdService, autoSwitchConfigMode, autoSwitchEditDefaultsModeToEditMode, false);
    }
    
    public JetspeedPortletFactory(RequestDispatcherService rdService, boolean autoSwitchConfigMode, boolean autoSwitchEditDefaultsModeToEditMode, boolean autoSwitchPreviewMode)
    {
        this.rdService = rdService;
        this.portletCache = Collections.synchronizedMap(new HashMap<String, Map<String, PortletInstance>>());
        this.validatorCache = Collections.synchronizedMap(new HashMap<String, Map<String, PreferencesValidator>>());
        this.classLoaderMap = Collections.synchronizedMap(new HashMap<String, ClassLoader>());
        this.portletFilterCache = Collections.synchronizedMap(new HashMap<String, Map<String, PortletFilterInstance>>());
        this.portletListenerCache = Collections.synchronizedMap(new HashMap<String, List<PortletURLGenerationListener>>());
        this.applicationResourceBundleCache = Collections.synchronizedMap(new HashMap<String, Map<Locale, ResourceBundle>>());
        this.portletsResourceBundleCache = Collections.synchronizedMap(new HashMap<String, Map<String, Map<Locale, ResourceBundle>>>());
        this.autoSwitchConfigMode = autoSwitchConfigMode;
        this.autoSwitchEditDefaultsModeToEditMode = autoSwitchEditDefaultsModeToEditMode;
        this.autoSwitchPreviewMode = autoSwitchPreviewMode;
        this.portletProxyUsed = (this.autoSwitchConfigMode || this.autoSwitchEditDefaultsModeToEditMode || this.autoSwitchPreviewMode);
        this.servletContextProvider = new JetspeedServletContextProviderImpl(rdService);
    }
    
    protected ResourceBundle loadResourceBundle( Locale locale, String bundleName, ClassLoader cl )
    {
        ResourceBundle resourceBundle = null;
        
        try
        {
            resourceBundle = ResourceBundle.getBundle(bundleName, locale, cl);
            
            if (resourceBundle instanceof PropertyResourceBundle)
            {
                resourceBundle = new ReloadablePropertyResourceBundle((PropertyResourceBundle) resourceBundle, bundleName);
            }
        }
        catch (MissingResourceException x)
        {
            return null;
        }
        
        return resourceBundle;
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
    
    public void setCustomPreviewModePortletUniqueName(String customPreviewModePortletUniqueName)
    {
        this.customPreviewModePortletUniqueName = customPreviewModePortletUniqueName;
    }

    public String getCustomPreviewModePortletUniqueName()
    {
        return this.customPreviewModePortletUniqueName;
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
        String paName = pa.getName();
        ClassLoader paCl = this.classLoaderMap.remove(paName);
        
        if (paCl != null)
        {
        	synchronized (cacheMutex)
        	{
                applicationResourceBundleCache.remove(paName);
                portletsResourceBundleCache.remove(paName);
                Map<String, PortletInstance> portletInstanceCache = this.portletCache.remove(paName);
                Map<String, PortletFilterInstance> portletFilterInstanceCache = this.portletFilterCache.remove(paName);
                
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
    }

    public PreferencesValidator getPreferencesValidator(PortletDefinition pd)
    {
    	synchronized (cacheMutex)
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
                        ClassLoader paCl = classLoaderMap.get(paName);
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
            catch (Exception e)
            {
                log.error(e.getMessage(),e);
            }
            
            return validator;
    	}
    }
    
    public ResourceBundle getResourceBundle(PortletApplication pa, Locale locale)
    {
    	synchronized (cacheMutex)
    	{
            ResourceBundle bundle = null;        
            try
            {
                if (locale != null && pa.getResourceBundle() != null)
                {
                    String paName = pa.getName();
                    Map<Locale, ResourceBundle> bundleCache = applicationResourceBundleCache.get(paName);
                    if (bundleCache == null)
                    {
                        bundleCache = Collections.synchronizedMap(new HashMap<Locale, ResourceBundle>());
                        applicationResourceBundleCache.put(paName, bundleCache);
                    }
                    bundle = bundleCache.get(locale);
                    // check if the bundle doesn't contain the key (as a null value might be stored before)
                    if (!bundleCache.containsKey(locale) )
                    {
                        ClassLoader paCl = classLoaderMap.get(paName);
                        
                        if (paCl == null) 
                        { 
                            throw new UnavailableException("Portlet Application " + paName + " not available");
                        }
                        bundle = loadResourceBundle(locale, pa.getResourceBundle(), paCl);
                        // even if bundle isn't found, store a null value in the HashMap so we don't need to go
                        // look for it again
                        bundleCache.put(locale, bundle);
                    }
                }
            }
            catch (Exception e)
            {
                log.error(e.getMessage(),e);
            }
            return bundle;
    	}
    }
    
    public ResourceBundle getResourceBundle(PortletDefinition pd, Locale locale)
    {
    	synchronized (cacheMutex)
    	{
            ResourceBundle bundle = null;
            
            try
            {
                String paName = pd.getApplication().getName();
                String pdName = pd.getPortletName();
                
                Map<String, Map<Locale, ResourceBundle>> portletResourceBundleCache = portletsResourceBundleCache.get(paName);
                if (portletResourceBundleCache == null)
                {
                    portletsResourceBundleCache.put(paName, Collections.synchronizedMap(new HashMap<String, Map<Locale, ResourceBundle>>()));
                    portletResourceBundleCache = portletsResourceBundleCache.get(paName);
                }
                Map<Locale, ResourceBundle> bundleCache = portletResourceBundleCache.get(pdName);
                if (bundleCache == null)
                {
                    portletResourceBundleCache.put(pdName, Collections.synchronizedMap(new HashMap<Locale, ResourceBundle>()));
                    bundleCache = portletResourceBundleCache.get(pdName);
                }
                bundle = bundleCache.get(locale);
                if (bundle == null)
                {
                    Language l = pd.getLanguage(locale);
                    if (pd.getResourceBundle() == null)
                    {
                        bundle = new InlinePortletResourceBundle(l.getTitle(), l.getShortTitle(), l.getKeywords());
                    }
                    else
                    {
                        ClassLoader paCl = classLoaderMap.get(paName);
                        
                        if (paCl == null) 
                        { 
                            throw new UnavailableException("Portlet Application " + paName + " not available");
                        }
                        ResourceBundle loadedBundle = loadResourceBundle(l.getLocale(), pd.getResourceBundle(), paCl);
                        if (loadedBundle != null)
                        {
                            bundle = new InlinePortletResourceBundle(l.getTitle(), l.getShortTitle(), l.getKeywords(), loadedBundle);
                        }
                        else
                        {
                            bundle = new InlinePortletResourceBundle(l.getTitle(), l.getShortTitle(), l.getKeywords());
                        }
                    }
                    bundleCache.put(locale, bundle);
                }
            }
            catch (Exception e)
            {
                log.error(e.getMessage(),e);
            }
            return bundle;
    	}
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
    	synchronized (cacheMutex)
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
                    ClassLoader paCl = classLoaderMap.get(paName);
                    
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
                                portlet = new JetspeedPortletProxyInstance(
                                                                           pd.getPortletName(),
                                                                           (Portlet) clazz.newInstance(),
                                                                           this.autoSwitchEditDefaultsModeToEditMode,
                                                                           this.autoSwitchConfigMode, this.customConfigModePortletUniqueName,
                                                                           this.autoSwitchPreviewMode, this.customPreviewModePortletUniqueName
                                                                           );
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
                    
                    JetspeedPortletContext portletContext = new JetspeedPortletContextImpl(servletContext, 
                                                                                           pa, 
                                                                                           ContainerInfo.getInfo(), 
                                                                                           portalContext.getConfiguration(),
                                                                                           rdService,
                                                                                           servletContextProvider
                                                                                           );
                    JetspeedPortletConfig portletConfig = new JetspeedPortletConfigImpl(this, portletContext, pd); 
                    
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
    }

    public void updatePortletConfig(PortletDefinition pd)
    {
    	synchronized (cacheMutex)
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
    }

    public ClassLoader getPortletApplicationClassLoader(PortletApplication pa)
    {
        return pa != null ? classLoaderMap.get(pa.getName()) : null;
    }

    public boolean isPortletApplicationRegistered(PortletApplication pa)
    {
        return getPortletApplicationClassLoader(pa) != null;
    }

    public List<PortletURLGenerationListener> getPortletApplicationListeners(PortletApplication pa) throws PortletException
    {
    	synchronized (cacheMutex)
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
    }
    
    public PortletFilterInstance getPortletFilterInstance(PortletApplication pa, String filterName) throws PortletException
    {
    	synchronized (cacheMutex)
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
                    ClassLoader paCl = classLoaderMap.get(paName);
                    
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

    public boolean hasRenderHelperMethod(PortletDefinition pd, PortletMode mode)
    {
    	synchronized (cacheMutex)
    	{
            PortletInstance portletInstance = null;
            String paName = pd.getApplication().getName();
            String pdName = pd.getPortletName();
            
            Map<String, PortletInstance> instanceCache = this.portletCache.get(paName);
            
            if (instanceCache != null)
            {
                portletInstance = instanceCache.get(pdName);
            }
            
            if (portletInstance != null)
            {
                return portletInstance.hasRenderHelperMethod(mode);
            }
            else
            {
                ClassLoader paCl = classLoaderMap.get(paName);
                
                if (paCl != null) 
                {
                    try
                    {
                        Class<?> portletClazz = paCl.loadClass(pd.getPortletClass());
                        
                        if (GenericPortlet.class.isAssignableFrom(portletClazz))
                        {
                            Method helperMethod = GenericPortletUtils.getRenderModeHelperMethod((Class<? extends GenericPortlet>) portletClazz, mode);
                            
                            if (helperMethod != null)
                            {
                                return true;
                            }
                        }
                    }
                    catch (ClassNotFoundException e)
                    {
                    }
                }
                
                return false;
            }
    	}
    }

    public void reloadResourceBundles(PortletApplication pa) throws PortletException
    {
    	synchronized (cacheMutex)
    	{
            String paName = pa.getName();
            Map<Locale, ResourceBundle> bundleCache = applicationResourceBundleCache.get(paName);
            
            if (bundleCache != null)
            {
                List<Locale> locales = null;
                
                synchronized (bundleCache)
                {
                    locales = new ArrayList<Locale>(bundleCache.keySet());
                }
                
                for (Locale locale : locales)
                {
                    ResourceBundle bundle = bundleCache.get(locale);
                    
                    if (bundle != null)
                    {
                        if (bundle instanceof InlinePortletResourceBundle)
                        {                    	
                            bundle = ((InlinePortletResourceBundle) bundle).getParent();
                        }
                        
                        if (bundle instanceof ReloadablePropertyResourceBundle)
                        {
                            try
                            {
                                ((ReloadablePropertyResourceBundle) bundle).reload(getPortletApplicationClassLoader(pa));
                            }
                            catch (IOException e)
                            {
                                log.error("Failed to reload resource bundle of " + paName + " for locale, " + locale, e);
                            }
                        }
                    }
                }
            }
    	}
    }

    public void reloadResourceBundles(PortletDefinition pd) throws PortletException
    {
    	synchronized (cacheMutex)
    	{
            PortletApplication pa = pd.getApplication();
            String paName = pa.getName();
            String pdName = pd.getPortletName();
            
            Map<String, Map<Locale, ResourceBundle>> portletResourceBundleCache = portletsResourceBundleCache.get(paName);
            
            if (portletResourceBundleCache != null)
            {
                Map<Locale, ResourceBundle> bundleCache = portletResourceBundleCache.get(pdName);
                
                if (bundleCache != null)
                {
                    List<Locale> locales = null;
                    
                    synchronized (bundleCache)
                    {
                        locales = new ArrayList<Locale>(bundleCache.keySet());
                    }
                    
                    for (Locale locale : locales)
                    {
                        ResourceBundle bundle = bundleCache.get(locale);
                        
                        if (bundle != null)
                        {
                            if (bundle instanceof InlinePortletResourceBundle)
                            {
                                bundle = ((InlinePortletResourceBundle) bundle).getParent();
                            }
                            
                            if (bundle != null && bundle instanceof ReloadablePropertyResourceBundle)
                            {
                                try
                                {
                                    ((ReloadablePropertyResourceBundle) bundle).reload(getPortletApplicationClassLoader(pa));
                                }
                                catch (IOException e)
                                {
                                    log.error("Failed to reload resource bundle of " + paName + "::" + pdName + " for locale, " + locale, e);
                                }
                            }
                        }
                    }
                }
            }
    	}
    }

	public void applicationUpdated(PortletApplication pa)
	{
		// do nothing for now: formost application updates are already triggered/triggering unregister/register of a pa
	}

	public void applicationRemoved(PortletApplication pa)
	{
		applicationUpdated(pa);
	}

	public void portletUpdated(PortletDefinition pd) {
		synchronized (cacheMutex)
		{
            if (pd != null)
            {
            	String paName = pd.getApplication().getName();
            	String pdName = pd.getPortletName();
            	
            	// clear PreferenceValidator cache
                Map<String, PreferencesValidator> pvCache = validatorCache.get(paName);
                if (pvCache != null)
                {
                	pvCache.remove(pdName);
                }
                
                // clear Portlet ResourceBundle cache
                Map<String, Map<Locale, ResourceBundle>> portletResourceBundleCache = portletsResourceBundleCache.get(paName);
                
                if (portletResourceBundleCache != null)
                {
                	portletResourceBundleCache.remove(pdName);
                }
            	
                // update PortletInstance PortletConfig instance (maybe should kill/destroy PortletInstance ?)
                updatePortletConfig(pd);
            }
		}
	}

	public void portletRemoved(PortletDefinition pd) 
	{
		portletUpdated(pd);
	}
}
