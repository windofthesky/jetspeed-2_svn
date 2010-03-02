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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.userinfo.UserAttributeRetrievalException;
import org.apache.jetspeed.userinfo.UserAttributeSource;

/**
 * Multisource User Information manager
 * One or more sources are assembled in Spring configuration and setter injected
 * 
 * @author <a href="mailto:KeithGarry.Boyce@bcbsma.com">Keith Garry Boyce </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class MultiSourceUserInfoManagerImpl extends UserInfoManagerImpl
{
    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(MultiSourceUserInfoManagerImpl.class);

    private List<UserAttributeSource> sources;
    
    public MultiSourceUserInfoManagerImpl(PortletRegistry registry, List<UserAttributeSource> sources)
    {
        super(registry);
        this.sources = sources;
    }

    public Map<String,String> getUserInfoMap(String appName, RequestContext context)
    {
        Map<String,String> userInfoMap = new HashMap<String,String>();
        try
        {
            Subject subject = context.getSubject();
            if (null != subject)
            {
                List<UserAttributeRef> linkedUserAttributes = getLinkedUserAttr(appName);
                
                for (UserAttributeSource source : sources)
                {
                    Map<String, String> sourceMap = source.getUserAttributeMap(subject, linkedUserAttributes, context);
                    if (sourceMap != null)
                    {
                        userInfoMap.putAll(sourceMap);
                    }
                }
            }
        } 
        catch (UserAttributeRetrievalException e)
        {
            // Until external api is changed return
            log.error(e.getMessage(), e);          
            return null;
        }
        return userInfoMap;
    }
}
