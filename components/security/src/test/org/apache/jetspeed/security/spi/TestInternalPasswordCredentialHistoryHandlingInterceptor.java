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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalCredentialImpl;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;

/**
* <p>
 * TestInternalPasswordCredentialHistoryHandlingInterceptor
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class TestInternalPasswordCredentialHistoryHandlingInterceptor extends AbstractSecurityTestcase
{
    private InternalUserPrincipal internalUser;
    private InternalCredentialImpl credential;
    
    protected void setUp() throws Exception
    {
        super.setUp(); 
        // cleanup for previously failed test
        destroyUser();
        initUser();
    }

    public void tearDown() throws Exception
    {
        destroyUser();
        super.tearDown();
    }

    public static Test suite()
    {
        return new TestSuite(TestInternalPasswordCredentialHistoryHandlingInterceptor.class);
    }

    public void testPasswordHistory() throws Exception
    {
        assertTrue("should be allowed to authenticate",ums.authenticate("testcred","password"));
        ums.setPassword("testcred","password","password1");
        ums.setPassword("testcred","password1","password2");
        assertTrue("should be allowed to authenticate",ums.authenticate("testcred","password2"));
        try
        {
            ums.setPassword("testcred","password2","password");
            fail("Should not be allowed to reuse a password from password history");
        }
        catch (SecurityException sex)
        {
            assertEquals(SecurityException.PASSWORD_ALREADY_USED, sex.getMessage());
        }
        ums.setPassword("testcred","password2","password3");
        ums.setPassword("testcred","password3","password4");
        ums.setPassword("testcred","password4","password");
        assertTrue("should be allowed to authenticate",ums.authenticate("testcred","password"));
    }

    protected void initUser() throws Exception
    {
        ums.addUser("testcred", "password");
    }
    
    protected void destroyUser() throws Exception
    {
        ums.removeUser("testcred");
    }
    
    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List confList = new ArrayList(Arrays.asList(confs));
        confList.add("META-INF/sipchhi.xml");
        return (String[])confList.toArray(new String[1]);
    }    
}
