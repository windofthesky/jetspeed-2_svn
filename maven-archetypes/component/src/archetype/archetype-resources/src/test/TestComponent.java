/*
 * Copyright 2006 The Apache Software Foundation.
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
package ${groupId};

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import ${groupId}.ComponentImpl;

/**
 * TestComponent
 *
 * @author <a href="mailto:"></a>
 * @version $Id:$
 */
public class TestComponent extends TestCase
{
    /**
     * Class specific log instance.
     */
    private final static Log log = LogFactory.getLog(TestComponent.class);

    /**
     * Main test runner entry point.
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]{TestComponent.class.getName()});
    }
    
    /**
     * Test suite configuration for test*() methods in this class.
     */
    public static Test suite()
    {
        return new TestSuite(TestComponent.class);
    }
    
    /**
     * Setup protocol method.
     */
    protected void setUp() throws Exception
    {
        super.setUp();        
    }

    /**
     * Cleanup protocol method.
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();        
    }

    /**
     * Unit test method.
     */
    public void testComponent() throws Exception
    {
        log.info("TestComponent start...");

        log.info("TestComponent testing: " + ComponentImpl.class.getName());

        log.info("TestComponent done.");
    }
}
