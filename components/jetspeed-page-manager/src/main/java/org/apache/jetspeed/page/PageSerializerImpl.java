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
package org.apache.jetspeed.page;

import java.util.Iterator;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.tools.ToolsLogger;

/**
 * PageSerializerImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */

public class PageSerializerImpl implements PageSerializer
{
    public static class Context implements Result
    {
        protected String folder;
        
        protected int folderCount;

        protected int pageCount;

        protected int linkCount;

        protected boolean overwritePages;

        protected boolean overwriteFolders;

        protected transient ToolsLogger logger;
        
        public String getFolder()
        {
            return folder;
        }

        public int getFolderCount()
        {
            return folderCount;
        }

        public int getPageCount()
        {
            return pageCount;
        }

        public int getLinkCount()
        {
            return linkCount;
        }

        public boolean isOverwritePages()
        {
            return overwritePages;
        }

        public boolean isOverwriteFolders()
        {
            return overwriteFolders;
        }
        
        public Context(String folder, boolean overwritePages, boolean overwriteFolders, ToolsLogger logger)
        {
            this.folder = folder;
            this.overwritePages = overwritePages;
            this.overwriteFolders = overwriteFolders;
            this.logger = logger;
        }
    }

    /* source page manager impl */
    private PageManager sourceManager;

    /* destination page manager impl */
    private PageManager destManager;

    public PageSerializerImpl(PageManager sourceManager, PageManager destManager)
    {
        this.sourceManager = sourceManager;
        this.destManager = destManager;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageSerializer#importPages(java.lang.String, boolean, boolean, boolean, org.apache.commons.logging.Log)
     */
    public Result importPages(ToolsLogger logger, String rootFolder, boolean overwriteFolders, boolean overwritePages, boolean fullImport) throws JetspeedException
    {
        Context context = new Context(rootFolder, overwritePages, overwriteFolders, logger);
        context.logger.info("Starting " + (fullImport ? "full" : "") + " import of folder: " + rootFolder
                + " (overwriting folders: " + overwriteFolders + ", pages: " + overwritePages + ")");
        importFolder(sourceManager.getFolder(rootFolder), context);

        if (fullImport)
        {
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
                context.logger.info("Importing page security");
                PageSecurity rootSecurity = destManager.copyPageSecurity(sourcePageSecurity);
                destManager.updatePageSecurity(rootSecurity);
            }
            else
            {
                context.logger.info("Skipping page security: not found");
            }
        }
        context.logger.info("Import finished: processed " + context.folderCount + " folder(s), " + context.pageCount
                + " page(s), " + context.linkCount + " link(s).");
        context.logger = null;
        return context;
    }

    private Folder importFolder(Folder srcFolder, Context context) throws JetspeedException
    {
        Folder dstFolder = lookupFolder(srcFolder.getPath());
        if (null != dstFolder)
        {
            if (context.overwriteFolders)
            {
                context.logger.info("overwriting folder " + srcFolder.getPath());
                destManager.removeFolder(dstFolder);
                dstFolder = destManager.copyFolder(srcFolder, srcFolder.getPath());
                destManager.updateFolder(dstFolder);
                context.folderCount++;

            }
            else
            {
                context.logger.info("skipping folder " + srcFolder.getPath());
            }
        }
        else
        {
            context.logger.info("importing new folder " + srcFolder.getPath());
            dstFolder = destManager.copyFolder(srcFolder, srcFolder.getPath());
            destManager.updateFolder(dstFolder);
            context.folderCount++;
        }
        Iterator pages = srcFolder.getPages().iterator();
        while (pages.hasNext())
        {
            Page srcPage = (Page) pages.next();
            Page dstPage = lookupPage(srcPage.getPath());
            if (null != dstPage)
            {
                if (context.overwritePages)
                {
                    context.logger.info("overwriting page " + srcPage.getPath());
                    destManager.removePage(dstPage);
                    dstPage = destManager.copyPage(srcPage, srcPage.getPath());
                    destManager.updatePage(dstPage);
                    context.pageCount++;
                }
                else
                {
                    context.logger.info("skipping page " + srcPage.getPath());
                }
            }
            else
            {
                context.logger.info("importing new page " + srcPage.getPath());
                dstPage = destManager.copyPage(srcPage, srcPage.getPath());
                destManager.updatePage(dstPage);
                context.pageCount++;
            }
        }

        Iterator links = srcFolder.getLinks().iterator();
        while (links.hasNext())
        {
            Link srcLink = (Link) links.next();
            Link dstLink = lookupLink(srcLink.getPath());
            if (null != dstLink)
            {
                if (context.overwritePages)
                {
                    context.logger.info("overwriting link " + srcLink.getPath());
                    destManager.removeLink(dstLink);
                    dstLink = destManager.copyLink(srcLink, srcLink.getPath());
                    destManager.updateLink(dstLink);
                    context.linkCount++;
                }
                else
                {
                    context.logger.info("skipping link " + srcLink.getPath());
                }
            }
            else
            {
                context.logger.info("importing new link " + srcLink.getPath());
                dstLink = destManager.copyLink(srcLink, srcLink.getPath());
                destManager.updateLink(dstLink);
                context.linkCount++;
            }
        }

        Iterator folders = srcFolder.getFolders().iterator();
        while (folders.hasNext())
        {
            Folder folder = (Folder) folders.next();
            importFolder(folder, context);
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

    private Link lookupLink(String path)
    {
        try
        {
            return destManager.getLink(path);
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
}
