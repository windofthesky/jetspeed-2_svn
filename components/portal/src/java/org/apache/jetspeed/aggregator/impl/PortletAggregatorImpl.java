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

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.PortletAggregator;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.ContentFragmentImpl;
import org.apache.jetspeed.request.RequestContext;

/**
 * PortletAggregator builds the content required to render a single portlet.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletAggregatorImpl implements PortletAggregator
{
    private final static Log log = LogFactory.getLog(PortletAggregatorImpl.class);    
    
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
        String name = context.getRequestParameter(PortalReservedParameters.PORTLET);
        if (name == null)
        {
            name = (String)context.getAttribute(PortalReservedParameters.PORTLET);
        }
        if (name == null)
        {
            return;
        }
        PortletAggregatorFragmentImpl fragment = new PortletAggregatorFragmentImpl(entity);
        fragment.setType(Fragment.PORTLET);
        fragment.setName(name);
        String decorator = fragment.getDecorator();

        // render and write portlet content to response
        if (decorator == null)
        {
            // decorator = context.getPage().getDefaultDecorator(fragment.getType());
            log.debug("No sepecific decorator portlet so using page default: "+decorator);
        }
        ContentDispatcher dispatcher = renderer.getDispatcher(context, false);
        ContentFragment contentFragment = new ContentFragmentImpl(fragment, new HashMap());
        renderer.renderNow(contentFragment, context);
//        dispatcher.include(fragment);
        context.getResponse().getWriter().write(contentFragment.getRenderedContent());
    }
    
    private void addStyle(RequestContext context, String decoratorName, String decoratorType) 
    {
        log.debug("addStyle: decoratorName=" + decoratorName + ", decoratorType=" + decoratorType );
/*
        HeaderResourceFactory headerResourceFactory=(HeaderResourceFactory)Jetspeed.getComponentManager().getComponent(HeaderResourceFactory.class);
        HeaderResource headerResource=headerResourceFactory.getHeaderResouce(context);
        
        if(decoratorType.equals(Fragment.LAYOUT))
        {
            headerResource.addStyleSheet("content/css/styles.css");
        }
        else
        {
            headerResource.addStyleSheet("content/"+decoratorName+"/css/styles.css");
        }
*/
    }
}
