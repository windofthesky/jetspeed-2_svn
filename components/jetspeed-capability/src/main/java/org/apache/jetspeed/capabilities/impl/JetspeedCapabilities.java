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
package org.apache.jetspeed.capabilities.impl;

import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.capabilities.CapabilitiesException;
import org.apache.jetspeed.capabilities.Capability;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.capabilities.Client;
import org.apache.jetspeed.capabilities.MediaType;
import org.apache.jetspeed.capabilities.MimeType;
import org.apache.jetspeed.capabilities.UnableToBuildCapabilityMapException;
import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;

/**
 * Jetspeed Capabilities
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public class JetspeedCapabilities extends InitablePersistenceBrokerDaoSupport implements Capabilities ,BeanFactoryAware 
{
    private static final Logger log =
        LoggerFactory.getLogger(JetspeedCapabilities.class);

    public static final String DEFAULT_AGENT = "Mozilla/4.0";

    public static final String AGENT_XML = "agentxml/1.0";
    
    public static final int MAX_CACHE_SIZE = 500;

    // Cache for the capability maps
    private Hashtable capabilityMapCache = new Hashtable();
    private LinkedList capabilityMapCacheKeyList = new LinkedList();

    private Collection clients = null;

    /**
     * added support for bean factory to create profile rules
     */
    private BeanFactory beanFactory;

    /** named bean references */
    private String clientBeanName; 
    private String capabilityBeanName; 
    private String mimeTypeBeanName; 
    private String mediaTypeBeanName; 

	   private Class clientClass;
	    private Class capabilityClass;
	    private Class mimeTypeClass;
	    private Class mediaTypeClass;
    
    
    public JetspeedCapabilities(String repositoryPath, String clientBeanName, String mediaTypeBeanName, String mimeTypeBeanName, String capabilityBeanName)
    {
        super(repositoryPath);
        this.clientBeanName =  clientBeanName;
        this.capabilityBeanName =  capabilityBeanName;
        this.mimeTypeBeanName =  mimeTypeBeanName;
        this.mediaTypeBeanName =  mediaTypeBeanName;
   }
    
    /**
     * Create a JetspeedProfiler with properties. Expected properties are:
     * 
     * 	   defaultRule   = the default profiling rule
     *     anonymousUser = the name of the anonymous user
     *     persistenceStoreName = The name of the persistence persistenceStore component to connect to  
     *     services.profiler.locator.impl = the pluggable Profile Locator impl
     *     services.profiler.principalRule.impl = the pluggable Principal Rule impl
     *     services.profiler.profilingRule.impl = the pluggable Profiling Rule impl
     *      
     * @param repositoryPath  The repositoryPath
     * @param properties  Properties for this component described above
     * @deprecated As of release 2.1, property-based class references replaced
     *             by container managed bean factory
     */
    public JetspeedCapabilities(String repositoryPath, Properties properties)
	{
        super(repositoryPath);
    }
    /*
     * Method called automatically by Spring container upon initialization
     * 
     * @param beanFactory automatically provided by framework @throws
     * BeansException
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        this.beanFactory = beanFactory;
    }


	    private Class getClientClass() throws ClassNotFoundException
	    {
	    	if (clientClass == null)
	    	{
	    		clientClass = createClient(null).getClass();
	    	}
	    	return clientClass;
	    }
	 
	    private Class getMimeTypeClass() throws ClassNotFoundException
	    {
	    	if (mimeTypeClass == null)
	    	{
	    		mimeTypeClass = this.createMimeType(null).getClass();
	    	}
	    	return mimeTypeClass;
	    }
	    private Class getCapabilityClass()throws ClassNotFoundException
	    {
	    	if (capabilityClass == null)
	    	{
	    		capabilityClass = this.createCapability(null).getClass();
	    	}
	    	return capabilityClass;
	    }

	    private Class getMediaTypeClass()throws ClassNotFoundException
	    {
	    	if (mediaTypeClass == null)
	    	{
	    		mediaTypeClass = this.createMediaType(null).getClass();
	    	}
	    	return mediaTypeClass;
	    }
    

    /**
     * @param userAgent Agent from the request
     * @throws UnableToBuildCapabilityMapException
     */
    public CapabilityMap getCapabilityMap(String userAgent) throws UnableToBuildCapabilityMapException
    {        
        CapabilityMap map = null;
        boolean bClientFound = false;
        String defaultAgent = null;

        if (userAgent == null)
        {
            userAgent = DEFAULT_AGENT;
        }

        // Check the cache if we have already a capability map for
        // the given Agent
        synchronized (capabilityMapCache)
        {
            map = (CapabilityMap) capabilityMapCache.get(userAgent);
            if (map != null)
            {
                capabilityMapCacheKeyList.remove(userAgent);
                capabilityMapCacheKeyList.addFirst(userAgent);
            }
        }

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
                    defaultAgent = userAgent;
                    userAgent = DEFAULT_AGENT;
                }
            } else
            {
                bClientFound = true;

                try
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

                    // Add Mimetypes to map
                    Iterator mimetypes = entry.getMimetypes().iterator();
                    while (mimetypes.hasNext())
                    {
                        map.addMimetype((MimeType) mimetypes.next());
                    }

                    // Add Mediatypes for Mimetype to map
                    Collection mediatypes =
                        getMediaTypesForMimeTypes(entry.getMimetypes().iterator());
                    Iterator media = mediatypes.iterator();
                    while (media.hasNext())
                    {
                        map.addMediaType((MediaType) media.next());
                    }

                    // Validate preferred Mimetype
                    MimeType mimeTypeEntry = map.getPreferredType();
                    if (mimeTypeEntry == null)
                    {
                        throw new RuntimeException("Unable to get preferred Mimetype for client: "+entry.getName());                        
                    }

                    // Set preferred Mediatype for Mimetype
                    MediaType mediaTypeEntry = getMediaTypeForMimeType(mimeTypeEntry.getName());
                    if (mediaTypeEntry == null)
                    {
                        throw new RuntimeException("Unable to find preferred Mediatype for Mimetype/client: "+mimeTypeEntry.getName()+"/"+entry.getName());
                    }
                    map.setPreferredMediaType(mediaTypeEntry);

                    // Add map to cache
                    synchronized (capabilityMapCache)
                    {
                        if (capabilityMapCache.put(userAgent, map) != null)
                        {
                            capabilityMapCacheKeyList.remove(userAgent);
                        }
                        capabilityMapCacheKeyList.addFirst(userAgent);
                        if (defaultAgent != null)
                        {
                            if (capabilityMapCache.put(defaultAgent, map) != null)
                            {
                                capabilityMapCacheKeyList.remove(defaultAgent);
                            }
                            capabilityMapCacheKeyList.addFirst(defaultAgent);
                        }
                        while (capabilityMapCache.size() > MAX_CACHE_SIZE)
                        {
                            String reapAgent = (String)capabilityMapCacheKeyList.removeLast();
                            capabilityMapCache.remove(reapAgent);
                        }
                    }
                }
                catch (Exception e)
                {
                    log.error("Unable to build capability map for "+userAgent+": "+e, e);
                    map = null;
                }
            }

        }
        
        if(map != null)
        {
               return map;
        }
        else
        {
            throw new UnableToBuildCapabilityMapException("We were unable to build a capability map for the agent, "+userAgent+
                                ".  This might be an indiciation that the capability database has not been correctly initialized.");
        }
    }

    /**
     * Returns the client which matches the given useragent string.
     *
     * @param userAgent the user agent to match
     * @return the found client or null if the user-agent does not match any
     *  defined client
     * @see Capabilities#findClient(String)
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
                    String exp = client.getUserAgentPattern();
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
     * @see org.apache.jetspeed.capabilities.CapabilityService#getClients()
     */
    public Iterator<Client> getClients()
    {
        if (null == clients)
        {
			try
			{
				QueryByCriteria query = QueryFactory.newQuery(getClientClass(), new Criteria());
	            query.addOrderByAscending("evalOrder");
	            this.clients = getPersistenceBrokerTemplate().getCollectionByQuery(query);
	    	}
	    	catch (Exception e)
	    	{
	            String message =
	                "CapabilityServiceImpl: getClients query used invalid class ";
	            log.error(message, e);
	            return null;
	    	}
        }

        return this.clients.iterator();
    }

    /* 
     * @see org.apache.jetspeed.capabilities.CapabilityService#getMediaTypesForMimeTypes(java.util.Iterator)
     */
    public Collection<MediaType> getMediaTypesForMimeTypes(Iterator mimetypes)
    {
        //Find the MediaType by matching the Mimetype
        
        Criteria filter = new Criteria();

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
        
        Collection co = null;
        if (temp.size() > 0)
        {
			try
			{
				filter.addIn("mimetypes.name", temp);
			            QueryByCriteria query = QueryFactory.newQuery(getMediaTypeClass(), filter);
			            co = getPersistenceBrokerTemplate().getCollectionByQuery(query);            
			}
			catch (Exception e)
			{
			    String message =
			        "CapabilityServiceImpl: getMediaTypesForMimeTypes -> getMediaTypeClass query used invalid class ";
			    log.error(message, e);
 
			}
        }
        if (co == null || co.isEmpty())
        {
            MediaType mt = getMediaType("html");
            Vector v = new Vector();
            v.add(mt);
            return v;
        }
        return co;
    }

    /* 
     * @see org.apache.jetspeed.capabilities.CapabilityService#deleteCapabilityMapCache()
     */
    public void deleteCapabilityMapCache()
    {
        synchronized (capabilityMapCache)
        {
            capabilityMapCache.clear();
            capabilityMapCacheKeyList.clear();
        }
        clients = null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.capabilities.CapabilityService#getMediaType(java.lang.String)
     */
    public MediaType getMediaType(String mediaType)
    {        
    	try
    	{
	        Criteria filter = new Criteria();        
	        filter.addEqualTo("name", mediaType);
	        QueryByCriteria query = QueryFactory.newQuery(getMediaTypeClass(), filter);
	        return (MediaType) getPersistenceBrokerTemplate().getObjectByQuery(query);                   
		}
		catch (Exception e)
		{
	        String message =
	            "CapabilityServiceImpl: getMediaType query used invalid class ";
	        log.error(message, e);
	        return null;
		}
    }

    /**
     * getMediaTypeForMimeType
     * @param mimeTypeName to use for lookup
     * @return MediaTypeEntry that matches the lookup in the MEDIATYPE_TO_MIMETYPE table
     */
    public MediaType getMediaTypeForMimeType(String mimeTypeName)
    {               
        //Find the MediaType by matching the Mimetype
    	Collection mediaTypeCollection = null;
		try
		{
	        Criteria filter = new Criteria();       
	        filter.addEqualTo("mimetypes.name", mimeTypeName);
	        
	        QueryByCriteria query = QueryFactory.newQuery(getMediaTypeClass(), filter);
	        mediaTypeCollection = getPersistenceBrokerTemplate().getCollectionByQuery(query);                    
		}
		catch (Exception e)
		{
	        String message =
	            "CapabilityServiceImpl: getMediaTypeForMimeType query used invalid class ";
	        log.error(message, e);
	        return null;
		}
        
        Iterator mtIterator = mediaTypeCollection.iterator();
        if (mtIterator.hasNext())
        {
            return (MediaType) mtIterator.next();
        } else
        {
            return null;
        }
    }

    /**
     * Obtain an iterator of all existing capabilities.
     * @return Returns an iterator for all existing Capabilities of type <code>Capability</code>
     */
    public Iterator<Capability> getCapabilities()
    {
    	QueryByCriteria query = null;
		try
		{
			query = QueryFactory.newQuery(getCapabilityClass(), new Criteria());
		}
		catch (Exception e)
		{
	        String message =
	            "CapabilityServiceImpl: getCapabilities query used invalid class ";
	        log.error(message, e);
	        return null;
		}
        query.addOrderByAscending("name");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query).iterator();        
    }

    /**
     * Obtain an iterator of all existing mime types.
     * @return Returns an iterator for all existing Mime Types of type <code>MimeType</code>
     */
    public Iterator<MimeType> getMimeTypes()
    {
		try
		{
			QueryByCriteria query = QueryFactory.newQuery(getMimeTypeClass(), new Criteria());
	        query.addOrderByAscending("name");
	        return getPersistenceBrokerTemplate().getCollectionByQuery(query).iterator();                
		}
		catch (Exception e)
		{
	        String message =
	            "CapabilityServiceImpl: getMimeTypes query used invalid class ";
	        log.error(message, e);
	        return null;
		}
    }
    
    /**
     * Obtain an iterator of all existing media types.
     * @return Returns an iterator for all existing media types of type <code>MediaType</code>
     */
    public Iterator<MediaType> getMediaTypes()
    {
		try
		{
			QueryByCriteria query = QueryFactory.newQuery(getMediaTypeClass(), new Criteria());
	        query.addOrderByAscending("name");
	        return getPersistenceBrokerTemplate().getCollectionByQuery(query).iterator();                        
		}
		catch (Exception e)
		{
	        String message =
	            "CapabilityServiceImpl: getMediaTypes query used invalid class ";
	        log.error(message, e);
	        return null;
		}
    }
    /* 
     * @see org.apache.jetspeed.capabilities.Capabilities#getMimeTypeBeanName()
     */
	public String getMimeTypeBeanName() {
		return mimeTypeBeanName;
	}

	/* 
     * @see org.apache.jetspeed.capabilities.Capabilities#setMimeTypeBeanName(String)
     */
	public void setMimeTypeBeanName(String mimeTypeBeanName) {
		this.mimeTypeBeanName = mimeTypeBeanName;
	}

	   /* 
     * @see org.apache.jetspeed.capabilities.Capabilities#getClientBeanName()
     */
	public String getClientBeanName() {
		return clientBeanName;
	}

	/* 
     * @see org.apache.jetspeed.capabilities.Capabilities#setClientBeanName(String)
     */
	public void setClientBeanName(String clientBeanName) {
		this.clientBeanName = clientBeanName;
	}

	   /* 
     * @see org.apache.jetspeed.capabilities.Capabilities#getMediaTypeBeanName()
     */
	public String getMediaTypeBeanName() {
		return mediaTypeBeanName;
	}

	/* 
     * @see org.apache.jetspeed.capabilities.Capabilities#setMediaTypeBeanName(String)
     */
	public void setMediaTypeBeanName(String mediaTypeBeanName) {
		this.mediaTypeBeanName = mediaTypeBeanName;
	}

	/* 
     * @see org.apache.jetspeed.capabilities.Capabilities#getCapabilityBeanName()
     */
	public String getCapabilityBeanName() {
		return capabilityBeanName;
	}

	/* 
     * @see org.apache.jetspeed.capabilities.Capabilities#setCapabilityBeanName(String)
     */
	public void setCapabilityBeanName(String capabilityBeanName) {
		this.capabilityBeanName = capabilityBeanName;
	}
    
	/* 
     * @see org.apache.jetspeed.capabilities.Capabilities#createMimeType(String)
     */
	public MimeType createMimeType(String mimeType)
	 throws ClassNotFoundException
	    {
		MimeType mimeTypeobj = null;
		if (mimeType != null)
		{
			//try to find it in space
			mimeTypeobj = this.getMimeType(mimeType);
			if (mimeTypeobj != null)
				return mimeTypeobj;
		}
        try
        {
        	mimeTypeobj = (MimeType) beanFactory.getBean(
                    this.mimeTypeBeanName, MimeType.class);
        	mimeTypeobj.setName(mimeType);
            return mimeTypeobj;
        } catch (Exception e)
        {
            log.error("Failed to create capability instance for " + this.mimeTypeBeanName 
                    + " error : " + e.getLocalizedMessage());
            throw new ClassNotFoundException("Spring failed to create the " + this.mimeTypeBeanName
                    + " mimeType bean.", e);
        }
	}
    

	/* 
     * @see org.apache.jetspeed.capabilities.Capabilities#createCapability(String)
     */
	public Capability createCapability(String capabilityName)	 throws ClassNotFoundException
	    {
		Capability capability = null;
		if (capabilityName != null)
		{
			//try to find it in space
			capability = this.getCapability(capabilityName);
			if (capability != null)
				return capability;
		}
        try
        {
        	capability = (Capability) beanFactory.getBean(
                    this.capabilityBeanName, Capability.class);
        	capability.setName(capabilityName);
            return capability;
        } catch (Exception e)
        {
            log.error("Failed to create capability instance for " + this.capabilityBeanName
                    + " error : " + e.getLocalizedMessage());
            throw new ClassNotFoundException("Spring failed to create the "
                    + " capability bean.", e);
        }
	}

	/* 
     * @see org.apache.jetspeed.capabilities.Capabilities#createMediaType(String)
     */
	public MediaType createMediaType(String mediaTypeName)	 throws ClassNotFoundException
	    {
		MediaType mediaType = null;
		if (mediaTypeName != null)
		{
			//try to find it in space
			mediaType = this.getMediaType(mediaTypeName);
			if (mediaType != null)
				return mediaType;
		}
        try
        {
        	mediaType = (MediaType) beanFactory.getBean(
                    this.mediaTypeBeanName, MediaType.class);
        	mediaType.setName(mediaTypeName);
            return mediaType;
        } catch (Exception e)
        {
            log.error("Failed to create mediaType instance for " + this.mediaTypeBeanName
                    + " error : " + e.getLocalizedMessage());
            throw new ClassNotFoundException("Spring failed to create the "
                    + " mediaType bean.", e);
        }
	}


	/* 
     * @see org.apache.jetspeed.capabilities.Capabilities#createClient(String)
     */
	public Client createClient(String clientName) throws ClassNotFoundException
	    {
		Client client = null;
		if (clientName != null)
		{
			//try to find it in space
			client = this.getClient(clientName);
			if (client != null)
				return client;
		}
        try
        {
        	client = (Client) beanFactory.getBean(
                    this.clientBeanName, Client.class);
        	client.setName(clientName);
            return client;
        } catch (Exception e)
        {
            log.error("Failed to create client instance for " + this.clientBeanName
                    + " error : " + e.getLocalizedMessage());
            throw new ClassNotFoundException("Spring failed to create the "
                    + " client bean.", e);
        }
	}
    /* (non-Javadoc)
     * @see org.apache.jetspeed.capabilities.MimeTypeservice#getCapability(java.lang.String)
     */
    public MimeType getMimeType(String mimeType)
    {
    	try
    	{
	        Criteria filter = new Criteria();        
	        filter.addEqualTo("name", mimeType);
	        QueryByCriteria query = QueryFactory.newQuery(getMimeTypeClass(), filter);
	        return (MimeType) getPersistenceBrokerTemplate().getObjectByQuery(query);
		}
		catch (Exception e)
		{
	        String message =
	            "MimeTypeserviceImpl: getCapability - query for getCapabilityClass failed ";
	        log.error(message, e);
	        return null;
	
		}

    }


    /* (non-Javadoc)
     * @see org.apache.jetspeed.capabilities.MimeTypeservice#getClientjava.lang.String)
     */
    public Client getClient(String clientName)
    {     
    	try
    	{
	        Criteria filter = new Criteria();        
	        filter.addEqualTo("name", clientName);
	        QueryByCriteria query = QueryFactory.newQuery(getClientClass(), filter);
	        return (Client) getPersistenceBrokerTemplate().getObjectByQuery(query);                   
		}
		catch (Exception e)
		{
	        String message =
	            "MimeTypeserviceImpl: getClient - query for getClientClass failed ";
	        log.error(message, e);
	        return null;
	
		}
   }
  

    /* (non-Javadoc)
     * @see org.apache.jetspeed.capabilities.MimeTypeservice#getCapability(java.lang.String)
     */
    public Capability getCapability(String capability)
    {      
    	try
    	{
    	
	        Criteria filter = new Criteria();        
	        filter.addEqualTo("name", capability);
	        QueryByCriteria query = QueryFactory.newQuery(getCapabilityClass(), filter);
	        return (Capability) getPersistenceBrokerTemplate().getObjectByQuery(query);                   
		}
		catch (Exception e)
		{
	        String message =
	            "MimeTypeserviceImpl: getCapability - query for getCapabilityClass failed ";
	        log.error(message, e);
	        return null;
	
		}
    }

    
	/* 
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.capabilities.Capabilities#storeMediaType(MediaType)
     */
    public void storeMediaType(MediaType mediaType) throws CapabilitiesException
    {

    	//TODO: change exception to better indicate cause
    	getPersistenceBrokerTemplate().store(mediaType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.capabilities.Capabilities#deleteMediaType(MediaType)
     */
    public void deleteMediaType(MediaType mediaType)
            throws CapabilitiesException
    {
    	//TODO: change exception to better indicate cause
        getPersistenceBrokerTemplate().delete(mediaType);
    }

	
	/* 
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.capabilities.Capabilities#storeCapability(MediaType)
     */
    public void storeCapability(Capability capability) throws CapabilitiesException
    {

    	//TODO: change exception to better indicate cause
    	getPersistenceBrokerTemplate().store(capability);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.capabilities.Capabilities#deleteCapability(Capability)
     */
    public void deleteCapability(Capability capability)
            throws CapabilitiesException
    {
    	//TODO: change exception to better indicate cause
        getPersistenceBrokerTemplate().delete(capability);
    }

	/* 
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.capabilities.Capabilities#storeMimeType(MimeType)
     */
    public void storeMimeType(MimeType mimeType) throws CapabilitiesException
    {

    	//TODO: change exception to better indicate cause
    	getPersistenceBrokerTemplate().store(mimeType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.capabilities.Capabilities#deleteMimeType(MimeType)
     */
    public void deleteMimeType(MimeType mimeType)
            throws CapabilitiesException
    {
    	//TODO: change exception to better indicate cause
        getPersistenceBrokerTemplate().delete(mimeType);
    }



	
	/* 
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.capabilities.Capabilities#storeClient(MediaType)
     */
    public void storeClient(Client client) throws CapabilitiesException
    {

    	//TODO: change exception to better indicate cause
    	getPersistenceBrokerTemplate().store(client);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.capabilities.Capabilities#deleteClient(Client)
     */
    public void deleteClient(Client client)
            throws CapabilitiesException
    {
    	//TODO: change exception to better indicate cause
        getPersistenceBrokerTemplate().delete(client);
    }
    
}
