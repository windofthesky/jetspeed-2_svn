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

import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.apache.jetspeed.container.TestPortletContainer;
import org.picocontainer.MutablePicoContainer;

/**
 * TestPortletRenderer
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestRenderer extends AbstractComponentAwareTestCase 
{
    private MutablePicoContainer container;
    private PortletRenderer renderer;
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestRenderer(String name)
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
        junit.awtui.TestRunner.main(new String[] { TestRenderer.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        container = (MutablePicoContainer) getContainer();
        renderer = (PortletRenderer) container.getComponentInstance(PortletRenderer.class);        
    }

    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestPortletContainer.class);
        suite.setScript("org/apache/jetspeed/containers/test-renderer-container.groovy");
        return suite;                                   
    }

    public void testBasic() throws Exception
    {
        assertNotNull("portlet renderer is null", renderer);
    }
    
}
