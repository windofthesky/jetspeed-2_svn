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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryEventListener;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.UserAttribute;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.request.RequestContext;

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
    private static final Logger log = LoggerFactory.getLogger(UserInfoManagerImpl.class);

    /** Map to cache user info keys for each mapped portlet application. */
    private static Map<String, List<UserAttributeRef>> appUserInfoAttrCache = Collections.synchronizedMap(new HashMap<String,List<UserAttributeRef>>());
    
    private static final SubjectUserAttributeSourceImpl subjectUserAttributeSource = new SubjectUserAttributeSourceImpl();

    /** The portlet registry. */
    protected PortletRegistry registry;
    
    /**
     * <p>
     * Constructor providing access to the {@link UserManager}.
     * </p>
     * 
     * @param userMgr The user manager.
     * @param registry The portlet registry component.
     */
    public UserInfoManagerImpl(PortletRegistry registry)
    {
        this.registry = registry;
        registry.addRegistryListener(this);
    }

    public Map<String, String> getUserInfoMap(String appName, RequestContext context)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Getting user info for portlet application: " + appName);
        }
        
        Map<String, String> userInfo = null;
        Subject subject = context.getSubject();
        if (null != subject)
        {
            userInfo = subjectUserAttributeSource.getUserAttributeMap(subject, getLinkedUserAttr(appName), context);
        }        
        return userInfo;
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

    public void applicationRemoved(PortletApplication app)
    {
        // clear cache element
        appUserInfoAttrCache.remove(app.getName());
    }

    public void applicationUpdated(PortletApplication app)
    {
        // clear cache element
        appUserInfoAttrCache.remove(app.getName());
    }

    public void portletRemoved(PortletDefinition def)
    {
        // ignore
    }

    public void portletUpdated(PortletDefinition def)
    {
        // ignore
    }
}
