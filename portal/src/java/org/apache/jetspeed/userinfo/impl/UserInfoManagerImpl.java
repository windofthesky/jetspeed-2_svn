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
package org.apache.jetspeed.userinfo.impl;

import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import javax.portlet.PortletRequest;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistryHelper;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.userinfo.UserInfoManager;


/**
 * <p>Implements the {@link org.apache.jetspeed.userinfo.UserInfoManager} interface.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class UserInfoManagerImpl implements UserInfoManager
{

    /** Logger */
    private static final Log log = LogFactory.getLog(UserInfoManagerImpl.class);

    // TODO Same caching issue as usual.  We should look into JCS. That wil do for now.
    /** Map used to cache user info maps for each mapped portlet application. */
    private static Map userInfoMapCache;

    /** <p>The default user attributes property set.</p> */
    static String USER_INFO_PROPERTY_SET = "userinfo";

    /** The user information property set. */
    String userInfoPropertySet;
    /** The user manager */
    UserManager userMgr;
    /** The portlet registry. */
    PortletRegistryComponent registry;
    /** The portlet application being processed. */
    String paName;

    /**
     * <p>Constructor providing access to the {@link UserManager}.</p>
     * @param userMgr The user manager.
     * @param registry The portlet registry component.
     */
    public UserInfoManagerImpl(UserManager userMgr, PortletRegistryComponent registry)
    {
        this.userMgr = userMgr;
        this.registry = registry;
        this.userInfoPropertySet = USER_INFO_PROPERTY_SET;
        initUserInfoMapCache();
    }

    /**
     * <p>Constructor providing access to the {@link UserManager} and specifying which 
     * property set to use for user information.</p>
     * @param userMgr The user manager.
     * @param registry The portlet registry component.
     * @param userInfoPropertySet The user information property set.
     *  
     */
    public UserInfoManagerImpl(UserManager userMgr, PortletRegistryComponent registry, String userInfoPropertySet)
    {
        this.userMgr = userMgr;
        this.registry = registry;
        this.userInfoPropertySet = userInfoPropertySet;
        initUserInfoMapCache();
    }

    /**
     * @see org.apache.jetspeed.userinfo.UserInfoManager#setUserInfoMap(org.apache.jetspeed.om.page.Fragment, org.apache.jetspeed.request.RequestContext)
     */
    public RequestContext setUserInfoMap(MutablePortletApplication pa, RequestContext context)
    {
        // Check if user info map is in cache.
        if (userInfoMapCache.containsKey(paName))
        {
            context.setAttribute(PortletRequest.USER_INFO, userInfoMapCache.get(paName));
            return context;
        }
        // Not in cache, map user info.
        Preferences userPrefs = getUserPreferences(context);
        if (null == userPrefs)
        {
            context.setAttribute(PortletRequest.USER_INFO, null);
            log.debug(PortletRequest.USER_INFO + " is set to null");
            return context;
        }
        if (null == pa)
        {
            context.setAttribute(PortletRequest.USER_INFO, null);
            log.debug(PortletRequest.USER_INFO + " is set to null");
            return context;
        }
        Preferences userInfoPrefs = userPrefs.node(userInfoPropertySet);
        Collection portletUserAttributes = pa.getUserAttributes();
        Map userInfoMap = mapUserInfo(userInfoPrefs, portletUserAttributes);
        if (null == userInfoMap)
        {
            context.setAttribute(PortletRequest.USER_INFO, null);
            log.debug(PortletRequest.USER_INFO + " is set to null");
            return context;
        }
        context.setAttribute(PortletRequest.USER_INFO, userInfoMapCache.get(paName));
        return context;
    }

    /**
     * <p>Maps the user info properties retrieved from the user preferences
     * to the user info attribute declared in the portlet.xml descriptor.</p>
     * @param userInfoPrefs The user info preferences.
     * @param portletUserAttributes The declarative portlet user attributes.
     * @return The user info map.
     */
    private Map mapUserInfo(Preferences userInfoPrefs, Collection portletUserAttributes)
    {
        if ((null == portletUserAttributes) || (portletUserAttributes.size() == 0))
        {
            return null;
        }

        Map userInfoMap = new HashMap();
        String[] propertyKeys = null;
        try
        {
            propertyKeys = userInfoPrefs.keys();
            if ((null != propertyKeys) && log.isDebugEnabled())
                log.debug("Found " + propertyKeys.length + " children for " + userInfoPrefs.absolutePath());
        }
        catch (BackingStoreException bse)
        {
            log.error("BackingStoreException: " + bse.toString());
        }
        if (null == propertyKeys)
        {
            return null;
        }

        Iterator iter = portletUserAttributes.iterator();
        while (iter.hasNext())
        {
            UserAttribute currentAttribute = (UserAttribute) iter.next();
            if (null != currentAttribute)
            {
                for (int i = 0; i < propertyKeys.length; i++)
                {
                    if ((currentAttribute.getName()).equals(propertyKeys[i]))
                    {
                        userInfoMap.put(propertyKeys[i], userInfoPrefs.get(propertyKeys[i], null));
                    }
                }
            }
        }

        userInfoMapCache.put(paName, userInfoMap);

        return userInfoMap;
    }

    /**
     * <p>Gets the user preferences from the user's request.</p>
     * <p>If no user is logged in, return null.</p>
     * @param context The request context.
     * @return The user preferences.
     */
    private Preferences getUserPreferences(RequestContext context)
    {
        Preferences userPrefs = null;
        Subject subject = context.getSubject();
        if (null != subject)
        {
            Principal userPrincipal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
            if (null != userPrincipal)
            {
                log.debug("Got user principal: " + userPrincipal.getName());
                try
                {
                    if (userMgr.userExists(userPrincipal.getName()))
                    {
                        User user = userMgr.getUser(userPrincipal.getName());
                        userPrefs = user.getPreferences();
                    }
                }
                catch (SecurityException sex)
                {
                    log.warn("Unexpected SecurityException in UserInfoManager", sex);
                }
            }
        }
        return userPrefs;
    }

    /**
     * <p>Gets the portlet application from the provided portlet fragment.</p>
     * @param portletFragment The portlet fragment.
     * @return The portlet application.
     */
    private MutablePortletApplication getPortletApplication(Fragment portletFragment)
    {
        paName = PortletRegistryHelper.parseAppName(portletFragment.getName());
        return registry.getPortletApplication(paName);
    }

    private void initUserInfoMapCache()
    {
        if (null == userInfoMapCache)
        {
            userInfoMapCache = new HashMap();
        }
    }

}
