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

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * <p>
 * SpringComponentManager
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class SpringComponentManager implements ComponentManager
{
    protected FileSystemXmlApplicationContext appContext;
    
    protected ArrayList factories;

    public SpringComponentManager(String[] springConfigs, ApplicationContext parentAppContext)
    {
        factories = new ArrayList();
        appContext = new FileSystemXmlApplicationContext(springConfigs, parentAppContext );
        
        factories.add(appContext);        
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
    public Object getComponent( Object componentName )
    {        
        if(componentName instanceof Class)
        {
            return appContext.getBean(((Class)componentName).getName());
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
     * @see org.apache.jetspeed.components.ComponentManagement#getComponent(java.lang.Object, java.lang.Object)
     * @param containerName
     * @param componentName
     * @return
     */
    public Object getComponent( Object containerName, Object componentName )
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
    public Object getContainer( String containerName )
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

    }

}
