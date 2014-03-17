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

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.Node;
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
        
        protected boolean all;

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
        
        public boolean isSerializeAll()
        {
            return all;
        }
        
        public Context(String folder, boolean overwritePages, boolean overwriteFolders, boolean all, ToolsLogger logger)
        {
            this.folder = folder;
            this.overwritePages = overwritePages;
            this.overwriteFolders = overwriteFolders;
            this.all = all;
            this.logger = logger;
        }
    }

    /* source page manager impl */
    private PageManager sourceManager;

    /* destination page manager impl */
    private PageManager destManager;
    
    private Boolean defaultOverwriteFolders = Boolean.TRUE;
    private Boolean defaultOverwritePages = Boolean.TRUE;
    private Boolean defaultAll = Boolean.TRUE;

    public PageSerializerImpl(PageManager sourceManager, PageManager destManager)
    {
        this.sourceManager = sourceManager;
        this.destManager = destManager;
    }

    public PageSerializerImpl(PageManager sourceManager, PageManager destManager, boolean defaultOverwriteFolders, boolean defaultOverwritePages, boolean defaultAll)
    {
        this.sourceManager = sourceManager;
        this.destManager = destManager;
        this.defaultOverwriteFolders = new Boolean(defaultOverwriteFolders);
        this.defaultOverwritePages = new Boolean(defaultOverwritePages);
        this.defaultAll = new Boolean(defaultAll);
    }

    private boolean boolValue(Boolean bool, Boolean defaultValue)
    {
        return bool != null ? bool.booleanValue() : defaultValue.booleanValue();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageSerializer#importPages(org.apache.jetspeed.tools.ToolsLogger, java.lang.String)
     */
    public Result importPages(ToolsLogger logger, String rootFolder) throws JetspeedException
    {
        return importPages(logger, rootFolder, defaultOverwriteFolders, defaultOverwritePages, defaultAll);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageSerializer#exportPages(org.apache.jetspeed.tools.ToolsLogger, java.lang.String)
     */
    public Result exportPages(ToolsLogger logger, String rootFolder) throws JetspeedException
    {
        return exportPages(logger, rootFolder, defaultOverwriteFolders, defaultOverwritePages, defaultAll);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageSerializer#importPages(java.lang.String, Boolean, Boolean, Boolean, org.apache.commons.logging.Log)
     */
    public Result importPages(ToolsLogger logger, String rootFolder, Boolean overwriteFolders, Boolean overwritePages, Boolean all) throws JetspeedException
    {
        return execute(sourceManager, destManager, new Context(rootFolder, boolValue(overwritePages, defaultOverwritePages), boolValue(overwriteFolders, defaultOverwriteFolders), boolValue(all, defaultAll),logger), true);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageSerializer#exportPages(java.lang.String, Boolean, Boolean, Boolean, org.apache.commons.logging.Log)
     */
    public Result exportPages(ToolsLogger logger, String rootFolder, Boolean overwriteFolders, Boolean overwritePages, Boolean all) throws JetspeedException
    {
        return execute(destManager, sourceManager, new Context(rootFolder, boolValue(overwritePages, defaultOverwritePages), boolValue(overwriteFolders, defaultOverwriteFolders), boolValue(all, defaultAll),logger), false);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageSerializer#importPages(java.lang.String, Boolean, Boolean, Boolean, org.apache.commons.logging.Log)
     */
    private Result execute(PageManager src, PageManager dest, Context context, boolean importing) throws JetspeedException
    {
        Folder folder = null;
        try
        {
            folder = src.getFolder(context.folder);
        }
        catch (FolderNotFoundException fnfe)
        {
        }
        if (folder != null)
        {
            context.logger.info("Starting " + (context.all ? "complete" : "") + " " + (importing?"import":"export") + " of folder: " + context.folder
                                + " (overwriting folders: " + context.overwriteFolders + ", pages: " + context.overwritePages + ")");
            processFolder(folder, dest, context);

            if (context.all)
            {
                // create the root page security
                PageSecurity sourcePageSecurity = null;
                try
                {
                    sourcePageSecurity = src.getPageSecurity();
                }
                catch (DocumentNotFoundException e)
                {
                    // skip over it, not found
                }

                if (sourcePageSecurity != null)
                {
                    context.logger.info((importing?"Importing":"Exporting")+" page security");
                    PageSecurity rootSecurity = dest.copyPageSecurity(sourcePageSecurity);
                    dest.updatePageSecurity(rootSecurity);
                }
                else
                {
                    context.logger.info("Skipping page security: not found");
                }
            }
            context.logger.info((importing?"Import":"Export")+" finished: processed " + context.folderCount + " folder(s), " + context.pageCount
                                + " page(s), " + context.linkCount + " link(s).");
        }
        else
        {
            context.logger.info((importing?"Import":"Export")+" skipped: "+context.folder+" not found.");
        }
        context.logger = null;
        // cleanup page manager thread/request caches
        src.cleanupRequestCache();
        dest.cleanupRequestCache();
        return context;
    }
    
    private Folder processFolder(Folder srcFolder, PageManager dest, Context context) throws JetspeedException
    {
        Folder dstFolder = lookupFolder(dest, srcFolder.getPath());
        if (null != dstFolder)
        {
            if (context.overwriteFolders)
            {
                context.logger.info("overwriting folder " + srcFolder.getPath());
                dest.removeFolder(dstFolder);
                dstFolder = dest.copyFolder(srcFolder, srcFolder.getPath());
                dest.updateFolder(dstFolder);
                context.folderCount++;

            }
            else
            {
                context.logger.info("skipping folder " + srcFolder.getPath());
            }
        }
        else
        {
            context.logger.info("processing new folder " + srcFolder.getPath());
            dstFolder = dest.copyFolder(srcFolder, srcFolder.getPath());
            dest.updateFolder(dstFolder);
            context.folderCount++;
        }
        for (Node srcPageNode : srcFolder.getPages())
        {
            Page srcPage = (Page) srcPageNode;
            Page dstPage = lookupPage(dest, srcPage.getPath());
            if (null != dstPage)
            {
                if (context.overwritePages)
                {
                    context.logger.info("overwriting page " + srcPage.getPath());
                    dest.removePage(dstPage);
                    dstPage = dest.copyPage(srcPage, srcPage.getPath(), true);
                    dest.updatePage(dstPage);
                    context.pageCount++;
                }
                else
                {
                    context.logger.info("skipping page " + srcPage.getPath());
                }
            }
            else
            {
                context.logger.info("processing new page " + srcPage.getPath());
                dstPage = dest.copyPage(srcPage, srcPage.getPath(), true);
                dest.updatePage(dstPage);
                context.pageCount++;
            }
        }
        for (Node srcPageTemplateNode : srcFolder.getPageTemplates())
        {
            PageTemplate srcPageTemplate = (PageTemplate) srcPageTemplateNode;
            PageTemplate dstPageTemplate = lookupPageTemplate(dest, srcPageTemplate.getPath());
            if (null != dstPageTemplate)
            {
                if (context.overwritePages)
                {
                    context.logger.info("overwriting page template " + srcPageTemplate.getPath());
                    dest.removePageTemplate(dstPageTemplate);
                    dstPageTemplate = dest.copyPageTemplate(srcPageTemplate, srcPageTemplate.getPath(), true);
                    dest.updatePageTemplate(dstPageTemplate);
                    context.pageCount++;
                }
                else
                {
                    context.logger.info("skipping page template " + srcPageTemplate.getPath());
                }
            }
            else
            {
                context.logger.info("processing new page template " + srcPageTemplate.getPath());
                dstPageTemplate = dest.copyPageTemplate(srcPageTemplate, srcPageTemplate.getPath(), true);
                dest.updatePageTemplate(dstPageTemplate);
                context.pageCount++;
            }
        }
        for (Node srcDynamicPageNode : srcFolder.getDynamicPages())
        {
            DynamicPage srcDynamicPage = (DynamicPage) srcDynamicPageNode;
            DynamicPage dstDynamicPage = lookupDynamicPage(dest, srcDynamicPage.getPath());
            if (null != dstDynamicPage)
            {
                if (context.overwritePages)
                {
                    context.logger.info("overwriting dynamic page " + srcDynamicPage.getPath());
                    dest.removeDynamicPage(dstDynamicPage);
                    dstDynamicPage = dest.copyDynamicPage(srcDynamicPage, srcDynamicPage.getPath(), true);
                    dest.updateDynamicPage(dstDynamicPage);
                    context.pageCount++;
                }
                else
                {
                    context.logger.info("skipping dynamic page " + srcDynamicPage.getPath());
                }
            }
            else
            {
                context.logger.info("processing new dynamic page " + srcDynamicPage.getPath());
                dstDynamicPage = dest.copyDynamicPage(srcDynamicPage, srcDynamicPage.getPath(), true);
                dest.updateDynamicPage(dstDynamicPage);
                context.pageCount++;
            }
        }
        for (Node srcFragmentDefinitionNode : srcFolder.getFragmentDefinitions())
        {
            FragmentDefinition srcFragmentDefinition = (FragmentDefinition) srcFragmentDefinitionNode;
            FragmentDefinition dstFragmentDefinition = lookupFragmentDefinition(dest, srcFragmentDefinition.getPath());
            if (null != dstFragmentDefinition)
            {
                if (context.overwritePages)
                {
                    context.logger.info("overwriting fragment definition " + srcFragmentDefinition.getPath());
                    dest.removeFragmentDefinition(dstFragmentDefinition);
                    dstFragmentDefinition = dest.copyFragmentDefinition(srcFragmentDefinition, srcFragmentDefinition.getPath(), true);
                    dest.updateFragmentDefinition(dstFragmentDefinition);
                    context.pageCount++;
                }
                else
                {
                    context.logger.info("skipping fragment definition " + srcFragmentDefinition.getPath());
                }
            }
            else
            {
                context.logger.info("processing fragment definition " + srcFragmentDefinition.getPath());
                dstFragmentDefinition = dest.copyFragmentDefinition(srcFragmentDefinition, srcFragmentDefinition.getPath(), true);
                dest.updateFragmentDefinition(dstFragmentDefinition);
                context.pageCount++;
            }
        }
        for (Node srcLinkNode : srcFolder.getLinks())
        {
            Link srcLink = (Link) srcLinkNode;
            Link dstLink = lookupLink(dest, srcLink.getPath());
            if (null != dstLink)
            {
                if (context.overwritePages)
                {
                    context.logger.info("overwriting link " + srcLink.getPath());
                    dest.removeLink(dstLink);
                    dstLink = dest.copyLink(srcLink, srcLink.getPath());
                    dest.updateLink(dstLink);
                    context.linkCount++;
                }
                else
                {
                    context.logger.info("skipping link " + srcLink.getPath());
                }
            }
            else
            {
                context.logger.info("processing new link " + srcLink.getPath());
                dstLink = dest.copyLink(srcLink, srcLink.getPath());
                dest.updateLink(dstLink);
                context.linkCount++;
            }
        }

        for (Node srcFolderNode : srcFolder.getFolders())
        {
            Folder folder = (Folder) srcFolderNode;
            processFolder(folder, dest, context);
        }

        return dstFolder;
    }

    private static Page lookupPage(PageManager mgr, String path)
    {
        try
        {
            return mgr.getPage(path);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static PageTemplate lookupPageTemplate(PageManager mgr, String path)
    {
        try
        {
            return mgr.getPageTemplate(path);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static DynamicPage lookupDynamicPage(PageManager mgr, String path)
    {
        try
        {
            return mgr.getDynamicPage(path);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static FragmentDefinition lookupFragmentDefinition(PageManager mgr, String path)
    {
        try
        {
            return mgr.getFragmentDefinition(path);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static Link lookupLink(PageManager mgr, String path)
    {
        try
        {
            return mgr.getLink(path);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static Folder lookupFolder(PageManager mgr, String path)
    {
        try
        {
            return mgr.getFolder(path);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
