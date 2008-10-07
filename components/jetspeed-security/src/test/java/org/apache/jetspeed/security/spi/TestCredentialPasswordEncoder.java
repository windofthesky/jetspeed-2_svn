/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.jetspeed.security.AbstractSecurityTestcase;
import org.apache.jetspeed.security.PasswordCredential;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
* <p>
 * TestDefaultInternalPasswordCredentialIntercepto
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class TestCredentialPasswordEncoder extends AbstractSecurityTestcase
{
    protected void setUp() throws Exception
    {
        super.setUp(); 
        initUser();
    }

    public static Test suite()
    {
        return new TestSuite(TestCredentialPasswordEncoder.class);
    }

    public void testEncodedPassword() throws Exception
    {
        PasswordCredential pwc = ums.getPasswordCredential(ums.getUser("testcred"));
        assertNotNull(pwc);
        assertEquals("testcred", pwc.getUserName());
        assertNotSame("Password should be not same (encoded)", "password", new String(pwc.getPassword()));
    }

    protected void initUser() throws Exception
    {
        // create user without password
        addUser("testcred", "password");
    }
}
