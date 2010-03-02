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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryEventListener;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.pluto.om.common.ObjectID;

/**
 * <p>
 * Implements the {@link org.apache.jetspeed.userinfo.UserInfoManager}
 * interface.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id$
 */
public class UserInfoManagerImpl extends AbstractUserInfoManagerImpl implements RegistryEventListener
{

    /** Logger */
    private static final Log log = LogFactory.getLog(UserInfoManagerImpl.class);
    
    /** Map to cache user info keys for each mapped portlet application. */
    private static Map appUserInfoAttrCache = Collections.synchronizedMap(new HashMap());

    private UserManagerUserAttributeSourceImpl userManagerUserAttributeSource;
    
    /** The user manager */
    protected UserManager userMgr;

    /** The portlet registry. */
    protected PortletRegistry registry;

    protected UserInfoManagerImpl(PortletRegistry registry)
    {
        this.registry = registry;
        registry.addRegistryListener(this);
    }
    /**
     * @param userMgr The user manager.
     * @param registry The portlet registry component.
     */
    public UserInfoManagerImpl(UserManager userMgr, PortletRegistry registry)
    {
        this(userMgr,registry, User.USER_INFO_PROPERTY_SET);
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
        this(registry);
        this.userMgr = userMgr;
        this.userManagerUserAttributeSource = new UserManagerUserAttributeSourceImpl(userMgr, userInfoPropertySet);
    }

    /**
     * @see org.apache.jetspeed.userinfo.UserInfoManager#setUserInfoMap(org.apache.jetspeed.om.page.Fragment,
     *      org.apache.jetspeed.request.RequestContext)
     */
    public Map getUserInfoMap(ObjectID oid, RequestContext context)
    {
        String appOid = oid.toString();
        
        if (log.isDebugEnabled())
            log.debug("Getting user info for portlet application: " + appOid);

        Map userInfo = null;
        Subject subject = context.getSubject();
        if (null != subject)
        {
            userInfo = userManagerUserAttributeSource.getUserAttributeMap(subject, getLinkedUserAttr(oid), context);
        }        
        return userInfo;
    }

    protected Collection getLinkedUserAttr(ObjectID oid)
    {
        // Check if user info map is in cache.
        Collection linkedUserAttr = (List)appUserInfoAttrCache.get(oid);
        
        if (linkedUserAttr == null)
        {
            MutablePortletApplication pa = registry.getPortletApplication(oid);
            if (null == pa)
            {
                log.debug(PortletRequest.USER_INFO + " is set to null");
                return null;
            }
            Collection userAttributes = pa.getUserAttributes();
            Collection userAttributeRefs = pa.getUserAttributeRefs();
            linkedUserAttr = mapLinkedUserAttributes(userAttributes, userAttributeRefs);
            appUserInfoAttrCache.put(oid, linkedUserAttr);
        }
        return linkedUserAttr;
    }
    
    public void applicationRemoved(MutablePortletApplication app)
    {
        // clear cache element
        appUserInfoAttrCache.remove(app.getId());
    }

    public void applicationUpdated(MutablePortletApplication app)
    {
        // clear cache element
        appUserInfoAttrCache.remove(app.getId());
    }

    public void portletRemoved(PortletDefinitionComposite def)
    {
        // ignore
    }

    public void portletUpdated(PortletDefinitionComposite def)
    {
        // ignore
    }
}