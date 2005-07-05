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
package org.apache.jetspeed.aggregator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.AbstractPortalContainerTestCase;
import org.apache.jetspeed.aggregator.impl.PortletRendererImpl;

/**
 * TestPortletRenderer
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: TestRenderer.java 187850 2004-10-29 14:33:27Z weaver $
 */
public class TestRenderer extends AbstractPortalContainerTestCase
{
    protected PortletRenderer renderer;
    
   
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestRenderer.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        renderer = new PortletRendererImpl(portletContainer, windowAccessor);       
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
        return new TestSuite(TestRenderer.class);
    }

    public void testBasic() throws Exception
    {
        assertNotNull("portlet renderer is null", renderer);
    }
    
}
