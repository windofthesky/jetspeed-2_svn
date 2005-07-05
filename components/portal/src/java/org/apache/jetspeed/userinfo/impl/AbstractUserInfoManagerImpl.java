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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.UserAttributeRef;
import org.apache.jetspeed.om.impl.UserAttributeRefImpl;

/**
 * <p> Common user info management support
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id: AbstractUserInfoManagerImpl.java 187254 2004-08-11 23:56:28Z taylor $
 */
public abstract class AbstractUserInfoManagerImpl
{
    /** Logger */
    private static final Log log = LogFactory.getLog(UserInfoManagerImpl.class);
    
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
     * @return The collection of linked attributes.
     */
    protected Collection mapLinkedUserAttributes(Collection userAttributes, Collection userAttributeRefs)
    {
        Collection linkedUserAttributes = new ArrayList();
        if ((null != userAttributeRefs) && (userAttributeRefs.size() > 0))
        {
            Iterator attrIter = userAttributes.iterator();
            while (attrIter.hasNext())
            {
                UserAttribute currentAttribute = (UserAttribute) attrIter.next();
                boolean linkedAttribute = false;
                if (null != currentAttribute)
                {
                    Iterator attrRefsIter = userAttributeRefs.iterator();
                    while (attrRefsIter.hasNext())
                    {
                        UserAttributeRef currentAttributeRef = (UserAttributeRef) attrRefsIter.next();
                        if (null != currentAttributeRef)
                        {
                            if ((currentAttribute.getName()).equals(currentAttributeRef.getNameLink()))
                            {
                                if (log.isDebugEnabled())
                                    log.debug("Linking user attribute ref: [[name, " + currentAttribute.getName()
                                            + "], [linked name, " + currentAttributeRef.getName() + "]]");
                                linkedUserAttributes.add(currentAttributeRef);
                                linkedAttribute = true;
                            }
                        }
                    }
                }
                if (!linkedAttribute)
                {
                    linkedUserAttributes.add(new UserAttributeRefImpl(currentAttribute));
                }
            }
        }
        else
        {
            Iterator attrIter = userAttributes.iterator();
            while (attrIter.hasNext())
            {
                UserAttribute currentAttribute = (UserAttribute) attrIter.next();
                linkedUserAttributes.add(new UserAttributeRefImpl(currentAttribute));
            }
        }
        return linkedUserAttributes;
    }

}
