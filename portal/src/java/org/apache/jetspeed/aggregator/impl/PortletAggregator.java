/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.aggregator.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.Aggregator;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.request.RequestContext;

/**
 * PortletAggregator is used to produce the content of a single portlet.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletAggregator extends BaseCommonService implements Aggregator
{
    private final static Log log = LogFactory.getLog(PortletAggregator.class);
    
    /* (non-Javadoc)
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws InitializationException
    {
        if (isInitialized())
        {
            return;
        }

        setInit(true);
    }

    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.aggregator.Aggregator#build(org.apache.jetspeed.request.RequestContext)
     */
    public void build(RequestContext context) throws JetspeedException
    {
        PortletRenderer renderer = (PortletRenderer)CommonPortletServices.getPortalService(PortletRenderer.SERVICE_NAME);
        Fragment fragment = new FragmentImpl(); // TODO: fragment factory
        fragment.setType(Fragment.PORTLET);
        fragment.setName(context.getRequestParameter(PortalReservedParameters.PORTLET));
        fragment.setId(context.getRequestParameter(PortalReservedParameters.PORTLET_ENTITY));            
        renderer.renderNow(fragment, context);
    }
}
