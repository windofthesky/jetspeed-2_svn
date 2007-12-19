/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.prefs;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.Property;

public class TestNodePreferences extends DatasourceEnabledSpringTestCase
{
    private PreferencesProvider provider;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();      
        provider = (PreferencesProvider) ctx.getBean("prefsProvider");        
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * @return The test suite.
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestNodePreferences.class);
    }
    

    /**
     * <p>
     * Test node and whether children exist under a given node.
     * </p>
     */
    String [] users = { "guest", "david", "admin" };
    int ENTITY_SIZE = 50;
    int PREF_SIZE = 20;
    boolean reset = false;
    boolean disableReads = false;
    
    public void testNodes()
    {
        if (1 == 1) return ; // disable this test, its a performance test
        assertNotNull("provider is null", provider);
        Node entityRoot = null;
        try
        {
            boolean hasBeenPopulated = false;
            Node root = provider.getNode("/", 0);
            assertNotNull("root node is null", root);
            if (!provider.nodeExists("/portlet_entity", 0))
            {
                entityRoot = provider.createNode(root, "portlet_entity", 0, "/portlet_entity");
                assertNotNull("entity-root node is null", entityRoot);                
            }
            else
            {
                if (reset)
                {
                    Node pe = provider.getNode("/portlet_entity", 0);
                    provider.removeNode(root, pe);
                    entityRoot = provider.createNode(root, "portlet_entity", 0, "/portlet_entity");
                    assertNotNull("entity-root node is null", entityRoot);
                }
                else
                    hasBeenPopulated = true;                
            }
            if (entityRoot == null)
                entityRoot = provider.getNode("/portlet_entity", 0);
            if (hasBeenPopulated == false)
            {
                for (int ix = 0; ix < ENTITY_SIZE; ix++)
                {
                    String path = "/portlet_entity/" + ix;
                    Node entity = provider.createNode(entityRoot, new Integer(ix).toString(), 0, path);
                    assertNotNull(path, entity);
                    for (int iy = 0; iy < users.length; iy++)
                    {
                        String uPath = "/portlet_entity/" + ix + "/" + users[iy];
                        Node uEntity = provider.createNode(entity, users[iy], 0, uPath);
                        assertNotNull(uPath, uEntity);
                        String pPath = uPath + "/preferences";
                        Node pEntity = provider.createNode(uEntity, "preferences", 0, pPath);
                        assertNotNull(pPath, pEntity);
                        for (int iz = 0; iz < PREF_SIZE; iz++)
                        {
                            String zPath = pPath + "/pref-" + iz;
                            Node zEntity = provider.createNode(pEntity, "pref-" + iz, 0, zPath);
                            assertNotNull(zPath, zEntity);
                            // size node
                            Node size = provider.createNode(zEntity, "size", 0, zPath + "/size" );
                            assertNotNull(zPath + "/size", size);                        
                            // values node
                            Node values = provider.createNode(zEntity, "values", 0, zPath + "/values" );
                            assertNotNull(values + "/values", values);                        
                            // size property
                            Property sizeProp = provider.createProperty(size, "size", "1");
                            size.getNodeProperties().add(sizeProp);
                            provider.storeNode(size);
                            // values property
                            Property valueProp = provider.createProperty(values, "0", new Integer(iz + 1000).toString());
                            values.getNodeProperties().add(valueProp);
                            provider.storeNode(values);
                        }
                    }                
                }
            }
            // Test for data using both new and old paths
            if (disableReads == false)
            {
                long start = System.currentTimeMillis();
                for (int ix = 0; ix < ENTITY_SIZE; ix++)
                {
                    for (int iy = 0; iy < users.length; iy++)
                    {
                        for (int iz = 0; iz < PREF_SIZE; iz++)
                        {
                            Node n;
                            String key = "/portlet_entity/" + ix + "/" + users[iy] + "/preferences/pref-" + iz;                                                
                            n = provider.getNode(key, 0);
                            assertNotNull("null pref: " + key, n);
                            Collection c = provider.getChildren(n);
                            assertNotNull("null collection ", c);
                            Iterator it = c.iterator();
                            while (it.hasNext())
                            {
                                Node child = (Node)it.next();
                                if (child.getNodeName().equals("size"))
                                {
                                    Object props[] = child.getNodeProperties().toArray();
                                    assertTrue("props isa ", (props[0] instanceof Property));
                                    Property p = (Property)props[0];
                                    String size = p.getPropertyValue();
                                    assertTrue("child size name ", "size".equals(p.getPropertyName()));                                
                                    assertTrue("child size value ", "1".equals(size));
                                }
                                else if (child.getNodeName().equals("values"))
                                {
                                    Object props[] = child.getNodeProperties().toArray();
                                    assertTrue("props isa ", (props[0] instanceof Property));
                                    Property p = (Property)props[0];
                                    String value = p.getPropertyValue();
                                    assertTrue("child value name ", "0".equals(p.getPropertyName()));                                
                                    assertTrue("child value value ", new Integer(iz + 1000).toString().equals(value));
                                }
                                
                            }
                        }
                    }
                }
                long end = System.currentTimeMillis();
                System.out.println("Retrieval time total: " +  (end - start));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue("exception in testNodes", false);
        }
    }

    /**
     * @see org.apache.jetspeed.components.test.AbstractSpringTestCase#getConfigurations()
     */
    protected String[] getConfigurations()
    {
        return new String[]
        { "prefs.xml", "transaction.xml", "cache.xml" };
    }
}
