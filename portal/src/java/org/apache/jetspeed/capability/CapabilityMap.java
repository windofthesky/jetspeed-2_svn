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
package org.apache.jetspeed.capability;

import java.util.Iterator;

/**
 * This interface provides lookup features on the capabilities supported
 * by a client user agent.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
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
     * @param Mediatype to add
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
     * @param MediaTypeEntry
     */
    public void setPreferredMediaType(MediaType type);

    /**
     * Returns an ordered list of supported media-types, from most preferred
     * to least preferred
     */
    public Iterator listMediaTypes();

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
     * @param CApabilityID
     * @return Returns true if the current agent has the specified capabilityID
     */
    public boolean hasCapability(int cap);

    /**
     * @param Capability
     * @return returns true if the current agent has the specified capability
     */
    public boolean hasCapability(String capability);

    /**
     * Get the mime types that this CapabilityMap supports.
     * @return Returns an Iterator over the MimeType map
     */
    public Iterator getMimeTypes();

    /**
     * @param  MimeType
     * @return Return true if this CapabilityMap supports the given MimeType
     */
    public boolean supportsMimeType(MimeType mimeType);

    /**
     * Return true if this CapabilityMap supports the given media type
     *
     * @param media the name of a media type registered in the
     * MediaType regsitry
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
