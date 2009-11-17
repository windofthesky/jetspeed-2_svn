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

package org.apache.jetspeed.ui;

import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;

import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.decoration.Theme;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.portlet.HeaderPhaseSupportConstants;
import org.apache.jetspeed.portlets.layout.ColumnLayout;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.HeadElementUtils;
import org.apache.jetspeed.util.KeyValue;

/**
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class Jetui
{
    private PortletRenderer renderer;
    private HeaderResourceFactory headerFactory;
    private String layoutTemplate;
    
    public Jetui(PortletRenderer renderer, HeaderResourceFactory headerFactory, String layoutTemplate)
    {
        this.renderer = renderer;
        this.headerFactory = headerFactory;
        this.layoutTemplate = layoutTemplate;
    }
    
    public void process( RequestContext request, ContentFragment maximized)
        throws PipelineException
    {
        try
        {
            RequestDispatcher dispatcher = request.getRequest().getRequestDispatcher(layoutTemplate);
            request.setAttribute("jetui", this);
            if (maximized == null)
            {
                Fragment rootFragment = request.getPage().getRootFragment();
                String jetspeedLayout = rootFragment.getName();
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // BOGUS: I would prefer to put all layout information directly in PSML, not in portlet.xml, right now its mixed
                // need to have a better algorithm to determine number of columns and fragment column sizes
                int numberOfColumns = 1;
                String layoutType = "OneColumn";
                String fragmentColumnSizes = "100%";
                if (jetspeedLayout.indexOf("Two") > -1)
                {
                    numberOfColumns = 2;
                    layoutType = "TwoColumn";
                    fragmentColumnSizes = "50%,50%";
                }
                else if (jetspeedLayout.indexOf("Three") > -1)
                {
                    numberOfColumns = 3;
                    layoutType = "ThreeColumn";
                    fragmentColumnSizes = "33%,34%,33%";                
                }                
                else if (jetspeedLayout.indexOf("Four") > -1)
                {
                    numberOfColumns = 4;
                    layoutType = "FourColumn";
                    fragmentColumnSizes = "25%,25%,25%,25%";                                
                }                
                String [] fragmentColumnSizesArray = fragmentColumnSizes.split("\\,");
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////            
                ColumnLayout columnLayout = new ColumnLayout(numberOfColumns, layoutType, rootFragment.getFragments(), fragmentColumnSizesArray);
                request.setAttribute("columnLayout", columnLayout);
            }
            else
            {
                ColumnLayout columnLayout = new ColumnLayout(1, "maximized", request.getPage().getRootFragment().getFragments(), new String[] { "100%" });
                request.setAttribute("columnLayout", columnLayout);                
            }
            dispatcher.include(request.getRequest(), request.getResponse());            
        }
        catch (Exception e)
        {
            throw new PipelineException(e.toString(), e);
        }
    }
    
    public String renderPortlet(ContentFragment fragment, RequestContext context)
    {
        return fragment.getRenderedContent();
    }
  
    public String getTitle(RequestContext context)
    {
        return context.getPage().getTitle(context.getLocale());
    }
    
    public String getBaseURL(RequestContext context)
    {
        StringBuffer url = new StringBuffer();
        url.append(context.getRequest().getScheme());
        url.append("://");
        url.append(context.getRequest().getServerName());
        url.append(":");
        url.append(context.getRequest().getServerPort());
        url.append(context.getRequest().getContextPath());
        url.append("/");
        return url.toString();
    }
    
    public Theme getTheme(RequestContext context)
    {
        return (Theme)context.getRequest().getAttribute("org.apache.jetspeed.theme");        
    }
    
    public Set<String> getStyleSheets(RequestContext context)
    {
        return getTheme(context).getStyleSheets();
    }
    
    public String includeHeaderResources(RequestContext context)
    {        
       HeaderResource hr = headerFactory.getHeaderResouce(context);
       StringBuffer result = new StringBuffer(hr.getContent());
       List<KeyValue<String, HeadElement>> headers = context.getMergedHeadElements();
       for (KeyValue<String, HeadElement> pair : headers)
       {
           if (!HeaderPhaseSupportConstants.CONTAINER_HEAD_ELEMENT_CONTRIBUTION_ELEMENT_ID_SET.contains(pair.getKey()))
           {
               HeadElement headElement = pair.getValue();
               result.append(HeadElementUtils.toHtmlString(headElement)).append('\n');
           }
       }
       return result.toString();
    }
    
    public String getRenderedContent(ContentFragment fragment, RequestContext context)
    {
        return fragment.getRenderedContent();
    }
    
    public String renderPortletWindow(String windowId, String portletUniqueName, RequestContext context)
    {
        try
        {
            if (windowId == null || portletUniqueName == null)
            {
                throw new IllegalArgumentException("Parameter windowId and portletUniqueName are both required");
            }
            PortletWindow window = context.getPortletWindow(windowId);
            if (window == null)
            {
                window = context.getInstantlyCreatedPortletWindow(windowId, portletUniqueName);
            }
            if (window.isValid())
            {
                PortletWindow currentPortletWindow = context.getCurrentPortletWindow();
                try
                {
                    context.setCurrentPortletWindow(window);                    
                    renderer.renderNow(window.getFragment(), context);
                    return window.getFragment().getRenderedContent();
                }
                finally
                {
                    context.setCurrentPortletWindow(currentPortletWindow);
                }
            }
            else
            {
                return "";
            }
        }
        catch (Exception e)
        {
            //handleError(e, e.toString(), getCurrentFragment());
            e.printStackTrace();
            return "";
        }
    }
  
}