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
package org.apache.jetspeed.capabilities.impl;

import org.apache.jetspeed.capabilities.MediaType;
import java.util.Vector;
import java.util.Collection;

/**
 * Default bean like implementation of MediaTypeEntry interface
 * suitable for serializing with Castor
 *
 * @author <a href="mailto:raphael@apache.org">Rapha\u00ebl Luta</a>
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
