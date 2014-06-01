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
package org.apache.jetspeed.components;

import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * SpringComponentManager
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 * 
 */
public class SpringComponentManager implements ComponentManager
{
    protected ConfigurableApplicationContext appContext;

    private ConfigurableApplicationContext bootCtx;

    protected ArrayList<ApplicationContext> factories;

    private Map<String, Object> preconfiguredBeans;

    private boolean started = false;

    public SpringComponentManager(JetspeedBeanDefinitionFilter filter, String[] bootConfigs, String[] appConfigs, ServletContext servletContext,
            String appRoot)
    {
        this(filter, bootConfigs, appConfigs, servletContext, appRoot, null);
    }

    public SpringComponentManager(JetspeedBeanDefinitionFilter filter, String[] bootConfigs, String[] appConfigs, ServletContext servletContext,
            String appRoot, Properties initProperties)
    {
        // Using \ characters will corrupt the path when used as (Spring expanded) variables
        // making sure default (Java) path separators are used which somehow always work, even on Windows platform.
        appRoot = appRoot.replace('\\', '/');
        
        if (initProperties == null)
        {
            initProperties = new Properties();
        }
        initProperties.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, appRoot);
        if (!initProperties.containsKey(JetspeedEngineConstants.JETSPEED_PROPERTIES_PATH_KEY))
        {
            initProperties.put(JetspeedEngineConstants.JETSPEED_PROPERTIES_PATH_KEY, appRoot+JetspeedEngineConstants.JETSPEED_PROPERTIES_PATH_DEFAULT);
        }
        
        if (bootConfigs != null && bootConfigs.length > 0)
        {
            bootCtx = new FilteringXmlWebApplicationContext(filter, bootConfigs, initProperties, servletContext);
        }
        else
        {
            bootCtx = new FileSystemXmlApplicationContext();
        }
        appContext = new FilteringXmlWebApplicationContext(filter, appConfigs, initProperties, servletContext, bootCtx);

        factories = new ArrayList<ApplicationContext>();
        factories.add(appContext);

        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appContext);
    }

    public SpringComponentManager(JetspeedBeanDefinitionFilter filter, String[] bootConfigs, String[] appConfigs, String appRoot, boolean fileSystem)
    {        
        this(filter, bootConfigs, appConfigs, appRoot, null, fileSystem);
    }
    
    public SpringComponentManager(JetspeedBeanDefinitionFilter filter, String[] bootConfigs, String[] appConfigs, String appRoot, Properties initProperties, boolean fileSystem)
    {        
        // Using \ characters will corrupt the path when used as (Spring expanded) variables
        // making sure default (Java) path separators are used which somehow always work, even on Windows platform.
        appRoot = appRoot.replace('\\', '/');
        
        if (initProperties == null)
        {
            initProperties = new Properties();
        }
        initProperties.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, appRoot);
        if (!initProperties.containsKey(JetspeedEngineConstants.JETSPEED_PROPERTIES_PATH_KEY))
        {
            initProperties.put(JetspeedEngineConstants.JETSPEED_PROPERTIES_PATH_KEY, appRoot+JetspeedEngineConstants.JETSPEED_PROPERTIES_PATH_DEFAULT);
        }
        
        if (bootConfigs != null && bootConfigs.length > 0)
        {
            if (fileSystem)
            {
                bootCtx = new FilteringFileSystemXmlApplicationContext(filter, bootConfigs, initProperties);
            }
            else
            {
                bootCtx = new FilteringClassPathXmlApplicationContext(filter, bootConfigs, initProperties);
            }
        }
        else
        {
            bootCtx = new FileSystemXmlApplicationContext();
        }
        
        if (fileSystem)
        {
            appContext = new FilteringFileSystemXmlApplicationContext(filter, appConfigs, initProperties, bootCtx);
        }
        else
        {
            appContext = new FilteringClassPathXmlApplicationContext(filter, appConfigs, initProperties, bootCtx);
        }
        factories = new ArrayList();
        factories.add(appContext);        
    }
    
    /**
     * <p>
     * containsComponent
     * </p>
     * 
     * @see org.apache.jetspeed.components.ComponentManagement#containsComponent(java.lang.Object)
     * @param componentName
     * @return
     */
    public boolean containsComponent(Object componentName)
    {
        if (componentName instanceof Class)
        {
            return appContext.containsBean(((Class) componentName).getName());
        }
        else
        {
            return appContext.containsBean(componentName.toString());
        }
    }
    
    /**
     * Lookup a Jetspeed Component in the IOC container, returning an un-casted instance of
     * the component service. Deprecated. Use {@link SpringComponentManager#lookupComponent(String)}
     *
     * @see org.apache.jetspeed.components.ComponentManagement#getComponent(java.lang.Object)
     * @param componentName can be either a String or a #{@link @java.lang.Class} If its a Class,
     *                      the component name must match the toString representation of that class
     * @deprecated in 2.3.0
     * @see {@link SpringComponentManager#lookupComponent(String)} or
     *      {@link SpringComponentManager#lookupComponent(Class)}
     * @return the component instance of null if not found
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
     * containsComponent
     * </p>
     * 
     * @see org.apache.jetspeed.components.ComponentManagement#containsComponent(java.lang.Object,
     *      java.lang.Object)
     * @param containerName
     * @param componentName
     * @return
     */
    public boolean containsComponent(Object containerName, Object componentName)
    {
        return containsComponent(componentName);
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
        if (started)
        {
            appContext.stop();
            appContext.close();

            bootCtx.stop();
            bootCtx.close();
            
            started = false;
        }
    }

    public ApplicationContext getApplicationContext()
    {
        return appContext;
    }

    public void addComponent(String name, Object bean)
    {
        if (preconfiguredBeans == null)
        {
            preconfiguredBeans = new HashMap<String, Object>();
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
        bootCtx.start();
        
        appContext.refresh();
        appContext.start();
        
        started = true;
    }

    /**
     * Lookup a Jetspeed Component in the IOC container, returning an automatically casted instance of
     * the component service
     *
     * @param componentName the name of the component (bean) to lookup
     * @param <T> the return type of the interface of the component
     * @return the implementing component service for the given name
     * @since 2.3.0
     */
    public <T> T lookupComponent(String componentName) {
        return appContext == null ? null : (T) appContext.getBean(componentName);
    }

    /**
     * Lookup a Jetspeed Component in the IOC container, returning an automatically casted instance of
     * the component service
     *
     * @param componentClass the class of the component (bean) singleton to lookup
     * @param <T> the return type of the interface of the component
     * @return the implementing component service for the given name
     * @since 2.3.0
     */
    public <T> T lookupComponent(Class componentClass) {
        return appContext == null ? null : (T) appContext.getBean(componentClass.getName());
    }

}
