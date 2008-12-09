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

import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.portletregistry.AbstractRegistryTest;
import org.apache.jetspeed.om.portlet.UserAttribute;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;

/**
 * 
 * TestRegistry runs a suite updating PAs
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 *  
 */
public class TestRegistryDirectPart1b extends AbstractRegistryTest
{
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestRegistryDirectPart1b.class);
    }
    
    public void testUpdates() throws Exception
    {
        String lang = Locale.getDefault().toString();
        
        PortletApplicationDefinitionImpl app = (PortletApplicationDefinitionImpl) registry.getPortletApplication("App_1");
        assertNotNull("PA App_1 is NULL", app);

        UserAttribute ua = app.addUserAttribute("user.pets.doggie");
        ua.addDescription(lang).setDescription("Busby");
        
        registry.updatePortletApplication(app);        
                                
        System.out.println("PA update test complete");
    }
    
}
