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
package org.apache.jetspeed.container.url;

import javax.portlet.PortletURL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * TestPortletURL
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */

public class TestPortletURL extends TestCase
{

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestPortletURL(String name)
    {
        super(name);
    }

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(
            new String[] { TestPortletURL.class.getName()});
    }

    public void setUp()
    {
        System.out.println("Setup: Testing Portlet URL impl");
    }

    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPortletURL.class);
    }


    public void testPortletURL1() throws Exception
    {
        PortletURL url;
        url = new JetspeedRenderURL("/jetspeed",
                                               "P-12345",
                                               false);
        assertEquals("http:///jetspeed/portal/peid/P-12345", url.toString() );
        url = new JetspeedActionURL("/jetspeed",
                                               "P-12345",
                                               false);
        assertEquals("http:///jetspeed/portal/action/true/peid/P-12345", url.toString() );
    }

    public void testPortletURL2() throws Exception
    {
        PortletURL url;
        url = new JetspeedRenderURL("/jetspeed",
                                               "P-12345",
                                               false);
        url.setSecure(true);
        assertEquals("https:///jetspeed/portal/peid/P-12345", url.toString() );
        url = new JetspeedActionURL("/jetspeed",
                                               "P-12345",
                                               false);
        url.setSecure(true);
        assertEquals("https:///jetspeed/portal/action/true/peid/P-12345", url.toString() );
    }

}
