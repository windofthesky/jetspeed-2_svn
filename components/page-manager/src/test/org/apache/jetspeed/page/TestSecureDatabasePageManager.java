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
package org.apache.jetspeed.page;

import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
                                                                                                     
import javax.security.auth.Subject;

import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.PrincipalsSet;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSecureDatabasePageManager
 * 
 * @author <a href="rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 *          
 */
public class TestSecureDatabasePageManager extends DatasourceEnabledSpringTestCase implements PageManagerTestShared
{
    protected PageManager pageManager;

    protected String somePortletId;
    
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestSecureDatabasePageManager.class.getName() });
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();
        pageManager = (PageManager)ctx.getBean("pageManager");
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSecureDatabasePageManager.class);
    }
    
    protected String[] getConfigurations()
    {
        return new String[]
        { "secure-database-page-manager.xml", "transaction.xml" };
    }

    public void testSecurePageManager() throws Exception
    {
        // utilize standard secure page manager test
        Shared.testSecurePageManager(this, pageManager);
    }
}
