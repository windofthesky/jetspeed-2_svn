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

import java.security.Principal;
import java.util.Iterator;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserPrincipal;


/**
 * PageManagerUtils
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PageManagerUtils
{
    protected static Log log = LogFactory.getLog(PageManagerUtils.class);    
    
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
        Principal principal = SecurityHelper.getBestPrincipal(subject, UserPrincipal.class); 
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
            Iterator roles = SecurityHelper.getPrincipals(subject, RolePrincipal.class).iterator();
            while (roles.hasNext())
            {                            
                RolePrincipal role = (RolePrincipal)roles.next();
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
        Iterator pages = srcFolder.getPages().iterator();
        while (pages.hasNext())
        {
            Page srcPage = (Page)pages.next();
            String path = concatenatePaths(destinationPath, srcPage.getName());
            if (!pageManager.pageExists(path))
            {
                Page dstPage = pageManager.copyPage(srcPage, path);
                pageManager.updatePage(dstPage);
            }
            else
            {
                path = concatenatePaths(destinationPath, uniqueName + "-" +srcPage.getName());               
                Page dstPage = pageManager.copyPage(srcPage, path);                
                pageManager.updatePage(dstPage);                
            }
        }
     
        Iterator links = srcFolder.getLinks().iterator();
        while (links.hasNext())
        {
            Link srcLink = (Link)links.next();
            String path = concatenatePaths(destinationPath, srcLink.getName());
            if (!pageManager.linkExists(path))
            {
                Link dstLink = pageManager.copyLink(srcLink, path);
                pageManager.updateLink(dstLink);
            }
            else
            {
                path = concatenatePaths(destinationPath, uniqueName + "-" +srcLink.getName());               
                Link dstLink = pageManager.copyLink(srcLink, path);                
                pageManager.updateLink(dstLink);                                
            }
        }     
        Iterator folders = srcFolder.getFolders().iterator();
        while (folders.hasNext())
        {
            Folder folder = (Folder)folders.next();
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
     * @param source source folder
     * @param dest destination folder
     */
    public static void deepCopyFolder(PageManager pageManager, Folder srcFolder, String destinationPath, String owner)
    throws NodeException
    {
        boolean found = true;
        try
        {
            Folder check = pageManager.getFolder(destinationPath);
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
        
        Iterator pages = srcFolder.getPages().iterator();
        while (pages.hasNext())
        {
            Page srcPage = (Page)pages.next();
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcPage.getName());
            Page dstPage = pageManager.copyPage(srcPage, path);
            pageManager.updatePage(dstPage);
        }
     
        Iterator links = srcFolder.getLinks().iterator();
        while (links.hasNext())
        {
            Link srcLink = (Link)links.next();
            String path = PageManagerUtils.concatenatePaths(destinationPath, srcLink.getName());
            Link dstLink = pageManager.copyLink(srcLink, path);
            pageManager.updateLink(dstLink);
        }
     
        Iterator folders = srcFolder.getFolders().iterator();
        while (folders.hasNext())
        {
            Folder folder = (Folder)folders.next();
            String newPath = concatenatePaths(destinationPath, folder.getName()); 
            deepCopyFolder(pageManager, folder, newPath, null);
        }        
    }
    
}
