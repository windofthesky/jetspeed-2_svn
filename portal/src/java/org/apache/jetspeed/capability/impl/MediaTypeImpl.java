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

import org.apache.jetspeed.capability.MediaType;
import java.util.Vector;
import java.util.Collection;

/**
 * Default bean like implementation of MediaTypeEntry interface
 * suitable for serializing with Castor
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class MediaTypeImpl 
    implements MediaType
{
    protected String characterSet;
    private Vector capabilities;
    private Collection mimetypes;
    private int mediatypeId;
    private String title;
    private String description;
    
    private String name;    // MediaTypeEntry name

    public MediaTypeImpl()
    {}

    public MediaTypeImpl(long id,
                              String name,
                              int _hidden,
                              String mimeType,
                              String title,
                              String description,
                              String image,
                               String role)
    {
         this.mimetypes.add(mimeType);
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

        MediaTypeImpl obj = (MediaTypeImpl)object;

        if (mimetypes.isEmpty()!= true)
        {
            if ( !mimetypes.contains(obj.getMimetypes().iterator().next()) )
            {
                return false;
            }
        }
        else
        {
            if (obj.getMimetypes().isEmpty() == false)
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

    
    public Vector getCapabilities()
    {
        return this.capabilities;
    }

    public void setCapabilities(Vector capabilities)
    {
        this.capabilities = capabilities;
    }
    
    public Collection getMimetypes()
    {
        return this.mimetypes;
    }
    
    public void setMimetypes(Collection mimetypes)
    {
        this.mimetypes = mimetypes;
    }
    
    public void addMimetype(String name)
    {
        if (!mimetypes.contains(name))
        {
            mimetypes.add(name);
        }
    }

    public void removeMimetype(String name)
    {
        mimetypes.remove(name);
    }
    
    /**
     * Set MediaType ID -- Assigns ID
     * @param id
     */
    public void setMediatypeId(int id)
    {
        this.mediatypeId = id;
    }

    /**
     * Get MediaType ID -- Return ID
     * @return MediaTypeID
     */
    public int getMediatypeId()
    {
        return this.mediatypeId;
    }
    
    /**
      * Set name ob MediaTypeEntry
      */
     public void setName(String name)
     {
         this.name = name;
     }
  
     /**
      * Get name ob MediaTypeEntry
      */
 
     public String getName()
     {
         return this.name;
     }
     
     public String getTitle()
     {
         return this.title;
     }

     public void setTitle(String title)
     {
         this.title = title;
     }
     
     public String getDescription()
     {
         return this.description;
     }


    public void setDescription(String desc)
    {
        this.description = desc;
    }
}
