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
package org.apache.jetspeed.page;

import java.util.Iterator;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * DelegatingPageManager
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */

public class PageImporter
{
    /* source page manager impl */
    private PageManager sourceManager;
    /* destination page manager impl */
    private PageManager destManager;    
    /* rootFolder to start importing from */
    private String rootFolder;
    /* flag: overwrite folders during import */
    private boolean overwriteFolders = false;
    /* flag: overwrite pages during import */ 
    private boolean overwritePages = true;
    /* count of total folders imported */    
    private int folderCount = 0;    
    /* count of total pages imported */
    private int pageCount = 0;
    
    public static void main(String args[])
    {
        String fileName = System.getProperty("org.apache.jetspeed.page.import.configuration", "import.properties");
        PropertiesConfiguration configuration = new PropertiesConfiguration();
        try
        {
            configuration.load(fileName);        
            String [] bootAssemblies = configuration.getStringArray("boot.assemblies");
            String [] assemblies = configuration.getStringArray("assemblies");
            ClassPathXmlApplicationContext ctx;            
            
            if (bootAssemblies != null)
            {
                ApplicationContext bootContext = new ClassPathXmlApplicationContext(bootAssemblies, true);
                ctx = new ClassPathXmlApplicationContext(assemblies, true, bootContext);
            }
            else
            {
                ctx = new ClassPathXmlApplicationContext(assemblies, true);
            }
            
            String rootFolder = configuration.getString("root.folder", "/");
            boolean overwriteFolders = configuration.getBoolean("overwrite.folders", true);
            boolean overwritePages = configuration.getBoolean("overwrite.pages", true);
            boolean fullImport = configuration.getBoolean("full.import", true);
            
            PageManager srcManager = (PageManager)ctx.getBean("castorPageManager");
            PageManager dstManager = (PageManager)ctx.getBean("dbPageManager");
            PageImporter importer = new PageImporter(srcManager, dstManager, rootFolder, overwriteFolders, overwritePages);
            if (fullImport)
            {
                importer.fullImport();
            }
            else
            {
                importer.folderTreeImport();
            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to import: " + e);
            e.printStackTrace();
        }
        
    }
    
    public PageImporter(PageManager sourceManager, 
                        PageManager destManager, 
                        String rootFolder,
                        boolean overwriteFolders,
                        boolean overwritePages)
    {
        this.sourceManager = sourceManager;
        this.destManager = destManager;
        this.rootFolder = rootFolder;
        this.overwriteFolders = overwriteFolders;
        this.overwritePages = overwritePages;
    }
        
    public void fullImport()
    throws JetspeedException
    {
        Folder fsRoot = sourceManager.getFolder(rootFolder);                
        Folder root = importFolder(fsRoot);
        
        
        // create the root page security
        PageSecurity sourcePageSecurity = null;
        try
        {
            sourcePageSecurity = sourceManager.getPageSecurity();
        }
        catch (DocumentNotFoundException e)
        {
            // skip over it, not found
        }
        
        if (sourcePageSecurity != null)
        {
            PageSecurity rootSecurity = destManager.copyPageSecurity(sourcePageSecurity);        
            destManager.updatePageSecurity(rootSecurity);
        }
    }

    public void folderTreeImport()
    throws JetspeedException
    {
        Folder fsRoot = sourceManager.getFolder(rootFolder);                
        Folder root = importFolder(fsRoot);                            
    }
    
    private Folder importFolder(Folder srcFolder)
    throws JetspeedException
    {
        Folder dstFolder = lookupFolder(srcFolder.getPath());        
        if (null != dstFolder)
        {
            if (isOverwriteFolders())
            {
                System.out.println("overwriting folder " + srcFolder.getPath());
                destManager.removeFolder(dstFolder);
                dstFolder = destManager
                        .copyFolder(srcFolder, srcFolder.getPath());
                destManager.updateFolder(dstFolder);
                folderCount++;

            } else
                System.out.println("skipping folder " + srcFolder.getPath());
        } else
        {
            System.out.println("importing new folder " + srcFolder.getPath());
            dstFolder = destManager.copyFolder(srcFolder, srcFolder.getPath());
            destManager.updateFolder(dstFolder);
            folderCount++;
        }
        Iterator pages = srcFolder.getPages().iterator();
        while (pages.hasNext())
        {
            Page srcPage = (Page)pages.next();
            Page dstPage = lookupPage(srcPage.getPath());
            if (null != dstPage)
            {
                if (isOverwritePages())
                {
                    System.out.println("overwriting page " + srcPage.getPath());                            
                    destManager.removePage(dstPage);
                    dstPage = destManager.copyPage(srcPage, srcPage.getPath());
                    destManager.updatePage(dstPage);
                    pageCount++;                    
                }
                else
                    System.out.println("skipping page " + srcPage.getPath());                
            }
            else            
            {
                System.out.println("importing new page " + srcPage.getPath());
                dstPage = destManager.copyPage(srcPage, srcPage.getPath());
                destManager.updatePage(dstPage);
                pageCount++;
            }
        }
        
        Iterator folders = srcFolder.getFolders().iterator();
        while (folders.hasNext())
        {
            Folder folder = (Folder)folders.next();
            importFolder(folder);
        }
        
        return dstFolder;
    }
    
    private Page lookupPage(String path)
    {
        try
        {
            return destManager.getPage(path);
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    private Folder lookupFolder(String path)
    {
        try
        {
            return destManager.getFolder(path);
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    /**
     * @return Returns the overwrite.
     */
    public boolean isOverwriteFolders()
    {
        return overwriteFolders;
    }
    /**
     * @param overwrite The overwrite to set.
     */
    public void setOverwriteFolders(boolean overwrite)
    {
        this.overwriteFolders = overwrite;
    }
    
    /**
     * @return Returns the destManager.
     */
    public PageManager getDestManager()
    {
        return destManager;
    }
    /**
     * @param destManager The destManager to set.
     */
    public void setDestManager(PageManager destManager)
    {
        this.destManager = destManager;
    }
    /**
     * @return Returns the folderCount.
     */
    public int getFolderCount()
    {
        return folderCount;
    }
    /**
     * @param folderCount The folderCount to set.
     */
    public void setFolderCount(int folderCount)
    {
        this.folderCount = folderCount;
    }
    /**
     * @return Returns the overwritePages.
     */
    public boolean isOverwritePages()
    {
        return overwritePages;
    }
    /**
     * @param overwritePages The overwritePages to set.
     */
    public void setOverwritePages(boolean overwritePages)
    {
        this.overwritePages = overwritePages;
    }
    /**
     * @return Returns the pageCount.
     */
    public int getPageCount()
    {
        return pageCount;
    }
    /**
     * @param pageCount The pageCount to set.
     */
    public void setPageCount(int pageCount)
    {
        this.pageCount = pageCount;
    }
    /**
     * @return Returns the rootFolder.
     */
    public String getRootFolder()
    {
        return rootFolder;
    }
    /**
     * @param rootFolder The rootFolder to set.
     */
    public void setRootFolder(String rootFolder)
    {
        this.rootFolder = rootFolder;
    }
    /**
     * @return Returns the sourceManager.
     */
    public PageManager getSourceManager()
    {
        return sourceManager;
    }
    /**
     * @param sourceManager The sourceManager to set.
     */
    public void setSourceManager(PageManager sourceManager)
    {
        this.sourceManager = sourceManager;
    }
}