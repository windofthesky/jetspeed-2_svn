/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.layout.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.page.PageManager;

/**
 * Abstract Get Resource aaction for folders, pages and links
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public abstract class BaseGetResourceAction
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants 
{
    protected static final Logger log = LoggerFactory.getLogger(BaseSiteUpdateAction.class);    
    
    public BaseGetResourceAction(String template, 
            String errorTemplate, 
            PageManager pageManager)
    {
        super(template, errorTemplate, pageManager);
    }
    
    public BaseGetResourceAction(String template, 
                             String errorTemplate, 
                             PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, securityBehavior);
    }

    public BaseGetResourceAction(String template, 
                             String errorTemplate, 
                             PageManager pageManager,
                             PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, pageManager, securityBehavior);        
    }

    protected void putSecurityInformation(Map resultMap, SecuredResource resource)
    {
        if (resource.getSecurityConstraints() != null)
        {
            resultMap.put(SECURITY_REFS, resource.getSecurityConstraints().getSecurityConstraintsRefs());
            resultMap.put(SECURITY_DEFS, resource.getSecurityConstraints().getSecurityConstraints());
            resultMap.put(SECURITY_OWNER, resource.getSecurityConstraints().getOwner());
        }
    }
}
