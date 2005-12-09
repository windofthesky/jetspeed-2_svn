/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.security.spi.ldap;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Test the {@link LdapUserSecurityDao}.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestLdapUserSecurityDao extends AbstractLdapTest
{

    /**
     * @see org.apache.jetspeed.security.spi.ldap.AbstractLdapTest#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        LdapDataHelper.seedUserData(uid1, password);
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.ldap.AbstractLdapTest#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        LdapDataHelper.removeUserData(uid1);
    }

    /**
     * <p>
     * Test <code>lookupByUid</code> with a good uid.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public void testLookupByGoodUID() throws SecurityException
    {
        assertFalse("The loookup failed for user.", StringUtils.isEmpty(ldapPrincipalDao.lookupByUid(uid1)));
    }

    /**
     * <p>
     * Test <code>lookupByUid</code> with a bad uid.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public void testLookupByBadUID() throws SecurityException
    {
        assertTrue("The lookup should have failed for user:" + uid1 + "123", StringUtils.isEmpty(ldapPrincipalDao.lookupByUid(uid1
                + "123")));
    }
}