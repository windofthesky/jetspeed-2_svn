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
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PortletAggregator;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentFragmentImpl;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.DOMUtils;
import org.apache.jetspeed.util.KeyValue;
import org.w3c.dom.Element;

/**
 * PortletAggregator builds the content required to render a single portlet.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletAggregatorImpl implements PortletAggregator
{
    private PortletRenderer renderer;
    private boolean titleInHeader;

    public PortletAggregatorImpl(PortletRenderer renderer) 
    {
        this(renderer, false);
    }

    public PortletAggregatorImpl(PortletRenderer renderer, boolean titleInHeader) 
    {
        this.renderer = renderer;
        this.titleInHeader = titleInHeader;
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
        
        PortletWindow window = context.resolvePortletWindow(entity);
        
        if (window == null) 
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
            
            Fragment fragment = new PortletAggregatorFragmentImpl(entity);        
            fragment.setType(Fragment.PORTLET);
            fragment.setName(name);
            window = context.getPortletWindow(new ContentFragmentImpl(fragment, new HashMap(), true));
            
            if (window.isValid())
            {
                context.registerInstantlyCreatedPortletWindow(window);
            }
        }
        
        ContentFragment contentFragment = window.getFragment();
        renderer.renderNow(contentFragment, context);
        
        if (titleInHeader && contentFragment.getPortletContent() != null)
        {            
            context.getResponse().setHeader( "JS_PORTLET_TITLE", StringEscapeUtils.escapeHtml( contentFragment.getPortletContent().getTitle() ) );
        }

        writeHeadElements(context, window);
        context.getResponse().getWriter().write(contentFragment.getRenderedContent());
        PortletContent content = contentFragment.getPortletContent();
        
        if (content != null && content.getExpiration() == 0)
        {
            contentFragment.getPortletContent().release();
        }        
    }
    
    protected void writeHeadElements(RequestContext context, PortletWindow window) throws IOException
    {
        List<KeyValue<String, Element>> headElements = window.getHeadElements();
        PrintWriter out = context.getResponse().getWriter();

        out.println("<JS_PORTLET_HEAD_ELEMENTS>");
        
        if (!headElements.isEmpty())
        {
            for (KeyValue<String, Element> kvPair : headElements)
            {
                out.println(DOMUtils.stringifyElementToHtml(kvPair.getValue()));
            }
        }
        
        out.print("</JS_PORTLET_HEAD_ELEMENTS>");
    }
    
}
