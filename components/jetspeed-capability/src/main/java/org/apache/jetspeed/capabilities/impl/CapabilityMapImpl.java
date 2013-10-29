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

import org.apache.jetspeed.capabilities.Capability;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.capabilities.Client;
import org.apache.jetspeed.capabilities.MediaType;
import org.apache.jetspeed.capabilities.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation for capabilityMap interface
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
class CapabilityMapImpl implements CapabilityMap
{
    private static Logger log =
        LoggerFactory.getLogger(JetspeedCapabilities.class);
    
    // Members
    private String userAgent; // User agent for request
    private Map<String,MimeType> mimeTypeMap = new HashMap<String,MimeType>(); // supported Mimetypes for Agent
    private Map<String,Capability> capabilityMap = new HashMap<String,Capability>();
    // supported Capabilities for Agent
    private Map<String,MediaType> mediaTypeMap = new HashMap<String,MediaType>(); // supported MediaTypes for Agent
    private Client client; // client for Agent
    private MediaType preferredMediaType; // Preferred MediaType for client.

    /**
        Sets the client for the CapabilityMap
    */
    public void setClient(Client client)
    {
        this.client = client;
    }

    /**
        Returns the Client for the CapabilityMap
    */
    public Client getClient()
    {
        return this.client;
    }

    /**
        Add capability to the CapabilityMap
    */
    public void addCapability(Capability capability)
    {	
    	if (capability != null) // avoid null due to duplicates in database 
    		this.capabilityMap.put(capability.getName(), capability);
    }

    /**
        Add Mimetype to the MimetypeMap
    */
    public void addMimetype(MimeType mimetype)
    {
    	if (mimetype != null) // avoid null due to duplicates in database
        this.mimeTypeMap.put(mimetype.getName(), mimetype);
    }

    /**
        Add MediaType to the MediaTypeMap
    */
    public void addMediaType(MediaType mediatype)
    {
    	if (mediatype != null) // avoid null due to duplicates in database
        this.mediaTypeMap.put(mediatype.getName(), mediatype);
    }

    /**
    Returns the preferred MIME type for the current user-agent
    */
    public MimeType getPreferredType()
    {
        // Return the value that matches the preferredMimeType defined in the Client
        int prefMimeTypeId = this.client.getPreferredMimeTypeId();

        MimeType mt = null;        
        Iterator e = this.mimeTypeMap.values().iterator();
        while (e.hasNext())
        {            
            mt = (MimeType) e.next();
            
            if (mt.getMimetypeId() == prefMimeTypeId)
                return mt;
        }
        log.error("Could not find preferred Mime Type for " + prefMimeTypeId);        

        // Should never reach this point. A preferred value needs to be set
        return null;
    }

    /**
          * Sets the preferred MediaType for this CapabilityMap
          * @param type
        */
    public void setPreferredMediaType(MediaType type)
    {
        this.preferredMediaType = type;
    }

    /**
    Returns the preferred media type for the current user-agent
    */
    public MediaType getPreferredMediaType()
    {
        return this.preferredMediaType;
    }

    /**
     * Returns an ordered list of supported media-types, from most preferred
     * to least preferred
     */
    public Iterator<MediaType> listMediaTypes()
    {
        return mediaTypeMap.values().iterator();
    }

    /**
    Returns the user-agent string
    */
    public String getAgent()
    {
        return this.userAgent;
    }

    /**
     * set userAgent
     */
    public void setAgent(String userAgent)
    {
        this.userAgent = userAgent;
    }

    /**
     * Checks to see if the current agent has the specified capability
     */
    public boolean hasCapability(int capability)
    {
        Iterator capabilities = capabilityMap.values().iterator();
        while (capabilities.hasNext())
        {
            if (((Capability) capabilities.next()).getCapabilityId()
                == capability)
            {
                return true;
            }
        }
        return false;
    }

    /**
     *  Checks to see if the current agent has the specified capability
     */
    public boolean hasCapability(String capability)
    {
        Iterator capabilities = capabilityMap.values().iterator();
        while (capabilities.hasNext())
        {
            if (((Capability) capabilities.next()).getName().equals(capability))
            {
                return true;
            }
        }
        return false;
    }

    /**
    Get the mime types that this CapabilityMap supports.
    */
    public Iterator<MimeType> getMimeTypes()
    {
        return mimeTypeMap.values().iterator();
    }

    /**
    Return true if this CapabilityMap supports the given MimeType
    */
    public boolean supportsMimeType(MimeType mimeType)
    {
        Iterator<MimeType> mimetypes = mimeTypeMap.values().iterator();
        while (mimetypes.hasNext())
        {
            if (mimetypes.next().getName().equals(mimeType.getName()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if this CapabilityMap supports the given media type
     *
     * @param media the name of a media type registered in the
     * MediaType registry
     *
     * @return true is the capabilities of this agent at least match those
     * required by the media type
     */
    public boolean supportsMediaType(String media)
    {
        Iterator<MediaType> mediatypes = mediaTypeMap.values().iterator();
        while (mediatypes.hasNext())
        {
            if (mediatypes.next().getName().equals(media))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a map -> string representation
     */
    public String toString()
    {
        return "";
    }

}
