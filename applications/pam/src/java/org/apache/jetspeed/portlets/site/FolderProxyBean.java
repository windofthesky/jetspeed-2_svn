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

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.page.PageManager;
import org.apache.portals.bridges.frameworks.ExternalComponentSupport;
import org.apache.portals.bridges.frameworks.Lookup;


/**
 * FolderProxyBean
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class FolderProxyBean 
    implements 
        ExternalComponentSupport, 
        Lookup,
        Serializable
{
    private transient PageManager pageManager = null;
    private transient Folder folder;
    private String title;
    private String lookupKey;
    
    public FolderProxyBean()
    {        
    }
    
    public void setExternalSupport(Object externalSupport)
    {
        if (externalSupport instanceof PageManager)
        {
            pageManager = (PageManager)externalSupport;
        }
    }
    
    public boolean lookup(String key)
    {
        boolean result = true;
        try
        {
            if (pageManager != null)
            {
                this.folder = pageManager.getFolder(key);    
                setTitle(folder.getTitle());
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
}
