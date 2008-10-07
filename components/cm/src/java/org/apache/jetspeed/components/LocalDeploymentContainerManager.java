/*
 * Created on May 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.components.pico.groovy.GroovyComponentAdapter;
import org.apache.jetspeed.components.util.ComponentInfo;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class LocalDeploymentContainerManager implements ContainerManager
{
    protected List components;
    protected Configuration parentConfig;    

    public LocalDeploymentContainerManager(Configuration parentConfig) throws IOException
    {
        components = new ArrayList();
        this.parentConfig = parentConfig;
        locateDeployableComponents();
    }
    
    public PicoContainer assembleContainer( MutablePicoContainer container )
            throws IOException
    {
        doAssembleContainer(container);
        return container;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.ContainerManager#getContainerClassLoader()
     */
    public ClassLoader getContainerClassLoader()
    {
        // TODO Auto-generated method stub
        return getClass().getClassLoader();
    }
    
    protected void locateDeployableComponents() throws IOException
    {
        Enumeration enum = Thread.currentThread().getContextClassLoader().getResources("META-INF/component.pkg");
        while(enum.hasMoreElements())
        {
            URL pkgUrl = (URL) enum.nextElement();
            SimpleComponentPackage pkg = new SimpleComponentPackage(pkgUrl, parentConfig);           
            components.add(pkg);
        }
    }
    
    protected void doAssembleContainer( MutablePicoContainer container ) throws  IOException
    {

        Iterator componentItr = components.iterator();        
        while (componentItr.hasNext())
        {
            SimpleComponentPackage packg = (SimpleComponentPackage) componentItr.next();
            Iterator infoItr = packg.getAllComponentInformation();
            try
            {
                while (infoItr.hasNext())
                {
                    ComponentInfo info = (ComponentInfo) infoItr.next();       
                                                                                
                    Configuration configuration = info.getConfiguration();
                    
                    Class componentClass = info
                            .getComponentClass(Thread.currentThread().getContextClassLoader());
                    
                    Object componentKey = info
                            .getComponentKey(Thread.currentThread().getContextClassLoader());
                    System.out.println("Loading component "+componentKey);
                    ComponentAdapter adapter = new GroovyComponentAdapter(componentKey, componentClass, null, info
                            .isSingleton(), Thread.currentThread().getContextClassLoader(), configuration);
                    container.registerComponent(adapter);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new IllegalStateException(
                        "Failed to register component: " + e.toString());
            }

        }
    }

}
