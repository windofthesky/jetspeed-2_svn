/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

import java.util.Vector;
import java.util.Collection;

/**
 * This entry describes all the properties that should be present in
 * a RegistryEntry describing a MediaType
 *
 * FIXME: we should add some additionnal attrbutes for separating 2 versions
 * of the same mime type
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public interface MediaType
{
    /**
     * Set MediaType ID -- Assigns ID
     * @param id
     */
    public void setMediatypeId(int id);

    /**
     * Get MediaType ID -- Return ID
     * @return MediaTypeID
     */
    public int getMediatypeId();

    /** @return the character set associated with this MediaType */
    public String getCharacterSet();

    /** Sets the character set associated with this MediaType */
    public void setCharacterSet(String charSet);

    /**
     * Returns all supported capablities as <CODE>CapabilityMap</CODE>.
     * The <CODE>CapabilityMap</CODE> contains all capabilities in arbitrary
     * order.
     *
     * @return a vector of capabilities
     */
    public Vector getCapabilities();

    /**
     * Set the capabilities
     * @param vector of capabilities
     */
    public void setCapabilities(Vector capabilities);

    /**
    * Returns all supported mimetypes as <CODE>MimeTypeMap</CODE>.
    * The <CODE>MimeTypeMap</CODE> contains all mimetypes in decreasing
    * order of importance.
    *
    * @return the MimeTypeMap
    * @see MimeTypeMap
    */
    public Collection getMimetypes();

    /**
     * Set mime types
     * @param mimetypes
     */
    public void setMimetypes(Collection mimetypes);

    /**
     * Removes the MimeType to the MimeType map 
     * @param name of MimeType to remove
     */

    public void removeMimetype(String name);

    /**
     * Add MimeType to the MimeType map 
     * @param name
     */
    public void addMimetype(String name);

    /**
     * Set Name of MediaType
     * @param name Name of MediaType
     */
    public void setName(String name);

    /**
     * Get Name of MediaType
     * @return Name of MediaType
     */
    public String getName();

    /**
     * Get Title of MediaType
     * @return Title of MediaType
     */
    public String getTitle();

    /**
     * Set MediaType title
     * @param title
     */
    public void setTitle(String title);

    /**
     * Get MediaType description
     * @return Returns description of MediaType
     */
    public String getDescription();

    /**
     * Set description of MediaType
     * @param desc Description string
     */
    public void setDescription(String desc);
}
