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
package org.apache.jetspeed.userinfo.impl;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.userinfo.UserAttributeSource;

/**
 * Default implementation of a UserAttribute source Provides users attributes from standard prefs implementation
 * 
 * @author <a href="mailto:KeithGarry.Boyce@bcbsma.com">Keith Garry Boyce</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class SubjectUserAttributeSourceImpl implements UserAttributeSource
{

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(SubjectUserAttributeSourceImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.jetspeed.userinfo.UserAttributeSource#getUserAttributeMap(javax.security.auth.Subject, java.util.Set)
     */
    public Map<String, String> getUserAttributeMap(Subject subject, Collection<UserAttributeRef> userAttributeRefs, RequestContext context)
    {

        Map<String,String> userAttributeMap = new HashMap<String,String>();
        Principal userPrincipal = SubjectHelper.getPrincipal(subject, User.class);
        if (null != userPrincipal)
        {
            log.debug("Got user principal: " + userPrincipal.getName());
            Map<String, String> userInfo = ((User)userPrincipal).getInfoMap();
            if (userAttributeRefs != null)
            {
                for (UserAttributeRef currentAttributeRef : userAttributeRefs)
                {
                    String key = currentAttributeRef.getNameLink();
                    String name = currentAttributeRef.getName();
                    if (key == null)
                    {                
                        key = name;
                    }
                    if (userInfo.containsKey(key))
                    {
                        userAttributeMap.put(name, userInfo.get(key));
                    }
                }
            }
        }
        return userAttributeMap;
    }
}
