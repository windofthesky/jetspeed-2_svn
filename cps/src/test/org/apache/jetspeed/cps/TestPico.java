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
package org.apache.jetspeed.cps;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.cps.template.Template;
import org.apache.jetspeed.cps.template.TemplateLocator;
import org.apache.jetspeed.cps.template.TemplateLocatorComponent;
import org.apache.jetspeed.cps.template.TemplateLocatorComponentImpl;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestPico
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestPico extends TestCase
{
    /**
      * Defines the testcase name for JUnit.
      *
      * @param name the testcase's name.
      */
    public TestPico( String name ) 
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
        junit.awtui.TestRunner.main( new String[] { TestPico.class.getName() } );
    }

    public void setup() 
    {
        System.out.println("Setup: Testing Email");
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite( TestPico.class );
    }
    
    public void testContainer()
          throws Exception
    {
        Configuration configuration = getConfiguration();
        
        MutablePicoContainer pico = new DefaultPicoContainer();
        // ConstructorComponentAdaptorFactory cca = new ConstructorComponentAdaptorFactory();
            
        pico.registerComponentImplementation(TemplateLocatorComponent.class, TemplateLocatorComponentImpl.class);    
        // pico.addParameterToComponent(TemplateLocatorComponent.class, Configuration.class, configuration);    
        pico.start();
        
        TemplateLocatorComponent locator1 = (TemplateLocatorComponent)pico.getComponentInstance(TemplateLocatorComponent.class);
        assertNotNull("locator 1 is null", locator1);
        System.out.println("locator 1 = " + locator1);
        
        TemplateLocatorComponent locator2 = (TemplateLocatorComponent)pico.getComponentInstance(TemplateLocatorComponent.class);
        assertNotNull("locator 1 is null", locator2);
        System.out.println("locator 2 = " + locator2);
        // simpleTest(locator2);
        
        ComponentAdapter adapter = null;
        Iterator adapters = pico.getComponentAdapters().iterator();
        while (adapters.hasNext())
        {
            adapter = (ComponentAdapter)adapters.next();
            System.out.println("adapter = " + adapter);                
        }
        
        pico.stop();
        
    }
    
    public void simpleTest(TemplateLocatorComponent service)
    throws Exception        
    {
        TemplateLocator locator = service.createLocator("email");
        locator.setName("test.vm");
        /*
        Template template = service.locateTemplate(locator);
        assertNotNull("template is null", template);
        System.out.println("template1 = " + template);
        assertTrue("template1 result", "type/email/name/test.vm".endsWith(template.toString()));
        */        
    }
    
    public Configuration getConfiguration()
    throws Exception
    {
        String propertiesFilename = getPropertiesFile();
        String applicationRoot = getApplicationRoot();
        Configuration configuration = (Configuration) 
            new PropertiesConfiguration(propertiesFilename);
            
        configuration.setProperty(CPSConstants.APPLICATION_ROOT_KEY, applicationRoot);
        return configuration;
    }
    
    /**
     * Override to set your own properties file
     *
     */
    public String getPropertiesFile()
    {
        return getApplicationRoot() + "/WEB-INF/conf/cps.properties";
    }

    
    /**
     * Override to set your own application root
     * If the default directory does not exist, then look in
     * the cps directory.  If the directory exist in the cps directory,
     * then return this directory.  Yes this is a hack, but it works.
     */
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
    
}
