/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.sso;

import org.apache.jetspeed.sso.SSOProvider;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;

/**
 * <p>
 * Unit testing for {@link Preferences}.
 * </p>
 * 
 * @author <a href="rogerrut@apache.org">Roger Ruttimann </a>
 */
public class TestSSOComponent extends DatasourceEnabledSpringTestCase
{

    /** The property manager. */
    private static SSOProvider ssoBroker = null;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();

        try
        {
            ssoBroker = (SSOProvider) ctx.getBean("ssoProvider");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new Exception("Exception while setup SSO TEST");
        }
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        clean();
        // super.tearDown();
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSSOComponent.class);
    }

    /**
     * <p>
     * Test user root.
     * </p>
     */
    public void testSSO()
    {
        // TODO: Test cases
    }

    /**
     * <p>
     * Clean properties.
     * </p>
     */
    protected void clean() throws Exception
    {
        // Cleanup any credentails added during the test
        /*
         * try { } catch (SSOException ex) { System.out.println("SSOException" +
         * ex); }
         */
    }

    protected String[] getConfigurations()
    {
        return new String[]
        { "META-INF/sso-dao.xml", "META-INF/transaction.xml"};
    }
}