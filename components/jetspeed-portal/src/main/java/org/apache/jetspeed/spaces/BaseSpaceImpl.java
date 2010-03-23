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
package org.apache.jetspeed.spaces;

import java.io.Serializable;
import java.util.Locale;

import org.apache.jetspeed.om.folder.Folder;

abstract public class BaseSpaceImpl implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public static final String META_DESCRIPTION = "description";
    public static final String META_TITLE       = "title";
    public static final String META_SHORT_TITLE       = "short-title";

    protected transient Folder backingFolder = null;

    abstract protected String getOwnerFieldName();
    
    public String getName()
    {
        return backingFolder.getName();
    }
        
    public String getPath()
    {
        return backingFolder.getPath();       
    }

	public String getOwner() 
	{
        return SpacesServiceImpl.retrieveField(backingFolder, Locale.ENGLISH, getOwnerFieldName());
	}

    public void setOwner(String owner)
    {
        SpacesServiceImpl.updateField(backingFolder, Locale.ENGLISH, getOwnerFieldName(), owner);
    }
    
	public String getTitle() 
	{
	    return backingFolder.getTitle();
	}

	public void setTitle(String title) 
	{
	    backingFolder.setTitle(title);
	}
	
    public String getShortTitle() 
    {
        return backingFolder.getShortTitle();
    }
    
    public void setShortTitle(String shortTitle) 
    {
        backingFolder.setShortTitle(shortTitle);
    }

    public String getTitle(Locale locale) 
    {
        return SpacesServiceImpl.retrieveField(backingFolder, locale, META_TITLE);
    }
    
    public void setTitle(String title, Locale locale)
    {
        SpacesServiceImpl.updateField(backingFolder, locale, META_TITLE, title);
    }
    
    public String getShortTitle(Locale locale) 
    {
        return SpacesServiceImpl.retrieveField(backingFolder, locale, META_SHORT_TITLE);
    }
    
    public void setShortTitle(String shortTitle, Locale locale)
    {
        SpacesServiceImpl.updateField(backingFolder, locale, META_SHORT_TITLE, shortTitle);
    }
	
    public String getDescription() 
    {
        return SpacesServiceImpl.retrieveField(backingFolder, null, META_DESCRIPTION);
    }
    
    public void setDescription(String description)
    {
        SpacesServiceImpl.updateField(backingFolder, null, META_DESCRIPTION, description);
    }
	
    public String getDescription(Locale locale) 
    {
        return SpacesServiceImpl.retrieveField(backingFolder, locale, META_DESCRIPTION);
    }
	
    public void setDescription(String description, Locale locale)
    {
        SpacesServiceImpl.updateField(backingFolder, locale, META_DESCRIPTION, description);
    }

	protected Folder getBackingFolder()
	{
		return this.backingFolder;
	}    
}
