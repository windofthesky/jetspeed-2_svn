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
package org.apache.jetspeed.components;


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestComponentManager
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class TestComponentManager extends ComponentAssemblyTestCase
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

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestComponentManager.class);
    }
        
    public void testContainer() throws Exception
    {
        SimpleComponent simple = (SimpleComponent)componentManager.getComponent("simple");
        assertNotNull("simple component is null", simple);        
        assertTrue("simple is not so simple", simple.getSimple().equals("simple"));        
    }

    public String getBaseProject()
    {
        return "components/cm";
    }
    
}
