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
