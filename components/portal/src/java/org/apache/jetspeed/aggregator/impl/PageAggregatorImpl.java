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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.FailedToRenderFragmentException;
import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.contentserver.ContentFilter;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * ContentPageAggregator builds the content required to render a page of portlets.
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class PageAggregatorImpl implements PageAggregator
{
    private final static Log log = LogFactory.getLog(PageAggregatorImpl.class);

    public final static int STRATEGY_SEQUENTIAL = 0;
    public final static int STRATEGY_PARALLEL = 1;

    private int strategy = STRATEGY_SEQUENTIAL;
    private PortletRenderer renderer;

    private List fallBackContentPathes;

    public PageAggregatorImpl( PortletRenderer renderer, int strategy, List fallBackContentPathes )
    {
        this.renderer = renderer;
        this.strategy = strategy;
        this.fallBackContentPathes = fallBackContentPathes;
    }

    public PageAggregatorImpl( PortletRenderer renderer, List fallBackContentPathes )
    {
        this(renderer, STRATEGY_SEQUENTIAL, fallBackContentPathes);
    }

    /**
     * Builds the portlet set defined in the context into a portlet tree.
     * 
     * @return Unique Portlet Entity ID
     */
    public void build( RequestContext context ) throws JetspeedException, IOException
    {
        ContentPage page = context.getPage();
        if (null == page)
        {
            throw new JetspeedException("Failed to find PSML Pin ContentPageAggregator.build");
        }

        ContentFragment root = page.getRootContentFragment();

        if (root == null)
        {
            throw new JetspeedException("No root ContentFragment found in ContentPage");
        }

        String layoutDecorator = root.getDecorator();
        if (layoutDecorator == null)
        {
            layoutDecorator = page.getDefaultDecorator(root.getType());
        }

        String defaultPortletDecorator = page.getDefaultDecorator(ContentFragment.PORTLET);

        ///////////////////////////////////////////////////////////////////////////////////////////////
        //TODO: Remove hard coding of locations and use CM + TL
        //      DST: Im going to encapsulate this into a class, which can be accessed
        // by
        //           the PowerTool when aggregating content, and make sure to modify the
        // search path
        //           according to the current decorator. Assigned issue to JiRa JS2-24
        List contentPathes = (List) context.getSessionAttribute(ContentFilter.SESSION_CONTENT_PATH_ATTR);

        if (contentPathes == null)
        {
            contentPathes = new ArrayList(2);
            context.setSessionAttribute(ContentFilter.SESSION_CONTENT_PATH_ATTR, contentPathes);
        }
        String mediaType = context.getCapabilityMap().getPreferredMediaType().getName();
        if (contentPathes.size() < 1)
        {
            // define the lookup order

            contentPathes.add(root.getType() + "/" + mediaType + "/" + layoutDecorator);
            // Start added by jamesliao, 27-05-2005
            contentPathes.add(ContentFragment.PORTLET + "/" + mediaType + "/" + defaultPortletDecorator);
            // End
            
            Iterator defaults = fallBackContentPathes.iterator();
            while (defaults.hasNext())
            {
                String path = (String) defaults.next();
                contentPathes.add(path.replaceAll("\\{mediaType\\}", mediaType));
            }

        }
        else
        {
            contentPathes.set(0, root.getType() + "/" + mediaType + "/" + layoutDecorator);
            // Start added by jamesliao, 27-05-2005, override the previous portlet-decorator
            contentPathes.set(1, ContentFragment.PORTLET + "/" + mediaType + "/" + defaultPortletDecorator);
            // End
        }

        if (layoutDecorator != null)
        {
            addStyle(context, layoutDecorator, ContentFragment.LAYOUT);
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////
        ContentDispatcher dispatcher = renderer.getDispatcher(context, (strategy == STRATEGY_PARALLEL));
        // handle maximized state
        NavigationalState nav = context.getPortalURL().getNavigationalState();
        PortletWindow window = nav.getMaximizedWindow();
        if (null != window)
        {
            renderMaximizedWindow(context, page, root, window);
        }
        else
        {
            aggregateAndRender(root, context, page);
        }
        
        //dispatcher.include(root);
        context.getResponse().getWriter().write(root.getRenderedContent());

        if (null != window)
        {
            context.getRequest().removeAttribute(PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE);
            context.getRequest().removeAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE);
        }
    }

    /**
     * <p>
     * renderMaximizedWindow
     * </p>
     * 
     * @param context
     * @param page
     * @param layoutContentFragment
     * @param defaultPortletDecorator
     * @param dispatcher
     * @param window
     * @throws FailedToRenderContentFragmentException
     */
    protected void renderMaximizedWindow( RequestContext context, ContentPage page, ContentFragment layoutContentFragment,
            PortletWindow window ) throws FailedToRenderFragmentException
    {
        String defaultPortletDecorator = page.getDefaultDecorator(ContentFragment.PORTLET);
        ContentFragment maxedContentFragment = page.getContentFragmentById(window.getId().toString());
        if (maxedContentFragment != null)
        {
            context.getRequest().setAttribute(PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE, maxedContentFragment);
            context.getRequest().setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, maxedContentFragment);
            context.getRequest().setAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE, page.getRootContentFragment());

            if (maxedContentFragment.getDecorator() != null)
            {
                log.debug("decorator=" + layoutContentFragment.getDecorator());
                addStyle(context, maxedContentFragment.getDecorator(), ContentFragment.PORTLET);
            }
            else
            {
                log.debug("no decorator for defined for portlet fragement," + layoutContentFragment.getId()
                        + ".  So using page default, " + defaultPortletDecorator);
                addStyle(context, defaultPortletDecorator, ContentFragment.PORTLET);
            }
            try
            {
                renderer.renderNow(maxedContentFragment, context);
                renderer.renderNow(layoutContentFragment, context);              
                
            }
            catch (Exception e)
            {
                log.error(e.getMessage(), e);
                maxedContentFragment.overrideRenderedContent("Sorry, but we were unable access the requested portlet.  Send the following message to your portal admin:  "+  e.getMessage());
            }


        }
    }

    private void addStyle( RequestContext context, String decoratorName, String decoratorType )
    {
        HeaderResourceFactory headerResourceFactory=(HeaderResourceFactory)Jetspeed.getComponentManager().getComponent(HeaderResourceFactory.class);
        HeaderResource headerResource=headerResourceFactory.getHeaderResouce(context);

        if (decoratorType.equals(ContentFragment.LAYOUT))
        {
            headerResource.addStyleSheet("content/css/styles.css");
        }
        else
        {
            headerResource.addStyleSheet("content/" + decoratorName + "/css/styles.css");
        }
    }

    protected void aggregateAndRender( ContentFragment f, RequestContext context, ContentPage page )
            throws FailedToRenderFragmentException
    {

        if (f.getContentFragments() != null && f.getContentFragments().size() > 0)
        {
            Iterator children = f.getContentFragments().iterator();
            while (children.hasNext())
            {
                ContentFragment child = (ContentFragment) children.next();
                if (!"hidden".equals(f.getState()))
                {
                    aggregateAndRender(child, context, page);
                }
            }
        }

        // Start the actual rendering process
        String defaultPortletDecorator = page.getDefaultDecorator(ContentFragment.PORTLET);
        if (log.isDebugEnabled())
        {
            log.debug("Rendering portlet fragment: [[name, " + f.getName() + "], [id, " + f.getId() + "]]");
        }
        
       

        
        if (strategy == STRATEGY_SEQUENTIAL)
        {
            renderer.renderNow(f, context);
        }
        else
        {
            renderer.render(f, context);
        }

        if (f.getDecorator() != null && f.getType().equals(ContentFragment.PORTLET))
        {
            log.debug("decorator=" + f.getDecorator());
            addStyle(context, f.getDecorator(), ContentFragment.PORTLET);
        }
        else if (f.getDecorator() == null && f.getType().equals(ContentFragment.PORTLET))
        {
            log.debug("no decorator for defined for portlet fragement," + f.getId() + ".  So using page default, "
                    + defaultPortletDecorator);
            addStyle(context, defaultPortletDecorator, ContentFragment.PORTLET);
        }
    }
}
