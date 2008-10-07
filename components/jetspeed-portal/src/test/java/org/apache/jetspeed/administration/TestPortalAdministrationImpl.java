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
package org.apache.jetspeed.administration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.aggregator.TestWorkerMonitor;


public class TestPortalAdministrationImpl extends  TestCase

{

    
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestWorkerMonitor.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        
        
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPortalAdministrationImpl.class);
    }

    public void testPasswordGen() throws Exception
    {
        PortalAdministrationImpl pai = new PortalAdministrationImpl(null,null,null,null,null,null,null);
        String newPassword = pai.generatePassword();
        assertNotNull("new password was NULL!!!",newPassword);
        assertTrue("password is not long enough",(newPassword.length() > 4) );
        
    }
    
    public void xtestSendEmail() throws Exception {
        PortalAdministrationImpl pai = new PortalAdministrationImpl(null,null,null,null,null,null,null);
        pai.sendEmail("chris@bluesunrise.com","this is a unittest","chris@bluesunrise.com","this is the content of the message");
        
    }
    
    // this needs too much init to test easily right now
    public void xtestRegUser() throws Exception
    {
        PortalAdministrationImpl pai = new PortalAdministrationImpl(null,null,null,null,null,null,null);
        String user = "user"+(Math.abs(new Date().getTime()));
        String password = "password";
        List emptyList = new ArrayList();
        Map emptyMap = new HashMap();
        Map userAttributes = new HashMap();
        String emailTemplate = "";
        pai.registerUser(user, 
                password, 
                emptyList, 
                emptyList, 
               userAttributes,              // note use of only PLT.D  values here.
               emptyMap, 
               emailTemplate);
        
    }
    

}
