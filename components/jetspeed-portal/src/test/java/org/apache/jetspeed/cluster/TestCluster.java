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
        super.setUp();

        single = scm.lookupComponent("org.apache.jetspeed.cluster.NodeManager");
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
    	Long revision = new Long(10);
    	
        assertNotNull("Manager should be instantiated", single);
        
        int numExistingApps = single.getNumberOfNodes();
        
        //create a new node
        int status = single.checkNode(revision, contextName);
        if (status != NodeManager.NODE_NEW)
        {
        	single.removeNode(contextName); //previous run didn't clean up
        	status = single.checkNode(revision, contextName);
            assertEquals("Should be a new node",NodeManager.NODE_NEW,status);
        }
        
        // ok - create a new node
        single.addNode(revision, contextName);
        int newApps = single.getNumberOfNodes();

        assertEquals("Should have added new node",newApps, numExistingApps+1);
        
        status = single.checkNode(revision, contextName);
        assertEquals("Should be a current (saved) node",NodeManager.NODE_SAVED,status);
        
    	revision = new Long(20);
        status = single.checkNode(revision, contextName);
        assertEquals("Should be an outdated node",NodeManager.NODE_OUTDATED,status);

        single.addNode(revision, contextName);
        status = single.checkNode(revision, contextName);
        assertEquals("Should be again a current (saved) node",NodeManager.NODE_SAVED,status);

    	revision = new Long(10);
        status = single.checkNode(revision, contextName);
        assertEquals("Should still be a current (saved) node",NodeManager.NODE_SAVED,status);

    	single.removeNode(contextName); //previous run didn't clean up
        status = single.checkNode(revision, contextName);
        assertEquals("Node should be gone....",NodeManager.NODE_NEW,status);
    }
    protected String[] getConfigurations()
    {
        return new String[]
        { "system-properties.xml", "cluster-node.xml"};
    }

}
