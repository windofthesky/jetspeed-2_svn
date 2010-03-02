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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.UserAttributeRef;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.userinfo.UserAttributeSource;

/**
 * Default implementation of a UserAttribute source Provides users attributes from standard prefs implementation
 * 
 * @author <a href="mailto:KeithGarry.Boyce@bcbsma.com">Keith Garry Boyce</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class UserManagerUserAttributeSourceImpl implements UserAttributeSource
{

    /** Logger */
    private static final Log log = LogFactory.getLog(UserManagerUserAttributeSourceImpl.class);

    private static final String USER_INFO_MAP_KEY = UserManagerUserAttributeSourceImpl.class.getName()+".user_info_map";
    
    /** The user manager */
    private UserManager userManager;
    /** The user information property set. */
    private String userInfoPropertySet;

    public UserManagerUserAttributeSourceImpl(UserManager userManager)
    {
        this(userManager, User.USER_INFO_PROPERTY_SET);
    }

    public UserManagerUserAttributeSourceImpl(UserManager userManager, String userInfoPropertySet)
    {
        this.userManager = userManager;
        this.userInfoPropertySet = userInfoPropertySet;
    }

    public Map getUserAttributeMap(Subject subject, Collection userAttributeRefs, RequestContext context)
    {
        Map userAttributeMap = new HashMap();
        Principal userPrincipal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
        if (null != userPrincipal)
        {
            log.debug("Got user principal: " + userPrincipal.getName());
            // first check session already contains userInfo map
            String userName = userPrincipal.getName();
            String userInfoKey = USER_INFO_MAP_KEY+"."+userName;
            Map userInfo = (Map)context.getSessionAttribute(userInfoKey);
            if (userInfo == null)
            {
                userInfo = Collections.EMPTY_MAP;
                try
                {
                    if (userManager.userExists(userName))
                    {
                        User user = userManager.getUser(userPrincipal.getName());
                        Preferences userPrefs = user.getPreferences();
                        if (null != userPrefs)
                        {
                            Preferences userInfoPrefs = userPrefs.node(userInfoPropertySet);
                            String[] propertyKeys = null;
                            try
                            {
                                propertyKeys = userInfoPrefs.keys();
                                if ((null != propertyKeys) && log.isDebugEnabled())
                                {
                                    log.debug("Found " + propertyKeys.length + " children for " + userInfoPrefs.absolutePath());
                                }
                            }
                            catch (BackingStoreException bse)
                            {
                                log.error("BackingStoreException: " + bse.toString());
                            }
                            if (null != propertyKeys && propertyKeys.length > 0)
                            {
                                userInfo = new HashMap();
                                for (int i = 0; i < propertyKeys.length; i++)
                                {
                                    userInfo.put(propertyKeys[i], userInfoPrefs.get(propertyKeys[i], null));
                                }
                            }
                        }
                    }
                }
                catch (SecurityException sex)
                {
                    log.warn("Unexpected SecurityException in UserInfoManager", sex);
                }                
                context.setSessionAttribute(userInfoKey, userInfo);
            }
            if (userAttributeRefs != null)
            {
                Iterator iter = userAttributeRefs.iterator();
                while (iter.hasNext())
                {
                    UserAttributeRef currentAttributeRef = (UserAttributeRef)iter.next();
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
