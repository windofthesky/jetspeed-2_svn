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
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.aggregator.Aggregator;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.contentserver.ContentFilter;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;

/**
 * Single-step multi-thread aggregation valve. Not very efficient because, hidden
 * portlets may be sent to rendering but useful for testing
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class PageAggregator extends BaseCommonService implements Aggregator
{
    private final static Log log = LogFactory.getLog(PageAggregator.class);
    private final static String DEFAULT_STRATEGY = "strategy.default";

    public final static int STRATEGY_SEQUENTIAL = 0;
    public final static int STRATEGY_PARALLEL = 1;
    private final static String CONFIG_STRATEGY_SEQUENTIAL = "sequential";
    private final static String CONFIG_STRATEGY_PARALLEL = "parallel";
    private int strategy = STRATEGY_SEQUENTIAL;

    /**
     */
    public void init() throws CPSInitializationException
    {
        if (isInitialized())
        {
            return;
        }

        try
        {
            initConfiguration();
        }
        catch (Exception e)
        {
            log.error("Aggregator: Failed to load Service: " + e);
            e.printStackTrace();
        }

        setInit(true);
    }

    /**
     */
    public void shutdown()
    {
    }

    private void initConfiguration() throws CPSInitializationException
    {
        String defaultStrategy = getConfiguration().getString(DEFAULT_STRATEGY, CONFIG_STRATEGY_SEQUENTIAL);
        if (defaultStrategy.equals(CONFIG_STRATEGY_SEQUENTIAL))
        {
            strategy = STRATEGY_SEQUENTIAL;
        }
        else if (defaultStrategy.equals(CONFIG_STRATEGY_PARALLEL))
        {
            strategy = STRATEGY_PARALLEL;
        }
    }

    /**
     * Builds the portlet set defined in the context into a portlet tree.
     *
     * @return Unique Portlet Entity ID
     */
    public void build(RequestContext context)
        throws JetspeedException
    {

        PortletRenderer renderer = (PortletRenderer)CommonPortletServices.getPortalService(PortletRenderer.SERVICE_NAME);
        Profiler profiler = (Profiler)Jetspeed.getComponentManager().getComponent(Profiler.class);
        
        ProfileLocator locator = context.getProfileLocator();
        if (null == locator)
        {
            throw new JetspeedException("Failed to find ProfileLocator in BasicAggregator.build");
        }
        Page page = profiler.getPage(locator);
        if (null == page)
        {
            throw new JetspeedException("Failed to find PSML Pin BasicAggregator.build");
        }
        context.setPage(page);

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
        if(layoutDecorator == null)
        {
            layoutDecorator = page.getDefaultDecorator(currentFragment.getType());
        }
        
        //TODO: Remove hard coding of locations and use CM + TL
        List contentPathes = (List) context.getSessionAttribute(ContentFilter.SESSION_CONTENT_PATH_ATTR);
        
        if(contentPathes == null)
        {
            contentPathes = new ArrayList(2);
            context.setSessionAttribute(ContentFilter.SESSION_CONTENT_PATH_ATTR, contentPathes);
        }
        
        if(contentPathes.size() < 1)
        {
            // define the lookup order
            contentPathes.add(currentFragment.getType()+"/html/"+layoutDecorator);
            contentPathes.add("portlet/html");
            contentPathes.add("generic/html");
            contentPathes.add("/html");
        }
        else
        {
            contentPathes.set(0, currentFragment.getType()+"/html/"+layoutDecorator);
        }
        

        if (checkAccess(context,(currentFragment.getAcl()!=null)?currentFragment.getAcl():acl, "render"))
        {
            if (strategy == STRATEGY_PARALLEL)
            {
                // initializes the rendering stack with root children
                // root fragement is always treated synchronously
                for(Iterator i = currentFragment.getFragments().iterator(); i.hasNext();)
                {
                    Fragment f = (Fragment)i.next();

                    if (!"hidden".equals(f.getState()))
                    {
                        stack.push(f);
                    }
                }

                // Walk through the Fragment tree, and start rendering "portlet" type
                // fragment
                while (!stack.isEmpty())
                {
                    currentFragment = (Fragment)stack.pop();

                    if (checkAccess(context,
                                    ((currentFragment.getAcl()!=null)?currentFragment.getAcl():acl),
                                    "render"))
                    {
                        if (currentFragment.getType().equals("portlet"))
                        {
                        	// make the page aggreator less fragile
                        	// by preventing failed rendering from screwing up the
                        	// whole process
                           try
                            {
                                 renderer.render(currentFragment,context);
                            }
                            catch (Exception e)
                            {
                                log.error("Failed to render portlet \""+currentFragment+"\": "+e.toString());
                            }
                        }

                        // push the children frgaments on the rendering stack
                        for(Iterator i = currentFragment.getFragments().iterator(); i.hasNext();)
                        {
                            Fragment f = (Fragment)i.next();

                            if (!"hidden".equals(f.getState()))
                            {
                                stack.push(f);
                            }
                        }
                    }
                    else
                    {
                        log.warn("Access denied RENDER fragment "+currentFragment);
                    }
                }
            }

            // Retrieves the content dispatcher appropriate for sequential
            // or parallel rendering

            ContentDispatcher dispatcher = renderer.getDispatcher(context,(strategy==STRATEGY_PARALLEL));


            // Now synchronously trigger the rendering of the whole page
            renderer.renderNow(page.getRootFragment(), context);

            // DEBUG Testing: Use ContentDispatcher to display all children
            // of root fragment
/*
            for(Iterator i = page.getRootFragment().getFragments().iterator(); i.hasNext();)
            {
                Fragment fragment = (Fragment)i.next();

                if (!"hidden".equals(fragment.getState()))
                {
                    dispatcher.include(fragment, context.getRequest(), context.getResponse());
                }
            }
*/            
        }
        else
        {
            log.warn("Access denied RENDER page "+page);
        }
        
    }

    public boolean checkAccess(RequestContext context, String acl, String action)
    {
        // This methid needs to be moved a secuity module.
        // Does nothing right now
        return true;
    }
}
