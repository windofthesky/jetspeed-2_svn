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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.contentserver.ContentFilter;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;
import org.picocontainer.Startable;

/**
 * PageAggregator builds the content required to render a page of portlets.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PageAggregatorImpl implements PageAggregator, Startable
{
    private final static Log log = LogFactory.getLog(PageAggregatorImpl.class);

    public final static int STRATEGY_SEQUENTIAL = 0;
    public final static int STRATEGY_PARALLEL = 1;

    private int strategy = STRATEGY_SEQUENTIAL;
    private PortletRenderer renderer;

    public PageAggregatorImpl(PortletRenderer renderer, int strategy)
    {
        this.renderer = renderer;
        this.strategy = strategy;
    }

    public PageAggregatorImpl(PortletRenderer renderer)
    {
        this(renderer, STRATEGY_SEQUENTIAL);
    }

    public void start()
    {
    }

    public void stop()
    {

    }

    /**
     * Builds the portlet set defined in the context into a portlet tree.
     *
     * @return Unique Portlet Entity ID
     */
    public void build(RequestContext context) throws JetspeedException
    {
        Page page = context.getPage();
        if (null == page)
        {
            throw new JetspeedException("Failed to find PSML Pin PageAggregator.build");
        }

        //Set default acl
        String acl = page.getAcl();
        if (acl == null)
        {
            //TBD get system default acl;
        }

        // Initialize fragment
        Stack stack = new Stack();
        Fragment currentFragment = page.getRootFragment();

        if (currentFragment == null)
        {
            throw new JetspeedException("No root Fragment found in Page");
        }

        String layoutDecorator = currentFragment.getDecorator();
        if (layoutDecorator == null)
        {
            layoutDecorator = page.getDefaultDecorator(currentFragment.getType());
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////
        //TODO: Remove hard coding of locations and use CM + TL
        //      DST: Im going to encapsulate this into a class, which can be accessed by 
        //           the PowerTool when aggregating content, and make sure to modify the search path
        //           according to the current decorator. Assigned issue to JiRa JS2-24        
        List contentPathes = (List) context.getSessionAttribute(ContentFilter.SESSION_CONTENT_PATH_ATTR);

        if (contentPathes == null)
        {
            contentPathes = new ArrayList(2);
            context.setSessionAttribute(ContentFilter.SESSION_CONTENT_PATH_ATTR, contentPathes);
        }

        if (contentPathes.size() < 1)
        {
            // define the lookup order
            contentPathes.add(currentFragment.getType() + "/html/" + layoutDecorator);
            contentPathes.add("portlet/html/jetspeed");
            contentPathes.add("portlet/html");
            contentPathes.add("generic/html");
            contentPathes.add("/html");
        }
        else
        {
            contentPathes.set(0, currentFragment.getType() + "/html/" + layoutDecorator);
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////

        if (checkAccess(context, (currentFragment.getAcl() != null) ? currentFragment.getAcl() : acl, "render"))
        {
            // handle maximized state
            NavigationalState nav = context.getNavigationalState();
            PortletWindow window = nav.getMaximizedWindow(context.getPage());
            if (null != window)
            {
                Fragment fragment = page.getFragmentById(window.getId().toString());
                if (checkAccess(context, (fragment.getAcl() != null) ? fragment.getAcl() : acl, "render"))
                {
                    context.getRequest().setAttribute("org.apache.jetspeed.maximized.Fragment", fragment);
                    context.getRequest().setAttribute("org.apache.jetspeed.maximized.Layout", page.getRootFragment());
                    
                    renderer.renderNow(page.getRootFragment(), context);
                    
                    context.getRequest().removeAttribute("org.apache.jetspeed.maximized.Fragment");
                    context.getRequest().removeAttribute("org.apache.jetspeed.maximized.Layout");
                }
                return;
            }
            
            // initializes the rendering stack with root children
            // root fragement is always treated synchronously
            for (Iterator i = currentFragment.getFragments().iterator(); i.hasNext();)
            {
                Fragment f = (Fragment) i.next();

                if (!"hidden".equals(f.getState()))
                {
                    stack.push(f);
                }
            }

            // Walk through the Fragment tree, and start rendering "portlet" type
            // fragment
            while (!stack.isEmpty())
            {
                currentFragment = (Fragment) stack.pop();

                if (checkAccess(context, ((currentFragment.getAcl() != null) ? currentFragment.getAcl() : acl), "render"))
                {
                    if (currentFragment.getType().equals("portlet"))
                    {
                        // make the page aggreator less fragile
                        // by preventing failed rendering from screwing up the
                        // whole process
                        try
                        {
                            if (log.isDebugEnabled())
                            {
                                log.debug(
                                    "Rendering portlet fragment: [[name, "
                                        + currentFragment.getName()
                                        + "], [id, "
                                        + currentFragment.getId()
                                        + "]]");
                            }
                            // TODO This is where we add User Info.
                            RequestContext portletContext = context;
                            renderer.render(currentFragment, portletContext);
                            if (strategy == STRATEGY_SEQUENTIAL)
                            {
                                ContentDispatcher dispatcher = renderer.getDispatcher(context, false);
                                dispatcher.sync(currentFragment);
                            }
                        }
                        catch (Exception e)
                        {
                            log.error("Failed to render portlet \"" + currentFragment + "\": " + e.toString());
                        }
                    }

                    // push the children frgaments on the rendering stack
                    for (Iterator i = currentFragment.getFragments().iterator(); i.hasNext();)
                    {
                        Fragment f = (Fragment) i.next();

                        if (!"hidden".equals(f.getState()))
                        {
                            stack.push(f);
                        }
                    }
                }
                else
                {
                    log.warn("Access denied RENDER fragment " + currentFragment);
                }
            }
            
            // Retrieves the content dispatcher appropriate for sequential
            // or parallel rendering

            ContentDispatcher dispatcher = renderer.getDispatcher(context, (strategy == STRATEGY_PARALLEL));

            // Now synchronously trigger the rendering of the whole page
            renderer.renderNow(page.getRootFragment(), context);
        }
        else
        {
            log.warn("Access denied RENDER page " + page);
        }

    }

    public boolean checkAccess(RequestContext context, String acl, String action)
    {
        // This methid needs to be moved a secuity module.
        // Does nothing right now
        return true;
    }
}
