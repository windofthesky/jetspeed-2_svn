/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.security;

import org.apache.jetspeed.portlets.security.rolemgt.RoleTreeItem;
import org.apache.jetspeed.portlets.security.rolemgt.RoleTreeTable;
import org.apache.myfaces.custom.tree.DefaultMutableTreeNode;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>
 * Test case for the {@link SecurityApplicationUtils}.
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat</a>
 */
public class TestSecurityApplicationUtils extends TestCase
{
        
    /**
     * @param name The test name.
     */
    public TestSecurityApplicationUtils(String name)
    {
        super(name);
    }
    
    /**
     * <p>
     * Start the tests.
     * </p>
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[]) 
    {
        junit.awtui.TestRunner.main( new String[] { TestSecurityApplicationUtils.class.getName() } );
    }
 
    /**
     * <p>
     * Creates the test suite.
     * </p>
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() 
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSecurityApplicationUtils.class);
    }
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
    }
    
    /**
     * <p>
     * Test getNodePathDepth.
     * </p>
     */
    public void testGetNodePathDepth()
    {
        String nodePath = "/role/manager/sales";
        int depth = SecurityApplicationUtils.getNodePathDepth(nodePath);
        assertEquals(3, depth);
    }
    
    /**
     * <p>
     * Test getNodePathAtDepth(String nodePath, int depth).
     * </p>
     */
    public void testGetNodePathAtDepth()
    {
        String nodePath = "/role/manager/sales";
        String depthNodePath = SecurityApplicationUtils.getNodePathAtDepth(nodePath, 0);
        assertEquals("/role", depthNodePath);
        
        depthNodePath = SecurityApplicationUtils.getNodePathAtDepth(nodePath, 1);
        assertEquals("/role/manager", depthNodePath);
        
        depthNodePath = SecurityApplicationUtils.getNodePathAtDepth(nodePath, 2);
        assertEquals("/role/manager/sales", depthNodePath);
    }
    
    public void testFindTreeNode()
    {
        DefaultMutableTreeNode treeNode = SecurityApplicationUtils.findTreeNode(new RoleTreeTable(), "/role/XY/A/a1");
        assertNotNull(treeNode);
        assertEquals("/role/XY/A/a1", ((RoleTreeItem) treeNode.getUserObject()).getFullPath());
    }
}
