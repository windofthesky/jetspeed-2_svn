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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.userinfo.UserAttributeRetrievalException;
import org.apache.jetspeed.userinfo.UserAttributeSource;
import org.apache.jetspeed.userinfo.UserInfoManager;
import org.apache.jetspeed.userinfo.impl.AbstractUserInfoManagerImpl;
import org.apache.pluto.om.common.ObjectID;

/**
 * Multisource User Information manager
 * One or more sources are assembled in Spring configuration and setter injected
 * 
 * @author <a href="mailto:KeithGarry.Boyce@bcbsma.com">Keith Garry Boyce </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class MultiSourceUserInfoManagerImpl extends AbstractUserInfoManagerImpl
        implements UserInfoManager
{

    /** Logger */
    private static final Log log = LogFactory
            .getLog(MultiSourceUserInfoManagerImpl.class);

    private List sources;

    private PortletRegistry portletRegistry;
   
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.userinfo.UserInfoManager#getUserInfoMap(org.apache.pluto.om.common.ObjectID,
     *      org.apache.jetspeed.request.RequestContext)
     */
    public Map getUserInfoMap(ObjectID oid, RequestContext context)
    {

        try
        {
            Map userInfoMap = new HashMap();
            Subject subject = context.getSubject();
            MutablePortletApplication pa = portletRegistry
                    .getPortletApplication(oid);
System.out.println("*** PA = " + pa);            
            if (null == pa)
            {
                log.debug(PortletRequest.USER_INFO + " is set to null");
                return null;
            }
            Collection userAttributes = pa.getUserAttributes();
            Collection userAttributeRefs = pa.getUserAttributeRefs();
            Collection linkedUserAttributes = mapLinkedUserAttributes(
                    userAttributes, userAttributeRefs);
            for (Iterator iter = sources.iterator(); iter.hasNext();)
            {
                UserAttributeSource source = (UserAttributeSource) iter.next();
                Map sourceMap;

                sourceMap = source.getUserAttributeMap(subject,
                        linkedUserAttributes, context);
                userInfoMap.putAll(sourceMap);
            }
            return userInfoMap;
        } catch (UserAttributeRetrievalException e)
        {
            // Until external api is changed return
            e.printStackTrace();            
            return null;
        }
    }

    /**
     * @param sources
     *            The sources to set.
     */
    public void setSources(List sources)
    {
        this.sources = sources;
    }

    /**
     * @param portletRegistry
     *            The portletRegistry to set.
     */
    public void setPortletRegistry(PortletRegistry portletRegistry)
    {
        this.portletRegistry = portletRegistry;
    }
}
