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
package org.apache.jetspeed.portlets.site;

import java.io.Serializable;

import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.portals.bridges.frameworks.ExternalComponentSupport;
import org.apache.portals.bridges.frameworks.Lookup;


/**
 * LinkProxyBean
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class LinkProxyBean 
    implements 
        ExternalComponentSupport, 
        Lookup,
        Serializable
{
    private static final long serialVersionUID = 1;
    private transient PageManager pageManager = null;
    private transient Link link;
    private String lookupKey;
    private boolean isNew = true;

    private String key;
    private String title;
    private String shortTitle;
    private String version;
    private String resourceType;
    private String name;
    private String url;
    
    public LinkProxyBean()
    {        
    }
    
    /**
     * @return Returns the resourceType.
     */
    public String getResourceType()
    {
        return resourceType;
    }
    /**
     * @param resourceType The resourceType to set.
     */
    public void setResourceType(String resourceType)
    {
        this.resourceType = resourceType;
    }
    /**
     * @return Returns the shortTitle.
     */
    public String getShortTitle()
    {
        return shortTitle;
    }
    /**
     * @param shortTitle The shortTitle to set.
     */
    public void setShortTitle(String shortTitle)
    {
        this.shortTitle = shortTitle;
    }
        
    public void setExternalSupport(Object externalSupport)
    {
        if (externalSupport instanceof PageManager)
        {
            pageManager = (PageManager)externalSupport;
        }
    }
    
    public void update(Link link)
    {
        link.setTitle(this.getTitle());
        link.setShortTitle(this.getShortTitle());
        link.setVersion(this.getVersion());
        link.setUrl(this.url);
    }
    
    public boolean lookup(String key)
    {
        boolean result = true;
        try
        {
            if (pageManager != null)
            {
                this.link = pageManager.getLink(key);    
                setTitle(link.getTitle());                
                setShortTitle(link.getShortTitle());
                setVersion(link.getVersion());
                setName(link.getName());
                setUrl(link.getUrl());
                setKey(key);
                isNew = false;
            }
        }
        catch (Exception ne)
        {        
            // create a new folder
            result = false;
        }
        return result;
    }
    
    /**
     * @return Returns the title.
     */
    public String getTitle()
    {
        return title;
    }
    /**
     * @param title The title to set.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }
    /**
     * @return Returns the lookupKey.
     */
    public String getLookupKey()
    {
        return lookupKey;
    }
    /**
     * @param lookupKey The lookupKey to set.
     */
    public void setLookupKey(String lookupKey)
    {
        this.lookupKey = lookupKey;
    }
    /**
     * @return Returns the isNew.
     */
    public boolean isNew()
    {
        return isNew;
    }
    /**
     * @param isNew The isNew to set.
     */
    public void setNew(boolean isNew)
    {
        this.isNew = isNew;
    }
    /**
     * @return Returns the key.
     */
    public String getKey()
    {
        return key;
    }
    /**
     * @param key The key to set.
     */
    public void setKey(String key)
    {
        this.key = key;
    }
    /**
     * @return Returns the version.
     */
    public String getVersion()
    {
        return version;
    }
    /**
     * @param version The version to set.
     */
    public void setVersion(String version)
    {
        this.version = version;
    }
    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    
    public String getUrl()
    {
        return url;
    }

    
    public void setUrl(String url)
    {
        this.url = url;
    }
}
