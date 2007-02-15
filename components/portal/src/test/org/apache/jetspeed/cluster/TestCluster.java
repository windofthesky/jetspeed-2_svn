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
package org.apache.jetspeed.cluster;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;

/**
 * <p>
 * TestCluster
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id: TestCluster.java 463270 2006-10-12 15:19:29Z taylor $
 *  
 */
public class TestCluster extends DatasourceEnabledSpringTestCase
{
 
	   /** The node manager. */
    private NodeManager single;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
    	System.setProperty("applicationRoot","target/jetspeed");
        super.setUp();

        single = (NodeManager) ctx.getBean("org.apache.jetspeed.cluster.NodeManager");
    }
  
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestCluster.class);
    }

    /** Test set user info map. * */
    public void testCluser() throws Exception
    {
    	String contextName = "SOME_NEW_PORTLET_APPLICATION";
    	Long id = new Long(10);
    	
        assertNotNull("Manager should be instantiated", single);
        
        int numExistingApps = single.getNumberOfNodes();
        
        //create a new node
        int status = single.checkNode(id, contextName);
        if (status != NodeManager.NODE_NEW)
        {
        	single.removeNode(contextName); //previous run didn't clean up
        	status = single.checkNode(id, contextName);
            assertEquals("Should be a new node",NodeManager.NODE_NEW,status);
        }
        
        // ok - create a new node
        single.addNode(id, contextName);
        int newApps = single.getNumberOfNodes();

        assertEquals("Should have added new node",newApps, numExistingApps+1);
        
        status = single.checkNode(id, contextName);
        assertEquals("Should be a current (saved) node",NodeManager.NODE_SAVED,status);
        
    	id = new Long(20);
        status = single.checkNode(id, contextName);
        assertEquals("Should be an outdated node",NodeManager.NODE_OUTDATED,status);

        single.addNode(id, contextName);
        status = single.checkNode(id, contextName);
        assertEquals("Should be again a current (saved) node",NodeManager.NODE_SAVED,status);

    	id = new Long(10);
        status = single.checkNode(id, contextName);
        assertEquals("Should still be a current (saved) node",NodeManager.NODE_SAVED,status);

    	single.removeNode(contextName); //previous run didn't clean up
        status = single.checkNode(id, contextName);
        assertEquals("Node should be gone....",NodeManager.NODE_NEW,status);
    }
    protected String[] getConfigurations()
    {
        return new String[]
        { "cluster-node.xml"};
    }

}