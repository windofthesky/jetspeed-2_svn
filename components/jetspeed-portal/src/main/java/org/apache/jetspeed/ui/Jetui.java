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

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;

import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.decoration.DecorationFactory;
import org.apache.jetspeed.decoration.LayoutDecoration;
import org.apache.jetspeed.decoration.PortletDecoration;
import org.apache.jetspeed.decoration.Theme;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.portlet.HeaderPhaseSupportConstants;
import org.apache.jetspeed.portlets.layout.ColumnLayout;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.UserSubjectPrincipal;
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
    private PageLayoutComponent pageLayoutComponent;
    private PortalConfiguration pc;
    private DecorationFactory decorationFactory;
    private String layoutTemplate;
    
    public Jetui(PortletRenderer renderer, HeaderResourceFactory headerFactory, PageLayoutComponent pageLayoutComponent, 
            PortalConfiguration pc, DecorationFactory decorationFactory, String layoutTemplate)
    {
        this.renderer = renderer;
        this.headerFactory = headerFactory;
        this.pageLayoutComponent = pageLayoutComponent;
        this.pc = pc;
        this.decorationFactory = decorationFactory;
        this.layoutTemplate = layoutTemplate;
    }
    
    public void process( RequestContext request, ContentFragment maximized)
        throws PipelineException
    {
        try
        {
            RequestDispatcher dispatcher = request.getRequest().getRequestDispatcher(layoutTemplate);
            request.setAttribute("jetui", this);
            ContentFragment rootFragment = request.getPage().getNonTemplateRootFragment();
            if (maximized == null)
            {
                String jetspeedLayout = rootFragment.getName();
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // BOGUS: I would prefer to put all layout information directly in PSML, not in portlet.xml, right now its mixed
                // need to have a better algorithm to determine number of columns and fragment column sizes
                int numberOfColumns = 1;
                String layoutType = "OneColumn";
                String fragmentColumnSizes = rootFragment.getProperty(ContentFragment.SIZES_PROPERTY_NAME);
                if (jetspeedLayout.indexOf("Two") > -1)
                {
                    numberOfColumns = 2;
                    layoutType = "TwoColumn";
                    if (fragmentColumnSizes == null)
                    	fragmentColumnSizes = "50%,50%";
                }
                else if (jetspeedLayout.indexOf("Three") > -1)
                {
                    numberOfColumns = 3;
                    layoutType = "ThreeColumn";
                    if (fragmentColumnSizes == null)                    
                    	fragmentColumnSizes = "33%,34%,33%";                
                }                
                else if (jetspeedLayout.indexOf("Four") > -1)
                {
                    numberOfColumns = 4;
                    layoutType = "FourColumn";
                    if (fragmentColumnSizes == null)                    
                    	fragmentColumnSizes = "25%,25%,25%,25%";                                
                }      
                else
                {
                    fragmentColumnSizes = "100%";
                }
                String [] fragmentColumnSizesArray = fragmentColumnSizes.split("\\,");
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ColumnLayout columnLayout = new ColumnLayout(numberOfColumns, layoutType, rootFragment.getFragments(), fragmentColumnSizesArray);
                columnLayout.buildDetachedPortletList(request.getPage().getRootFragment().getFragments());
                request.setAttribute("columnLayout", columnLayout);
            }
            else
            {
                ColumnLayout columnLayout = new ColumnLayout(1, "maximized", rootFragment.getFragments(), new String[] { "100%" }, maximized);
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
        return getStyleSheets(context, null);
    }
    
    public Set<String> getStyleSheets(RequestContext context, String overrideDecoratorName)
    {
        if (overrideDecoratorName == null)
        {
            return getTheme(context).getStyleSheets();
        }
        else
        {
            Set<String> styleSheets = new HashSet<String>();
            
            LayoutDecoration layoutDecoration = decorationFactory.getLayoutDecoration(overrideDecoratorName, context);
            
            if (layoutDecoration != null)
            {
                styleSheets.add(layoutDecoration.getStyleSheet());
                styleSheets.add(layoutDecoration.getStyleSheetPortal());
            }
            
            PortletDecoration portletDecoration = decorationFactory.getPortletDecoration(overrideDecoratorName, context);
            
            if (portletDecoration != null)
            {
                styleSheets.add(portletDecoration.getStyleSheet());
                styleSheets.add(portletDecoration.getStyleSheetPortal());
            }
            
            return styleSheets;
        }
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
    
    public ContentFragment getContentFragment(String windowId, RequestContext context)
    {
        ContentPage page = context.getPage();
        return page.getFragmentByFragmentId(windowId);
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
 
    public Map<String,String> getUserAttributes(RequestContext rc)
    {
        Map<String,String> map = null;
        Principal principal = rc.getRequest().getUserPrincipal();
        if (principal instanceof UserSubjectPrincipal)
        {
            UserSubjectPrincipal jp = (UserSubjectPrincipal)principal;
            map = jp.getUser().getInfoMap();
        }
        return map;
    }
    
    public String getUserAttribute(RequestContext rc, String attributeName, String defaultValue)
    {
        Map<String,String> infoMap = getUserAttributes(rc);
        String value = infoMap != null ? infoMap.get(attributeName) : null;
        return value != null ? value : defaultValue;
    }

    
    
    public PortalConfiguration getPortalConfiguration()
    {
        return this.pc;
    }
    
    public Toolbar getToolbar(RequestContext context, Toolbar.Orientation orientation)
    {
        Toolbar toolbar = null;
        String id;
        if (orientation == Toolbar.Orientation.LEFT)
        {
            id = Toolbar.LEFT_TOOLBAR_ID;
        }
        else if (orientation == Toolbar.Orientation.RIGHT)
        {
            id = Toolbar.RIGHT_TOOLBAR_ID;            
        }
        else
            return toolbar;
        ContentPage page = context.getPage();
        ContentFragment cf = page.getFragmentByFragmentId(id); 
        if (cf != null) 
        {
            toolbar = new Toolbar(orientation, id, cf);
            toolbar.setCssClass(cf.getProperty("class"));
            String state = cf.getProperty("state");
            if (state != null)
                toolbar.setClosed(state.equals("closed"));
        }
        return toolbar;
    }
    
    public ContentFragment getToolbox(RequestContext context)
    {
        List<ContentFragment> result = context.getPage().getFragmentsByName("j2-admin::JetspeedToolbox");
        if (result != null && result.size() > 0)
            return result.get(0);
        return null;
    }

    public ContentFragment getPageNavigator(RequestContext context)
    {
        List<ContentFragment> result = context.getPage().getFragmentsByName("j2-admin::PageNavigator");
        if (result != null && result.size() > 0)
            return result.get(0);
        return null;
    }
    
    public String getPortletIcon(RequestContext context, String windowId, String defaultPortletIcon)
    {
        String portletIcon = null;
        PortletWindow window = context.getPortletWindow(windowId);
        
        if (window != null && window.getPortletDefinition() != null)
        {
            InitParam portletIconInitParam = window.getPortletDefinition().getInitParam("portlet-icon");
            
            if (portletIconInitParam != null)
            {
                portletIcon = portletIconInitParam.getParamValue();
            }
        }
        
        return (portletIcon != null ? portletIcon : defaultPortletIcon);
    }
    
    
    
}
