/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetspeed.om.folder.impl;

import java.util.Collection;

import org.apache.jetspeed.om.folder.Folder;

/**
 * FolderImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public class FolderImpl implements Folder {
    
    private int id;
    private String name;
    private String defaultPage;
    private String defaultTheme;
    private Collection folders;
    private Collection pages;
    //private GenericMetadata metadata;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultPage()
     */
    public String getDefaultPage()
    {
        return defaultPage;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setDefaultPage()
     */
    public void setDefaultPage(String defaultPage)
    {
        this.defaultPage = defaultPage;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultTheme()
     */
    public String getDefaultTheme() {
        return defaultTheme;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setDefaultTheme()
     */
    public void setDefaultTheme(String defaultTheme)
    {
        this.defaultTheme = defaultTheme;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getFolders()
     */
    public Collection getFolders()
    {
        return folders;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setFolders(java.util.Collection)
     */
    public void setFolders(Collection folders)
    {
        this.folders = folders;        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#getPages()
     */
    public Collection getPages()
    {
        return pages;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.Folder#setPages(java.util.Collection)
     */
    public void setPages(Collection pages)
    {
        this.pages = pages;
    }

}
