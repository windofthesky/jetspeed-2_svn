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
package org.apache.jetspeed.security.spi;

import java.security.Principal;

import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Unit testing for {@link GroupSecurityHandler}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class TestGroupSecurityHandler extends AbstractSecurityTestcase
{

   

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * <p>
     * Constructs the suite.
     * </p>
     * 
     * @return The {@Test}.
     */
    public static Test suite()
    {
        return new TestSuite(TestGroupSecurityHandler.class);
    }

    /**
     * <p>
     * Test <code>getGroupPrincipal</code>.
     * </p>
     */
    public void testGetGroupPrincipal() throws Exception
    {
        initGroup();
        Principal principal = gsh.getGroupPrincipal("testusertogroup1");
        assertNotNull(principal);
        assertEquals("testusertogroup1", principal.getName());
        destroyGroup();
    }
    
    /**
     * <p>
     * Initialize group test object.
     * </p>
     */
    protected void initGroup() throws Exception
    {
        gms.addGroup("testusertogroup1");
    }

    /**
     * <p>
     * Destroy group test object.
     * </p>
     */
    protected void destroyGroup() throws Exception
    {
        gms.removeGroup("testusertogroup1");
    }

}