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
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;

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
    protected PageManager pageManager;
    
    public PortletActionSecurityPathMergeBehavior(PageManager pageManager)
    {
        super(pageManager);
    }

    public Subject getSubject(RequestContext context)
    {
        Subject currentSubject = context.getSubject();
        Iterator roles = currentSubject.getPrincipals(RolePrincipalImpl.class).iterator();
        StringBuffer combo = new StringBuffer();
        int count = 0;
        while (roles.hasNext())
        {
            RolePrincipal role = (RolePrincipal)roles.next();
            if (count > 0)
            {
                combo.append("-");
            }
            combo.append(role.getName());
            count++;                        
        }
        Set principals = new HashSet();
        principals.add(SecurityHelper.getBestPrincipal(currentSubject, UserPrincipal.class));
        principals.add(new RolePrincipalImpl(combo.toString()));
        Subject subject = 
            new Subject(true, principals, new HashSet(), new HashSet());
        return subject;
    }
    
}
