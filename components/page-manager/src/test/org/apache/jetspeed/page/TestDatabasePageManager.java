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
package org.apache.jetspeed.page;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.NanoDeployerBasedTestSuite;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;

/**
 * TestPageService
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestDatabasePageManager extends AbstractComponentAwareTestCase
{
    private PageManager service = null;
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestDatabasePageManager(String name)
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
        junit.awtui.TestRunner.main(new String[] { TestDatabasePageManager.class.getName()});
    }

    public void setup()
    {
        System.out.println("Setup: Testing Page Service");
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
       // return new TestSuite(TestDatabasePageManager.class);
    	NanoDeployerBasedTestSuite suite = new NanoDeployerBasedTestSuite(TestDatabasePageManager.class);
    	return suite;
    }
    
    
    public void testBuildBasePage()
    {
    	PageManager pm = (PageManager)getContainer().getComponentInstance(PageManager.class);
        assertNotNull("page manager is null", pm);            
        
        Page page = pm.newPage();
        page.setTitle("TEST");

        Fragment frag = pm.newFragment();
        frag.setId("Frag1");
        frag.setType(Fragment.LAYOUT);

        page.setRootFragment(frag);
    }
    
}
