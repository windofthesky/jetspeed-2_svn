/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.capability.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.capability.Capabilities;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.Client;
import org.apache.jetspeed.capability.MediaType;
import org.picocontainer.Startable;


import org.apache.jetspeed.capability.Capability;
import org.apache.jetspeed.capability.MimeType;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.Transaction;

/**
 * Jetspeed Capabilities
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public class JetspeedCapabilities implements Capabilities, Startable 
{
    private PersistenceStoreContainer pContainer;
    private String storeName = "jetspeed";

    private String originalAlias;

    private static final Log log =
        LogFactory.getLog(JetspeedCapabilities.class);

    public static final String DEFAULT_AGENT = "Mozilla/4.0";

    public static final String AGENT_XML = "agentxml/1.0";

    // Cache for the capability maps
    Hashtable capabilityMapCache = new Hashtable();

    private Collection clients = null;

    private Class clientClass = ClientImpl.class;
    private Class capabilityClass = CapabilityImpl.class;
    private Class mimeTypeClass = MimeTypeImpl.class;
    private Class mediaTypeClass = MediaTypeImpl.class;

    public JetspeedCapabilities(PersistenceStoreContainer pContainer)
    {
        this.pContainer = pContainer;        
    }
    
    /**
     * Create a JetspeedProfiler with properties. Expected properties are:
     * 
     * 	   defaultRule   = the default profiling rule
     *     anonymousUser = the name of the anonymous user
     *     storeName = The name of the persistence store component to connect to  
     *     services.profiler.locator.impl = the pluggable Profile Locator impl
     *     services.profiler.principalRule.impl = the pluggable Principal Rule impl
     *     services.profiler.profilingRule.impl = the pluggable Profiling Rule impl
     *      
     * @param pContainer  The persistence store container
     * @param properties  Properties for this component described above
     */
    public JetspeedCapabilities(PersistenceStoreContainer pContainer, Properties properties)
	{
        this.pContainer = pContainer;
        initModelClasses(properties);
    }
    
    private void initModelClasses(Properties properties)
	{
        String modelName = "";
        try
        {
	        if ((modelName = properties.getProperty("client.impl")) != null)
	        {
	            clientClass = Class.forName(modelName);
	        }
	        if ((modelName = properties.getProperty("capability.impl")) != null)
	        {
	            capabilityClass = Class.forName(modelName);
	        }
	        if ((modelName = properties.getProperty("mimetype.impl")) != null)
	        {
	            mimeTypeClass = Class.forName(modelName);
	        }
	        if ((modelName = properties.getProperty("mediatype.impl")) != null)
	        {
	            mediaTypeClass = Class.forName(modelName);
	        }	        	        
	        
        }
        catch (ClassNotFoundException e)
        {
            log.error("Model class not found: " + modelName);
        }
    }
    
    public void start()
	{
	}
    
    public void stop()
	{
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

                //Set preferred Mimetype
                MediaType mtEntry =
                    getMediaTypeForMimeType(map.getPreferredType().getName());

                map.setPreferredMediaType(mtEntry);

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
            PersistenceStore store = getPersistenceStore();            
            this.clients = store.getExtent(ClientImpl.class);
        }

        return this.clients.iterator();
    }

    /* 
     * @see org.apache.jetspeed.capability.CapabilityService#getMediaTypesForMimeTypes(java.util.Iterator)
     */
    public Collection getMediaTypesForMimeTypes(Iterator mimetypes)
    {
        //Find the MediaType by matching the Mimetype
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();        

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
        filter.addIn("mimetypes.name", temp);

        
        Object query = store.newQuery(mediaTypeClass, filter);
        
        Collection co = store.getCollectionByQuery(query);

        if (co.isEmpty())
        {
            MediaType mt = getMediaType("html");
            Vector v = new Vector();
            v.add(mt);
            return v;
        }
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
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();        
        filter.addEqualTo("name", mediaType);
        Object query = store.newQuery(mediaTypeClass, filter);
        return (MediaType) store.getObjectByQuery(query);
    }

    /**
     * getMediaTypeForMimeType
     * @param mimeType to use for lookup
     * @return MediaTypeEntry that matches the lookup in the MEDIATYPE_TO_MIMETYPE table
     */
    public MediaType getMediaTypeForMimeType(String mimeTypeName)
    {               
        //Find the MediaType by matching the Mimetype
        PersistenceStore store = getPersistenceStore();
        
        Filter filter = store.newFilter();        
        filter.addEqualTo("mimetypes.name", mimeTypeName);
        Object query = store.newQuery(mediaTypeClass, filter);        
        Collection mediaTypeCollection = store.getCollectionByQuery(query);                
        Iterator mtIterator = mediaTypeCollection.iterator();
        if (mtIterator.hasNext())
        {
            return (MediaType) mtIterator.next();
        } else
        {
            return null;
        }
    }
    
    protected PersistenceStore getPersistenceStore()
    {
        PersistenceStore store = pContainer.getStoreForThread(storeName);
        Transaction tx = store.getTransaction();
        if (!tx.isOpen())
        {
            tx.begin();
        }
        return store;
    }
    
}
