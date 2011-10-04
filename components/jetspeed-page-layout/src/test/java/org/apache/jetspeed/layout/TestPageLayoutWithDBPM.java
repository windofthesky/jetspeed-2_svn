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
package org.apache.jetspeed.layout;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestPageLayoutWithDBPM
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class TestPageLayoutWithDBPM extends AbstractTestPageLayout
{
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] {TestPageLayoutWithDBPM.class.getName()});
    }

    /**
     * Define test suite.
     *
     * @return the test suite
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPageLayoutWithDBPM.class);
    }

    /**
     * Define configuration paths.
     *
     * @return array of paths.
     */
    protected String[] getConfigurations()
    {
        return new String[] {"/JETSPEED-INF/spring/test-spring-dbpm.xml", "cache-test.xml"};
    }
    
    protected String getBeanDefinitionFilterCategories()
    {
        return "default,xmlPageManager";
    }
}
