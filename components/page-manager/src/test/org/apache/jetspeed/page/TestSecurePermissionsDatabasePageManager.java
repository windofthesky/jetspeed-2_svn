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

import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.Principal;
import java.security.ProtectionDomain;

import org.apache.jetspeed.security.FolderPermission;
import org.apache.jetspeed.security.FragmentPermission;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.PagePermission;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.UserPrincipal;

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
    public static class PageManagerPermissionsPolicy extends Policy
    {
        private Policy defaultPolicy;

        public PageManagerPermissionsPolicy(Policy defaultPolicy)
        {
            this.defaultPolicy = defaultPolicy;
        }

        public boolean implies(ProtectionDomain domain, Permission permission)
        {
            // classify policy query for local test case; this implementation
            // is not optimized: multiple protection domains exist on the
            // call stack, so this method will be invoked 2-3 times for each
            // access check with the identical principals and permission
            Principal[] principals = domain.getPrincipals();
            if ((principals != null) && (principals.length > 0) &&
                ((permission instanceof FolderPermission) ||
                 (permission instanceof PagePermission) ||
                 (permission instanceof FragmentPermission)))
            {
                // check permission using principals if available
                Permissions permissions = new Permissions();
                for (int i = 0; (i < principals.length); i++)
                {
                    if (principals[i] instanceof UserPrincipal)
                    {
                        // get permissions for users
                        String user = principals[i].getName();
                        if (user.equals("admin"))
                        {
                            // owner permissions
                            permissions.add(new FolderPermission("/", "view, edit"));
                            permissions.add(new PagePermission("/default-page.psml", "view, edit"));
                        }
                        else if (user.equals("user"))
                        {
                            // owner permissions
                            permissions.add(new FragmentPermission("/default-page.psml/some-app::SomePortlet", "view, edit"));
                            
                            // granted permissions
                            permissions.add(new PagePermission("/user-page.psml", "view, edit"));
                            permissions.add(new FragmentPermission("/user-page.psml/*", "view"));
                        }
                        
                        // public view permissions
                        permissions.add(new FolderPermission("/", "view"));
                        permissions.add(new PagePermission("/default-page.psml", "view"));
                        permissions.add(new PagePermission("/page.security", "view"));
                        permissions.add(new FragmentPermission("security::*", "view"));
                    }
                    else if (principals[i] instanceof RolePrincipal)
                    {
                        // get permissions for roles
                        String role = principals[i].getName();
                        if (role.equals("admin"))
                        {
                            // global permissions
                            permissions.add(new FolderPermission("<<ALL FILES>>", "view, edit"));
                            permissions.add(new FragmentPermission("<<ALL FRAGMENTS>>", "view, edit"));
                        }
                        else if (role.equals("manager"))
                        {
                            // granted permissions
                            permissions.add(new PagePermission("/default-page.psml", "edit"));
                        }
                    }
                }
                
                // check permission
                if (permissions.implies(permission))
                {
                    return true;
                }
            }

            // check default permissions
            if (defaultPolicy != null)
            {
                return defaultPolicy.implies(domain, permission);
            }
            return false;
        }

        public PermissionCollection getPermissions(ProtectionDomain domain)
        {
            // return default permissions only since
            // domain and permsission not available
            if (defaultPolicy != null)
            {
                return defaultPolicy.getPermissions(domain);
            }
            return new Permissions();
        }

        public PermissionCollection getPermissions(CodeSource codesource)
        {
            // return default permissions only since
            // domain and permsission not available
            if (defaultPolicy != null)
            {
                return defaultPolicy.getPermissions(codesource);
            }
            return new Permissions();
        }

        public void refresh()
        {
            // propagate refresh
            if (defaultPolicy != null)
            {
                defaultPolicy.refresh();
            }
        }
    }

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
        return new String[]
        { "secure-permissions-database-page-manager.xml", "transaction.xml" };
    }
}
