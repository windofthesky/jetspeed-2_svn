/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
    }

    public void testRemoteJMX()
    {

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
