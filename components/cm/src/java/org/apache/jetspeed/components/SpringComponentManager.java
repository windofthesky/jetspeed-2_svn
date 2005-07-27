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
package org.apache.jetspeed.components;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * <p>
 * SpringComponentManager
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 * 
 */
public class SpringComponentManager implements ComponentManager
{
    protected ConfigurableApplicationContext appContext;

    private ConfigurableApplicationContext bootCtx;

    protected ArrayList factories;

    private Map preconfiguredBeans;

    private boolean started = false;

    public SpringComponentManager(String[] bootConfigs, String[] appConfigs, ServletContext servletContext,
            String appRoot)
    {
        File appRootDir = new File(appRoot);
        System.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, appRootDir.getAbsolutePath());

        if (bootConfigs != null && bootConfigs.length > 0)
        {
            bootCtx = new XmlWebApplicationContext();
            ((XmlWebApplicationContext) bootCtx).setServletContext(servletContext);
            ((XmlWebApplicationContext) bootCtx).setConfigLocations(bootConfigs);
        }
        else
        {
            bootCtx = new GenericApplicationContext();
        }

        appContext = new XmlWebApplicationContext();
        ((XmlWebApplicationContext) appContext).setParent(bootCtx);
        ((XmlWebApplicationContext) appContext).setServletContext(servletContext);
        ((XmlWebApplicationContext) appContext).setConfigLocations(appConfigs);

        factories = new ArrayList();
        factories.add(appContext);

        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this);
    }

    public SpringComponentManager(String[] bootConfigs, String[] appConfigs, ServletContext servletContext,
            String appRoot, Map preconfiguredBeans)
    {
        this(bootConfigs, appConfigs, servletContext, appRoot);
        this.preconfiguredBeans = preconfiguredBeans;
    }

    /**
     * <p>
     * getComponent
     * </p>
     * 
     * @see org.apache.jetspeed.components.ComponentManagement#getComponent(java.lang.Object)
     * @param componentName
     * @return
     */
    public Object getComponent(Object componentName)
    {
        if (componentName instanceof Class)
        {
            return appContext.getBean(((Class) componentName).getName());
        }
        else
        {
            return appContext.getBean(componentName.toString());
        }
    }

    /**
     * <p>
     * getComponent
     * </p>
     * 
     * @see org.apache.jetspeed.components.ComponentManagement#getComponent(java.lang.Object,
     *      java.lang.Object)
     * @param containerName
     * @param componentName
     * @return
     */
    public Object getComponent(Object containerName, Object componentName)
    {
        return getComponent(componentName);
    }

    /**
     * <p>
     * getContainer
     * </p>
     * 
     * @see org.apache.jetspeed.components.ContainerManagement#getContainer(java.lang.String)
     * @param containerName
     * @return
     */
    public Object getContainer(String containerName)
    {
        return appContext;
    }

    /**
     * <p>
     * getRootContainer
     * </p>
     * 
     * @see org.apache.jetspeed.components.ContainerManagement#getRootContainer()
     * @return
     */
    public Object getRootContainer()
    {
        return appContext;
    }

    /**
     * <p>
     * getContainers
     * </p>
     * 
     * @see org.apache.jetspeed.components.ContainerManagement#getContainers()
     * @return
     */
    public Collection getContainers()
    {
        return factories;
    }

    /**
     * <p>
     * stop
     * </p>
     * 
     * @see org.apache.jetspeed.components.ContainerManagement#stop()
     * 
     */
    public void stop()
    {
        appContext.close();
        bootCtx.close();
        started = false;
    }

    public ApplicationContext getApplicationContext()
    {
        return appContext;
    }

    public void addComponent(String name, Object bean)
    {
        if (preconfiguredBeans == null)
        {
            preconfiguredBeans = new HashMap();
        }
        preconfiguredBeans.put(name, bean);

        if (started)
        {
            bootCtx.getBeanFactory().registerSingleton(name, bean);
        }
    }

    public void start()
    {
        bootCtx.refresh();
        if (preconfiguredBeans != null)
        {
            Iterator itr = preconfiguredBeans.entrySet().iterator();
            while (itr.hasNext())
            {
                Map.Entry entry = (Map.Entry) itr.next();
                bootCtx.getBeanFactory().registerSingleton(entry.getKey().toString(), entry.getValue());
            }
        }

        appContext.refresh();
        started = true;
    }

}
