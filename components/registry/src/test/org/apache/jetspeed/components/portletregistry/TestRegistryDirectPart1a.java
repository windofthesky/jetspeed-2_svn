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
package org.apache.jetspeed.components.portletregistry;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.cache.PortletCache;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.factory.JetspeedPortletFactory;
import org.apache.jetspeed.factory.JetspeedPortletFactoryProxy;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.components.persistence.store.Transaction;

/**
 * 
 * TestRegistry runs a suite updating PAs
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 *  
 */
public class TestRegistryDirectPart1a extends AbstractRegistryTest
{
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();                
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
       //  super.tearDown();
    }

    /**
     * @param testName
     */
    public TestRegistryDirectPart1a(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestRegistryDirectPart1a.class);
    }
    
    public void testUpdates() throws Exception
    {
        Transaction tx = persistenceStore.getTransaction();
        tx.begin();
        Filter filter = persistenceStore.newFilter();
        PortletApplicationDefinitionImpl app = (PortletApplicationDefinitionImpl) registry.getPortletApplication("App_1");
        assertNotNull("PA App_1 is NULL", app);

        app.addUserAttribute("user.pets.doggie", "Busby");
        
        registry.getPersistenceStore().lockForWrite(app);
        
        tx.commit();
                        
        System.out.println("PA update test complete");
    }
    
}
