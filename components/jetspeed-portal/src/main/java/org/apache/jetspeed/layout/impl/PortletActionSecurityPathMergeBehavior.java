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
package org.apache.jetspeed.layout.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.impl.TransientRole;

/**
 * Abstracted behavior of security checks when used with the
 * profiling rule "user-rolecombo". This behavior merges 
 * all roles into a single role combo.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortletActionSecurityPathMergeBehavior
    extends PortletActionSecurityPathBehavior
    implements PortletActionSecurityBehavior
{
    protected Log log = LogFactory.getLog(PortletActionSecurityPathMergeBehavior.class);
    
    public PortletActionSecurityPathMergeBehavior( PageManager pageManager )
    {
    	this( pageManager, Boolean.FALSE );
    }
    public PortletActionSecurityPathMergeBehavior( PageManager pageManager, Boolean enableCreateUserPagesFromRolesOnEdit )
    {
        super( pageManager, enableCreateUserPagesFromRolesOnEdit );
    }

    public Subject getSubject(RequestContext context)
    {
        Subject currentSubject = context.getSubject();
        Iterator roles = currentSubject.getPrincipals(Role.class).iterator();
        StringBuffer combo = new StringBuffer();
        int count = 0;
        while (roles.hasNext())
        {
            Role role = (Role)roles.next();
            if (count > 0)
            {
                combo.append("-");
            }
            combo.append(role.getName());
            count++;                        
        }
        Set principals = new HashSet();
        principals.add(SubjectHelper.getBestPrincipal(currentSubject, User.class));
        principals.add(new TransientRole(combo.toString()));
        Subject subject = 
            new Subject(true, principals, new HashSet(), new HashSet());
        return subject;
    }
    
}
