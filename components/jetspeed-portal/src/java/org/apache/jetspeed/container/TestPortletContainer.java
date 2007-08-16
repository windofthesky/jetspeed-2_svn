/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.container;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestPortletContainer
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestPortletContainer extends TestCase 
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestPortletContainer(String name)
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
        junit.awtui.TestRunner.main(new String[] { TestPortletContainer.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
      //  portletContainer = new JetspeedPortletContainerWrapper(new PortletContainerImpl());
    }

   public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPortletContainer.class);
    }

    public void testBasic()
    {
        
        // not much more i can do without setting up a mock servlet or portlet framework
    }
}
