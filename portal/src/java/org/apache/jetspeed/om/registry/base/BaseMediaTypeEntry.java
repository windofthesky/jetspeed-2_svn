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
package org.apache.jetspeed.om.registry.base;

import org.apache.jetspeed.om.registry.CapabilityMap;
import org.apache.jetspeed.om.registry.MediaTypeEntry;

/**
 * Default bean like implementation of MediaTypeEntry interface
 * suitable for serializing with Castor
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class BaseMediaTypeEntry extends BaseRegistryEntry
    implements MediaTypeEntry
{

    protected String mimeType;
    protected String characterSet;
    private CapabilityMap capabilities = new BaseCapabilityMap();

    public BaseMediaTypeEntry()
    {}

    public BaseMediaTypeEntry(long id,
                              String name,
                              int _hidden,
                              String mimeType,
                              String title,
                              String description,
                              String image,
                               String role)
    {
        super(id, name, _hidden, title, description, image, role);

        this.mimeType = mimeType;
    }

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object==null)
        {
            return false;
        }

        BaseMediaTypeEntry obj = (BaseMediaTypeEntry)object;

        if (mimeType!=null)
        {
            if (!mimeType.equals(obj.mimeType))
            {
                return false;
            }
        }
        else
        {
            if (obj.mimeType!=null)
            {
                return false;
            }
        }

        if (characterSet!=null)
        {
            if (!characterSet.equals(obj.characterSet))
            {
                return false;
            }
        }
        else
        {
            if (obj.characterSet!=null)
            {
                return false;
            }
        }

        if (!capabilities.equals(obj.capabilities))
        {
            return false;
        }

        return super.equals(object);
    }

    /** @return the mime type associated with this MediaType */
    public String getMimeType()
    {
        return this.mimeType;
    }

    /** Sets the MimeType associated with this MediaType
     *  @param mimeType the MIME type to associate
     */
    public void setMimeType( String mimeType )
    {
        this.mimeType = mimeType;
    }

    /** @return the character set associated with this MediaType */
    public String getCharacterSet()
    {
        return this.characterSet;
    }

    /** Sets the character set associated with this MediaType */
    public void setCharacterSet( String charSet)
    {
        this.characterSet = charSet;
    }

    public CapabilityMap getCapabilityMap()
    {
        return capabilities;
    }

    // castor related method definitions

    public BaseCapabilityMap getCapabilities()
    {
        return (BaseCapabilityMap)capabilities;
    }

    public void setCapabilities(BaseCapabilityMap capabilities)
    {
        this.capabilities = capabilities;
    }
}
