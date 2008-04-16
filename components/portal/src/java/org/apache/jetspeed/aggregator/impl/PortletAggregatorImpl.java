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
package org.apache.jetspeed.aggregator.impl;

import java.io.IOException;
import java.util.HashMap;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PortletAggregator;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentFragmentImpl;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;

/**
 * PortletAggregator builds the content required to render a single portlet.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletAggregatorImpl implements PortletAggregator
{
    private PortletRenderer renderer;

    public PortletAggregatorImpl(PortletRenderer renderer) 
    {
        this.renderer = renderer;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.aggregator.Aggregator#build(org.apache.jetspeed.request.RequestContext)
     */
    public void build(RequestContext context) throws JetspeedException, IOException
    {
        // construct Fragment for rendering use with
        // appropriate id to match portlet entity
        String entity = context.getRequestParameter(PortalReservedParameters.PORTLET_ENTITY);
        if (entity == null)
        {
            entity = (String)context.getAttribute(PortalReservedParameters.PORTLET_ENTITY);
        }
        if (entity == null)
        {
            return;
        }        
        Fragment fragment = context.getPage().getFragmentById(entity);
        if (fragment == null) 
        {        
            String name = context.getRequestParameter(PortalReservedParameters.PORTLET);
            if (name == null)
            {
                name = (String)context.getAttribute(PortalReservedParameters.PORTLET);
            }
            if (name == null)
            {
                return;
            }
            fragment = new PortletAggregatorFragmentImpl(entity);        
            fragment.setType(Fragment.PORTLET);
            fragment.setName(name);
        }
        ContentFragment contentFragment = new ContentFragmentImpl(fragment, new HashMap());
        renderer.renderNow(contentFragment, context);
        context.getResponse().getWriter().write(contentFragment.getRenderedContent());
        PortletContent content = contentFragment.getPortletContent();
        if (content.getExpiration() == 0)
        {
            contentFragment.getPortletContent().release();
        }        
    }
    
}
