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
package org.apache.jetspeed.capabilities;

import java.util.Iterator;

/**
 * This interface provides lookup features on the capabilities supported
 * by a client user agent.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha\u00ebl Luta</a>
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @version $Id$
 */
public interface CapabilityMap
{

    /**
     * Sets the client for the CapabilityMap
     *
     * @param client The client associated with this map
     */
    public void setClient(Client client);

    /**
     *  Returns the Client for the CapabilityMap
     *
     * @return The client associated with this map
     */
    public Client getClient();

    /**
     * Add capability to the CapabilityMap
     *
     * @param capability
     */
    public void addCapability(Capability capability);

    /**
     * Add Mimetype to the MimetypeMap
     *
     * @param mimetype
     */
    public void addMimetype(MimeType mimetype);

    /**
     * Add MediaType to the MediaTypeMap
     *
     * @param mediatype to add
     */
    public void addMediaType(MediaType mediatype);

    /**
     * @return Returns the preferred MIME type for the current user-agent
     */
    public MimeType getPreferredType();

    /**
     * @return Returns the preferred media type for the current user-agent
     */
    public MediaType getPreferredMediaType();

    /**
     * Sets the preferred MediaType for this CapabilityMap
     * @param type
     */
    public void setPreferredMediaType(MediaType type);

    /**
     * Returns an ordered list of supported media-types, from most preferred
     * to least preferred
     *
     * @return an iterator over all media types
     */
    public Iterator<MediaType> listMediaTypes();

    /**
     * @return Returns the user-agent string
     */
    public String getAgent();

    /**
     * @parm userAgent Agent from the request
     *
     * Set the userAgent in the capabilityMap
     */
    public void setAgent(String userAgent);

    /**
     * @param capabilityId
     * @return Returns true if the current agent has the specified capabilityID
     */
    public boolean hasCapability(int capabilityId);

    /**
     * @param capability
     * @return returns true if the current agent has the specified capability
     */
    public boolean hasCapability(String capability);

    /**
     * Get the mime types that this CapabilityMap supports.
     * @return Returns an Iterator over the MimeType map
     */
    public Iterator<MimeType> getMimeTypes();

    /**
     * @param  mimeType
     * @return Return true if this CapabilityMap supports the given MimeType
     */
    public boolean supportsMimeType(MimeType mimeType);

    /**
     * Return true if this CapabilityMap supports the given media type
     *
     * @param media the name of a media type registered in the
     * MediaType registry
     *
     * @return true is the capabilities of this agent at least match those
     * required by the media type
     */
    public boolean supportsMediaType(String media);

    /**
     * @return Create a map -> string representation
     */
    public String toString();

}
