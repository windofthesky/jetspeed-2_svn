/*
 * Created on Jul 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

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
