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
package org.apache.jetspeed.capabilities;

import java.util.Collection;
import java.util.Iterator;

/**
 * Capabilities Component Interface
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public interface Capabilities 
{

    /**
     * Creates a Capability Map with Capabilities, Mimetypes and mediaTypes for the given UserAgentPattern
     * @param userAgent Agent from the request
     * @return CapabilityMap populated with Capabilities, Mimetypes and Mediatype
     * that match the userAgent.  Never returns <code>null</code>
     * @throws UnableToBuildCapabilityMapException  If a capability could not be created
     */
    CapabilityMap getCapabilityMap(String userAgent) throws UnableToBuildCapabilityMapException;

    /**
     * Obtain an iterator of all existing clients.
     * @return Returns an iterator for all existing Clients
     */
    Iterator getClients();

    /**
     * Finds a client for a given userAgentPattern
     * @param userAgent
     * @return Client that matches agent or null if no match is found
     *
     */
    Client findClient(String userAgent);

    /**
     * Returns a collection of MediaTypes that matches the MimeTypes defined in the mimetype parameter
     * @param Mimetype
     *
     * @return Collection of Mediatypes that matches the mimetypes
     */
    Collection getMediaTypesForMimeTypes(Iterator mimetypes);

    /**
     * Clears CapabilityMap cache
     * TODO: Roger, why is this on the public interface. It seems to be impl specific 
     */
    void deleteCapabilityMapCache();

    /**
     * Given a media type string, look up the corresponding media type object.
     * 
     * @param mediaType The string representation of a media type.
     * @return The found media type object or if not found, null.
     */
    MediaType getMediaType(String mediaType);

    /**
     * Given a Mimetype string lookup the corresponding media type object
     * @param mimeTypeName to use for lookup
     * @return MediaTypeEntry that matches the lookup in the MEDIATYPE_TO_MIMETYPE table
     */
    public MediaType getMediaTypeForMimeType(String mimeTypeName);

    /**
     * Given a capability string, look up the corresponding capability object.
     * 
     * @param capability The string representation of a capability.
     * @return The found capability object or if not found, null.
     */
    Capability getCapability(String capability);

    /**
     * Given a mime type string, look up the corresponding mime type object.
     * 
     * @param mimeType The string representation of a mime type.
     * @return The found mime type object or if not found, null.
     */
    MimeType getMimeType(String mimeType);
    /**
     * Given a client name, look up the corresponding client object.
     * 
     * @param clientName The name of the client.
     * @return The found client object or if not found, null.
     */
    Client getClient(String clientName);

    /**
     * Obtain an iterator of all existing capabilities.
     * @return Returns an iterator for all existing Capabilities of type <code>Capability</code>
     */
    Iterator getCapabilities();
    
    /**
     * Obtain an iterator of all existing mime types.
     * @return Returns an iterator for all existing Mime Types of type <code>MimeType</code>
     */
    Iterator getMimeTypes();
    
    /**
     * Obtain an iterator of all existing media types.
     * @return Returns an iterator for all existing media types of type <code>MediaType</code>
     */
    Iterator getMediaTypes();

    
    /**
     * Obtain the name of the CapabilityBean reference 
     * @return ref-id of the capability bean
     */
	public String getCapabilityBeanName();

    /**
     * Set the name of the CapabilityBean reference - used exclusively in IoC 
     * @param capabilityBeanName The ref-id of the capability bean.
     */
	public void setCapabilityBeanName(String capabilityBeanName);


    /**
     * Obtain the name of the ClientBean reference 
     * @return ref-id of the client bean
     */
	public String getClientBeanName();

    /**
     * Set the name of the ClientBean reference - used exclusively in IoC 
     * @param clientBeanName The ref-id of the client bean.
     */
	public void setClientBeanName(String clientBeanName);

    /**
     * Obtain the name of the Media Type reference 
     * @return ref-id of the media type bean
     */
	public String getMediaTypeBeanName();

	   /**
     * Set the name of the MediaType bean reference - used exclusively in IoC 
     * @param mediaTypeBeanName The ref-id of the mediaType bean.
     */
	public void setMediaTypeBeanName(String mediaTypeBeanName);

	  /**
     * Obtain the name of the Mime Type reference 
     * @return ref-id of the mime type bean
     */
	public String getMimeTypeBeanName();

	/**
     * Set the name of the MimeType bean reference - used exclusively in IoC 
     * @param mimeTypeBeanName The ref-id of the mimeType bean.
     */
	public void setMimeTypeBeanName(String mimeTypeBeanName);
		
	
	/**
     * Create a new capability in the system or return the existing one if already exists
     * @param capabilityName The string describing the capability
     * @return A new (or existing) capability
    */
	public Capability createCapability(String capabilityName) throws ClassNotFoundException;
    

	/**
     * Create a new mimetype in the system or return the existing one if already exists
     * @param mimeTypeName The string describing the mimeType
     * @return A new (or existing) MimeType
    */
	public MimeType createMimeType(String mimeTypeName)throws ClassNotFoundException;

	/**
     * Create a new mediaType in the system or return the existing one if already exists
     * @param mediaTypeName The string describing the mediaType
     * @return A new (or existing) MediaType
    */
	public MediaType createMediaType(String mediaTypeName)throws ClassNotFoundException;

	/**
     * Create a new client in the system or return the existing one if already exists
     * @param clientName The string describing the client
     * @return A new (or existing) client
    */
	public Client createClient(String clientName)throws ClassNotFoundException;


	
	/**
     * Save media type to backend storage
     * 
     * @param mediaType valid mediatype object
     */
    public void storeMediaType(MediaType mediaType) throws Exception;
    	//TODO: change exception to better indicate cause
 
	/**
     * delete existing media type from backend storage
     * 
     * @param mediaType valid mediatype object
     */
    public void deleteMediaType(MediaType mediaType)
            throws Exception;

	
	/**
     * Save capability to backend storage
     * 
     * @param capability valid capability object
     */
    public void storeCapability(Capability capability) throws Exception;

    /**
     * delete existing capability from backend storage
     * 
     * @param capability valid capability object
     */
    public void deleteCapability(Capability capability)
            throws Exception;

	/**
     * Save mime type to backend storage
     * 
     * @param mimeType valid mimetype object
     */
    public void storeMimeType(MimeType mimeType) throws Exception;
    	//TODO: change exception to better indicate cause
 
	/**
     * delete existing mime type from backend storage
     * 
     * @param mimeType valid mimetype object
     */
    public void deleteMimeType(MimeType mimeType)
            throws Exception;


	
	/**
     * Save client to backend storage
     * 
     * @param client valid Client object
     */
    public void storeClient(Client client) throws Exception;

    /**
     * delete existing client from backend storage
     * 
     * @param client valid client object
     */
    public void deleteClient(Client client)
            throws Exception;

}
