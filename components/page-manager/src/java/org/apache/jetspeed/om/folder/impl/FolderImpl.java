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

import java.io.File;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderSet;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSet;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.impl.PageSetImpl;

/**
 * FolderImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:jford@apache.org">Jeremy Ford </a>
 * @version $Id$
 */
public class FolderImpl implements Folder
{

    private int id;
    private String name;
    private String defaultPage;
    private String defaultTheme;
    private FolderSet folders;
    private PageSet pages;
    private String acl;
    private Folder parent;
    private File directory;
    private PageManager pageManager;

    //private GenericMetadata metadata;

    public FolderImpl( File directory, String name, PageManager pageManager )
    {

        this.directory = directory;
        this.name = name;
        this.pageManager = pageManager;
    }

    /**
     * @return Returns the directory.
     */
    public File getDirectory()
    {
        return directory;
    }

    /**
     * @return Returns the parent.
     */
    public Folder getParent()
    {
        if(parent == null)
        {
            int lastSlash = name.lastIndexOf('/');
            if(lastSlash != -1)
            {
                parent = pageManager.getFolder(name.substring(0, lastSlash));
            }            
        }
        
        return parent;
    }

    /**
     * @param parent
     *            The parent to set.
     */
    public void setParent( Folder parent )
    {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getName()
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return Returns the acl.
     */
    public String getAcl()
    {
        return acl;
    }

    /**
     * @param acl
     *            The acl to set.
     */
    public void setAcl( String acl )
    {
        this.acl = acl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#setName(java.lang.String)
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultPage()
     */
    public String getDefaultPage()
    {
        return defaultPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#setDefaultPage()
     */
    public void setDefaultPage( String defaultPage )
    {
        this.defaultPage = defaultPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultTheme()
     */
    public String getDefaultTheme()
    {
        return defaultTheme;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#setDefaultTheme()
     */
    public void setDefaultTheme( String defaultTheme )
    {
        this.defaultTheme = defaultTheme;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getFolders()
     */
    public FolderSet getFolders()
    {
        if (folders == null)
        {
            folders = new FolderSetImpl(this);
            File[] children = getDirectory().listFiles();
            for (int i = 0; i < children.length; i++)
            {
                if (children[i].isDirectory())
                {
                    String folderName = name + "/" + children[i].getName();

                    folders.add(pageManager.getFolder(folderName));
                }
            }
        }

        return folders;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#setFolders(java.util.Collection)
     */
    public void setFolders( FolderSet folders )
    {
        this.folders = folders;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getPages()
     */
    public PageSet getPages()
    {
        if (pages == null)
        {
            pages = new PageSetImpl(this);
            File[] children = getDirectory().listFiles();
            for (int i = 0; i < children.length; i++)
            {
                if (children[i].isFile())
                {
                    pages.add(pageManager.getPage(name + "/" + children[i].getName()));
                }

            }
        }

        return pages;
    }
    
    public Page getPage(String name)
    {
        return pages.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#setPages(java.util.Collection)
     */
    public void setPages( PageSet pages )
    {
        this.pages = pages;
    }

}