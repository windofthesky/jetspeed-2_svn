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
package org.apache.jetspeed.security.impl.ext;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.impl.DefaultLoginModule;

/**
 * <p>Configures Subject principals for JBoss JAAS implementation
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 */
public class JBossLoginModule extends DefaultLoginModule
{
    private static class JBossGroup implements Group
    {
        private String name;
        private ArrayList members = new ArrayList();
        
        public JBossGroup(String name, List members)
        {
            this.name = name;
            this.members.addAll(members);
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

        public Enumeration members()
        {
            return Collections.enumeration(members);
        }

        public String getName()
        {
            return name;
        }        
    }
    
    protected void commitPrincipals(Subject subject, User user)
    {
        // add UserPrincipal to subject
        subject.getPrincipals().add(getUserPrincipal(user));
        subject.getPrincipals().add(new JBossGroup("Roles",getUserRoles(user)));
    }
}
