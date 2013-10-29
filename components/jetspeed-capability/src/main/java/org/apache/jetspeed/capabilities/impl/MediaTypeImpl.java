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

import org.apache.commons.collections.CollectionUtils;
import org.apache.jetspeed.capabilities.Capability;
import org.apache.jetspeed.capabilities.MediaType;
import org.apache.jetspeed.capabilities.MimeType;

import java.util.ArrayList;
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
    private Collection<Capability> capabilities;
    private Collection<MimeType> mimetypes;
    private int mediatypeId;
    private String title;
    private String description;
    
    private String name;    // MediaTypeEntry name

    public MediaTypeImpl()
    {}

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (!(object instanceof MediaType))
        	return false;

        MediaTypeImpl obj = (MediaTypeImpl)object;

        if (this.name!=null)
        {
            if (!name.equals(obj.name))
            {
                return false;
            }
        }
        else
        {
            if (obj.name!=null)
            {
                return false;
            }
        }

        if (this.description!=null)
        {
            if (!description.equals(obj.description))
            {
                return false;
            }
        }
        else
        {
            if (obj.description!=null)
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


        if (this.title!=null)
        {
            if (!title.equals(obj.title))
            {
                return false;
            }
        }
        else
        {
            if (obj.title!=null)
            {
                return false;
            }
        }


        if (mimetypes != null)
        {
        	if (!CollectionUtils.isEqualCollection(mimetypes, obj.mimetypes))
            {
                return false;
            }
        }
        else
        {
            if (obj.mimetypes != null)
            {
                return false;
            }
        }

         if (capabilities != null)
        {
	       if (!(CollectionUtils.isEqualCollection(capabilities,obj.capabilities )))
	            return false;
	    }
        else
        {
            if (obj.capabilities != null)
            {
                return false;
            }
        }

        return true;
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

    
    public Collection<Capability> getCapabilities()
    {
        return this.capabilities;
    }

    public void setCapabilities(Collection<Capability> capabilities)
    {
        this.capabilities = capabilities;
    }
    
    
    public Collection<MimeType> getMimetypes()
    {
        return this.mimetypes;
    }
    
    public void setMimetypes(Collection<MimeType> mimetypes)
    {
        this.mimetypes = mimetypes;
    }

    public void addMimetype(MimeType mimeType)
    {
    	if (mimetypes == null)
    		mimetypes = new ArrayList();
        if (!mimetypes.contains(mimeType.getName()))
        {
            mimetypes.add(mimeType);
        }
    }


    public void addCapability(Capability capability)
    {
    	if (capabilities == null)
    		capabilities = new ArrayList();
        if (!capabilities.contains(capability.getName()))
        {
        	capabilities.add(capability);
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
