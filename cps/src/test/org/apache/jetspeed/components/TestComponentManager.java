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

import java.io.File;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.DOM4JConfiguration;
import org.apache.jetspeed.cps.template.TemplateLocatorComponent;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * TestComponentManager
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestComponentManager extends TestCase
{
    public TestComponentManager(String name) 
    {
        super( name );
    }

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[]) 
    {
        junit.awtui.TestRunner.main( new String[] { TestComponentManager.class.getName() } );
    }

    public void setup() 
    {
        System.out.println("Setup: Testing Email");
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestComponentManager.class);
    }
    
    public void testManager()
          throws Exception
    {
        Configuration config = getConfiguration();
        ComponentManager cm = new ComponentManager(config);
        
        cm.start();
        
        int count = 0;   
        Iterator containers = cm.getContainers().iterator();
        while (containers.hasNext())
        {
            MutablePicoContainer container = (MutablePicoContainer)containers.next();
            System.out.println("container = " + container);
            count++;                                        
        }
        assertTrue("count = 2" + count, count == 2);

        MutablePicoContainer container = cm.getContainer("default");
        assertNotNull("default container is null", container);
        
        container = cm.getContainer("persistence");
        assertNotNull("persistence container is null", container);
        
        TemplateLocatorComponent locator1 = (TemplateLocatorComponent)cm.getComponent("locator");
        assertNotNull("locator1 is null", locator1);
        
        TemplateLocatorComponent locator2 = (TemplateLocatorComponent)cm.getComponent("persistence", "locator2");
        assertNotNull("locator2 is null", locator2);
        
		Smart smart = (Smart) cm.getComponent(Smart.class.getName());
				assertNotNull("Smart is null", smart);
	   smart.test();


        cm.stop();                
                
        System.out.println("Component Manager Test completed");                               
    }

    public Configuration getConfiguration()
    throws Exception
    {        
        String filename = getConfigurationFile();
        // DOM4JConfiguration configuration = new DOM4JConfiguration();
        PropertiesConfiguration configuration = new PropertiesConfiguration();                 
        configuration.setFileName(filename);
        configuration.load();
        
        return configuration;
    }
    
    public String getConfigurationFile()
    {
        // return getApplicationRoot() + "/WEB-INF/conf/components.xml";
        return getApplicationRoot() + "/WEB-INF/conf/components.properties";
    }

        public String getApplicationRoot()
    {
        String applicationRoot = "test";
        File testPath = new File(applicationRoot);
        if (!testPath.exists())
        {
            testPath = new File( "cps" + File.separator + applicationRoot);
            if (testPath.exists())
            {
                applicationRoot = testPath.getAbsolutePath();
            }
        }
        return applicationRoot;
    }

    public void testInterceptorAdapter()
        throws Exception
    {
        InterceptorAdapterFactory adapterFactory 
                    = new InterceptorAdapterFactory(new DefaultComponentAdapterFactory());
        CachingComponentAdapterFactory cachingComponentAdapterFactory  
                    = new CachingComponentAdapterFactory(adapterFactory);
        ComponentAdapterFactory caf = cachingComponentAdapterFactory;
        DefaultPicoContainer pico = new DefaultPicoContainer(caf);

        CachingComponentAdapter dumbAdapter = (CachingComponentAdapter) caf.createComponentAdapter("dumb", DumbImpl.class, null);
        
        pico.registerComponent(dumbAdapter);

        Dumb dumb = (Dumb) dumbAdapter.getComponentInstance();
        System.out.println("dumb = " + dumb);
        dumb.test();
        Dumb dumb2 = (Dumb) dumbAdapter.getComponentInstance();
        System.out.println("dumb2 = " + dumb2);
                    
    }
    
}
