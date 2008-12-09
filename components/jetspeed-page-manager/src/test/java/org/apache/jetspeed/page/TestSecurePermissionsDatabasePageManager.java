/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.page;

import java.security.Policy;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSecurePermissionsDatabasePersistence
 * 
 * @author <a href="rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 *          
 */
public class TestSecurePermissionsDatabasePageManager extends TestSecureDatabasePageManager
{
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestSecurePermissionsDatabasePageManager.class.getName() });
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();

        // configure custom policy for test
        Policy.setPolicy(new PageManagerPermissionsPolicy(Policy.getPolicy()));
        Policy.getPolicy().refresh();
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSecurePermissionsDatabasePageManager.class);
    }
    
    protected String[] getConfigurations()
    {
        return new String[]{"secure-permissions-database-page-manager.xml", "transaction.xml"};
    }
}
