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

import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.Client;
import org.apache.jetspeed.capability.Capability;
import org.apache.jetspeed.capability.MediaType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jetspeed.capability.MimeType;

/**
 * Implementation for capabilityMap interface
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
class CapabilityMapImpl implements CapabilityMap
{
    // Members
    private String useragent; // User agent for request
    private Map mimeTypeMap = new HashMap(); // supported Mimetypes for Agent
    private Map capabilityMap = new HashMap();
    // supported Capabilities for Agent
    private Map mediaTypeMap = new HashMap(); // supported MediaTypes for Agent
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
        this.capabilityMap.put(capability.getName(), capability);
    }

    /**
        Add Mimetype to the MimetypeMap
    */
    public void addMimetype(MimeType mimetype)
    {
        this.mimeTypeMap.put(mimetype.getName(), mimetype);
    }

    /**
        Add MediaType to the MediaTypeMap
    */
    public void addMediaType(MediaType mediatype)
    {
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
        System.out.println("+++ NEVER " + prefMimeTypeId);        

        // Should never reach this point. A preferred value needs to be set
        return null; // TODO: NEVER RETURN NULL
    }

    /**
          * Sets the preferred MediaType for this CapabilityMap
          * @param MediaTypeEntry 
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
    public Iterator listMediaTypes()
    {
        return mediaTypeMap.values().iterator();
    }

    /**
    Returns the user-agent string
    */
    public String getAgent()
    {
        return this.useragent;
    }

    /**
     * set userAgent
     */
    public void setAgent(String userAgent)
    {
        this.useragent = userAgent;
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
            if (((Capability) capabilities.next()).getName() == capability)
            {
                return true;
            }
        }
        return false;
    }

    /**
    Get the mime types that this CapabilityMap supports.
    */
    public Iterator getMimeTypes()
    {
        return mimeTypeMap.values().iterator();
    }

    /**
    Return true if this CapabilityMap supports the given MimeType
    */
    public boolean supportsMimeType(MimeType mimeType)
    {
        Iterator mimetypes = mimeTypeMap.values().iterator();
        while (mimetypes.hasNext())
        {
            if (((MimeType) mimetypes.next()).getName() == mimeType.getName())
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
     * MediaType regsitry
     *
     * @return true is the capabilities of this agent at least match those
     * required by the media type
     */
    public boolean supportsMediaType(String media)
    {
        Iterator mediatypes = mediaTypeMap.values().iterator();
        while (mediatypes.hasNext())
        {
            if (((MediaType) mediatypes.next()).getName() == media)
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
