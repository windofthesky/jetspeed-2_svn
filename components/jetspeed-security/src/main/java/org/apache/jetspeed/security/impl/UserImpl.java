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
package org.apache.jetspeed.security.impl;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.attributes.SecurityAttribute;
import org.apache.jetspeed.security.attributes.SecurityAttributes;

/**
 * <p>Represents a security 'user' made of a {@link org.apache.jetspeed.security.RolePrincipal} and security attributes.</p>
 * <p>Modified 2008-08-05 - DST - decoupled java preferences</p> 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class UserImpl implements User
{
    private Subject subject;
    private SecurityAttributes attributes;

    /**
     * <p>Default constructor.</p>
     */
    public UserImpl()
    {
    }
    
    /**
     * <p>{@link User} constructor given a subject and security attributes.</p>
     * @param subject The subject.
     * @param attributes The security attributes
     */
    public UserImpl(Subject subject, SecurityAttributes attributes)
    {
        this.subject = subject;
        this.attributes = attributes;
    }

    /**
     * @see org.apache.jetspeed.security.User#getSubject()
     */
    public Subject getSubject()
    {
        return subject;
    }

    /**
     * @see org.apache.jetspeed.security.User#setSubject(javax.security.auth.Subject)
     */
    public void setSubject(Subject subject)
    {
        this.subject = subject;
    }

    public SecurityAttributes getAttributes()
    {
        return this.attributes;
    }

    public void setAttributes(SecurityAttributes attributes)
    {
        this.attributes = attributes;        
    }

    public Map<String, String> getUserAttributes()
    {
        Map<String, String> userInfo = new HashMap<String, String>();
        for (String key : this.attributes.getAttributes().keySet())
        {
            SecurityAttribute attr = this.attributes.getAttributes().get(key);
            if (attr.getType().equals(SecurityAttributes.USER_INFORMATION))
                userInfo.put(attr.getName(), attr.getValue());
        }
        return userInfo;
    }

    public UserPrincipal getUserPrincipal()
    {
        return (UserPrincipal) SecurityHelper.getBestPrincipal(subject, UserPrincipal.class);
    }
}
