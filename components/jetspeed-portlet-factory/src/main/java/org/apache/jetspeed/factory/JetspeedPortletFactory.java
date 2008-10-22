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
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.container.JetspeedPortletConfig;
import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.jetspeed.container.PortalAccessor;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.portlet.PortletObjectProxy;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.internal.InternalPortletContext;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

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

    private Map portletCache;

    private Map validatorCache;

    private static final Log log = LogFactory
            .getLog(JetspeedPortletFactory.class);

    private final Map<String, PortletFactoryInfo> classLoaderMap;

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

    private ServletConfig jetspeedConfig;
    
    public JetspeedPortletFactory(ServletConfig jetspeedConfig, boolean autoSwitchConfigMode,
            boolean autoSwitchEditDefaultsModeToEditMode)
    {
        this.jetspeedConfig = jetspeedConfig;
        this.portletCache = Collections.synchronizedMap(new HashMap());
        this.validatorCache = Collections.synchronizedMap(new HashMap());
        classLoaderMap = Collections.synchronizedMap(new HashMap<String, PortletFactoryInfo>());

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

    public void setCustomConfigModePortletUniqueName(
            String customConfigModePortletUniqueName)
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
            ServletContext servletContext = jetspeedConfig.getServletContext();
            ServletContext portletAppContext = servletContext.getContext(pa.getName());
            InternalPortletContext context = new JetspeedPortletContext(portletAppContext, pa);      
            classLoaderMap.put(pa.getName(), new PortletFactoryInfo(cl, context));
        }
    }

    public void unregisterPortletApplication(PortletApplication pa)
    {
        synchronized (classLoaderMap)
        {
            synchronized (portletCache)
            {
                PortletFactoryInfo info = (PortletFactoryInfo) classLoaderMap.remove(pa.getName());
                if (info != null)
                {
                    ClassLoader cl = info.getClassLoader();
                    ClassLoader currentContextClassLoader = Thread
                            .currentThread().getContextClassLoader();
                    Iterator portletDefinitions = pa.getPortletDefinitions()
                            .iterator();
                    while (portletDefinitions.hasNext())
                    {
                        PortletDefinition pd = (PortletDefinition) portletDefinitions
                                .next();
                        String pdId = pd.getPortletName();
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
                                Thread.currentThread().setContextClassLoader(
                                        currentContextClassLoader);
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
                validator = (PreferencesValidator) validatorCache.get(pdId);
                if (validator == null)
                {
                    String className = ((PortletDefinition) pd)
                            .getPreferenceValidatorClassname();
                    if (className != null)
                    {
                        PortletApplication pa = (PortletApplication) pd
                                .getPortletApplicationDefinition();
                        PortletFactoryInfo info = classLoaderMap.get(pa.getName());
                        if (info != null)
                        {
                            ClassLoader paCl = info.getClassLoader();
                            if (paCl == null) { throw new UnavailableException(
                                    "Portlet Application " + pa.getName()
                                            + " not available"); }
    
                            ClassLoader currentContextClassLoader = Thread
                                    .currentThread().getContextClassLoader();
                            try
                            {
                                Class clazz = paCl.loadClass(className);
                                try
                                {
                                    Thread.currentThread().setContextClassLoader(
                                            paCl);
                                    validator = (PreferencesValidator) clazz
                                            .newInstance();
                                    validatorCache.put(pdId, validator);
                                }
                                finally
                                {
                                    Thread.currentThread().setContextClassLoader(
                                            currentContextClassLoader);
                                }
                            }
                            catch (Exception e)
                            {
                                String msg = "Cannot create PreferencesValidator instance "
                                        + className
                                        + " for Portlet "
                                        + pd.getPortletName();
                                log.error(msg, e);
                            }
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
    public PortletInstance getPortletInstance(ServletContext servletContext,
            PortletDefinition pd) throws PortletException
    {
        PortletInstance portlet = null;
        String pdId = pd.getId().toString();
        PortletApplication pa = (PortletApplication) pd
                .getPortletApplicationDefinition();

        try
        {
            synchronized (portletCache)
            {
                portlet = (PortletInstance) portletCache.get(pdId);
                if (null != portlet) { return portlet; }

                PortletFactoryInfo info = classLoaderMap.get(pa.getName());
                ClassLoader paCl = info.getClassLoader();
                if (paCl == null) 
                { 
                    throw new UnavailableException(
                        "Portlet Application " + pa.getName()
                                + " not available"); }

                ClassLoader currentContextClassLoader = Thread.currentThread()
                        .getContextClassLoader();

                try
                {
                    Class clazz = paCl.loadClass(pd.getPortletClass());
                    try
                    {
                        Thread.currentThread().setContextClassLoader(paCl);
                        // wrap new Portlet inside PortletInstance which ensures
                        // the destroy
                        // method will wait for all its invocation threads to
                        // complete
                        // and thereby releasing all its ClassLoader locks as
                        // needed for local portlets.

                        if (this.portletProxyUsed
                                && !PortletObjectProxy.isPortletObjectProxied())
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
                            portlet = new JetspeedPortletInstance(pd
                                    .getPortletName(), (Portlet) clazz
                                    .newInstance());
                        }
                    }
                    finally
                    {
                        Thread.currentThread().setContextClassLoader(
                                currentContextClassLoader);
                    }
                }
                catch (Exception e)
                {
                    String msg = "Cannot create Portlet instance "
                            + pd.getPortletClass()
                            + " for Portlet Application " + pa.getName();
                    log.error(msg, e);
                    throw new UnavailableException(msg);
                }

                PortletContext portletContext = PortalAccessor
                        .createPortletContext(servletContext, pa);
                PortletConfig portletConfig = PortalAccessor
                        .createPortletConfig(portletContext, pd);

                try
                {
                    try
                    {
                        Thread.currentThread().setContextClassLoader(paCl);
                        portlet.init(portletConfig);
                    }
                    finally
                    {
                        Thread.currentThread().setContextClassLoader(
                                currentContextClassLoader);
                    }
                }
                catch (PortletException e1)
                {
                    log.error("Failed to initialize Portlet "
                            + pd.getPortletClass()
                            + " for Portlet Application " + pa.getName(), e1);
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
            log.error("PortletFactory: Failed to load portlet "
                    + pd.getPortletClass(), e);
            throw new UnavailableException("Failed to load portlet "
                    + pd.getPortletClass() + ": " + e.toString());
        }
        return portlet;
    }

    public void updatePortletConfig(PortletDefinition pd)
    {
        if (pd != null)
        {
            // System.out.println("$$$$ updating portlet config for " +
            // pd.getName());
            String key = pd.getId().toString();
            PortletInstance instance = (PortletInstance) portletCache.get(key);
            if (instance != null)
            {
                JetspeedPortletConfig config = (JetspeedPortletConfig) instance
                        .getConfig();
                config.setPortletDefinition(pd);
            }
        }
    }

    public ClassLoader getPortletApplicationClassLoader(PortletApplication pa)
    {
        synchronized (classLoaderMap)
        {
            if (pa != null) 
            { 
                PortletFactoryInfo info = classLoaderMap.get(pa.getName());
                if (info != null)
                    return info.getClassLoader(); 
            }
            return null;
        }
    }

    public boolean isPortletApplicationRegistered(PortletApplication pa)
    {
        return getPortletApplicationClassLoader(pa) != null;
    }

    public InternalPortletContext getPortletContext(PortletApplicationDefinition pad)
        throws PortletContainerException
    {
        PortletFactoryInfo info = classLoaderMap.get(pad.getName());
        if (info != null)
        {
            return info.getContext();
        }
        throw new PortletContainerException("App context not found for application " + pad.getName());
//        ServletContext context = jetspeedConfig.getServletContext();
//        ServletContext portletAppContext = context.getContext(pad.getName());
//        JetspeedPortletContext jpc = new JetspeedPortletContext(portletAppContext, pad);
//        classLoaderMap.put(pa.getName(), new PortletFactoryInfo(cl, context));        
//        return jpc;
    }

}
