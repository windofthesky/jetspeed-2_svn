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
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.registry.JetspeedPortletRegistry;
import org.apache.pluto.om.portlet.PortletDefinition;

import org.apache.jetspeed.container.PortletContainerFactory;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Basic Aggregator, nothing complicated. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class BasicAggregator extends BaseService 
    implements Aggregator
{
    private final static Log log = LogFactory.getLog(BasicAggregator.class);
    private final static String DEFAULT_STRATEGY = "strategy.default";

    public  final static int STRATEGY_SEQUENTIAL = 0;
    public  final static int STRATEGY_PARALLEL = 1;
    private final static String CONFIG_STRATEGY_SEQUENTIAL = "sequential";
    private final static String CONFIG_STRATEGY_PARALLEL = "parallel"; 
    private int strategy = STRATEGY_SEQUENTIAL;
    

    /**
     * This is the early initialization method called by the
     * Turbine <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public void init() throws InitializationException
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

        // initialization done
        setInit(true);

     }

    private void initConfiguration() throws InitializationException
    {
        String defaultStrategy = getConfiguration().getString(DEFAULT_STRATEGY, 
                                                        CONFIG_STRATEGY_SEQUENTIAL);
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
     * This is the shutdown method called by the
     * Turbine <code>Service</code> framework
     */
    public void shutdown()
    {
    }

    /**
     * Builds the portlet set defined in the context into a portlet tree.
     *
     * @return Unique Portlet Entity ID
     */     
    public void build(RequestContext request)
        throws JetspeedException
    {
        Profile profile = request.getProfile();
        if (null == profile)
        {
            throw new JetspeedException("Failed to find Profile in BasicAggregator.build");
        }
        PSMLDocument document = profile.getDocument();
        if (null == document)
        {
            throw new JetspeedException("Failed to find PSML Document in BasicAggregator.build");
        }
        Portlets portlets = document.getPortlets();
        if (null == portlets)
        {
            throw new JetspeedException("Failed to find Root Portlets Collection in BasicAggregator.build");
        }

        PortletContainer container;
        try
        {        
            container = PortletContainerFactory.getPortletContainer();
        }
        catch (PortletContainerException e)
        {
            throw new JetspeedException("Failed to get PortletContainer: " + e);            
        }

        for (Iterator eit = portlets.getEntriesIterator(); eit.hasNext(); )
        {
            Entry psmlEntry = (Entry)eit.next();

            // 
            // Load Portlet from registry
            // 
            System.out.println("*** Getting portlet from registry: " + psmlEntry.getName());            
            PortletDefinition portletDefinition = JetspeedPortletRegistry.getPortletDefinitionByUniqueName(psmlEntry.getParent());
            if (portletDefinition == null)
            {
                throw new JetspeedException("Failed to load: " + psmlEntry.getName() + " from registry");
            }

            //
            // create the portlet window and render the portlet
            //
            try
            {
                PortletWindow portletWindow = PortletWindowFactory.getWindow(portletDefinition, 
                                                                             psmlEntry.getName());                
                container.renderPortlet(portletWindow, request.getRequest(), request.getResponse());                
            }
            catch (Throwable t)
            {
                t.printStackTrace();                
                log.error("Failed to service portlet, portlet exception: " +  t);
                break;
            }
        }
    }
    
}