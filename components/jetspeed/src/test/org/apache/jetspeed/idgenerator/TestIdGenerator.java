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
package org.apache.jetspeed.idgenerator;

// Java imports
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.ComponentAssemblyTestCase;

/**
 * TestIdGenerator
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @version $Id$
 */
public class TestIdGenerator extends ComponentAssemblyTestCase
{
    
    private static int ID_TEST_TRIES = 10000;
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestIdGenerator(String name) 
    {
        super( name );
    }
    
    public String getBaseProject()
    {
        return "components/jetspeed";
    }
    
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[]) 
    {
        junit.awtui.TestRunner.main( new String[] { TestIdGenerator.class.getName() } );
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
        return new TestSuite( TestIdGenerator.class );
    }
    
    /**
     * Simple test that verify the PEID are unique.  This test will generate
     * <CODE>ID_TEST_TRIES<CODE> PEIDs.  It will test for a NULL PEID.
     *
     * Granted, passing this test does <B>not</B> guarantee that a duplicate
     * PEID will not be generated.
     *
     * @throws Exception
     */
    public void testVerifyUniquePeid() throws Exception
    {
        IdGenerator generator = (IdGenerator)componentManager.getComponent("IdGenerator");
        assertNotNull("idgenerator is null", generator);            
 
        HashMap generatedIds = new HashMap( ID_TEST_TRIES + 1);
        String  newId;
        
        // Add a NULL  to verify a NULL is not being generated.
        generatedIds.put(null, null);
        
        for (int counter = 1; counter <= ID_TEST_TRIES; counter++)
        {
            newId = generator.getNextPeid();
            assertTrue( "PEID already generated. PEID = " + newId, !generatedIds.containsKey(newId));
            generatedIds.put(newId, null);
        }
    }

    /**
     * Simple test that verify the PEIDs are increasing. Although this is not a 
     * requirement of the IdGenerator, it is recommended
     *
     * @throws Exception
     */
    public void testVerifyIncreasingPeid() throws Exception
    {
        IdGenerator generator = (IdGenerator)componentManager.getComponent("IdGenerator");
        assertNotNull("idgenerator service is null", generator);            
        
        String  newId;
        String  lastId = null;
        
        for (int counter = 1; counter <= ID_TEST_TRIES; counter++)
        {
            newId = generator.getNextPeid();
            if (lastId == null)
            {
                lastId = newId;
                continue;
            }
            assertTrue( "PEID is not greater then last generated PEID. PEID = " + newId, (lastId.compareTo(newId)<0));
            lastId = newId;
        }
    }
}
