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
import java.util.Set;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalCredentialImpl;
import org.apache.jetspeed.security.spi.impl.DefaultPasswordCredentialImpl;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 */
public class TestDefaultInternalPasswordCredentialInterceptor extends AbstractSecurityTestcase
{
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
        return new TestSuite(TestDefaultInternalPasswordCredentialInterceptor.class);
    }

    public void testEncodedPassword() throws Exception
    {
        Set privateCredentials = ums.getUser("testcred").getSubject().getPrivateCredentials();
        assertNotNull(privateCredentials);
        assertEquals(1, privateCredentials.size());
        PasswordCredential[] pwdCreds = (PasswordCredential[]) privateCredentials.toArray(new PasswordCredential[0]);
        assertEquals("testcred", pwdCreds[0].getUserName());
        assertNotSame("Password should be not same (encoded)", "password", new String(pwdCreds[0].getPassword()));
    }

    protected void initUser() throws Exception
    {
        // create user without password
        ums.addUser("testcred", null);
        // add a non-encoded password credential directly 
        InternalUserPrincipal internalUser = securityAccess.getInternalUserPrincipal("testcred");
        ArrayList credentials = new ArrayList();
        InternalCredentialImpl credential = 
            new InternalCredentialImpl(internalUser.getPrincipalId(),
                    "password", 0, DefaultPasswordCredentialImpl.class.getName());
        credentials.add(credential);
        internalUser.setCredentials(credentials);
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
        confList.add("META-INF/defipci.xml");
        return (String[])confList.toArray(new String[1]);
    }    
}
