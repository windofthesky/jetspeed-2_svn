/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.components;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nanocontainer.script.bsh.BeanShellComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.Startable;
import org.picocontainer.defaults.BeanComponentAdapter;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * ComponentManager manages Jetspeed components.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ComponentManager implements Startable, ContainerManagement, ComponentManagement
{
    private static final Log log = LogFactory.getLog(ComponentManager.class);

    private Configuration config = null;
    private Map containers = new HashMap();
    private MutablePicoContainer defaultContainer = null;
    
    private ComponentManager()
    {       
        // requires a components configuration     
    }
    
    public ComponentManager(Configuration config)
    {
        this.config = config;
    }
    
    public void start()
    {
        createContainers();
        loadComponents();

        // start containers
        // with container hierachies, will starting the root container start its children?                
        // defaultContainer.start();
        Iterator all = containers.values().iterator();
        while (all.hasNext())
        {
            MutablePicoContainer container = (MutablePicoContainer)all.next();
            container.start();
        }
    }
    
    public void stop()
    {
    }

    public Object getComponent(String componentName)
    {
        // TODO: might be better to use a container hierarchy, with parent containers    
        return this.defaultContainer.getComponentInstance(componentName);
    }
    
    public Object getComponent(String containerName, String componentName)
    {
        Object component = null;
        MutablePicoContainer container = getContainer(containerName);
        if (container != null)
        {
            component = container.getComponentInstance(componentName);
        }
        return component;
    }

    public MutablePicoContainer getContainer(String containerName)
    {
        return (MutablePicoContainer)this.containers.get(containerName);
    }
    
    public MutablePicoContainer getDefaultContainer()
    {
        return this.defaultContainer;
    }
    
    public Collection getContainers()
    {
        return this.containers.values();
    }

        
    private void loadComponents()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
        Enumeration resources = null;   
        try
        { 
            resources = classLoader.getResources("META-INF/js-components.properties") ;
        }
        catch (IOException e)
        {
            log.error("failed to load js components", e);
            return;
        }
        
        while(resources.hasMoreElements()) 
        {
            URL url = (URL)resources.nextElement();
            System.out.println("Loading URL " + url);                    
            this.loadComponent(url);
        }        
    }

    private void createContainers()
    {
        // for now this is kinda hard-coded to the simple properties file format
        String defaultContainer = config.getString("default.container"); 
        String [] containerNames = config.getStringArray("containers");
        MutablePicoContainer container = null;            
        
        for (int ix = 0; ix < containerNames.length; ix++)
        {
            String className = config.getString("container." + containerNames[ix] + ".classname");
            String adapter = config.getString("container." + containerNames[ix] + ".adapter");
            try
            {            
                Class containerClass = Class.forName(className);
                container = (MutablePicoContainer)containerClass.newInstance();
                if (containerNames.equals(defaultContainer))
                {
                    this.defaultContainer = container;
                }
                containers.put(containerNames[ix], container);
                System.out.println("Created container for container " + containerNames[ix] + " and class  " + className);
            }
            catch (Exception e)
            {
                log.error("failed to create container  " + containerNames[ix] + " and class " + className, e);
            }            
        }
        if (this.defaultContainer == null)
        {
            if (container != null)
            {
                this.defaultContainer = container;
            }
            else
            {
                this.defaultContainer = new DefaultPicoContainer();
                containers.put("default", this.defaultContainer);
            }
        }            
    }
    
    private void loadComponent(URL url)
    {   
    	//TODO We should be looking for a persistent configuration then use the one found in the CL to fill any dafults     
        PropertiesConfiguration configuration = new PropertiesConfiguration();
        configuration.setFileName(url.getFile().toString());
        
        try
        {                 
            System.out.println("loading configuration: " + configuration);
            configuration.load();
        }
        catch (IOException e)
        {
            log.error("Failed to load js component configuration for " + url, e);
            return;
        }
        
        String [] componentNames = configuration.getStringArray("components");
        
        for (int ix = 0; ix < componentNames.length; ix++)
        {
            System.out.println("loading component: [" + componentNames[ix] + "]");
            
            String className = configuration.getString(componentNames[ix] + ".classname");
            String containerName = configuration.getString(componentNames[ix] + ".container");
            // String adapter = config.getString("container." + componentNames[ix] + ".adapter");
            try
            {            
                Configuration componentConfig = configuration.subset(componentNames[ix]);
                // debugConfig(componentConfig);
                Class componentClass = Class.forName(className);                
                Parameter [] parameters =  {new ConstantParameter(componentConfig)};
              
                
                MutablePicoContainer container = this.getContainer(containerName);
                if (null == container)
                {
                    container = this.defaultContainer;                  
                }
                
                if(hasBeanshellConfig(componentClass))
                {
                	container.registerComponent(new ConfigurableBeanShellComponentAdapter(componentNames[ix], componentClass, null, componentConfig));
                }
                else
                {
					container.registerComponentImplementation(componentNames[ix], componentClass, parameters);
                }
                
                
                System.out.println("Created component " + componentNames[ix] + " and class  " + className);
            }
            catch (Exception e)
            {
                log.error("failed to create component " + componentNames[ix] + " and class " + className, e);
            }            
        }
    }
    
    private void debugConfig(Configuration config)
    {
        Iterator it = config.getKeys();
        while (it.hasNext())
        {
            String key = (String)it.next();
            System.out.println("key = " + key + ", value = " + config.getProperty(key));
        }
        System.out.println("*** roots = " + config.getString("roots"));        
    }
    
    private boolean hasBeanshellConfig(Class componentClass)
    {    	
    	String scriptName = componentClass.getName().replace('.','/')+".bsh";
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
		URL scriptUrl = classLoader.getResource(scriptName);
    	return scriptUrl != null;
    }
}
