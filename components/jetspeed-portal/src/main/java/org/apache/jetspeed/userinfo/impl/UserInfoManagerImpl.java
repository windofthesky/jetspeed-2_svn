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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.UserAttribute;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.userinfo.UserInfoManager;

/**
 * <p>
 * Implements the {@link org.apache.jetspeed.userinfo.UserInfoManager}
 * interface.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id$
 */
public class UserInfoManagerImpl extends AbstractUserInfoManagerImpl implements UserInfoManager
{

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(UserInfoManagerImpl.class);

    // TODO: needs cache invalidation when portlet application user info configuration changes
    /** Map to cache user info keys for each mapped portlet application. */
    private static Map<String, List<UserAttributeRef>> appUserInfoAttrCache = Collections.synchronizedMap(new HashMap<String,List<UserAttributeRef>>());

    /** The user manager */
    protected UserManager userMgr;

    /** The portlet registry. */
    protected PortletRegistry registry;
    
    protected UserInfoManagerImpl()
    {
    }

    /**
     * <p>
     * Constructor providing access to the {@link UserManager}.
     * </p>
     * 
     * @param userMgr The user manager.
     * @param registry The portlet registry component.
     */
    public UserInfoManagerImpl(UserManager userMgr, PortletRegistry registry)
    {
        this.userMgr = userMgr;
        this.registry = registry;
    }

    /**
     * <p>
     * Constructor providing access to the {@link UserManager}and specifying
     * which property set to use for user information.
     * </p>
     * 
     * @param userMgr The user manager.
     * @param registry The portlet registry component.
     * @param userInfoPropertySet The user information property set.
     *  
     */
    public UserInfoManagerImpl(UserManager userMgr, PortletRegistry registry, String userInfoPropertySet)
    {
        this.userMgr = userMgr;
        this.registry = registry;
    }

    public Map<String, String> getUserInfoMap(String appName, RequestContext context)
    {
        if (log.isDebugEnabled())
            log.debug("Getting user info for portlet application: " + appName);
        
        Map<String, String> userInfo = getUserInformation(context);
        if (null == userInfo || userInfo.isEmpty())
        {
            log.debug(PortletRequest.USER_INFO + " is null or empty");
            return null;
        }
        
        return mapUserInfo(userInfo, getLinkedUserAttr(appName));
    }
    
    protected List<UserAttributeRef> getLinkedUserAttr(String appName)
    {
        // Check if user info map is in cache.
        List<UserAttributeRef> linkedUserAttr = appUserInfoAttrCache.get(appName);
        
        if (linkedUserAttr == null)
        {
            PortletApplication pa = registry.getPortletApplication(appName, true);
            if (null == pa)
            {
                log.debug(PortletRequest.USER_INFO + " is set to null");
                return null;
            }
            List<UserAttribute> userAttributes = pa.getUserAttributes();
            List<UserAttributeRef> userAttributeRefs = pa.getUserAttributeRefs();
            linkedUserAttr = mapLinkedUserAttributes(userAttributes, userAttributeRefs);
            appUserInfoAttrCache.put(appName, linkedUserAttr);
        }
        return linkedUserAttr;
    }

    /**
     * <p>
     * Maps the user info properties retrieved from the user information to the
     * user info attribute declared in the portlet.xml descriptor.
     * </p>
     * 
     * @param userInfo The user info attributes.
     * @param userAttributes The declarative portlet user attributes.
     * @param userAttributeRefs The declarative jetspeed portlet extension user
     *            attributes reference.
     * @return The user info map.
     */
    protected Map<String, String> mapUserInfo(Map<String, String> userInfo, List<UserAttributeRef> linkedUserAttributes)
    {
        Map<String, String>userInfoMap = new HashMap<String, String>();
        for (UserAttributeRef currentAttributeRef : linkedUserAttributes)
        {
            String key = currentAttributeRef.getNameLink();
            String name = currentAttributeRef.getName();
            if (key == null)
            {                
                key = name;
            }
            if (userInfo.containsKey(key))
            {
                userInfoMap.put(name, userInfo.get(key));
            }
        }
        return userInfoMap;
    }

    /**
     * <p>
     * Gets the user info from the user's request.
     * </p>
     * <p>
     * If no user is logged in, return null.
     * </p>
     * 
     * @param context The request context.
     * @return The user info.
     */
    private Map<String, String> getUserInformation(RequestContext context)
    {
        Map<String, String> userInfo = null;
        Subject subject = context.getSubject();
        if (null != subject)
        {
            Principal userPrincipal = SubjectHelper.getPrincipal(subject, User.class);
            if (null != userPrincipal)
            {
                log.debug("Got user principal: " + userPrincipal.getName());
                if (userPrincipal instanceof JetspeedPrincipal)
                {
                    return ((JetspeedPrincipal)userPrincipal).getInfoMap();
                }
            }
        }
        return userInfo;
    }
}