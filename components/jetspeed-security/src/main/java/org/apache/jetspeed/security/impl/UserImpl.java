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

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.User;

/**
 * <p>Represents a security 'user' extending BaseJetspeedPrincipal.</p>
 * @version $Id$
 */
public class UserImpl extends BaseJetspeedPrincipal implements User
{
    private static final long serialVersionUID = 5484179899807809619L;

    private Subject subject;
    
    /**
     * <p>Default constructor.</p>
     */
    public UserImpl()
    {
    }

    public Subject getSubject()
    {
        return this.subject;
    }

    public void setSubject(Subject subject)
    {
        this.subject = subject;
    }

    public Map<String, String> getUserInfo()
    {
        Map<String, String> userInfo = new HashMap<String, String>();
        Map<String, SecurityAttribute> infoAttrMap = getSecurityAttributes().getInfoAttributeMap();
        
        for (Map.Entry entry : infoAttrMap.entrySet())
        {
            userInfo.put((String) entry.getKey(), ((SecurityAttribute) entry.getValue()).getStringValue());
        }
            
        return userInfo;
    }
    
}
