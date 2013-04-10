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
package org.apache.jetspeed.page;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.page.psml.CastorXmlPageManager;
import org.apache.jetspeed.test.JetspeedTestCase;

/**
 * TestSecureCastorXmlPageManager
 * 
 * @author <a href="rwatler@apache.org">Randy Watler</a>
 * @version $Id$
*/
public class TestSecureCastorXmlPageManager extends JetspeedTestCase implements PageManagerTestShared 
{
    protected CastorXmlPageManager pageManager;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        pageManager = Shared.makeCastorXMLPageManager(getBaseDir(), "secure-pages", false, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        Shared.shutdownCastorXMLPageManager(pageManager);
    }

    /**
     * Defines the testcase name for JUnit.
     * 
     * @param name
     *            the testcase's name.
     */
    public TestSecureCastorXmlPageManager( String name )
    {
        super(name);
    }

    /**
     * Start the tests.
     * 
     * @param args
     *            the arguments. Not used
     */
    public static void main( String args[] )
    {
        junit.awtui.TestRunner.main(new String[]{TestSecureCastorXmlPageManager.class.getName()});
    }

    /**
     * Creates the test suite.
     * 
     * @return a test suite (<code>TestSuite</code>) that includes all
     *         methods starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSecureCastorXmlPageManager.class);
    }

    public void testSecurePageManager() throws Exception
    {
        // utilize standard secure page manager test
        Shared.testSecurePageManager(this, pageManager);
    }

    public void testSecurityConstraintsRefExpressions() throws Exception
    {
        if (pageManager.getConstraintsEnabled())
        {
            // utilize standard secure page manager test
            Shared.testSecurityConstraintsRefExpressions(this, pageManager);
        }
    }
}
