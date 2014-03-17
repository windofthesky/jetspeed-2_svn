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

import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.security.Principal;

/**
 * PageManagerUtils
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:firevelocity@gmail.com">Vivek Kumar</a>
 * @version $Id: $
 */
public class PageManagerUtils
{
    protected static Logger log = LoggerFactory.getLogger(PageManagerUtils.class);    
    
    /**
     * Creates a user's home page from the roles of the current user.
     * The use case: when a portal is setup to use shared pages, but then
     * the user attempts to customize. At this point, we create the new page(s) for the user.
     * 
     * @param subject
     */
    public static void createUserHomePagesFromRoles(PageManager pageManager, Subject subject)
    throws NodeException
    {
        Principal principal = SubjectHelper.getBestPrincipal(subject, User.class); 
        if (principal == null)
        {
            String errorMessage = "Could not create user home for null principal";
            log.error(errorMessage);
            throw new NodeException(errorMessage);
        }
        try
        {
            String userName = principal.getName();            
            // get user home
            Folder newUserFolder;
            if (pageManager.userFolderExists(userName))
            {
                newUserFolder = pageManager.getUserFolder(userName);
            }
            else
            {
                newUserFolder = pageManager.newFolder(Folder.USER_FOLDER + userName);
                SecurityConstraints constraints = pageManager.newSecurityConstraints();
                newUserFolder.setSecurityConstraints(constraints);
                newUserFolder.getSecurityConstraints().setOwner(userName);
                pageManager.updateFolder(newUserFolder);                
            }            
            // for each role for a user, deep copy the folder contents for that role 
            // into the user's home
            // TODO: this algorithm could actually merge pages on dups
            for (Principal role : SubjectHelper.getPrincipals(subject, Role.class))
            {
                if (pageManager.folderExists(Folder.ROLE_FOLDER + role.getName()))
                {
                    Folder roleFolder = pageManager.getFolder(Folder.ROLE_FOLDER + role.getName());                    
                    deepMergeFolder(pageManager, roleFolder, Folder.USER_FOLDER + newUserFolder.getName(), userName, role.getName());
                }
            }
        }
        catch (Exception e)
        {
            String errorMessage = "createUserHomePagesFromRoles failed: " + e.getMessage();
            log.error(errorMessage, e);
            throw new NodeException(errorMessage, e);
        }
    }

    /**
     * Deep merges from a source folder into a destination path for the given owner.
     * The unique name is used in conflict resolution for name collisions.
     * Example: deep merge a given role folder 'X' into /_user/david
     *          uniqueName = 'X'
     *          owner = 'david'
     *          destinationPath = '_user/david'
     *          
     * @param srcFolder
     * @param destinationPath
     * @param owner
     * @param uniqueName
     * @throws NodeException
     */
    public static void deepMergeFolder(PageManager pageManager, Folder srcFolder, String destinationPath, String owner, String uniqueName)
    throws NodeException
    {        
        for (Node srcPageNode : srcFolder.getPages())
        {
            Page srcPage = (Page)srcPageNode;
            String path = concatenatePaths(destinationPath, srcPage.getName());
            if (!pageManager.pageExists(path))
            {
                Page dstPage = pageManager.copyPage(srcPage, path);
                pageManager.updatePage(dstPage);
            }
        }
        for (Node srcPageTemplateNode : srcFolder.getPageTemplates())
        {
            PageTemplate srcPageTemplate = (PageTemplate)srcPageTemplateNode;
            String path = concatenatePaths(destinationPath, srcPageTemplate.getName());
            if (!pageManager.pageTemplateExists(path))
            {
                PageTemplate dstPageTemplate = pageManager.copyPageTemplate(srcPageTemplate, path);
                pageManager.updatePageTemplate(dstPageTemplate);
            }
        }
        for (Node srcDynamicPageNode : srcFolder.getDynamicPages())
        {
            DynamicPage srcDynamicPage = (DynamicPage)srcDynamicPageNode;
            String path = concatenatePaths(destinationPath, srcDynamicPage.getName());
            if (!pageManager.dynamicPageExists(path))
            {
                DynamicPage dstDynamicPage = pageManager.copyDynamicPage(srcDynamicPage, path);
                pageManager.updateDynamicPage(dstDynamicPage);
            }
        }
        for (Node srcFragmentDefinitionNode : srcFolder.getFragmentDefinitions())
        {
            FragmentDefinition srcFragmentDefinition = (FragmentDefinition)srcFragmentDefinitionNode;
            String path = concatenatePaths(destinationPath, srcFragmentDefinition.getName());
            if (!pageManager.fragmentDefinitionExists(path))
            {
                FragmentDefinition dstFragmentDefinition = pageManager.copyFragmentDefinition(srcFragmentDefinition, path);
                pageManager.updateFragmentDefinition(dstFragmentDefinition);
            }
        }

        for (Node srcLinkNode : srcFolder.getLinks())
        {
            Link srcLink = (Link)srcLinkNode;
            String path = concatenatePaths(destinationPath, srcLink.getName());
            if (!pageManager.linkExists(path))
            {
                Link dstLink = pageManager.copyLink(srcLink, path);
                pageManager.updateLink(dstLink);
            }
        }

        for (Node srcFolderNode : srcFolder.getFolders())
        {
            Folder folder = (Folder)srcFolderNode;
            String newPath = concatenatePaths(destinationPath, folder.getName());
            if (!pageManager.folderExists(newPath))
            {
                Folder dstFolder = pageManager.copyFolder(folder, newPath);
                pageManager.updateFolder(dstFolder);
            }
            deepMergeFolder(pageManager, folder, newPath, null, uniqueName);
        }                
    }

