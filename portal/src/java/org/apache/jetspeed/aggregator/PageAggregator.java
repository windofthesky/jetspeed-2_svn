/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *     "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" or
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.aggregator;

import java.util.Iterator;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.profiler.ProfilerService;
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
/*
        ProfileLocator locator = context.getProfileLocator();
        if (null == locator)
        {
            throw new JetspeedException("Failed to find ProfileLocator in BasicAggregator.build");
        }
        Page page = Profiler.getPage(locator);
        if (null == page)
        {
            throw new JetspeedException("Failed to find PSML Pin BasicAggregator.build");
        }
*/
        //DEBUG CODE: use this to test a specific page
        Page page = org.apache.jetspeed.services.page.PageManager.getPage("p001");

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
                            renderer.render(currentFragment,context);
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
//            renderer.renderNow(page.getRootFragment(),context);

            // DEBUG Testing: Use ContentDispatcher to display all children
            // of root fragment

            for(Iterator i = page.getRootFragment().getFragments().iterator(); i.hasNext();)
            {
                Fragment fragment = (Fragment)i.next();

                if (!"hidden".equals(fragment.getState()))
                {
                    dispatcher.include(fragment, context.getRequest(), context.getResponse());
                }
            }
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
