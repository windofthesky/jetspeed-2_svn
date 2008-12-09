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
package org.apache.jetspeed.components.portletregistry.direct;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.portletregistry.AbstractRegistryTest;
import org.apache.jetspeed.om.portlet.PortletApplication;

/**
 * 
 * TestRegistry runs a suite tests creating, updating, retreiving and deleting
 * portlet information from the registry.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class TestRegistryDirectPart2a extends AbstractRegistryTest
{
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        Iterator itr = registry.getPortletApplications().iterator();
        while(itr.hasNext())
        {        
            registry.removeApplication((PortletApplication)itr.next());
        }
        
        
        
        super.tearDown();
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestRegistryDirectPart2a.class);
    }

    public void testData() throws Exception
    {
        verifyData(true);
    }
}
