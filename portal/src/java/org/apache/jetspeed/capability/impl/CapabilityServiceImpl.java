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
package org.apache.jetspeed.capability.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.Capability;
import org.apache.jetspeed.capability.MimeType;
import org.apache.jetspeed.capability.MediaType;
import org.apache.jetspeed.capability.CapabilityService;
import org.apache.jetspeed.capability.Client;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;

import org.apache.jetspeed.persistence.LookupCriteria;

/**
 * CapabilityServiceImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public class CapabilityServiceImpl
    extends BaseCommonService
    implements CapabilityService
{
    private PersistencePlugin plugin;

    private PersistencePlugin originalPlugin;

    private String originalAlias;

    private static final Log log =
        LogFactory.getLog(CapabilityServiceImpl.class);

    public static final String DEFAULT_AGENT = "Mozilla/4.0";

    public static final String AGENT_XML = "agentxml/1.0";

    // Cache for the capability maps
    Hashtable capabilityMapCache = null;

    private Collection clients = null;

    private Class clientClass;
    private Class capabilityClass;
    private Class mimeTypeClass;
    private Class mediaTypeClass;

    /**
     * <p>
     * init
     * </p>
     * Initialize CapabilityService
     * @see org.apache.jetspeed.services.capability.CapabilityService#init()
     * @throws CPSInitializationException
     */
    public void init() throws CPSInitializationException
    {
        if (!isInitialized())
        {
            PersistenceService ps =
                (PersistenceService) CommonPortletServices.getPortalService(
                    PersistenceService.SERVICE_NAME);
            String pluginName =
                getConfiguration().getString(
                    "persistence.plugin.name",
                    "jetspeed");

            plugin = ps.getPersistencePlugin(pluginName);

            // Create classes
            clientClass = createClass("client.impl");
            capabilityClass = createClass("capability.impl");
            mimeTypeClass = createClass("mimetype.impl");
            mediaTypeClass = createClass("mediatype.impl");

            capabilityMapCache = new Hashtable();

            setInit(true);
        }
    }

    /**
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    private Class createClass(String classDescriptor)
        throws CPSInitializationException
    {
        String className = getConfiguration().getString(classDescriptor, null);

        if (null == className)
        {
            throw new CPSInitializationException("Factory class properties not found in configuration.");
        }

        try
        {
            return clientClass = Class.forName(className);
        } catch (ClassNotFoundException e)
        {
            throw new CPSInitializationException(
                "Could not preload client implementation class.",
                e);
        }
    }

    /**
     * @param userAgent Agent from the request
     * @see org.apache.jetspeed.services.capability.CapabilityService#getCapabilityMap(java.lang.String)
     */
    public CapabilityMap getCapabilityMap(String userAgent)
    {
        CapabilityMap map = null;
        boolean bClientFound = false;

        if (userAgent == null)
        {
            userAgent = DEFAULT_AGENT;
        }

        // Check the cache if we have already a capability map for
        // the given Agent
        map = (CapabilityMap) capabilityMapCache.get(userAgent);

        if (map != null)
        {
            // Entry found
            return map;
        }

        while (!bClientFound)
        {
            Client entry = findClient(userAgent);

            if (entry == null)
            {
                if (userAgent.equals(DEFAULT_AGENT))
                {
                    log.error(
                        "CapabilityMap: Default agent not found in Client Registry !");

                    // Stop searching -- event the default userAgent can't be found
                    bClientFound = true;
                } else
                {
                    // Retry with the default Agent
                    if (log.isDebugEnabled())
                    {
                        log.debug(
                            "CapabilityMap: useragent "
                                + userAgent
                                + "unknown, falling back to default");
                    }

                    // Use default Client
                    userAgent = DEFAULT_AGENT;
                }
            } else
            {
                // Found Client entry start populating the capability map.
                map = new CapabilityMapImpl();

                // Add client to CapabilityMap
                map.setClient(entry);

                // Add capabilities
                Iterator capabilities = entry.getCapabilities().iterator();
                while (capabilities.hasNext())
                {
                    map.addCapability((Capability) capabilities.next());
                }

                Collection mediatypes =
                    getMediaTypesForMimeTypes(entry.getMimetypes().iterator());

                // Add Mimetypes to map
                Iterator mimetypes = entry.getMimetypes().iterator();
                while (mimetypes.hasNext())
                {
                    map.addMimetype((MimeType) mimetypes.next());
                }

                Iterator media = mediatypes.iterator();
                while (media.hasNext())
                {
                    map.addMediaType((MediaType) media.next());
                }

                // Add map to cache
                capabilityMapCache.put(userAgent, map);

                return map;
            }

        }

        return map;
    }

    /**
     * Returns the client which matches the given useragent string.
     *
     * @param useragent     the useragent to match
     * @return the found client or null if the user-agent does not match any
     *  defined client
     * @see org.apache.jetspeed.capability.CapabilityService#findClient(java.lang.String)
     */

    public Client findClient(String userAgent)
    {
        Client clientEntry = null;
        Iterator clients = getClients();

        if (log.isDebugEnabled())
        {
            log.debug(
                "ClientRegistry: Looking for client with useragent :"
                    + userAgent);
        }

        while (clients.hasNext())
        {
            Client client = (Client) clients.next();
            if (client.getUserAgentPattern() != null)
            {
                try
                {
                    // Java 1.4 has regular expressions build in
                    String exp = client.getUserAgentPattern();
                    //RE r = new RE(client.getUserAgentPattern());
                    //r.setMatchFlags(RE.MATCH_CASEINDEPENDENT);
                    //if (r.match(userAgent))
                    if (userAgent.matches(exp))
                    {

                        if (log.isDebugEnabled())
                        {
                            log.debug(
                                "Client: "
                                    + userAgent
                                    + " matches "
                                    + client.getUserAgentPattern());
                        }

                        return client;
                    } else
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug(
                                "Client: "
                                    + userAgent
                                    + " does not match "
                                    + client.getUserAgentPattern());
                        }
                    }
                } catch (java.util.regex.PatternSyntaxException e)
                {
                    String message =
                        "CapabilityServiceImpl: UserAgentPattern not valid : "
                            + client.getUserAgentPattern()
                            + " : "
                            + e.getMessage();
                    log.error(message, e);
                }
            }
        }

        return clientEntry;
    }

    /* 
     * @see org.apache.jetspeed.capability.CapabilityService#getClients()
     */
    public Iterator getClients()
    {
        if (null == clients)
        {
            this.clients = plugin.getExtent(ClientImpl.class);
        }

        return this.clients.iterator();
    }

    /* 
     * @see org.apache.jetspeed.capability.CapabilityService#getMediaTypesForMimeTypes(java.util.Iterator)
     */
    public Collection getMediaTypesForMimeTypes(Iterator mimetypes)
    {
        //Find the MediaType by matching the Mimetype
        LookupCriteria criteria = plugin.newLookupCriteria();

        Vector temp = new Vector();
        // Add Mimetypes to map and create query
        while (mimetypes.hasNext())
        {
            MimeType mt = (MimeType) mimetypes.next();
            
            // Add mimetype to query
            // Note: mimetypes is a member of MediaTypeImpl
            // criteria.addEqualTo("mimetypes.name", mt.getName());
            //stuff.add(new Integer(mt.getMimetypeId()));
            temp.add(mt.getName());
        }
        criteria.addIn("mimetypes.name", temp);

        Collection co = plugin.getCollectionByQuery(
            mediaTypeClass,
            plugin.generateQuery(mediaTypeClass, criteria));
            
        if (co.isEmpty())
        {            
            System.out.println("collection is empty");
            MediaType mt = getMediaType("html");
            Vector v = new Vector();
            v.add(mt);
            return v;
        }
        System.out.println("collection is NOT empty");
                
        return co;                   
    }

    /* 
     * @see org.apache.jetspeed.capability.CapabilityService#deleteCapabilityMapCache()
     */
    public void deleteCapabilityMapCache()
    {
        capabilityMapCache.clear();
        clients = null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.capability.CapabilityService#getMediaType(java.lang.String)
     */
    public MediaType getMediaType(String mediaType)
    {
        LookupCriteria criteria = plugin.newLookupCriteria();
        criteria.addEqualTo("name", mediaType);
        Object query = plugin.generateQuery(mediaTypeClass, criteria);        
        return (MediaType) plugin.getObjectByQuery(mediaTypeClass, query);
    }
}
