/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.locator;

import org.apache.jetspeed.components.ComponentAssemblyTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestTemplateLocator
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestTemplateLocator extends ComponentAssemblyTestCase
{
    public TestTemplateLocator(String name) 
    {
        super( name );
    }

    public String getBaseProject()
    {
        return "components/jetspeed";
    }
    
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[]) 
    {
        junit.awtui.TestRunner.main( new String[] { TestTemplateLocator.class.getName() } );
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestTemplateLocator.class);
    }
            
    public void testLocateTemplate()
          throws Exception
    {
        TemplateLocator component = (TemplateLocator)componentManager.getComponent("TemplateLocator");
        assertNotNull("template service is null", component);            
        LocatorDescriptor locator = component.createLocatorDescriptor("email");
        locator.setName("test.vm");
        TemplateDescriptor template = component.locateTemplate(locator);
        assertNotNull("template is null", template);
        System.out.println("template1 = " + template);
        assertTrue("template1 result", "type/email/name/test.vm".endsWith(template.toString()));
        
        LocatorDescriptor locator2 = component.createLocatorDescriptor("email");
        locator2.setName("htmltest.vm");
        locator2.setMediaType("html");        
        template = component.locateTemplate(locator2);
        assertNotNull("template is null", template);                
        System.out.println("template2 = " + template);            
        assertTrue("template2 result", "type/email/media-type/html/name/htmltest.vm".endsWith(template.toString()));

        LocatorDescriptor locator3 = component.createLocatorDescriptor("email");
        locator3.setName("entest.vm");
        locator3.setMediaType("html");
        locator3.setLanguage("en");                
        template = component.locateTemplate(locator3);
        assertNotNull("template is null", template);        
        System.out.println("template3 = " + template);            
        assertTrue("template3 result", "type/email/media-type/html/language/en/name/entest.vm".endsWith(template.toString()));

        LocatorDescriptor locator4 = component.createLocatorDescriptor("email");
        locator4.setName("ustest.vm");
        locator4.setMediaType("html");
        locator4.setLanguage("en");
        locator4.setCountry("US");                
        template = component.locateTemplate(locator4);
        assertNotNull("template is null", template);        
        System.out.println("template4 = " + template);            
        assertTrue("template4 result", 
            "type/email/media-type/html/language/en/country/US/name/ustest.vm".endsWith(template.toString()));

        // test fallback
        LocatorDescriptor locator5 = component.createLocatorDescriptor("email");
        locator5.setName("entest.vm");
        locator5.setMediaType("html");
        locator5.setLanguage("en");
        locator5.setCountry("UZ");                
        template = component.locateTemplate(locator5);
        assertNotNull("template is null", template);        
        System.out.println("template5 = " + template);            
        assertTrue("template5 result", 
            "type/email/media-type/html/language/en/name/entest.vm".endsWith(template.toString()));

        // test fallback all the way to email
        LocatorDescriptor locator6 = component.createLocatorDescriptor("email");
        locator6.setName("test.vm");
        locator6.setMediaType("html");
        locator6.setLanguage("en");
        locator6.setCountry("UZ");                
        template = component.locateTemplate(locator6);
        System.out.println("template6 = " + template);            
        assertTrue("template6 result", 
            "type/email/name/test.vm".endsWith(template.toString()));
                    
    }
    
}
