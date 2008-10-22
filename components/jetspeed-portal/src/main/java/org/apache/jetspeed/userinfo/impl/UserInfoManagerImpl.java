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

import javax.portlet.PortletRequest;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.UserAttributeRef;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.userinfo.UserInfoManager;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.PortletWindow;

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
    private static final Log log = LogFactory.getLog(UserInfoManagerImpl.class);

    // TODO Same caching issue as usual. We should look into JCS. That wil do
    // for now.
    /** Map used to cache user info maps for each mapped portlet application. */
    private static Map<String, Map<String, String>> userInfoMapCache;

    /** The user manager */
    UserManager userMgr;

    /** The portlet registry. */
    PortletRegistry registry;

    /** The object id of the portlet application being processed. */
    String oid;

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
        initUserInfoMapCache();
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
        initUserInfoMapCache();
    }

    public Map<String, String> getUserInfoMap(String appName, RequestContext context)
    {
        if (log.isDebugEnabled())
            log.debug("Getting user info for portlet application: " + oid.toString());

        // Check if user info map is in cache.
        if (userInfoMapCache.containsKey(appName))
        {
            return userInfoMapCache.get(appName);
        }
        // Not in cache, map user info.
        Map<String, String> userInfo = getUserInformation(context);
        if (null == userInfo)
        {
            log.debug(PortletRequest.USER_INFO + " is set to null");
            return null;
        }

        PortletApplication pa = registry.getPortletApplication(appName);
        if (null == pa)
        {
            log.debug(PortletRequest.USER_INFO + " is set to null");
            return null;
        }
        Collection userAttributes = pa.getUserAttributes();
        Collection userAttributeRefs = pa.getUserAttributeRefs();
        return mapUserInfo(userInfo, userAttributes, userAttributeRefs);
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
    private Map<String, String> mapUserInfo(Map<String, String> userInfo, Collection userAttributes, Collection userAttributeRefs)
    {
        Map<String, String>userInfoMap = new HashMap<String, String>();
        if ((null == userAttributes) || (userAttributes.size() == 0))
        {
            return null;
        }
        Collection linkedUserAttributes = mapLinkedUserAttributes(userAttributes, userAttributeRefs);
        Iterator iter = linkedUserAttributes.iterator();
        while (iter.hasNext())
        {
            UserAttributeRef currentAttributeRef = (UserAttributeRef) iter.next();
            if (null != currentAttributeRef)
            {
                for (String key : userInfo.keySet())
                {
                    if (null != currentAttributeRef.getNameLink())
                    {
                        if ((currentAttributeRef.getNameLink()).equals(key))
                        {
                            userInfoMap.put(currentAttributeRef.getName(), userInfo.get(key));
                        }
                    }
                    else
                    {
                        if ((currentAttributeRef.getName()).equals(key))
                        {
                            userInfoMap.put(currentAttributeRef.getName(), userInfo.get(key));
                        }
                    }
                }
            }
        }
        userInfoMapCache.put(oid, userInfoMap);
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
        Map<String, String> userInfo = new HashMap<String, String>();
        Subject subject = context.getSubject();
        if (null != subject)
        {
            Principal userPrincipal = SubjectHelper.getPrincipal(subject, User.class);
            if (null != userPrincipal)
            {
                log.debug("Got user principal: " + userPrincipal.getName());
                try
                {
                    if (userMgr.userExists(userPrincipal.getName()))
                    {
                        User user = userMgr.getUser(userPrincipal.getName());
                        userInfo = user.getInfoMap();
                    }
                }
                catch (SecurityException sex)
                {
                    log.warn("Unexpected SecurityException in UserInfoManager", sex);
                }
            }
        }
        return userInfo;
    }

    private void initUserInfoMapCache()
    {
        if (null == userInfoMapCache)
        {
            userInfoMapCache = Collections.synchronizedMap(new HashMap());
        }
    }

    /**
     * For Pluto 2.0
     */
    public Map<String, String> getUserInfo(PortletRequest request, PortletWindow window) throws PortletContainerException
    {
        String remoteUser = request.getRemoteUser(); 
        if ( remoteUser != null ) 
        {
            return Collections.EMPTY_MAP;
        }
        RequestContext requestContext=(RequestContext)request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        return this.getUserInfoMap(window.getPortletEntity().getPortletDefinition().getApplication().getName(), requestContext);        
    }

}