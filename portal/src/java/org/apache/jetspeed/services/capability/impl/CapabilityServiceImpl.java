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
package org.apache.jetspeed.services.capability.impl;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.om.registry.ClientEntry;
import org.apache.jetspeed.om.registry.impl.ClientEntryImpl;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.services.capability.CapabilityService;
import org.apache.regexp.RE;

/**
 * CapabilityServiceImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class CapabilityServiceImpl extends BaseCommonService implements CapabilityService
{
    private PersistencePlugin plugin;

    private PersistencePlugin originalPlugin;

    private String originalAlias;

    private static final Log log = LogFactory.getLog(CapabilityServiceImpl.class);

    public static final String DEFAULT_AGENT = "Mozilla/4.0";

    public static final String AGENT_XML = "agentxml/1.0";

    private Collection clients = null;

    private Class clientClass;

    /**
     *  
     * <p>
     * init
     * </p>
     * 
     * @see org.apache.fulcrum.Service#init()
     * @throws CPSInitializationException
     */
    public void init() throws CPSInitializationException
    {
        if (!isInitialized())
        {
            PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
            String pluginName = getConfiguration().getString("persistence.plugin.name", "jetspeed");

            plugin = ps.getPersistencePlugin(pluginName);

            String className = getConfiguration().getString("client.impl", null);

            if (null == className)
            {
                throw new CPSInitializationException("Factory class properties not found in configuration.");
            }

            try
            {
                clientClass = createClass(className);
            }
            catch (ClassNotFoundException e)
            {
                throw new CPSInitializationException("Could not preload client implementation class.", e);
            }

            setInit(true);
        }
    }

    /**
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    private Class createClass(String className) throws ClassNotFoundException
    {
        return Class.forName(className);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.capability.CapabilityService#getCapabilityMap(java.lang.String)
     */
    public CapabilityMap getCapabilityMap(String userAgent)
    {
        CapabilityMap map = null;

        if (userAgent == null)
        {
            userAgent = DEFAULT_AGENT;
        }

        ClientEntry entry = findClient(userAgent);

        if (entry == null)
        {
            if (userAgent.equals(DEFAULT_AGENT))
            {
                log.error("CapabilityMap: Default agent not found in Client Registry !");
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("CapabilityMap: useragent " + userAgent + "unknown, falling back to default");
                }
                // LEFT OFF HERE: map = getDefaultCapabilityMap();
            }
        }
        else
        {
            // LEFT OFF HERE: map = new BaseCapabilityMap(userAgent, entry);
        }

        if (log.isDebugEnabled())
        {
            log.debug("CapabilityMap: User-agent: " + userAgent + " mapped to " + map);
        }

        return map;
    }

    /**
     * Returns the client which matches the given useragent string.
     *
     * @param useragent     the useragent to match
     * @return the found client or null if the user-agent does not match any
     *  defined client
     */
    public ClientEntry findClient(String userAgent)
    {
        ClientEntry clientEntry = null;
        Iterator clients = getClients();

        if (log.isDebugEnabled())
        {
            log.debug("ClientRegistry: Looking for client with useragent :" + userAgent);
        }

        while (clients.hasNext())
        {
            ClientEntry client = (ClientEntry) clients.next();
            if (client.getUseragentpattern() != null)
            {
                try
                {
                    RE r = new RE(client.getUseragentpattern());
                    r.setMatchFlags(RE.MATCH_CASEINDEPENDENT);

                    if (r.match(userAgent))
                    {

                        if (log.isDebugEnabled())
                        {
                            log.debug("ClientRegistry: " + userAgent + " matches " + client.getUseragentpattern());
                        }

                        return client;
                    }
                    else
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("ClientRegistry: " + userAgent + " does not match " + client.getUseragentpattern());
                        }
                    }
                }
                catch (org.apache.regexp.RESyntaxException e)
                {
                    String message =
                        "ClientRegistryService: UserAgentPattern not valid : "
                            + client.getUseragentpattern()
                            + " : "
                            + e.getMessage();
                    log.error(message, e);
                }
            }
        }

        return clientEntry;
    }

    /**
     * @return
     */
    public Iterator getClients()
    {
        if (null == clients)
        {
            this.clients = plugin.getExtent(ClientEntryImpl.class);
        }

        return this.clients.iterator();
    }
}
