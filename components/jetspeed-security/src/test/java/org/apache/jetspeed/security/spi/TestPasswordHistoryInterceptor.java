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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.AbstractSecurityTestcase;

/**
* <p>
 * TestInternalPasswordCredentialHistoryHandlingInterceptor
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class TestPasswordHistoryInterceptor extends AbstractSecurityTestcase
{
    protected void setUp() throws Exception
    {
        super.setUp(); 
        // cleanup for previously failed test
        initUser();
    }

    public static Test suite()
    {
        return new TestSuite(TestPasswordHistoryInterceptor.class);
    }

    public void testPasswordHistory() throws Exception
    {
/*      TODO: fix test when PasswordHistoryInterceptor is reimplemented        
        // note that the automated test here must wait between
        // create user and set password operations to ensure that
        // passwords get unique timestamps
        assertTrue("should be allowed to authenticate",ums.authenticate("testcred","password"));
        Thread.sleep(1000);
        ums.setPassword("testcred","password","password1");
        Thread.sleep(1000);
        ums.setPassword("testcred","password1","password2");
        assertTrue("should be allowed to authenticate",ums.authenticate("testcred","password2"));
        try
        {
            Thread.sleep(1000);
            ums.setPassword("testcred","password2","password");
            fail("Should not be allowed to reuse a password from password history");
        }
        catch (SecurityException sex)
        {
            assertTrue(SecurityException.PASSWORD_ALREADY_USED.equals(sex.getKeyedMessage()));
        }
        Thread.sleep(1000);
        ums.setPassword("testcred","password2","password3");
        Thread.sleep(1000);
        ums.setPassword("testcred","password3","password4");
        Thread.sleep(1000);
        ums.setPassword("testcred","password4","password");
        
        assertTrue("should be allowed to authenticate",ums.authenticate("testcred","password"));
*/        
    }

    protected void initUser() throws Exception
    {
        addUser("testcred", "password");
    }
    
    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List confList = new ArrayList(Arrays.asList(confs));
        confList.add("JETSPEED-INF/spring/TestPasswordHistoryInterceptor.xml");
        return (String[])confList.toArray(new String[1]);
    }    
}
