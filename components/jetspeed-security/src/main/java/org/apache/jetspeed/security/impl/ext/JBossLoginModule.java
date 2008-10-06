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
package org.apache.jetspeed.security.impl.ext;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.AuthenticatedUser;
import org.apache.jetspeed.security.AuthenticationProvider;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserSubjectPrincipal;
import org.apache.jetspeed.security.impl.DefaultLoginModule;
import org.apache.jetspeed.security.impl.RoleImpl;

/**
 * <p>Configures Subject principals for JBoss JAAS implementation
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 */
public class JBossLoginModule extends DefaultLoginModule
{
    private static class JBossGroup implements Group
    {
        private String name;
        private ArrayList<Principal> members = new ArrayList<Principal>();
        
        public JBossGroup(String name)
        {
            this.name = name;
        }

        public boolean addMember(Principal user)
        {
            if ( !isMember(user) )
            {
                members.add(user);
                return true;
            }
            return false;
        }

        public boolean isMember(Principal member)
        {
            return members.contains(member);
        }

        public boolean removeMember(Principal user)
        {
            return members.remove(user);
        }

        public Enumeration<Principal> members()
        {
            return Collections.enumeration(members);
        }

        public String getName()
        {
            return name;
        }        
    }
    
    /**
     * Create a new JBoss login module
     */
    public JBossLoginModule () {
        super ();
    }

    /**
     * Create a new JBoss login module that uses the given user manager.
     * @param userManager
     * @see DefaultLoginModule#DefaultLoginModule(UserManager)
     */
    protected JBossLoginModule (AuthenticationProvider authProvider, UserManager userManager)
    {
        super (authProvider, userManager);
    }
        
    public JBossLoginModule(AuthenticationProvider authProvider, UserManager userManager, String portalUserRole)
    {
        super(authProvider, userManager, portalUserRole);
    }

    protected void commitSubject(Subject containerSubject, Subject jetspeedSubject, AuthenticatedUser user)
    {
        // add user specific portal user name and roles
        Principal userSubjectPrincipal = SubjectHelper.getPrincipal(jetspeedSubject, UserSubjectPrincipal.class);
        subject.getPrincipals().add(userSubjectPrincipal);
        boolean hasPortalUserRole = false;
        JBossGroup roles = new JBossGroup("Roles");
        
        for (Principal role : SubjectHelper.getPrincipals(jetspeedSubject, Role.class))
        {
            roles.addMember(role);
            if (role.getName().equals(portalUserRole))
            {
                hasPortalUserRole = true;
            }
        }
        if (!hasPortalUserRole)
        {
            // add portal user role: used in web.xml authorization to
            // detect authenticated portal users
            roles.addMember(new RoleImpl(portalUserRole));        
        }
        subject.getPrincipals().add(roles);
    }
}
