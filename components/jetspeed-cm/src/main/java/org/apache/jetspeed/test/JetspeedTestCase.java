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
package org.apache.jetspeed.test;

import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @version $Id$
 *
 */
public class JetspeedTestCase extends TestCase
{
    public static TestSuite createFixturedTestSuite(Class testClass, String firstTest, String lastTest)
    {
        // All methods starting with "test" will automatically be executed in the test suite.
        TestSuite tmp = new TestSuite(testClass);
        Enumeration e = tmp.tests();
        TestSuite ts = new TestSuite();
        if (firstTest != null)
        {
            // add lirst test to be executed
            ts.addTest(ts.createTest(testClass, firstTest));
        }
        while (e.hasMoreElements())
        {
            ts.addTest((Test)e.nextElement());
        }
        if (lastTest != null)
        {
            // add lirst test to be executed
            ts.addTest(ts.createTest(testClass, lastTest));
        }
        return ts;
    }

    private String baseDir;
    
    public JetspeedTestCase()
    {
        super();
    }

    public JetspeedTestCase(String name)
    {
        super(name);
    }
    
    public String getBaseDir()
    {
        if (baseDir == null)
        {
            baseDir = System.getProperty("basedir");
            if (baseDir == null || baseDir.length() == 0)
            {
                baseDir = ".";
            }
            baseDir += "/";
        }
        return baseDir;
    }
}
