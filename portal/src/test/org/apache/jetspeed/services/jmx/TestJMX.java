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

package org.apache.jetspeed.services.jmx;



import junit.framework.Test;
import junit.framework.TestSuite;

import mx4j.connector.RemoteMBeanServer;
import mx4j.connector.rmi.jrmp.JRMPConnector;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.modeler.ManagedBean;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;
/**
 * TestRegistryCategories
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */

public class TestJMX extends JetspeedTest
{
    /**
     * @see org.apache.jetspeed.test.JetspeedTest#overrideProperties(org.apache.commons.configuration.Configuration)
     */
//    public void overrideProperties(Configuration properties)
//    {
//        super.overrideProperties(properties);
//    }

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestJMX(String name)
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
        junit.awtui.TestRunner.main(new String[] { TestJMX.class.getName()});
    }

    public void setup()
    {
        System.out.println("Setup: Testing categories of Registry");
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
        return new JetspeedTestSuite(TestJMX.class);
    }

    /**
     * Tests categories
     * @throws Exception
     */
    public void testJMX() throws Exception
    {
        
/* fucking maven!        
        try
        {
            String[] beans = JMX.getManagedBeans();
            for (int i = 0; i < beans.length; i++)
            {
                String bean = beans[i];
                System.out.println("Trying to find mbean " + bean);
                ManagedBean mbean = JMX.getManagedBean(bean);
                assertNotNull(mbean);
            }

            assertNotNull(JMX.getAttribute(JMX.resolveObjectName("Registry"), "portlets"));

        }
        catch (Exception e)
        {
            String errmsg = "Error in category test: " + e.toString();
            // e.printStackTrace();
            e.printStackTrace();
            assertNotNull(errmsg, null);
        }
*/        
    }

    public void testRemoteJMX()
    {
/*
        try
        {
            JMX.startJMX();
            JRMPConnector connector = new JRMPConnector();

            // Pass in the adaptor's JNDI name, no properties
            String jndiName = "jrmp";
            connector.connect(jndiName, null);

            // Use the connector directly to retrieve some information
            // about host name and IP address
            String remoteHostName = connector.getRemoteHostName();
            String remoteHostAddress = connector.getRemoteHostAddress();

            // Get the remote MBeanServer from the connector
            // And use it as if it is an MBeanServer
            RemoteMBeanServer server = connector.getRemoteMBeanServer();

            assertNotNull(server);

            assertNotNull(server.getAttribute(JMX.resolveObjectName("Registry"), "portlets"));

            //Context context = NamingManager.getInitialContext(null);
            //assertNotNull(context.lookup("MBeanSimpleNames"));

            //        server.
        }
        catch (Exception e)
        {
            //  e.printStackTrace();
            fail(e.getMessage());
        }
*/        
    }

    private String lastGroup = "";
    private String lastCategory = "";

    private void init()
    {
        lastGroup = "";
        lastCategory = "";
    }

    //    private void print(Iterator iterator, PortletEntry entry)
    //    {
    //        String group = ((CategoryIterator) iterator).getGroup();
    //        String category = ((CategoryIterator) iterator).getCategory();
    //        if (!lastGroup.equals(group))
    //        {
    //            System.out.println("Group: [" + group + "]");
    //            lastGroup = group;
    //        }
    //        if (!lastCategory.equals(category))
    //        {
    //            System.out.println("....Cat: [" + category + "]");
    //            lastCategory = category;
    //        }
    //        System.out.println("........" + entry.getName());
    //    }

    /**
     * Tests IdentityElement unmarshaling entryset base stuff
     * @throws Exception
     */

}