    public static String concatenatePaths(String base, String path)
    {
        String result = "";
        if (base == null)
        {
            if (path == null)
            {
                return result;
            }
            return path;
        }
        else
        {
            if (path == null)
            {
                return base;
            }
        }
        if (base.endsWith(Folder.PATH_SEPARATOR)) 
        {
            if (path.startsWith(Folder.PATH_SEPARATOR))
            {
                result = base.concat(path.substring(1));
                return result;
            }
        
        }
        else
        {
            if (!path.startsWith(Folder.PATH_SEPARATOR)) 
            {
                result = base.concat(Folder.PATH_SEPARATOR).concat(path);
                return result;
            }
        }
        return base.concat(path);
    }
    
    /**
     * Deep copy a folder
     *  
     * @param pageManager target page manager
     * @param srcFolder source folder
     * @param destinationPath destination folder
     * @param owner user name of owner or null
     * @param copyIds flag indicating whether to copy ids
     */
    public static void deepCopyFolder(PageManager pageManager, Folder srcFolder, String destinationPath, String owner, boolean copyIds)
    throws NodeException
    {
        boolean found = true;
        try
        {
            pageManager.getFolder(destinationPath);
        }
        catch (FolderNotFoundException e)
        {
            found = false;
        }
        if (found)
        {
            throw new NodeException("Destination already exists");
        }
        Folder dstFolder = pageManager.copyFolder(srcFolder, destinationPath);
        if (owner != null)
        {
            SecurityConstraints constraints = dstFolder.getSecurityConstraints();
            if (constraints == null)
            {
                constraints = pageManager.newSecurityConstraints();
                dstFolder.setSecurityConstraints(constraints);
            }
            dstFolder.getSecurityConstraints().setOwner(owner);
        }
        pageManager.updateFolder(dstFolder);
        
        for (Node srcPageNode : srcFolder.getPages())
        {
            Page srcPage = (Page)srcPageNode;
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcPage.getName());
            Page dstPage = pageManager.copyPage(srcPage, path, copyIds);
            pageManager.updatePage(dstPage);
        }
        for (Node srcPageTemplateNode : srcFolder.getPageTemplates())
        {
            PageTemplate srcPageTemplate = (PageTemplate)srcPageTemplateNode;
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcPageTemplate.getName());
            PageTemplate dstPageTemplate = pageManager.copyPageTemplate(srcPageTemplate, path, copyIds);
            pageManager.updatePageTemplate(dstPageTemplate);
        }     
        for (Node srcDynamicPageNode : srcFolder.getDynamicPages())
        {
            DynamicPage srcDynamicPage = (DynamicPage)srcDynamicPageNode;
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcDynamicPage.getName());
            DynamicPage dstDynamicPage = pageManager.copyDynamicPage(srcDynamicPage, path, copyIds);
            pageManager.updateDynamicPage(dstDynamicPage);
        }     
        for (Node srcFragmentDefinitionNode : srcFolder.getFragmentDefinitions())
        {
            FragmentDefinition srcFragmentDefinition = (FragmentDefinition)srcFragmentDefinitionNode;
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcFragmentDefinition.getName());
            FragmentDefinition dstFragmentDefinition = pageManager.copyFragmentDefinition(srcFragmentDefinition, path, copyIds);
            pageManager.updateFragmentDefinition(dstFragmentDefinition);
        }     
     
        for (Node srcLinkNode : srcFolder.getLinks())
        {
            Link srcLink = (Link)srcLinkNode;
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcLink.getName());
            Link dstLink = pageManager.copyLink(srcLink, path);
            pageManager.updateLink(dstLink);
        }
     
        for (Node srcFolderNode : srcFolder.getFolders())
        {
            Folder folder = (Folder)srcFolderNode;
            String newPath = concatenatePaths(destinationPath, folder.getName()); 
            deepCopyFolder(pageManager, folder, newPath, null, copyIds);
        }        
    }

    /**
     * Deep merge a folder
     *  
     * @param pageManager target page manager
     * @param srcFolder source folder
     * @param destinationPath destination folder
     * @param owner user name of owner or null
     * @param copyIds flag indicating whether to copy ids on merge
     */
    public static void deepMergeFolder(PageManager pageManager, Folder srcFolder, String destinationPath, String owner, boolean copyIds)
    throws NodeException
    {
        boolean found = true;
        Folder dstFolder = null;
        try
        {
            dstFolder = pageManager.getFolder(destinationPath);
        }
        catch (FolderNotFoundException e)
        {
            found = false;
        }
        if (!found)
        {
            dstFolder = pageManager.copyFolder(srcFolder, destinationPath);
            if (owner != null)
            {
                SecurityConstraints constraints = dstFolder.getSecurityConstraints();
                if (constraints == null)
                {
                    constraints = pageManager.newSecurityConstraints();
                    dstFolder.setSecurityConstraints(constraints);
                }
                dstFolder.getSecurityConstraints().setOwner(owner);
            }
            pageManager.updateFolder(dstFolder);
        }
        for (Node srcPageNode : srcFolder.getPages())
        {
            Page srcPage = (Page)srcPageNode;
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcPage.getName());
            if (!pageManager.pageExists(path))
            {
                Page dstPage = pageManager.copyPage(srcPage, path, copyIds);
                pageManager.updatePage(dstPage);
            }
        }
        for (Node srcPageTemplateNode : srcFolder.getPageTemplates())
        {
            PageTemplate srcPageTemplate = (PageTemplate)srcPageTemplateNode;
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcPageTemplate.getName());
            if (!pageManager.pageTemplateExists(path))
            {
                PageTemplate dstPageTemplate = pageManager.copyPageTemplate(srcPageTemplate, path, copyIds);
                pageManager.updatePageTemplate(dstPageTemplate);
            }
        }     
        for (Node srcDynamicPageNode : srcFolder.getDynamicPages())
        {
            DynamicPage srcDynamicPage = (DynamicPage)srcDynamicPageNode;
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcDynamicPage.getName());
            if (!pageManager.dynamicPageExists(path))
            {
                DynamicPage dstDynamicPage = pageManager.copyDynamicPage(srcDynamicPage, path, copyIds);
                pageManager.updateDynamicPage(dstDynamicPage);
            }
        }
        for (Node srcFragmentDefinitionNode : srcFolder.getFragmentDefinitions())
        {
            FragmentDefinition srcFragmentDefinition = (FragmentDefinition)srcFragmentDefinitionNode;
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcFragmentDefinition.getName());
            if (!pageManager.fragmentDefinitionExists(path))
            {
                FragmentDefinition dstFragmentDefinition = pageManager.copyFragmentDefinition(srcFragmentDefinition, path, copyIds);
                pageManager.updateFragmentDefinition(dstFragmentDefinition);
            }
        }
     
        for (Node srcLinkNode : srcFolder.getLinks())
        {
            Link srcLink = (Link)srcLinkNode;
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcLink.getName());
            if (!pageManager.linkExists(path))
            {            
                Link dstLink = pageManager.copyLink(srcLink, path);
                pageManager.updateLink(dstLink);
            }
        }
     
        for (Node srcFolderNode : srcFolder.getFolders())
        {
            Folder folder = (Folder)srcFolderNode;
            String newPath = concatenatePaths(destinationPath, folder.getName()); 
            deepMergeFolder(pageManager, folder, newPath, null, copyIds);
        }        
    }
    
}
