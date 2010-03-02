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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.om.portlet.UserAttribute;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.om.portlet.impl.UserAttributeRefImpl;
import org.apache.jetspeed.userinfo.UserInfoManager;
import org.apache.jetspeed.container.PortletWindow;

/**
 * <p> Common user info management support
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id$
 */
public abstract class AbstractUserInfoManagerImpl implements UserInfoManager
{
    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(UserInfoManagerImpl.class);
    
    /**
     * <p>
     * Return the linked attributes mapping portlet user attributes to portal
     * user attributes.
     * </p>
     * 
     * @param userAttributes
     *            The declarative portlet user attributes.
     * @param userAttributeRefs
     *            The declarative jetspeed portlet extension user attributes
     *            reference.
     * @return The list of linked attributes.
     */
    protected List<UserAttributeRef> mapLinkedUserAttributes(List<UserAttribute> userAttributes, List<UserAttributeRef> userAttributeRefs)
    {
        UserAttributeRefImpl impl;
        List<UserAttributeRef> linkedUserAttributes = new ArrayList<UserAttributeRef>();
        if ((null != userAttributeRefs) && (userAttributeRefs.size() > 0))
        {
            for (UserAttribute currentAttribute : userAttributes)
            {
                boolean linkedAttribute = false;
                impl = new UserAttributeRefImpl();
                for (UserAttributeRef currentAttributeRef : userAttributeRefs)
                {
                    if ((currentAttribute.getName()).equals(currentAttributeRef.getNameLink()))
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Linking user attribute ref: [[name, " + currentAttribute.getName()
                                    + "], [linked name, " + currentAttributeRef.getName() + "]]");
                        }
                        impl.setName(currentAttributeRef.getName());
                        impl.setNameLink(currentAttributeRef.getNameLink());
                        linkedAttribute = true;
                        break;
                    }
                }
                if (!linkedAttribute)
                {
                    impl.setName(currentAttribute.getName());
                }
                linkedUserAttributes.add(impl);
            }
        }
        else
        {
            for (UserAttribute currentAttribute : userAttributes)
            {
                impl = new UserAttributeRefImpl();
                impl.setName(currentAttribute.getName());
                linkedUserAttributes.add(impl);
            }
        }
        return linkedUserAttributes;
    }

    /**
     * For Pluto 2.0
     */
    public Map<String, String> getUserInfo(PortletRequest request, org.apache.pluto.container.PortletWindow window)
    {
        if ( request.getUserPrincipal() == null ) 
        {
            return null;
        }
        PortletWindow portletWindow = (PortletWindow)window;
        return getUserInfoMap(portletWindow.getPortletDefinition().getApplication().getName(), portletWindow.getRequestContext());        
    }
}
