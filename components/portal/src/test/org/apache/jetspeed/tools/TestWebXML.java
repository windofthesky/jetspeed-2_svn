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
package org.apache.jetspeed.tools;

import org.apache.jetspeed.test.JetspeedTestSuite;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * Tests of rewriting the Web.xml for PAM deployment
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @since 2.0
 * @version $Id: TestWebXML.java 186206 2004-03-25 21:42:32Z jford $
 */
public class TestWebXML extends TestCase
{
    /**
     * Creates a new instance.
     */
    public TestWebXML(String testName)
    {
        super(testName);
    }

    /**
     * Return the Test
     */
    public static Test suite()
    {
        return new JetspeedTestSuite(TestWebXML.class);
    }

    /**
     * Setup the test.
     */
    public void setUp()
    {
    }

    private static final String TEST_WEB_XML = "./test/testdata/deploy/web.xml";

    public void testReadWrite() throws Exception
    {
        // DeployUtilities util = new DeployUtilities();
        // util.processWebXML(TEST_WEB_XML, "PATest");
    }

}
