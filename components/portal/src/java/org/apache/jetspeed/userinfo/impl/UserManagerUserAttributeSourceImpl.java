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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import org.apache.jetspeed.userinfo.UserAttributeRetrievalException;
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

    /** The user manager */
    private UserManager userManager;

    /**
     * @param userManager
     *            The userManager to set.
     */
    public void setUserManager(UserManager userManager)
    {
        this.userManager = userManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jetspeed.userinfo.UserAttributeSource#getUserAttributeMap(javax.security.auth.Subject, java.util.Set)
     */
    public Map getUserAttributeMap(Subject subject, Collection userAttributeRefs, RequestContext context)
            throws UserAttributeRetrievalException
    {

        Map userAttributeMap = new HashMap();
        Principal userPrincipal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
        if (null != userPrincipal)
        {
            log.debug("Got user principal: " + userPrincipal.getName());
            try
            {
                if (userManager.userExists(userPrincipal.getName()))
                {
                    User user = userManager.getUser(userPrincipal.getName());
                    Preferences userInfoPrefs = user.getPreferences();
                    for (Iterator iter = userAttributeRefs.iterator(); iter.hasNext();)
                    {
                        UserAttributeRef currentAttributeRef = (UserAttributeRef) iter.next();
                        Object value = userInfoPrefs.get(currentAttributeRef.getName(), null);
                        if (value != null)
                        {
                            userAttributeMap.put(currentAttributeRef.getName(), value);
                        }

                    }
                }
            }
            catch (SecurityException sex)
            {
                log.warn("Unexpected SecurityException in UserInfoManager", sex);
            }
        }

        return userAttributeMap;
    }

}
