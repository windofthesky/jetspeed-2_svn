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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalCredentialImpl;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;

/**
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 */
public class TestInternalPasswordCredentialStateHandlingInterceptor extends AbstractSecurityTestcase
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
        return new TestSuite(TestInternalPasswordCredentialStateHandlingInterceptor.class);
    }

    public void testExpired() throws Exception
    {
        assertTrue("should be allowed to authenticate",ums.authenticate("testcred","password"));
        credential.setExpirationDate(new Date(System.currentTimeMillis()));
        updateCredential();
        assertFalse("should be expired",ums.authenticate("testcred","password"));
        ums.setPassword("testcred","password","password2");
        assertTrue("should be allowed to authenticate",ums.authenticate("testcred","password2"));
        assertFalse("should not be allowed to authenticate (wrong password1)",ums.authenticate("testcred","password"));
        assertFalse("should not be allowed to authenticate (wrong password2)",ums.authenticate("testcred","password"));
        assertFalse("should not be allowed to authenticate (wrong password3)",ums.authenticate("testcred","password"));
        assertFalse("should not be allowed to authenticate (disabled)",ums.authenticate("testcred","password2"));
        ums.setPassword("testcred",null,"password3");
        assertFalse("should still not be allowed to authenticate (disabled)",ums.authenticate("testcred","password3"));
        ums.setPasswordEnabled("testcred", true);
        assertTrue("should be allowed to authenticate again",ums.authenticate("testcred","password3"));
    }

    protected void initUser() throws Exception
    {
        ums.addUser("testcred", "password");
        loadUser();
    }
    
    protected void loadUser() throws Exception
    {
        internalUser = securityAccess.getInternalUserPrincipal("testcred");
        credential = (InternalCredentialImpl)internalUser.getCredentials().iterator().next();
    }
    
    protected void updateCredential() throws Exception
    {
        securityAccess.setInternalUserPrincipal(internalUser,false);
    }

    protected void destroyUser() throws Exception
    {
        ums.removeUser("testcred");
    }
    
    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List confList = new ArrayList(Arrays.asList(confs));
        confList.add("META-INF/sipcshi.xml");
        return (String[])confList.toArray(new String[1]);
    }    
}
