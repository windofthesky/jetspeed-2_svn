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
     * that match the userAgent
     */
    CapabilityMap getCapabilityMap(String userAgent);

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

    
}
